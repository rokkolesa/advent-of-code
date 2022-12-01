fun main() {
    fun readElves(input: List<String>): ArrayList<Int> {
        val elves = ArrayList<Int>()
        var elf = 0
        input.forEach {
            if (it.isBlank()) {
                elves.add(elf);
                elf = 0
            } else {
                elf += it.toInt()
            }
        }
        elves.add(elf)
        return elves
    }

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

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
