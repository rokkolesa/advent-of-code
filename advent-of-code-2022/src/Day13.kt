package day13

import utils.*

sealed interface PacketElement : Comparable<PacketElement>
data class PacketValue(val intValue: Int) : PacketElement {

    override fun compareTo(other: PacketElement): Int = when (other) {
        is PacketValue -> intValue.compareTo(other.intValue)
        is PacketList -> PacketList(mutableListOf(this)).compareTo(other)
    }

    override fun toString(): String = intValue.toString()
}

data class PacketList(val elements: List<PacketElement> = mutableListOf()) : PacketElement {

    override fun compareTo(other: PacketElement): Int = when (other) {
        is PacketValue -> compareTo(PacketList(mutableListOf(other)))
        is PacketList ->
            (elements zip other.elements)
                .asSequence()
                .map { (left, right) -> left.compareTo(right) }
                .firstOrNull { it != 0 }
                ?: elements.size.compareTo(other.elements.size)
    }

    override fun toString(): String = elements.toString()
}

fun String.tokenize(delimiter: Char = ','): List<String> {
    val tokens = mutableListOf<String>()
    var bracketCount = 0
    var tokenStart = 0
    for (i in indices) {
        bracketCount += when (this[i]) {
            '[' -> 1
            ']' -> -1
            else -> 0
        }
        if (bracketCount <= 0 && this[i] == delimiter) {
            tokens.add(substring(tokenStart until i))
            tokenStart = i + 1
        }
    }
    tokens.add(substring(tokenStart until length))
    return tokens
}

fun main() {
    fun parsePacket(input: String): PacketElement = when {
        input.isEmpty() || input == "[]" -> PacketList()
        !input.contains("[") -> PacketValue(input.toInt())
        else -> PacketList(input.substring(1 until input.lastIndex).tokenize().map(::parsePacket))
    }

    fun part1(input: List<String>): Int = input
        .split(String::isBlank)
        .asSequence()
        .map { it.map(::parsePacket) }
        .withIndex()
        .filter { (_, p) -> p.first() < p.last() }
        .sumOf { (idx, _) -> idx + 1 }

    fun part2(input: List<String>): Int = input
        .asSequence()
        .filter(String::isNotBlank)
        .map(::parsePacket)
        .let { Triple(it, parsePacket("[[2]]"), parsePacket("[[6]]")) }
        .let { (packets, divider2, divider6) ->
            (packets.count { it < divider2 } + 1) * (packets.count { it < divider6 } + 2)
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check("Part 1", 13) { part1(testInput) }
    check("Part 2", 140) { part2(testInput) }

    val input = readInput("Day13")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
