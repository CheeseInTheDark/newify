package io.newify.processor;

import io.newify.dagger.DaggerNewifyComponent;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("io.newify.annotation.New")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NewifyProcessor extends AbstractProcessor {

    private TheRealProcessor processor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        processor = DaggerNewifyComponent.builder().processingEnvironment(processingEnv).build().theRealProcess();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return processor.process(annotations, roundEnv);
    }
}
