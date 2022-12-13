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
    check(part1(testInput), 157, part = "Part 1")
    check(part2(testInput), 70, part = "Part 2")

    val input = readInput("Day03")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
