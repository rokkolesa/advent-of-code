package day25

import utils.check
import utils.readInput
import kotlin.math.pow

fun main() {
    fun Long.toSnafu() = buildString {
        val numberString = toString()
        var referenceValue = this@toSnafu
        var n = numberString.lastIndex
        while (referenceValue > 0) {
            val positionPow = 5.toDouble().pow(n - numberString.lastIndex).toLong()
            var positionValue = referenceValue.mod(positionPow * 5) / positionPow
            if (positionValue > 2) {
                positionValue -= 5
            }

            referenceValue -= positionPow * positionValue
            append(
                when (positionValue) {
                    -2L -> "="
                    -1L -> "-"
                    else -> positionValue.toString()
                }
            )

            n++
        }
    }.reversed()

    fun String.toDecimal(): Long {
        var referenceNumber = 0L

        reversed().forEachIndexed { index, positionValueChar ->
            val positionValue = when (positionValueChar) {
                '=' -> -2
                '-' -> -1
                else -> positionValueChar.digitToInt()
            }
            referenceNumber += positionValue * (5.toDouble().pow(index)).toLong()
        }
        return referenceNumber
    }


    fun part1(input: List<String>): String = input.sumOf { it.toDecimal() }.toSnafu()

    check("Check", "1") { 1L.toSnafu() }
    check("Check", "2") { 2L.toSnafu() }
    check("Check", "1=") { 3L.toSnafu() }
    check("Check", "1-") { 4L.toSnafu() }
    check("Check", "10") { 5L.toSnafu() }
    check("Check", "11") { 6L.toSnafu() }
    check("Check", "12") { 7L.toSnafu() }
    check("Check", "2=") { 8L.toSnafu() }
    check("Check", "2-") { 9L.toSnafu() }
    check("Check", "20") { 10L.toSnafu() }
    check("Check", "1=0") { 15L.toSnafu() }
    check("Check", "1-0") { 20L.toSnafu() }
    check("Check", "1=11-2") { 2022L.toSnafu() }
    check("Check", "1-0---0") { 12345L.toSnafu() }
    check("Check", "1121-1110-1=0") { 314159265L.toSnafu() }
    check("Check", 1L) { "1".toDecimal() }
    check("Check", 2L) { "2".toDecimal() }
    check("Check", 3L) { "1=".toDecimal() }
    check("Check", 4L) { "1-".toDecimal() }
    check("Check", 5L) { "10".toDecimal() }
    check("Check", 6L) { "11".toDecimal() }
    check("Check", 7L) { "12".toDecimal() }
    check("Check", 8L) { "2=".toDecimal() }
    check("Check", 9L) { "2-".toDecimal() }
    check("Check", 10L) { "20".toDecimal() }
    check("Check", 15L) { "1=0".toDecimal() }
    check("Check", 20L) { "1-0".toDecimal() }
    check("Check", 2022L) { "1=11-2".toDecimal() }
    check("Check", 12345L) { "1-0---0".toDecimal() }
    check("Check", 314159265L) { "1121-1110-1=0".toDecimal() }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check("Part 1", "2=-1=0") { part1(testInput) }

    val input = readInput("Day25")
    check("Part 1", "2=2-1-010==-0-1-=--2") { part1(input) }
}
