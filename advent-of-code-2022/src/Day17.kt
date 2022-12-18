package day17

import utils.*

enum class BlockType {
    HORIZONTAL,
    PLUS,
    L,
    VERTICAL,
    SQUARE
}

typealias GustBlock = Pair<Int, BlockType>
typealias HeightNumOfBlocks = Pair<Long, Long>

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
        val movedPoints = points.map { P(it.x, it.y - 1) }
        if (movedPoints.all { it.y >= 0 && it !in settled }) {
            this.points = movedPoints
            return true
        }
        return false
    }
}

data class P(var x: Int, var y: Long)

fun main() {
    fun generateBlock(serial: Long, y: Long): Pair<BlockType, Block> = when (serial % 5) {
        0L -> BlockType.HORIZONTAL to Block(P(2, y), P(3, y), P(4, y), P(5, y))
        1L -> BlockType.PLUS to Block(P(3, y), P(2, y + 1), P(3, y + 1), P(4, y + 1), P(3, y + 2))
        2L -> BlockType.L to Block(P(2, y), P(3, y), P(4, y), P(4, y + 1), P(4, y + 2))
        3L -> BlockType.VERTICAL to Block(P(2, y), P(2, y + 1), P(2, y + 2), P(2, y + 3))
        4L -> BlockType.SQUARE to Block(P(2, y), P(3, y), P(2, y + 1), P(3, y + 1))
        else -> throw IllegalArgumentException("??")
    }

    fun part1(input: List<String>): Long = input.single()
        .let { gasMoves ->
            var top = -1L
            val settled = mutableSetOf<P>()
            var gas = 0

            var serial = 0L
            while (serial < 2022) {
                val (_, block) = generateBlock(serial, top + 4)
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
            top + 1
        }

    fun part2(input: List<String>, totalBlocks: Long): Long = input.single()
        .let { gasMoves ->
            var top = -1L
            val settled = mutableSetOf<P>()
            var gas = 0

            var serial = 0L
            var blocks = totalBlocks

            var periodHeightGained = 0L
            val cache: MutableMap<GustBlock, HeightNumOfBlocks> = mutableMapOf()
            var shouldCache = true
            while (serial < blocks) {
                val (blockType, block) = generateBlock(serial, top + 4)
                val gustBlock = (gas % gasMoves.length to blockType)

                // magic number, where the real period starts to show itself
                if (serial > 2758 && gustBlock in cache) {
                    val (previousTop, previousBlockCount) = cache[gustBlock]!!

                    val periodBlocks = serial - previousBlockCount
                    val loopingBlocks = totalBlocks - previousBlockCount
                    val allPeriods = loopingBlocks / periodBlocks
                    blocks = loopingBlocks - allPeriods * periodBlocks + serial

                    val periodHeight = top - previousTop + 2
                    periodHeightGained = (allPeriods - 1) * periodHeight

                    cache.clear()
                    shouldCache = false
                }

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

                if (shouldCache) cache[gustBlock] = top to serial
                serial++
            }
            top + 1 + periodHeightGained
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check("Part 1", 3068) { part1(testInput) }
    check("Part 2", 3068) { part2(testInput, 2022) }

    val input = readInput("Day17")
    simulate("Part 1") { part1(input) }
    check("Part 2", 3173) { part2(input, 2022) }
    check("Part 2", 15729) { part2(input, 10000) }
    check("Part 2", 31432) { part2(input, 20000) }
    check("Part 2", 47122) { part2(input, 30000) }
    check("Part 2", 1570930232582) { part2(input, 1000000000000) }
}
