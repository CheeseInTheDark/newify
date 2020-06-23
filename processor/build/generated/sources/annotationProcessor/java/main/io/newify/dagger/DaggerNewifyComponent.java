package io.newify.dagger;

import dagger.internal.Preconditions;
import io.newify.processor.InnerClassConstructor;
import io.newify.processor.TheRealProcessor;
import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerNewifyComponent implements NewifyComponent {
  private final ProcessingEnvironment processingEnvironment;

  private DaggerNewifyComponent(ProcessingEnvironment processingEnvironmentParam) {
    this.processingEnvironment = processingEnvironmentParam;
  }

  public static NewifyComponent.Builder builder() {
    return new Builder();
  }

  private InnerClassConstructor.Factory getInnerClassConstructorFactory() {
    return new InnerClassConstructor.Factory(processingEnvironment);}

  @Override
  public TheRealProcessor theRealProcess() {
    return new TheRealProcessor(processingEnvironment, getInnerClassConstructorFactory());}

  private static final class Builder implements NewifyComponent.Builder {
    private ProcessingEnvironment processingEnvironment;

    @Override
    public Builder processingEnvironment(ProcessingEnvironment processingEnvironment) {
      this.processingEnvironment = Preconditions.checkNotNull(processingEnvironment);
      return this;
    }

    @Override
    public NewifyComponent build() {
      Preconditions.checkBuilderRequirement(processingEnvironment, ProcessingEnvironment.class);
      return new DaggerNewifyComponent(processingEnvironment);
    }
  }
}
