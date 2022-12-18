package day17

import utils.*
import java.math.BigInteger

data class Block(var points: List<P>) {
    constructor(vararg points: P) : this(listOf(*points))

    val top
        get() = points.maxOf(P::y)

    fun incX(settled: Set<P>, x: Int) {
        val movedPoints = points.map { P(it.x + x, it.y) }
        if (movedPoints.all { it.x in 0..6 && it !in settled }) {
            this.points = movedPoints
        }
    }

    fun moveDown(settled: Set<P>): Boolean {
        val movedPoints = points.map { P(it.x, it.y.minus(BigInteger.ONE)) }
        if (movedPoints.all { it.y >= BigInteger.ZERO && it !in settled }) {
            this.points = movedPoints
            return true
        }
        return false
    }
}

data class P(var x: Int, var y: BigInteger)

val ZERO: BigInteger = BigInteger.ZERO
val ONE: BigInteger = BigInteger.ONE
val TWO: BigInteger = BigInteger.TWO
val THREE = ONE + TWO
val FOUR = ONE + THREE
val FIVE = ONE + FOUR

fun main() {
    fun generateBlock(serial: BigInteger, y: BigInteger): Block = when (serial.mod(FIVE).toInt()) {
        0 -> Block(P(2, y), P(3, y), P(4, y), P(5, y))
        1 -> Block(P(3, y), P(2, y + ONE), P(3, y + ONE), P(4, y + ONE), P(3, y + TWO))
        2 -> Block(P(2, y), P(3, y), P(4, y), P(4, y + ONE), P(4, y + TWO))
        3 -> Block(P(2, y), P(2, y + ONE), P(2, y + TWO), P(2, y + THREE))
        4 -> Block(P(2, y), P(3, y), P(2, y + ONE), P(3, y + ONE))
        else -> throw IllegalArgumentException("??")
    }

    fun part1(input: List<String>, rocks: BigInteger): BigInteger = input.single()
        .let { gasMoves ->
            var top = ZERO - ONE
            val settled = mutableSetOf<P>()
            var gas = 0

            var serial = ZERO
            while (serial < rocks) {
                val block = generateBlock(serial, top + FOUR)
                do {
                    block.incX(settled, when (gasMoves[gas % gasMoves.length]) {
                        '<' -> -1
                        '>' -> 1
                        else -> throw IllegalArgumentException("Incorrect input!")
                    })

                    gas++
                } while (block.moveDown(settled))

                settled += block.points
                top = block.top.coerceAtLeast(top)

                serial++
            }
            top + ONE
        }

    // TODO
    fun part2(input: List<String>): BigInteger = part1(input, "1000000000000".toBigInteger())

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check("Part 1", 3068.toBigInteger()) { part1(testInput, 2022.toBigInteger()) }
//    check("Part 2", 1514285714288.toBigInteger()) { part2(testInput) }

    val input = readInput("Day17")
    simulate("Part 1") { part1(input, 2022.toBigInteger()) }
//    simulate("Part 2") { part2(input) }
}
