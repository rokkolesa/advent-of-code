data class Move(val amount: Int, val from: Int, val to: Int)

fun <E> ArrayDeque<E>.removeFirst(length: Int): ArrayDeque<E> {
    return (1..length).mapTo(ArrayDeque()) { removeFirst() }
}

fun <E> ArrayDeque<E>.addAllFirst(collection: List<E>, preserveOrder: Boolean = false) {
    (if (preserveOrder) collection.reversed() else collection)
        .forEach(::addFirst)
}

fun main() {
    val moveRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

    fun parseStacks(line: String): List<String> {
        return line.substring(1)
            .windowed(size = 1, step = 4)
            .toList()
    }

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
        val stackLines = mutableListOf<String>()
        var switchIdx = 0
        for ((idx, line) in input.withIndex()) {
            if (line.isEmpty()) {
                stackLines.removeLast()
                switchIdx = idx + 1
                break
            }
            stackLines.add(line)
        }

        val stackDefs: List<List<String>> = stackLines
            .map(::parseStacks)
        val maxStackDef = stackDefs.maxOf(List<String>::size)
        val stacks: List<ArrayDeque<String>> = (1..maxStackDef).map { ArrayDeque() }
        stackDefs.forEach {
            it.forEachIndexed { idx, crate -> if (crate.isNotBlank()) stacks[idx].addLast(crate) }
        }
        val moves: List<Move> = input.subList(switchIdx, input.size).map(::parseMove)

        moves.forEach {
            stacks[it.to].addAllFirst(
                stacks[it.from].removeFirst(it.amount),
                preserveOrder
            )
        }

        return stacks.joinToString(transform = ArrayDeque<String>::first, separator = "")
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
