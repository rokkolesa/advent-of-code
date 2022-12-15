package day15

import utils.*
import java.math.BigInteger
import java.util.*
import kotlin.math.abs

open class P(var x: Int, var y: Int) {
    infix fun distanceTo(other: P): Int = abs(x - other.x) + abs(y - other.y)

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null || other !is P -> false
        else -> x == other.x && y == other.y
    }

    override fun hashCode(): Int = Objects.hash(x, y)
}

class Beacon(x: Int, y: Int) : P(x, y)
class Sensor(x: Int, y: Int, var beacon: Beacon) : P(x, y) {
    fun findEdge(y: Int): Int = maxX - abs(this.y - y)

    val radius = this distanceTo beacon
    val minX = x - radius
    val maxX = x + radius
}

fun main() {
    val lineRegex = """Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)""".toRegex()

    fun parseInput(input: List<String>): Pair<List<Sensor>, Set<Beacon>> = input.map {
        val (sensorX, sensorY, beaconX, beaconY) = lineRegex
            .matchEntire(it)
            ?.destructured
            ?.toList()
            ?.map(String::toInt)
            ?: throw IllegalArgumentException("Incorrect input line $it")
        Sensor(sensorX, sensorY, Beacon(beaconX, beaconY))
    }
        .fold(mutableListOf<Sensor>() to mutableSetOf<Beacon>()) { acc, s ->
            acc.first.add(s)
            acc.second.add(s.beacon)
            acc
        }

    fun part1(input: List<String>, y: Int): Int = parseInput(input)
        .let { (sensors, beacons) ->
            val bound = sensors.maxOf(Sensor::maxX)
            var x = sensors.minOf(Sensor::minX)
            var covered = 0
            while (x <= bound) {
                val currentPosition = P(x, y)
                if (currentPosition in beacons) {
                    x++
                    continue
                }
                if (sensors.any { currentPosition distanceTo it <= it.radius }) covered++
                x++
            }
            return covered
        }

    fun part2(input: List<String>, bound: Int): BigInteger? = parseInput(input)
        .let { (sensors, _) ->
            for (y in 0..bound) {
                var x = 0
                while (x <= bound) {
                    val currentPosition = P(x, y)
                    x = sensors.find { currentPosition distanceTo it <= it.radius }
                        ?.findEdge(y)
                        ?: return x.toBigInteger().multiply(4_000_000.toBigInteger()).plus(y.toBigInteger())
                    x++
                }
            }
            return null
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check("Part 1", 26) { part1(testInput, 10) }
    check("Part 2", "56000011".toBigInteger()) { part2(testInput, 20) }

    val input = readInput("Day15")
    simulate("Part 1") { part1(input, 2_000_000) }
    simulate("Part 2") { part2(input, 4_000_000) }
}
