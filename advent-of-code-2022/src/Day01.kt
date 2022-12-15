package day01

import utils.*

fun main() {
    fun readElves(input: List<String>): List<Int> =
        input.split(String::isEmpty)
            .map { it.sumOf(String::toInt) }

    fun part1(input: List<String>): Int {
        return readElves(input)
            .max()
    }

    fun part2(input: List<String>): Int {
        return readElves(input)
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check("Part 1", 24000) { part1(testInput) }
    check("Part 2", 45000) { part2(testInput) }

    val input = readInput("Day01")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
