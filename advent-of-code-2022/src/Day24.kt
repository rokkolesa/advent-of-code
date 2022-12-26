package day24

import utils.check
import utils.readInput

data class P(val x: Int, val y: Int) {
    operator fun plus(other: P): P = P((x + other.x), (y + other.y))
    fun mod(boundX: Int, boundY: Int): P = P(x.mod(boundX), y.mod(boundY))
}

enum class Direction(val vector: P) {
    RIGHT(P(1, 0)), DOWN(P(0, 1)), LEFT(P(-1, 0)), UP(P(0, -1)), WAIT(P(0, 0))
}

typealias Blizzards = Map<P, MutableSet<Direction>>

data class State(val position: P, val walkLength: Int) {
    operator fun plus(direction: Direction): State = State(position + direction.vector, walkLength + 1)
}

data class Valley(var start: P, var target: P, var blizzards: Blizzards, val boundX: Int, val boundY: Int) {
    private fun moveBlizzards() {
        blizzards = buildMap {
            blizzards.forEach { (blizzard, directions) ->
                directions.forEach { direction ->
                    getOrPut((blizzard + direction.vector).mod(boundX, boundY), ::mutableSetOf) += direction
                }
            }
        }
    }

    fun walk(): Int {
        val cache = mutableSetOf<State>()
        val queue = ArrayDeque<State>()
        queue.add(State(start, 0))

        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            val (position, walkLength) = state

            if (position == target) {
                swapEndpoints()
                return walkLength
            }
            if (queue.isEmpty() || queue.first().walkLength != walkLength) {
                cache.clear()
                moveBlizzards()
            }

            for (direction in Direction.values()) {
                val nextState = state + direction
                val proposedPosition = nextState.position

                if (nextState in cache || proposedPosition in blizzards) {
                    continue
                }

                if (direction == Direction.WAIT
                    || proposedPosition.x in 0 until boundX && proposedPosition.y in 0 until boundY
                    || proposedPosition == target
                ) {
                    cache += nextState
                    queue.add(nextState)
                }
            }
        }
        throw IllegalStateException("No walk exists!")
    }

    private fun swapEndpoints() {
        val tmp = start
        start = target
        target = tmp
    }
}

fun main() {
    fun parseInput(input: List<String>): Valley {
        val start = P(input.first().indexOfFirst { it == '.' } - 1, -1)
        val target = P(input.last().indexOfFirst { it == '.' } - 1, input.lastIndex - 1)
        val boundX = input.first().length - 2
        val boundY = input.size - 2
        val blizzards: Blizzards = buildMap {
            for (y in 1 until input.lastIndex) {
                for (x in 1 until input[y].lastIndex) {
                    when (input[y][x]) {
                        '>' -> getOrPut(P(x - 1, y - 1), ::mutableSetOf) += Direction.RIGHT
                        'v' -> getOrPut(P(x - 1, y - 1), ::mutableSetOf) += Direction.DOWN
                        '<' -> getOrPut(P(x - 1, y - 1), ::mutableSetOf) += Direction.LEFT
                        '^' -> getOrPut(P(x - 1, y - 1), ::mutableSetOf) += Direction.UP
                        '.' -> continue
                        else -> throw IllegalStateException("Illegal line definition!")
                    }
                }
            }
        }
        return Valley(start, target, blizzards, boundX, boundY)
    }

    fun part1(input: List<String>): Int = parseInput(input).walk() - 1
    fun part2(input: List<String>): Int = parseInput(input).let { valley -> (1..3).sumOf { valley.walk() } - 1 }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    check("Part 1", 18) { part1(testInput) }
    check("Part 2", 54) { part2(testInput) }

    val input = readInput("Day24")
    check("Part 1", 305) { part1(input) }
    check("Part 2", 905) { part2(input) }
}
