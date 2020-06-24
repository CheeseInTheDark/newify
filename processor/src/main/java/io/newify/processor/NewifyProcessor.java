package io.newify.processor;

import com.squareup.javapoet.*;
import io.newify.annotation.New;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.newify.processor.Casing.qualifiedNameToMethodName;
import static io.newify.processor.Casing.toMethodCase;
import static java.util.stream.Collectors.*;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;

public class NewifyProcessor {

    private final ProcessingEnvironment processingEnvironment;
    private final NewClassBuilder newClassBuilder;

    private boolean processed = false;

    @Inject
    public NewifyProcessor(ProcessingEnvironment processingEnvironment, NewClassBuilder newClassBuilder) {
        this.processingEnvironment = processingEnvironment;
        this.newClassBuilder = newClassBuilder;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed) { return false; }

        processed = true;

        TypeSpec newClass = newClassBuilder.buildFrom(roundEnv);

        try {
            JavaFile.builder("io.newify.generated", newClass).build().writeTo(processingEnvironment.getFiler());
        } catch (IOException e) {
            processingEnvironment.getMessager().printMessage(ERROR, "Error generating New class: " + e.getMessage());
        }

        return false;
    }
}
