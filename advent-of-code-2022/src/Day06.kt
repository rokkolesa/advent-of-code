fun main() {

    fun distinctChars(input: String, numOfChars: Int): Int =
        input.asSequence()
            .windowed(numOfChars)
            .map(List<Char>::toSet)
            .indexOfFirst { it.size == numOfChars } + numOfChars

    fun part1(input: String): Int = distinctChars(input, 4)
    fun part2(input: String): Int = distinctChars(input, 14)

    // test if implementation meets criteria from the description, like:
    check(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb"), 7, part = "Part 1")
    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz"), 5, part = "Part 1")
    check(part1("nppdvjthqldpwncqszvftbrmjlhg"), 6, part = "Part 1")
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"), 10, part = "Part 1")
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"), 11, part = "Part 1")

    check(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb"), 19, part = "Part 2")
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz"), 23, part = "Part 2")
    check(part2("nppdvjthqldpwncqszvftbrmjlhg"), 23, part = "Part 2")
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"), 29, part = "Part 2")
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"), 26, part = "Part 2")


    val input = readInput("Day06").first()
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
