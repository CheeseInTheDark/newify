package io.newify.processor;

public class Casing {
    public static String toMethodCase(String input) {
        return input.substring(0, 1).toLowerCase()  + input.substring(1);
    }

    public static String qualifiedNameToMethodName(String qualifiedName) {
        String[] methodParts = qualifiedName.split("\\.");
        int last = methodParts.length - 1;
        methodParts[last] = toMethodCase(methodParts[last]);
        return String.join("_", methodParts);
    }
}
