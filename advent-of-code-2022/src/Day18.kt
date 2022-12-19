package day18

import utils.*
import kotlin.math.abs

data class Cube(val x: Int, val y: Int, val z: Int, var visited: Boolean = false) {
    var sides = 6
    infix fun distanceTo(other: Cube): Int = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    fun adjacent(): Set<Cube> = buildSet(6) {
        add(Cube(x + 1, y, z))
        add(Cube(x - 1, y, z))
        add(Cube(x, y + 1, z))
        add(Cube(x, y - 1, z))
        add(Cube(x, y, z + 1))
        add(Cube(x, y, z - 1))
    }
}

fun main() {

    fun parseCubes(input: List<String>): List<Cube> = input
        .map {
            val (x, y, z) = it.split(",").map(String::toInt)
            Cube(x, y, z)
        }

    fun part1(input: List<String>): Int {
        val cubes = mutableSetOf<Cube>()
        parseCubes(input).forEach { cube ->
            val adjacentCubes = cubes.filter { it distanceTo cube == 1 }
            adjacentCubes.forEach { it.sides-- }
            cube.sides -= adjacentCubes.size
            if (cube.sides > 0) cubes += cube
        }
        return cubes.sumOf(Cube::sides)
    }

    fun part2(input: List<String>): Int = parseCubes(input)
        .let { cubes ->
            cubes to buildSet {
                for (x in cubes.minOf(Cube::x)..cubes.maxOf(Cube::x)) {
                    for (y in cubes.minOf(Cube::y)..cubes.maxOf(Cube::y)) {
                        for (z in cubes.minOf(Cube::z)..cubes.maxOf(Cube::z)) {
                            add(Cube(x, y, z))
                        }
                    }
                }
            }
        }
        .let { (cubes, allCubes) ->
            val queue = ArrayDeque<Cube>()
            queue.add(allCubes.first())
            allCubes.first().visited = true

            val dropletComplement = mutableSetOf<Cube>()
            while (queue.isNotEmpty()) {
                val cube = queue.removeFirst()
                dropletComplement += cube

                val adjacent = cube.adjacent()
                allCubes
                    .filter { it in adjacent }
                    .filterNot { it in cubes }
                    .filterNot(Cube::visited)
                    .forEach {
                        queue += it
                        it.visited = true
                    }
            }
            allCubes - dropletComplement
        }
        .apply {
            onEach { cube ->
                val adjacent = cube.adjacent()
                cube.sides -= count { it in adjacent }
            }
        }.sumOf(Cube::sides)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check("Part 1", 64) { part1(testInput) }
    check("Part 2", 58) { part2(testInput) }

    val input = readInput("Day18")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
