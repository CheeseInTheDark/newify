package io.newify.processor;

import io.newify.annotation.New;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("io.newify.annotation.New")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NewifyProcessor extends AbstractProcessor {

    private boolean processed = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed) {
            return false;
        }

        processed = true;

        Collection<? extends Element> elements = roundEnv.getElementsAnnotatedWith(New.class);
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(elements);

        StringBuilder builder = new StringBuilder();

        builder.append("package io.newify.generated;\n")
                .append("public class New {\n");

        constructors.forEach(constructor -> addConstructor(constructor, builder));

        builder.append("}\n");

        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile("io.newify.generated.New");
            Writer writer = javaFileObject.openWriter();
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    private void addConstructor(ExecutableElement constructor, StringBuilder builder) {
        TypeElement enclosingClass = (TypeElement) constructor.getEnclosingElement();
        String qualifiedClassName = enclosingClass.getQualifiedName().toString();
        String simpleClassName = enclosingClass.getSimpleName().toString();
        String lowerCaseClassName = simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);

        builder.append("public ").append(qualifiedClassName).append(" ").append(lowerCaseClassName).append("() {\n")
                .append("return new ").append(qualifiedClassName).append("();\n")
                .append("}\n");

    }
}
