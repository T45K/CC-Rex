package io.github.t45k.ccrex.loopProcess.cloneDetection

import io.github.t45k.ccrex.entity.CodeBlock
import io.github.t45k.ccrex.entity.FileAST
import io.reactivex.Observable
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.Statement
import java.nio.file.Path

class CodeBlockExtractor {
    fun extract(fileAST: FileAST): Observable<CodeBlock> {
        return Observable.empty()
    }
}

private class CodeBlockExtractVisitor(fileAST: FileAST) : ASTVisitor() {
    private val codeBlocks = mutableListOf<CodeBlock>()
    private val filePath: Path = fileAST.filePath
    private val compilationUnit: CompilationUnit = fileAST.ast

    private fun addStatementIfNeeded(node: Statement) {
        codeBlocks.add(CodeBlock(node, filePath, node.startPosition))
    }

    override fun visit(node: Block?): Boolean {
        if (node?.statements()?.isEmpty() != false) {
            return false
        }

        addStatementIfNeeded(node)
        return super.visit(node)
    }
}
