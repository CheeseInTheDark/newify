package io.newify.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.List;

import static io.newify.processor.Casing.toMethodCase;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PUBLIC;

public class InnerClassConstructor {
    private ExecutableElement constructor;

    public InnerClassConstructor(ExecutableElement constructor) {
        this.constructor = constructor;
    }

    public TypeSpec toTypeSpec() {
        String className = toMethodCase(constructor.getEnclosingElement().getSimpleName().toString());
        String innerClassName = toMethodCase(constructor.getEnclosingElement().getEnclosingElement().getSimpleName().toString());

        NewMethod newMethod = new NewMethod(innerClassName, ClassName.get((TypeElement) constructor.getEnclosingElement()), parametersFor(constructor));

        return TypeSpec.classBuilder(className).addModifiers(PUBLIC).addMethod(newMethod.toMethodSpec()).build();
    }

    private List<ParameterSpec> parametersFor(ExecutableElement constructor) {
        return constructor.getParameters().stream().map(ParameterSpec::get).collect(toList());
    }

    public FieldSpec toFieldSpec() {
        String className = toMethodCase(constructor.getEnclosingElement().getSimpleName().toString());
        TypeSpec thisInnerClass = toTypeSpec();
        TypeElement topLevelClass = (TypeElement) constructor.getEnclosingElement().getEnclosingElement();

        return null;
    }
}
