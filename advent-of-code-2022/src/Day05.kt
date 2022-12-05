import java.util.TreeMap

data class Move(val amount: Int, val from: Int, val to: Int)

fun <E> ArrayDeque<E>.removeFirst(length: Int): ArrayDeque<E> {
    return (1..length).mapTo(ArrayDeque()) { removeFirst() }
}

fun <E> ArrayDeque<E>.addAllFirst(collection: Iterable<E>, preserveOrder: Boolean = false) {
    (if (preserveOrder) collection.reversed() else collection)
        .forEach(::addFirst)
}

typealias CrateStack = ArrayDeque<String>

fun main() {
    val moveRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

    fun parseStack(line: String): List<String> = line.substring(1).windowed(size = 1, step = 4)

    fun parseMove(line: String): Move {
        val (amount, from, to) = moveRegex
            .matchEntire(line)
            ?.destructured
            ?.toList()
            ?.map { it.toInt() }
            ?: throw IllegalArgumentException("Incorrect input line $line")
        return Move(amount, from - 1, to - 1)
    }

    fun moveCrates(
        input: List<String>,
        preserveOrder: Boolean = false
    ): String {
        val (stackLines, moveLines) = input.split(String::isEmpty)

        val stacks = TreeMap<Int, CrateStack>()
        stackLines
            .dropLast(1)
            .map(::parseStack)
            .forEach {
                it.forEachIndexed { idx, crate ->
                    if (crate.isNotBlank()) stacks.getOrPut(idx, ::CrateStack).addLast(crate)
                }
            }

        moveLines
            .map(::parseMove)
            .forEach { (amount, from, to) ->
                stacks[to]!!.addAllFirst(
                    stacks[from]!!.removeFirst(amount),
                    preserveOrder
                )
            }

        return stacks.values.joinToString(transform = ArrayDeque<String>::first, separator = "")
    }


    fun part1(input: List<String>): String = moveCrates(input)
    fun part2(input: List<String>): String = moveCrates(input, preserveOrder = true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
