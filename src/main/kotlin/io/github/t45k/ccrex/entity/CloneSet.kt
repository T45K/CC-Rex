package io.github.t45k.ccrex.entity

data class CloneSet(val hashValue: String, val codeBlocks: List<CodeBlock>)

infix fun String.to(codeBlocks: List<CodeBlock>): CloneSet = CloneSet(this, codeBlocks)
