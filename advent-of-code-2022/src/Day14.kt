fun main() {
    val rockRegex = """(\d+),(\d+)""".toRegex()
    fun parseRockAsObstacle(input: List<String>): MutableSet<P> = input.flatMap {
        rockRegex
            .findAll(it)
            .map(MatchResult::destructured)
            .map { (x, y) -> P(x.toInt(), y.toInt()) }
            .windowed(2)
            .map { interval -> interval.sortedWith(compareBy(P::x).thenBy(P::y)) }
            .flatMap { (start, end) ->
                buildSet {
                    for (i in start.x..end.x) {
                        for (j in start.y..end.y) {
                            add(P(i, j))
                        }
                    }
                }
            }
    }.toMutableSet()

    fun dropGrains(input: List<String>, stopAtFloor: Boolean): Int {
        val obstacles = parseRockAsObstacle(input)
        val rocks = obstacles.size
        val floor = obstacles.maxOf(P::y) + 2

        drop@ while (P(500, 0) !in obstacles) {
            var grain = P(500, 0)
            while (grain.y < floor) {
                val nextPosition = listOf(
                    P(grain.x, grain.y + 1),
                    P(grain.x - 1, grain.y + 1),
                    P(grain.x + 1, grain.y + 1)
                )
                    .find { it !in obstacles }

                if (nextPosition != null) {
                    if (nextPosition.y >= floor) {
                        if (stopAtFloor) {
                            break@drop
                        }
                        obstacles.add(P(grain.x, grain.y))
                        break
                    }
                    grain = nextPosition
                } else {
                    obstacles.add(P(grain.x, grain.y))
                    break
                }
            }
        }
        return obstacles.size - rocks
    }

    fun part1(input: List<String>): Int = dropGrains(input, true)
    fun part2(input: List<String>): Int = dropGrains(input, false)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput), 24, part = "Part 1")
    check(part2(testInput), 93, part = "Part 2")

    val input = readInput("Day14")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")

}
