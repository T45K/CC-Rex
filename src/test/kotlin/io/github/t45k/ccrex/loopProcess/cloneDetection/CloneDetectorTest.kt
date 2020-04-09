package io.github.t45k.ccrex.loopProcess.cloneDetection

import io.github.t45k.ccrex.entity.CloneSet
import org.eclipse.jdt.core.dom.ForStatement
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CloneDetectorTest {
    @Test
    fun testCloneDetection() {
        val filePaths: List<Path> = Files.walk(Paths.get("./src/test/resources/sample/cloneDetection"))
                .filter { it.toString().endsWith(".java") }
                .toList()
        val result: List<CloneSet> = CloneDetector().detect(filePaths, mutableListOf()).toList().blockingGet()
        assertEquals(1, result.size)
        assertEquals(4, result[0].codeBlocks.size)
        assertTrue(result[0].codeBlocks[0].ast is ForStatement)
    }
}
