package io.newify.processor;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Provider;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class TheRealProcessor_Factory implements Factory<TheRealProcessor> {
  private final Provider<ProcessingEnvironment> processingEnvironmentProvider;

  private final Provider<InnerClassConstructor.Factory> innerClassConstructorFactoryProvider;

  public TheRealProcessor_Factory(Provider<ProcessingEnvironment> processingEnvironmentProvider,
      Provider<InnerClassConstructor.Factory> innerClassConstructorFactoryProvider) {
    this.processingEnvironmentProvider = processingEnvironmentProvider;
    this.innerClassConstructorFactoryProvider = innerClassConstructorFactoryProvider;
  }

  @Override
  public TheRealProcessor get() {
    return newInstance(processingEnvironmentProvider.get(), innerClassConstructorFactoryProvider.get());
  }

  public static TheRealProcessor_Factory create(
      Provider<ProcessingEnvironment> processingEnvironmentProvider,
      Provider<InnerClassConstructor.Factory> innerClassConstructorFactoryProvider) {
    return new TheRealProcessor_Factory(processingEnvironmentProvider, innerClassConstructorFactoryProvider);
  }

  public static TheRealProcessor newInstance(ProcessingEnvironment processingEnvironment,
      InnerClassConstructor.Factory innerClassConstructorFactory) {
    return new TheRealProcessor(processingEnvironment, innerClassConstructorFactory);
  }
}
