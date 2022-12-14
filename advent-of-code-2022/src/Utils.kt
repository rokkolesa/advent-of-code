package utils

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.function.Predicate
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun List<String>.split(predicate: Predicate<String>): List<List<String>> {
    val split = mutableListOf<List<String>>()
    var part = mutableListOf<String>()
    split.add(part)
    forEach {
        if (predicate.test(it)) {
            part = mutableListOf()
            split.add(part)
        } else {
            part.add(it)
        }
    }

    return split
}

@OptIn(ExperimentalTime::class)
fun <T> check(
    part: String,
    expected: T,
    compare: (T, T) -> Boolean = { x, y -> x == y },
    message: ((T, T) -> Any)? = null,
    retrieveValue: () -> T
) {
    val (actual, duration) = measureTimedValue {
        val actual = retrieveValue()
        if (actual !is Unit && !compare(actual, expected)) {
            val errorMessage = message?.invoke(actual, expected) ?: "Actual:   $actual\nExpected: $expected"
            throw IllegalStateException("\n$part: Check failed!\n$errorMessage")
        }
        actual
    }
    println("$part: $actual (OK) | Elapsed time: $duration")
}

@OptIn(ExperimentalTime::class)
fun <T> simulate(part: String, retrieveValue: () -> T) {
    val (value, duration) = measureTimedValue(retrieveValue)
    println("$part: $value | Elapsed time: $duration")
}
