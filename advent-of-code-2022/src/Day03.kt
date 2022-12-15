package day03

import utils.*

fun main() {
    val charMap = buildMap {
        putAll("abcdefghijklmnopqrstuvwxyz".toCharArray().associateWith { it.code - 96 })
        putAll("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray().associateWith { it.code - 64 + 26 })
    }

    fun part1(input: List<String>): Int = input
        .map { listOf(it.substring(0, it.length / 2), it.substring(it.length / 2)) }
        .map { it.map(String::toCharArray) }
        .map { (firstChars, secondChars) -> firstChars.find { it in secondChars } }
        .sumOf { charMap[it]!! }

    fun part2(input: List<String>): Int = input
        .windowed(size = 3, step = 3)
        .map { it.map(String::toCharArray) }
        .map { (firstChars, secondChars, thirdChars) -> firstChars.find { it in secondChars && it in thirdChars }!! }
        .sumOf { charMap[it]!! }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check("Part 1", 157) { part1(testInput) }
    check("Part 2", 70) { part2(testInput) }

    val input = readInput("Day03")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
