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

fun main() {
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

    fun parsePacketList(input: String): PacketElement = when {
        input.isEmpty() || input == "[]" -> PacketList()
        !input.contains("[") -> PacketValue(input.toInt())
        else -> PacketList(input.substring(1 until input.lastIndex).tokenize().map(::parsePacketList))
    }

    fun part1(input: List<String>): Int = input
        .split(String::isBlank)
        .asSequence()
        .map { it.map(::parsePacketList) }
        .mapIndexed { idx, (left, right) -> (left < right) to (idx + 1) }
        .sumOf { (result, idx) -> if (result) idx else 0 }

    fun part2(input: List<String>): Int = (input + listOf("", "[[2]]", "[[6]]"))
        .split(String::isBlank)
        .asSequence()
        .flatMap { it.map(::parsePacketList) }
        .sorted()
        .withIndex()
        .filter { (_, packetElement) -> packetElement == parsePacketList("[[2]]") || packetElement == parsePacketList("[[6]]") }
        .map { (idx, _) -> idx + 1 }
        .reduce { acc, idx -> acc * idx }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
