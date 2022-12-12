data class Step(
    val position: Pair<Int, Int>,
    val name: Char,
    var visited: Boolean = false,
    var level: Int = 0
) {
    val value: Int
        get() = when (name) {
            'E' -> 'z'.code
            'S' -> 'a'.code
            else -> name.code
        }
}

fun main() {
    fun parseInput(input: List<String>): List<List<Step>> =
        input.mapIndexed { i, row ->
            row.mapIndexed { j, column ->
                Step(
                    position = i to j,
                    name = column,
                )
            }
        }

    fun walk(grid: List<List<Step>>, start: Step): Int? {
        // clear grid before start
        grid.onEach {
            it.onEach { step ->
                step.visited = false
                step.level = 0
            }
        }

        val queue = ArrayDeque<Step>()
        queue.add(start)
        start.visited = true

        while (queue.isNotEmpty()) {
            val step = queue.removeFirst()
            if (step.name == 'E') {
                return step.level
            }

            val i = step.position.first
            val j = step.position.second
            sequenceOf(
                grid.getOrNull(i + 1)?.getOrNull(j),
                grid.getOrNull(i)?.getOrNull(j + 1),
                grid.getOrNull(i - 1)?.getOrNull(j),
                grid.getOrNull(i)?.getOrNull(j - 1),
            )
                .filterNotNull()
                .filter { it.value <= step.value + 1 }
                .filterNot(Step::visited)
                .forEach {
                    queue.add(it)
                    it.visited = true
                    it.level = step.level + 1
                }
        }
        return null
    }

    fun findMinimumPath(input: List<String>, startId: (Step) -> Boolean): Int =
        parseInput(input)
            .let { grid ->
                grid.asSequence()
                    .flatMap { it.filter(startId) }
                    .map { walk(grid, it) }
                    .filterNotNull()
                    .min()
            }

    fun part1(input: List<String>): Int = findMinimumPath(input) { it.name == 'S' }
    fun part2(input: List<String>): Int = findMinimumPath(input) { it.value == 'a'.code }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
