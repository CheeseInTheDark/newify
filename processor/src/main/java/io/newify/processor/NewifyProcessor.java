package io.newify.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.newify.annotation.New;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PUBLIC;

@SupportedAnnotationTypes("io.newify.annotation.New")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NewifyProcessor extends AbstractProcessor {

    private boolean processed = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed) {
            return false;
        }

        processed = true;

        Collection<? extends Element> elements = roundEnv.getElementsAnnotatedWith(New.class);
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(elements);

        List<MethodSpec> newMethods = constructors.stream()
                .collect(groupingBy(ExecutableElement::getSimpleName))
                .values().stream()
                .flatMap(this::toNonConflictingMethods)
                .collect(toList());

        TypeSpec newClass = TypeSpec.classBuilder("New").addModifiers(PUBLIC).addMethods(newMethods).build();

        try {
            JavaFile.builder("io.newify.generated", newClass).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {}

        return true;
    }

    private Stream<MethodSpec> toNonConflictingMethods(List<ExecutableElement> constructorsWithSameSimpleName) {
        Function<ExecutableElement, @Nullable MethodSpec> generateMethod = constructorsWithSameSimpleName.size() > 1 ?
                this::generateQualifiedNewMethod : this::generateNewMethod;

        return constructorsWithSameSimpleName.stream().map(generateMethod);
    }

    private MethodSpec generateNewMethod(ExecutableElement constructorElement) {
        TypeElement constructorType = constructorType(constructorElement);
        String methodName = toMethodCase(constructorType.getSimpleName().toString());
        ClassName classToBeReturned = ClassName.get(constructorType);

        return generateNewMethod(methodName, classToBeReturned);
    }

    private MethodSpec generateQualifiedNewMethod(ExecutableElement constructorElement) {
        TypeElement constructorType = constructorType(constructorElement);
        String methodName = qualifiedNameToMethodName(constructorType.getQualifiedName().toString());
        ClassName classToBeReturned = ClassName.get(constructorType);

        return generateNewMethod(methodName, classToBeReturned);
    }

    private TypeElement constructorType(ExecutableElement constructor) {
        return (TypeElement) constructor.getEnclosingElement();
    }

    private MethodSpec generateNewMethod(String methodName, ClassName classToBeReturned) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(PUBLIC)
                .returns(classToBeReturned)
                .addStatement("return new $T()", classToBeReturned)
                .build();
    }

}
