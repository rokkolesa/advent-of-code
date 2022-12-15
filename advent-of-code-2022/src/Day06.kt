package day06

import utils.*

fun main() {

    fun distinctChars(input: String, numOfChars: Int): Int =
        input.asSequence()
            .windowed(numOfChars)
            .map(List<Char>::toSet)
            .indexOfFirst { it.size == numOfChars } + numOfChars

    fun part1(input: String): Int = distinctChars(input, 4)
    fun part2(input: String): Int = distinctChars(input, 14)

    // test if implementation meets criteria from the description, like:
    check("Part 1", 7) { part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb") }
    check("Part 1", 5) { part1("bvwbjplbgvbhsrlpgdmjqwftvncz") }
    check("Part 1", 6) { part1("nppdvjthqldpwncqszvftbrmjlhg") }
    check("Part 1", 10) { part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") }
    check("Part 1", 11) { part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") }

    check("Part 2", 19) { part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") }
    check("Part 2", 23) { part2("bvwbjplbgvbhsrlpgdmjqwftvncz") }
    check("Part 2", 23) { part2("nppdvjthqldpwncqszvftbrmjlhg") }
    check("Part 2", 29) { part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") }
    check("Part 2", 26) { part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") }


    val input = readInput("Day06").first()
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
