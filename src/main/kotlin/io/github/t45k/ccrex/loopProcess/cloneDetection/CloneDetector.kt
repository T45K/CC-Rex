package io.github.t45k.ccrex.loopProcess.cloneDetection

import io.github.t45k.ccrex.entity.CloneSet
import io.github.t45k.ccrex.entity.to
import io.github.t45k.ccrex.loopProcess.ASTConstructor
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.nio.file.Path

class CloneDetector {
    fun detect(filePaths: List<Path>): Observable<CloneSet> =
            ASTConstructor().construct(filePaths, emptyList())
                    .flatMap { extractCodeBlocks(it).observeOn(Schedulers.computation()) }
                    .groupBy { StatementNormalizer().normalize(it.ast) }
                    .map { it.key!! to it.toList().blockingGet() }
                    .filter { it.codeBlocks.size >= 2 }
}
