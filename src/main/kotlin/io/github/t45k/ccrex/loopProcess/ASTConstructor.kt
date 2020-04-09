package io.github.t45k.ccrex.loopProcess

import io.github.t45k.ccrex.entity.FileAST
import io.github.t45k.ccrex.entity.to
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.FileASTRequestor
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants
import org.slf4j.LoggerFactory
import java.nio.file.Path

class ASTConstructor {
    private val logger = LoggerFactory.getLogger(ASTConstructor::class.java)

    fun construct(filePathList: List<Path>, classpathEntryList: List<Path>): List<FileAST> {
        logger.debug("[Begin]\tconstructing ASTs")

        val parser: ASTParser = createParser()

        val classpathEntries: Array<String> = classpathEntryList.map { it.toString() }.toTypedArray()
        val sourceFileEntries: Array<String> = filePathList.map { it.parent.toString() }.distinct().toTypedArray()
        parser.setEnvironment(classpathEntries, sourceFileEntries, null, true)

        val myRequester = MyASTRequester()
        val sourceFilePaths: Array<String> = filePathList.map { it.toString() }.toTypedArray()
        parser.createASTs(sourceFilePaths, null, arrayOf(), myRequester, NullProgressMonitor())

        logger.debug("[End]\tconstructing ASTs")
        return myRequester.filesASTs
    }

    @Suppress("UNCHECKED_CAST")
    private fun createParser(): ASTParser {
        val parser: ASTParser = ASTParser.newParser(AST.JLS13)
        parser.setKind(ASTParser.K_COMPILATION_UNIT)

        val options: MutableMap<String, String> = DefaultCodeFormatterConstants.getEclipseDefaultSettings() as MutableMap<String, String>
        options[JavaCore.COMPILER_COMPLIANCE] = JavaCore.VERSION_10
        options[JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM] = JavaCore.VERSION_10
        options[JavaCore.COMPILER_SOURCE] = JavaCore.VERSION_10

        parser.setCompilerOptions(options)
        parser.setBindingsRecovery(true)
        parser.setResolveBindings(true)

        return parser
    }
}

private class MyASTRequester : FileASTRequestor() {
    val filesASTs = mutableListOf<FileAST>()

    override fun acceptAST(sourceFilePath: String?, ast: CompilationUnit?) {
        filesASTs.add(Path.of(sourceFilePath!!) to ast!!)
    }
}
