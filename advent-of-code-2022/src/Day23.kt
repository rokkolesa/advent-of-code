package day23

import utils.*

enum class Heading(val adjacent: List<Elf>, val unit: Elf) {
    E((-1..1).map { Elf(1, it) }, Elf(1, 0)),
    S((-1..1).map { Elf(it, 1) }, Elf(0, 1)),
    W((-1..1).map { Elf(-1, it) }, Elf(-1, 0)),
    N((-1..1).map { Elf(it, -1) }, Elf(0, -1));

    operator fun inc(): Heading = when (this) {
        N -> S
        S -> W
        W -> E
        E -> N
    }

    fun headings() = buildList {
        var temp = this@Heading
        add(temp)
        while (++temp != this@Heading) {
            add(temp)
        }
    }
}

data class Elf(var x: Int, var y: Int) {
    operator fun plusAssign(other: Elf) {
        x += other.x
        y += other.y
    }

    operator fun plus(other: Elf): Elf = Elf(x + other.x, y + other.y)
    fun isAlone(elves: Elves): Boolean =
        (x - 1..x + 1).all { x ->
            (y - 1..y + 1).all { y ->
                Elf(x, y).let { it == this || it !in elves }
            }
        }
}

typealias Elves = MutableSet<Elf>

fun main() {
    fun Elves.bounds(): List<Int> = buildList {
        add(minOf(Elf::x))
        add(maxOf(Elf::x))
        add(minOf(Elf::y))
        add(maxOf(Elf::y))
    }

    fun Elves.missingPlaces(): Int = bounds()
        .let { (minX, maxX, minY, maxY) -> (maxX - minX + 1) * (maxY - minY + 1) - size }

    fun Elves.print() = bounds()
        .let { (minX, maxX, minY, maxY) ->
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    if (Elf(x, y) in this) print("#") else print(".")
                }
                println()
            }
        }

    fun parseInput(input: List<String>): Elves = buildSet {
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == '#') add(Elf(x, y))
            }
        }
    }.toMutableSet()

    fun separateElves(input: List<String>, breakAt10Rounds: Boolean = false): Pair<Int, Int> = parseInput(input)
        .let { elves ->
            val intermediateRounds = 10
            var missingPlacesAfter10Rounds: Int? = null

            var startHeading = Heading.N
            var moveCandidates = elves.filterNot { it.isAlone(elves) }.toMutableSet()
            var rounds = 1
            while (moveCandidates.isNotEmpty()) {

                val proposedLocations = mutableMapOf<Elf, Elves>()
                for (elf in moveCandidates) {
                    val proposedHeading = startHeading.headings()
                        .find { it.adjacent.none { adj -> (adj + elf) in elves } }
                        ?: continue

                    val newElfLocation = elf + proposedHeading.unit
                    proposedLocations.getOrPut(newElfLocation, ::mutableSetOf) += elf
                }

                proposedLocations.filterValues { it.size == 1 }
                    .forEach { (newElf, oldElf) ->
                        elves.add(newElf)
                        elves.remove(oldElf.single())
                    }

                if (rounds == intermediateRounds) {
                    missingPlacesAfter10Rounds = elves.missingPlaces()
                    if (breakAt10Rounds) {
                        return@let missingPlacesAfter10Rounds to rounds
                    }
                }

                rounds++
                startHeading++
                moveCandidates = elves.filterNot { it.isAlone(elves) }.toMutableSet()
            }

            (missingPlacesAfter10Rounds ?: elves.missingPlaces()) to rounds
        }

    fun part1(input: List<String>): Int = separateElves(input, breakAt10Rounds = true).first
    fun part2(input: List<String>): Int = separateElves(input).second

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    val testInput2 = readInput("Day23_test_2")
    check("Part 1", 25) { part1(testInput2) }
    check("Part 1", 110) { part1(testInput) }
    check("Part 2", 4) { part2(testInput2) }
    check("Part 2", 20) { part2(testInput) }

    val input = readInput("Day23")
    check("Part 1", 3923) { part1(input) }
    check("Part 2", 1019) { part2(input) }
}
