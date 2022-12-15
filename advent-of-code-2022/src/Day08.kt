package day08

import utils.*
import kotlin.math.max

fun <T> Collection<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    val indexOfFirst = indexOfFirst(predicate)
    return when {
        indexOfFirst < 0 -> null
        else -> indexOfFirst
    }
}

fun <T> Collection<T>.indexOfLastOrNull(predicate: (T) -> Boolean): Int? {
    val indexOfLast = indexOfLast(predicate)
    return when {
        indexOfLast < 0 -> null
        else -> indexOfLast
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var visibleTrees = 0
        input.forEachIndexed { i, row ->

            row.forEachIndexed loopRow@{ j, height ->
                if (i == 0 || i == input.lastIndex || j == 0 || j == row.lastIndex) {
                    visibleTrees++
                    return@loopRow
                }
                val currentHeight = height.digitToInt()

                val leftRow = List(j) {
                    input[i][it].digitToInt()
                }

                val rightRow = List(row.lastIndex - j) {
                    input[i][row.lastIndex - it].digitToInt()
                }

                val topColumn = List(i) {
                    input[it][j].digitToInt()
                }
                val bottomColumn = List(input.lastIndex - i) {
                    input[input.lastIndex - it][j].digitToInt()
                }

                if (
                    leftRow.all { it < currentHeight } ||
                    rightRow.all { it < currentHeight } ||
                    topColumn.all { it < currentHeight } ||
                    bottomColumn.all { it < currentHeight }
                ) {
                    visibleTrees++
                    return@loopRow
                }

            }
        }
        return visibleTrees
    }

    fun part2(input: List<String>): Int {
        var score = 0
        input.forEachIndexed { i, row ->

            row.forEachIndexed loopRow@{ j, height ->
                if (i == 0 || i == input.lastIndex || j == 0 || j == row.lastIndex) {
                    return@loopRow
                }
                val currentHeight = height.digitToInt()

                val leftRow = List(j) {
                    input[i][it].digitToInt()
                }

                val rightRow = List(row.lastIndex - j) {
                    input[i][j + 1 + it].digitToInt()
                }

                val topColumn = List(i) {
                    input[it][j].digitToInt()
                }
                val bottomColumn = List(input.lastIndex - i) {
                    input[i + 1 + it][j].digitToInt()
                }

                val leftScore = leftRow.size - (leftRow.indexOfLastOrNull { it >= currentHeight } ?: 0)
                val rightScore = (rightRow.indexOfFirstOrNull { it >= currentHeight } ?: rightRow.lastIndex) + 1
                val topScore = topColumn.size - (topColumn.indexOfLastOrNull { it >= currentHeight } ?: 0)
                val bottomScore = (bottomColumn.indexOfFirstOrNull { it >= currentHeight }
                    ?: bottomColumn.lastIndex) + 1

                score = max(score, leftScore * rightScore * topScore * bottomScore)
            }
        }
        return score
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput), 21, part = "Part 1")
    check(part2(testInput), 8, part = "Part 2")

    val input = readInput("Day08")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
