package io.github.t45k.ccrex.entity

import org.eclipse.jdt.core.dom.Statement
import java.nio.file.Path

data class CodeBlock(val ast: Statement, val filePath: Path, val startPosition: Int)
