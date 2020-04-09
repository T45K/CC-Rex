package io.github.t45k.ccrex.loopProcess.cloneDetection

import com.google.common.hash.Hashing
import io.github.t45k.ccrex.util.getNodeInCopiedStatement
import io.github.t45k.ccrex.util.replaceNode
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.CharacterLiteral
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.FieldAccess
import org.eclipse.jdt.core.dom.IBinding
import org.eclipse.jdt.core.dom.IVariableBinding
import org.eclipse.jdt.core.dom.Name
import org.eclipse.jdt.core.dom.NumberLiteral
import org.eclipse.jdt.core.dom.QualifiedName
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.StringLiteral
import org.eclipse.jdt.core.dom.SwitchCase
import org.eclipse.jdt.core.dom.TypeLiteral


class StatementNormalizer {
    @Suppress("UnstableApiUsage")
    fun normalize(statement: Statement): String {
        val visitor = StatementNormalizeVisitor(statement)
        statement.accept(visitor)
        return Hashing.sha256().newHasher().putString(visitor.copiedStatement.toString(), Charsets.UTF_8).hash().toString()
    }
}

private class StatementNormalizeVisitor(private val statement: Statement) : ASTVisitor() {
    val copiedStatement: Statement = ASTNode.copySubtree(statement.ast, statement) as Statement
    private val variableBindings = mutableListOf<IBinding>()

    private fun createSpecialCharacter(node: ASTNode, binding: IBinding): SimpleName {
        val index: Int = variableBindings.indexOf(binding)
        return if (index >= 0) {
            node.ast.newSimpleName("$$index")
        } else {
            variableBindings.add(binding)
            node.ast.newSimpleName("$${variableBindings.size - 1}")
        }
    }

    private fun Name.canResolveBinding(): Boolean =
            this.resolveBinding() != null && this.resolveTypeBinding() != null

    private fun visitName(name: Name) {
        if (name.canResolveBinding() && name.resolveBinding().kind == IBinding.VARIABLE) {
            replaceNode(getNodeInCopiedStatement(name, statement, copiedStatement), createSpecialCharacter(name, name.resolveBinding()))
        }
    }

    override fun visit(node: SimpleName): Boolean {
        visitName(node)
        return false
    }

    override fun visit(node: QualifiedName): Boolean {
        visitName(node)
        return false
    }

    private fun FieldAccess.canResolveBinding(): Boolean =
            this.resolveFieldBinding() != null && this.resolveTypeBinding() != null

    override fun visit(node: FieldAccess): Boolean {
        if (!node.canResolveBinding()) {
            return false
        }

        val binding: IVariableBinding = node.resolveFieldBinding()
        replaceNode(getNodeInCopiedStatement(node, statement, copiedStatement), createSpecialCharacter(node, binding))
        return false
    }

    override fun visit(node: SwitchCase): Boolean {
        return false
    }

    override fun visit(node: NumberLiteral): Boolean {
        visitLiteral(node)
        return false
    }

    override fun visit(node: StringLiteral): Boolean {
        visitLiteral(node)
        return false
    }

    override fun visit(node: CharacterLiteral): Boolean {
        visitLiteral(node)
        return false
    }

    override fun visit(node: TypeLiteral): Boolean {
        visitLiteral(node)
        return false
    }

    override fun visit(node: BooleanLiteral): Boolean {
        visitLiteral(node)
        return false
    }

    private fun visitLiteral(node: Expression) {
        val simpleName: SimpleName = node.ast.newSimpleName("$")
        replaceNode(getNodeInCopiedStatement(node, statement, copiedStatement), simpleName)
    }
}
