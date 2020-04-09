package io.github.t45k.ccrex.loopProcess.cloneDetection

import io.github.t45k.ccrex.entity.CloneSet
import io.github.t45k.ccrex.entity.to
import io.github.t45k.ccrex.loopProcess.ASTConstructor
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

class CloneDetector {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun detect(filePaths: List<Path>, classpathEntryList: MutableList<Path>): Observable<CloneSet> =
            ASTConstructor().construct(filePaths, classpathEntryList).toObservable()
                    .doOnSubscribe { logger.debug("[Begin]\tclone detection") }
                    .flatMap { CodeBlockExtractor().extract(it).observeOn(Schedulers.computation()) }
                    .groupBy { StatementNormalizer().normalize(it.ast) }
                    .toList().blockingGet().toObservable()
                    .map { it.key!! to it.toList().blockingGet() }
                    .filter { it.codeBlocks.size >= 2 }
                    .doFinally { logger.debug("[End]\tclone detection") }
}
