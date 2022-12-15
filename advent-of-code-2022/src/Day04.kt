package day04

import utils.*

fun main() {
    infix fun IntRange.contains(other: IntRange): Boolean =
        this.contains(other.first) && this.contains(other.last)

    infix fun IntRange.intersects(other: IntRange): Boolean =
        this.contains(other.first) || this.contains(other.last)

    val inputLineRegex = """(\d+)-(\d+),(\d+)-(\d+)""".toRegex()

    fun parseInput(line: String): List<IntRange> {
        val (firstFrom, firstTo, secondFrom, secondTo) = inputLineRegex
            .matchEntire(line)
            ?.destructured
            ?.toList()
            ?.map(String::toInt)
            ?: throw IllegalArgumentException("Incorrect input line $line")
        return listOf(firstFrom..firstTo, secondFrom..secondTo)
    }

    fun part1(input: List<String>): Int = input
        .map(::parseInput)
        .count { (first, second) -> first contains second || second contains first }

    fun part2(input: List<String>): Int = input
        .map(::parseInput)
        .count { (first, second) -> first intersects second || second intersects first }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check("Part 1", 2) { part1(testInput) }
    check("Part 2", 4) { part2(testInput) }

    val input = readInput("Day04")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
