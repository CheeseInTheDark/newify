package io.newify.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

import static io.newify.processor.Casing.toMethodCase;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.PUBLIC;

public class InnerClassConstructor {
    private ExecutableElement constructor;
    private ProcessingEnvironment processingEnvironment;

    public InnerClassConstructor(ExecutableElement constructor, ProcessingEnvironment processingEnvironment) {
        this.constructor = constructor;
        this.processingEnvironment = processingEnvironment;
    }

    public TypeSpec toTypeSpec() {
        String innerClassName = constructor.getEnclosingElement().getEnclosingElement().getSimpleName().toString();
        String constructedClassName = constructor.getEnclosingElement().getSimpleName().toString();

        NewMethod newMethod = new NewMethod(toMethodCase(constructedClassName), ClassName.get((TypeElement) constructor.getEnclosingElement()), parametersFor(constructor));

        return TypeSpec.classBuilder(innerClassName).addModifiers(PUBLIC).addMethod(newMethod.toMethodSpec()).build();
    }

    private List<ParameterSpec> parametersFor(ExecutableElement constructor) {
        return constructor.getParameters().stream().map(ParameterSpec::get).collect(toList());
    }

    public FieldSpec toFieldSpec() {
        String className = constructor.getEnclosingElement().getSimpleName().toString();
        TypeSpec thisInnerClass = toTypeSpec();
        TypeElement topLevelClass = (TypeElement) constructor.getEnclosingElement().getEnclosingElement();
        String topLevelClassName = topLevelClass.getSimpleName().toString();

        ClassName fieldType = ClassName.get("io.newify.generated", "New", topLevelClass.getSimpleName().toString());

        return FieldSpec.builder(fieldType, toMethodCase(topLevelClassName), PUBLIC).build();
    }

    public static class Factory {
        private ProcessingEnvironment processingEnvironment;

        @Inject
        public Factory(ProcessingEnvironment processingEnvironment) {
            this.processingEnvironment = processingEnvironment;
        }

        public InnerClassConstructor create(ExecutableElement constructor) {
            return new InnerClassConstructor(constructor, processingEnvironment);
        }
    }
}
