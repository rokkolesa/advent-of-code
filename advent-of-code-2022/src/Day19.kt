package day19

import utils.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

data class OreRobot(val ore: Int)
data class ClayRobot(val ore: Int)
data class ObsidianRobot(val ore: Int, val clay: Int)
data class GeodeRobot(val ore: Int, val obsidian: Int)

data class Blueprint(val id: Int, val oreRobot: OreRobot, val clayRobot: ClayRobot, val obsidianRobot: ObsidianRobot, val geodeRobot: GeodeRobot) {
    fun canBuildOreRobot(ore: Int) = oreRobot.ore <= ore
    fun canBuildClayRobot(ore: Int) = clayRobot.ore <= ore
    fun canBuildObsidianRobot(ore: Int, clay: Int) = obsidianRobot.ore <= ore && obsidianRobot.clay <= clay
    fun canBuildGeodeRobot(ore: Int, obsidian: Int) = geodeRobot.ore <= ore && geodeRobot.obsidian <= obsidian

    val maxOreRobots = sequenceOf(oreRobot.ore, clayRobot.ore, obsidianRobot.ore, geodeRobot.ore).max()
    val maxClayRobots = obsidianRobot.clay
    val maxObisidanRobots = geodeRobot.obsidian
}

data class Memo(
    val oreRobots: Int, val clayRobots: Int, val obsidianRobots: Int, val geodeRobots: Int,
    val ore: Int, val clay: Int, val obsidian: Int, val geode: Int,
    val minutes: Int
)

@OptIn(ExperimentalTime::class)
fun main() {
    val blueprintRegex = """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()

    fun parseBlueprint(input: List<String>): List<Blueprint> = input.map {
        val (id, ore, clay, obsidianOre, obsidianClay, geodeOre, geodeObsidian) =
            blueprintRegex.matchEntire(it)
                ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $it")
        Blueprint(
            id.toInt(),
            OreRobot(ore.toInt()),
            ClayRobot(clay.toInt()),
            ObsidianRobot(obsidianOre.toInt(), obsidianClay.toInt()),
            GeodeRobot(geodeOre.toInt(), geodeObsidian.toInt())
        )
    }


    fun calculateScore(blueprint: Blueprint, totalMinutes: Int): Int = measureTimedValue {
        val cache = mutableMapOf<Memo, Int>()

        fun findOptimalGeodeNumber(
            oreRobots: Int, clayRobots: Int, obsidianRobots: Int, geodeRobots: Int,
            ore: Int, clay: Int, obsidian: Int, geode: Int,
            minutes: Int
        ): Int {
            if (minutes <= 0) {
                return 0
            }

            cache[Memo(oreRobots, clayRobots, obsidianRobots, geodeRobots, ore, clay, obsidian, geode, minutes)]
                ?.let { return@findOptimalGeodeNumber it }

            if (oreRobots >= blueprint.maxOreRobots + 1 || clayRobots >= blueprint.maxClayRobots || obsidianRobots >= blueprint.maxObisidanRobots) {
                cache[Memo(oreRobots, clayRobots, obsidianRobots, geodeRobots, ore, clay, obsidian, geode, minutes)] = 0
                return 0
            }

            val possibilities = mutableListOf<Int>()

            // priority on geode robots
            if (blueprint.canBuildGeodeRobot(ore, obsidian)) {
                possibilities += findOptimalGeodeNumber(
                    oreRobots, clayRobots, obsidianRobots, geodeRobots + 1,
                    ore + oreRobots - blueprint.geodeRobot.ore, clay + clayRobots, obsidian + obsidianRobots - blueprint.geodeRobot.obsidian, geode + geodeRobots,
                    minutes - 1
                )
            } else {
                // enough obsidian robots if the current production is enough for maximum possible geode robots
                if (obsidian + obsidianRobots * minutes < minutes * blueprint.maxObisidanRobots && blueprint.canBuildObsidianRobot(ore, clay)) {
                    possibilities += findOptimalGeodeNumber(
                        oreRobots, clayRobots, obsidianRobots + 1, geodeRobots,
                        ore + oreRobots - blueprint.obsidianRobot.ore, clay + clayRobots - blueprint.obsidianRobot.clay, obsidian + obsidianRobots, geode + geodeRobots,
                        minutes - 1
                    )
                }
                if (clay + clayRobots * minutes < minutes * blueprint.maxClayRobots && blueprint.canBuildClayRobot(ore)) {
                    possibilities += findOptimalGeodeNumber(
                        oreRobots, clayRobots + 1, obsidianRobots, geodeRobots,
                        ore + oreRobots - blueprint.clayRobot.ore, clay + clayRobots, obsidian + obsidianRobots, geode + geodeRobots,
                        minutes - 1
                    )
                }
                if (ore + oreRobots * minutes < minutes * blueprint.maxOreRobots && blueprint.canBuildOreRobot(ore)) {
                    possibilities += findOptimalGeodeNumber(
                        oreRobots + 1, clayRobots, obsidianRobots, geodeRobots,
                        ore + oreRobots - blueprint.oreRobot.ore, clay + clayRobots, obsidian + obsidianRobots, geode + geodeRobots,
                        minutes - 1
                    )
                }

                possibilities += findOptimalGeodeNumber(
                    oreRobots, clayRobots, obsidianRobots, geodeRobots,
                    ore + oreRobots, clay + clayRobots, obsidian + obsidianRobots, geode + geodeRobots,
                    minutes - 1
                )
            }

            val maxGeodes = possibilities.max().coerceAtLeast(geode)
            cache[Memo(oreRobots, clayRobots, obsidianRobots, geodeRobots, ore, clay, obsidian, geode, minutes)] = maxGeodes
            return maxGeodes
        }

        findOptimalGeodeNumber(1, 0, 0, 0, 1, 0, 0, 0, totalMinutes)
    }
        .let { (score, duration) ->
            println("Blueprint ${blueprint.id} score: $score | Elapsed time: $duration")
            score
        }


    fun part1(input: List<String>): Int = parseBlueprint(input)
        .sumOf { it.id * calculateScore(it, 24) }

    fun part2(input: List<String>): Int = parseBlueprint(input)
        .take(3)
        .map { calculateScore(it, 32) }
        .reduce { acc: Int, score: Int -> acc * score }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")
    check("Part 1", 33) { part1(testInput) }
    check("Part 1", 1346) { part1(input) }

    check("Part 2", 56 * 62) { part2(testInput) }
    check("Part 2", 7644) { part2(input) }
}
