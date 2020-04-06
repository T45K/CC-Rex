package io.github.t45k.ccrex.loopProcess

import io.github.t45k.ccrex.entity.FileAST
import io.github.t45k.ccrex.entity.to
import io.reactivex.Observable
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

    fun construct(filePathList: List<Path>, classpathEntryList: List<Path>): Observable<FileAST> =
            Observable.just(filePathList)
                    .doOnSubscribe { logger.info("[Start]\nconstructing\nASTs") }
                    .flatMap { list ->
                        val parser: ASTParser = createParser()

                        val classpathEntries: Array<String> = classpathEntryList.map { it.toString() }.toTypedArray()
                        val sourceFileEntries: Array<String> = list.map { it.parent.toString() }.distinct().toTypedArray()
                        parser.setEnvironment(classpathEntries, sourceFileEntries, null, true)

                        val filesASTs = mutableListOf<FileAST>()
                        val myRequestor = object : FileASTRequestor() {
                            override fun acceptAST(sourceFilePath: String?, ast: CompilationUnit?) {
                                filesASTs.add(Path.of(sourceFilePath)!! to ast!!)
                            }
                        }

                        val sourceFilePaths: Array<String> = list.map { it.toString() }.toTypedArray()
                        parser.createASTs(sourceFilePaths, null, arrayOf(), myRequestor, NullProgressMonitor())
                        Observable.fromIterable(filesASTs)
                    }
                    .doFinally { logger.info("[End]") }

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
