package io.newify.dagger;

import dagger.BindsInstance;
import dagger.Component;
import io.newify.processor.TheRealProcessor;

import javax.annotation.processing.ProcessingEnvironment;

@Component(modules = NewifyModule.class)
public interface NewifyComponent {
    TheRealProcessor theRealProcess();

    @Component.Builder
    interface Builder {
        @BindsInstance Builder processingEnvironment(ProcessingEnvironment processingEnvironment);

        NewifyComponent build();
    }
}
