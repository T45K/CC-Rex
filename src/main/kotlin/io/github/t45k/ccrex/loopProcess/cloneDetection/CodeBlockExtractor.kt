package io.github.t45k.ccrex.loopProcess.cloneDetection

import io.github.t45k.ccrex.entity.CodeBlock
import io.github.t45k.ccrex.entity.FileAST
import io.reactivex.Observable
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.EnhancedForStatement
import org.eclipse.jdt.core.dom.ForStatement
import org.eclipse.jdt.core.dom.IfStatement
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.SwitchStatement
import org.eclipse.jdt.core.dom.SynchronizedStatement
import org.eclipse.jdt.core.dom.TryStatement
import org.eclipse.jdt.core.dom.WhileStatement
import java.nio.file.Path

fun extractCodeBlocks(fileAST: FileAST): Observable<CodeBlock> =
        Observable.just(fileAST)
                .flatMap {
                    val visitor = CodeBlockExtractVisitor(it)
                    fileAST.ast.accept(visitor)
                    Observable.fromIterable(visitor.codeBlocks)
                }

private class CodeBlockExtractVisitor(fileAST: FileAST) : ASTVisitor() {
    companion object {
        private const val LINE_THRESHOLD = 3
    }

    val codeBlocks = mutableListOf<CodeBlock>()
    private val filePath: Path = fileAST.filePath
    private val compilationUnit: CompilationUnit = fileAST.ast

    override fun visit(node: Block): Boolean {
        if (node.statements()?.isEmpty() != false) {
            return false
        }

        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: EnhancedForStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: ForStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: IfStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: SwitchStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: SynchronizedStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: TryStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    override fun visit(node: WhileStatement): Boolean {
        codeBlocks.addIfNeeded(node)
        return super.visit(node)
    }

    private fun MutableList<CodeBlock>.addIfNeeded(node: Statement) {
        if (node.isLessThanLineThreshold()) {
            this.add(CodeBlock(node, filePath, node.startPosition))
        }
    }

    private fun Statement.isLessThanLineThreshold(): Boolean {
        val startLine: Int = compilationUnit.getLineNumber(this.startPosition)
        val endLine: Int = compilationUnit.getLineNumber(this.startPosition + this.length - 1)
        return if (this is Block) {
            endLine - startLine - 1 <= LINE_THRESHOLD
        } else {
            endLine - startLine + 1 <= LINE_THRESHOLD
        }
    }
}
