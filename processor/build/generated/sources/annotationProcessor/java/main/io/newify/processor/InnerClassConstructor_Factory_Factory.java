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
public final class InnerClassConstructor_Factory_Factory implements Factory<InnerClassConstructor.Factory> {
  private final Provider<ProcessingEnvironment> processingEnvironmentProvider;

  public InnerClassConstructor_Factory_Factory(
      Provider<ProcessingEnvironment> processingEnvironmentProvider) {
    this.processingEnvironmentProvider = processingEnvironmentProvider;
  }

  @Override
  public InnerClassConstructor.Factory get() {
    return newInstance(processingEnvironmentProvider.get());
  }

  public static InnerClassConstructor_Factory_Factory create(
      Provider<ProcessingEnvironment> processingEnvironmentProvider) {
    return new InnerClassConstructor_Factory_Factory(processingEnvironmentProvider);
  }

  public static InnerClassConstructor.Factory newInstance(
      ProcessingEnvironment processingEnvironment) {
    return new InnerClassConstructor.Factory(processingEnvironment);
  }
}
