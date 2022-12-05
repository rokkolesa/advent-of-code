import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.function.Predicate

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