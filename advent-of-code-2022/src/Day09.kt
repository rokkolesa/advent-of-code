import kotlin.math.abs

data class P(var x: Int, var y: Int) {
    operator fun plusAssign(other: P) {
        x += other.x
        y += other.y
    }

    operator fun unaryMinus(): P = P(-x, -y)
    operator fun plus(other: P): P = P(x + other.x, y + other.y)
    operator fun minus(other: P): P = this + (-other)

    infix fun touching(other: P): Boolean = abs(x - other.x) <= 1 && abs(y - other.y) <= 1

    fun toUnit() = P(x / (abs(x).coerceAtLeast(1)), y / (abs(y).coerceAtLeast(1)))
}

fun main() {
    fun parseLine(line: String): Pair<P, Int> {
        val (direction, length) = line.split(" ")
        val unitVector = when (direction) {
            "R" -> P(1, 0)
            "L" -> P(-1, 0)
            "U" -> P(0, 1)
            "D" -> P(0, -1)
            else -> throw IllegalArgumentException("Incorrect input line $line")
        }
        return unitVector to length.toInt()
    }

    fun optimizedPart1(input: List<String>): Int {
        val head = P(0, 0)
        var tail = P(0, 0)

        val tailPositions = mutableSetOf(tail)
        input.forEach {
            val (unitVector, length) = parseLine(it)

            for (i in 1..length) {
                val previousHead = P(head.x, head.y)
                head += unitVector
                if (!(head touching tail)) {
                    tail = previousHead
                    tailPositions.add(tail)
                }
            }
        }
        return tailPositions.size
    }

    fun moveRope(input: List<String>, ropeLength: Int): Int {
        val knots = generateSequence { P(0, 0) }.take(ropeLength).toList()
        val tailPositions = mutableSetOf(P(0, 0))
        input.forEach {
            val (unitVector, length) = parseLine(it)

            for (i in 1..length) {
                knots.first() += unitVector
                knots.windowed(2).forEach { (first, second) ->
                    if (!(first touching second)) {
                        second += (first - second).toUnit()
                    }
                }

                tailPositions.add(P(knots.last().x, knots.last().y))
            }
        }
        return tailPositions.size
    }

    fun part1(input: List<String>) = moveRope(input, 2)
    fun part2(input: List<String>) = moveRope(input, 10)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val testInput2 = readInput("Day09_test_2")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
