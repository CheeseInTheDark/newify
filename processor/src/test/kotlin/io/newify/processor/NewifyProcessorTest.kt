package io.newify.processor;

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.IOException
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.tools.Diagnostic.Kind.ERROR

class NewifyProcessorTest {

    private lateinit var subject: NewifyProcessor
    private val processingEnvironment = mock<ProcessingEnvironment>()
    private val roundEnvironment = mock<RoundEnvironment>()
    private val generatedNewClass = mock<TypeSpec>()
    private val newClassBuilder = mock<NewClassBuilder>()
    private val javaFileBuilder = mock<JavaFile.Builder>()
    private val filer = mock<Filer>()
    private val newClassJavaFile = mock<JavaFile>()
    private val ioException = mock<IOException>()
    private val messager = mock<Messager>()

    @Before
    fun setup() {
        mockkStatic(JavaFile::class)

        every { processingEnvironment.filer } returns filer
        every { processingEnvironment.messager } returns messager
        every { newClassBuilder.buildFrom(roundEnvironment) } returns generatedNewClass
        every { JavaFile.builder("io.newify.generated", generatedNewClass) } returns javaFileBuilder
        every { javaFileBuilder.build() } returns newClassJavaFile

        subject = NewifyProcessor(processingEnvironment, newClassBuilder)
    }

    @Test
    fun writesTheNewJavaFile() {
        subject.process(mutableSetOf(), roundEnvironment)

        verify { newClassJavaFile.writeTo(filer) }
    }

    @Test
    fun logsAnErrorIfTheFileCouldNotBeWritten() {
        val errorMessage = "there was a really bad error, man"
        every { ioException.message } returns errorMessage
        every { newClassJavaFile.writeTo(any<Filer>()) } throws ioException

        subject.process(mutableSetOf(), roundEnvironment)

        verify { messager.printMessage(ERROR, match { it.contains(errorMessage) } )}
    }

    @Test
    fun doesNotProcessAnnotationsTwice() {
        subject.process(mutableSetOf(), roundEnvironment)
        subject.process(mutableSetOf(), roundEnvironment)

        verify(exactly = 1) { newClassJavaFile.writeTo(filer) }
    }
}
