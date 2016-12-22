package xyz.nulldev.stubit

import junit.framework.TestCase
import java.io.File

/**
 * Created by nulldev on 12/21/16.
 */
class StubItTest : TestCase() {
    fun testMain() {
        val testFolder = File("src/test/tmp")
        testFolder.deleteRecursively()

        val testFile = File("src/test/res/Library.java")
        val expectedOutput = File("src/test/res/Library.stub.java")
        val tmpFile = File("src/test/tmp/Library.java")

        val expectedOutputString = expectedOutput.readText()

        val output1 = File("src/test/tmp/Library.stub.java")

        val output2 = File("src/test/tmp/Library.someext")

        testFile.copyTo(tmpFile)

        StubIt.main(arrayOf(tmpFile.absolutePath))

        assertEquals(expectedOutputString, output1.readText())

        StubIt.main(arrayOf(tmpFile.absolutePath, "--extension", "someext"))

        assertEquals(expectedOutputString, output2.readText())
    }
}