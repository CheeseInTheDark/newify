package io.newify.dagger;

import dagger.BindsInstance;
import dagger.Component;
import io.newify.processor.NewifyProcessor;

import javax.annotation.processing.ProcessingEnvironment;

@Component()
public interface NewifyComponent {
    NewifyProcessor theRealProcessor();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder processingEnvironment(ProcessingEnvironment processingEnvironment);

        NewifyComponent build();
    }
}
