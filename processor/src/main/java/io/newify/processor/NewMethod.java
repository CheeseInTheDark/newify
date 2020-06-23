package io.newify.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;

public class NewMethod {
    private final String methodName;
    private final ClassName classToBeReturned;
    private final List<ParameterSpec> parameters;

    public NewMethod(String methodName, ClassName classToBeReturned, List<ParameterSpec> parameters) {
        this.methodName = methodName;
        this.classToBeReturned = classToBeReturned;
        this.parameters = parameters;
    }

    public MethodSpec toMethodSpec() {
        String parametersList = parameters.stream().map(parameter -> parameter.name).collect(joining(", "));

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(PUBLIC)
                .addParameters(parameters)
                .returns(classToBeReturned)
                .addStatement("return new $T(" + parametersList + ")", classToBeReturned)
                .build();
    }
}
