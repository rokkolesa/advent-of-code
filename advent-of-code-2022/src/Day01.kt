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
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
