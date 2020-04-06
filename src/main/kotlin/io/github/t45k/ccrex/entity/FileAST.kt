package io.github.t45k.ccrex.entity

import org.eclipse.jdt.core.dom.CompilationUnit
import java.nio.file.Path

data class FileAST(val filePath: Path, val ast: CompilationUnit)

infix fun Path.to(ast: CompilationUnit): FileAST = FileAST(this, ast)
