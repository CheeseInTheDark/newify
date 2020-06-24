package io.newify.processor;

import com.squareup.javapoet.*;
import io.newify.annotation.New;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.newify.processor.Casing.qualifiedNameToMethodName;
import static io.newify.processor.Casing.toMethodCase;
import static java.util.stream.Collectors.*;
import static javax.lang.model.element.Modifier.PUBLIC;

public class NewClassBuilder {

    private InnerClassConstructor.Factory innerClassConstructorFactory;

    @Inject
    public NewClassBuilder(InnerClassConstructor.Factory innerClassConstructorFactory) {
        this.innerClassConstructorFactory = innerClassConstructorFactory;
    }

    public TypeSpec buildFrom(RoundEnvironment roundEnvironment) {
        Collection<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(New.class);
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(elements);

        List<InnerClassConstructor> innerClassConstructors = toInnerClassConstructors(constructors).collect(toList());

        List<MethodSpec> newMethods = constructors.stream()
                .filter(constructor -> !isInInnerClass(constructor))
                .collect(groupingBy(ExecutableElement::getSimpleName))
                .values().stream()
                .flatMap(this::toNonConflictingMethods)
                .collect(toList());

        List<TypeSpec> newInnerClasses = innerClassConstructors.stream().map(InnerClassConstructor::toTypeSpec).collect(toList());
        List<FieldSpec> newFields = innerClassConstructors.stream().map(InnerClassConstructor::toFieldSpec).collect(toList());

        return TypeSpec.classBuilder("New")
                .addModifiers(PUBLIC)
                .addMethods(newMethods)
                .addTypes(newInnerClasses)
                .addFields(newFields)
                .build();
    }

    private Stream<InnerClassConstructor> toInnerClassConstructors(List<ExecutableElement> allConstructors) {
        return allConstructors.stream().filter(this::isInInnerClass).map(innerClassConstructorFactory::create);
    }

    private boolean isInInnerClass(ExecutableElement constructor) {
        return constructor.getEnclosingElement().getKind() == ElementKind.CLASS &&
                constructor.getEnclosingElement().getEnclosingElement().getKind() == ElementKind.CLASS;
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

        return generateNewMethod(methodName, classToBeReturned, parametersFor(constructorElement));
    }

    private MethodSpec generateQualifiedNewMethod(ExecutableElement constructorElement) {
        TypeElement constructorType = constructorType(constructorElement);
        String methodName = qualifiedNameToMethodName(constructorType.getQualifiedName().toString());
        ClassName classToBeReturned = ClassName.get(constructorType);

        return generateNewMethod(methodName, classToBeReturned, parametersFor(constructorElement));
    }

    private List<ParameterSpec> parametersFor(ExecutableElement constructor) {
        return constructor.getParameters().stream().map(ParameterSpec::get).collect(toList());
    }

    private TypeElement constructorType(ExecutableElement constructor) {
        return (TypeElement) constructor.getEnclosingElement();
    }

    private MethodSpec generateNewMethod(String methodName, ClassName classToBeReturned, List<ParameterSpec> parameters) {
        String parametersList = parameters.stream().map(parameter -> parameter.name).collect(joining(", "));

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(PUBLIC)
                .addParameters(parameters)
                .returns(classToBeReturned)
                .addStatement("return new $T(" + parametersList + ")", classToBeReturned)
                .build();
    }

}
