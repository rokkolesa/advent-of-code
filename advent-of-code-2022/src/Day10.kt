package day10

import utils.*

fun main() {
    fun runProgram(input: List<String>, cycleAction: (Int, Int) -> Unit) {
        var x = 1
        var cycle = 0

        fun completeCycle() {
            cycle++
            cycleAction(cycle, x)
        }

        input.forEach {
            completeCycle()

            val splitLine = it.split(" ")
            if (splitLine[0] == "addx") {
                completeCycle()
                x += splitLine[1].toInt()
            }
        }
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        runProgram(input) { cycle, x ->
            if (cycle % 40 == 20) {
                sum += cycle * x
            }
        }
        return sum
    }

    fun part2(input: List<String>) = runProgram(input) { cycle, x ->
        val pixel = (cycle - 1) % 40
        if (pixel in x - 1..x + 1) print("#") else print(".")
        if (cycle % 40 == 0) println()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check("Part 1", 13140) { part1(testInput) }
    check("Part 2", null) { part2(testInput) }

    val input = readInput("Day10")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
