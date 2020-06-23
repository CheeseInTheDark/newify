package test;

import io.newify.annotation.New;

public class InnerClassConstructor {

    public static class Inner {
        @New
        public Inner() {}
    }
}