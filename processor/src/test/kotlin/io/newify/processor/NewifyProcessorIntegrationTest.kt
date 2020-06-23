package io.newify.processor

import com.google.testing.compile.Compilation
import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import org.junit.Before
import org.junit.Test
import javax.tools.JavaFileObject


class NewifyProcessorIntegrationTest {

    private lateinit var subject: NewifyProcessor

    @Before
    fun setUp() {
        subject = NewifyProcessor()
    }

    @Test
    fun newOnEmptyConstructorAddsToNewClass() {
        assertThat(processing("input/test/EmptyConstructor.java"))
                .generatedSourceFile("io.newify.generated.New")
                .hasSourceEquivalentTo(expected("EmptyConstructor.java"))
    }

    @Test
    fun newOnClassesWithSameNameAvoidsNameCollisions() {
        assertThat(processing("input/test/EmptyConstructor.java", "input/test/more/EmptyConstructor.java"))
                .generatedSourceFile("io.newify.generated.New")
                .hasSourceEquivalentTo(expected("NameCollisionAvoidance.java"))
    }

    @Test
    fun parametersAreAddedToNewMethods() {
        assertThat(processing("input/test/ConstructorWithParameters.java"))
                .generatedSourceFile("io.newify.generated.New")
                .hasSourceEquivalentTo(expected("ConstructorWithParameters.java"))
    }

    @Test
    fun addsConstructorsForInnerClasses() {
        assertThat(processing("input/test/InnerClassConstructor.java"))
                .generatedSourceFile("io.newify.generated.New")
                .hasSourceEquivalentTo(expected("InnerClassConstructor.java"))
    }

    fun processing(vararg files: String): Compilation? {
        val resources = files.map(JavaFileObjects::forResource)

        return javac().withProcessors(subject).compile(resources)
    }

    fun expected(file: String): JavaFileObject? {
        return JavaFileObjects.forResource("expected/$file")
    }
}
