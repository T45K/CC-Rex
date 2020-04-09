@file:Suppress("UNCHECKED_CAST")

package io.github.t45k.ccrex.util

import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor
import java.util.ArrayDeque
import java.util.Deque

fun replaceNode(oldNode: ASTNode, newNode: ASTNode?) {
    val locationInParent: StructuralPropertyDescriptor = oldNode.locationInParent
    val copiedNode: ASTNode = ASTNode.copySubtree(oldNode.ast, newNode)
    when {
        locationInParent.isChildListProperty -> {
            val siblings: MutableList<ASTNode> = oldNode.parent.getStructuralProperty(locationInParent) as MutableList<ASTNode>
            val replaceIndex: Int = siblings.indexOf(oldNode)
            siblings[replaceIndex] = copiedNode
        }

        locationInParent.isChildProperty -> {
            oldNode.parent.setStructuralProperty(locationInParent, copiedNode)
        }

        else -> {
            throw RuntimeException("can't replace node")
        }
    }
}

fun getNodeInCopiedStatement(node: ASTNode, statement: Statement, copiedStatement: Statement): ASTNode {
    val stack: Deque<StructuralPropertyDescriptor> = ArrayDeque()
    val indexStack: Deque<Int> = ArrayDeque()
    var tmpNode: ASTNode = node
    while (tmpNode !== statement) {
        val locationInParent: StructuralPropertyDescriptor = tmpNode.locationInParent
        stack.push(locationInParent)
        if (locationInParent.isChildListProperty) {
            val structuralProperty: List<ASTNode> = tmpNode.parent.getStructuralProperty(locationInParent) as List<ASTNode>
            indexStack.push(structuralProperty.indexOf(tmpNode))
        }
        tmpNode = tmpNode.parent
    }

    var target: ASTNode = copiedStatement
    while (stack.size > 0) {
        val locationInParent: StructuralPropertyDescriptor = stack.pop()
        target = when {
            locationInParent.isChildListProperty -> {
                val index: Int = indexStack.poll() ?: throw NullPointerException()
                val structuralProperty: List<ASTNode> = target.getStructuralProperty(locationInParent) as List<ASTNode>
                structuralProperty[index]
            }

            locationInParent.isChildProperty -> {
                target.getStructuralProperty(locationInParent) as ASTNode
            }

            else -> {
                throw RuntimeException("can't get node")
            }
        }
    }
    return target
}
