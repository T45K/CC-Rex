package io.github.t45k.ccrex.loopProcess.cloneDetection

import io.github.t45k.ccrex.entity.CodeBlock
import io.github.t45k.ccrex.entity.FileAST
import io.github.t45k.ccrex.loopProcess.ASTConstructor
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.nio.file.Path

class CloneDetector {
    fun detect(filePaths: List<Path>): Observable<List<CodeBlock>> =
            ASTConstructor().construct(filePaths, emptyList())
                    .flatMap { detectFromSingleAST(it).observeOn(Schedulers.computation()) }
                    .groupBy { StatementNormalizer().normalize(it.ast) }
                    .flatMap { it.toList().toObservable() }

    private fun detectFromSingleAST(fileAST: FileAST): Observable<CodeBlock> =
            Observable.just(fileAST)
                    .flatMap { CodeBlockExtractor().extract(it) }
}
