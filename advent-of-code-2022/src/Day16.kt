package day16

import utils.*

data class Valve(val name: String, val flow: Int, val nextValves: List<String>)

typealias Flow = Pair<String, Int>
typealias DoubleFlow = Triple<String, String, Int>
typealias OpenedValves = Set<String>

data class Memo(val valveName: String, val minutes: Int, val opened: OpenedValves)
data class MultiMemo(val valves: Set<String>, val minutes: Set<Int>, val opened: OpenedValves)

infix fun <T, U> Collection<T>.times(other: Collection<U>): List<Pair<T, U>> = buildList(size * other.size) {
    this@times.forEach { t ->
        other.forEach { u ->
            add(t to u)
        }
    }
}

fun main() {
    val valveRegex = """Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)""".toRegex()

    fun parseValves(input: List<String>): Map<String, Valve> = input.map {
        val (name, flow, nextValves) = valveRegex
            .matchEntire(it)
            ?.destructured
            ?: throw IllegalArgumentException("Incorrect input line $it")
        Valve(name, flow.toInt(), nextValves.split(", "))
    }
        .associateBy(Valve::name)

    fun shortestPaths(valves: Map<String, Valve>): Map<Valve, Map<Valve, Pair<String, Int>>> = valves.values
        .filter { it.flow != 0 || it.name == "AA" }
        .associateWith { v ->
            buildMap {
                val queue = ArrayDeque<Triple<Valve, String, Int>>()
                queue += Triple(v, "", 0)
                val visited = mutableSetOf<Valve>()
                while (queue.isNotEmpty()) {
                    val (valve, path, length) = queue.removeFirst()
                    if (valve.flow != 0 && valve !in this && v != valve) {
                        put(valve, path to length)
                    }

                    valve.nextValves
                        .map { valves[it]!! }
                        .filterNot { it in visited }
                        .forEach {
                            visited.add(it)
                            queue += Triple(it, path + it.name, length + 1)
                        }
                }
            }
        }

    fun part1(input: List<String>): Int {
        val valves = parseValves(input)
        val shortestPaths = shortestPaths(valves)
        val valvePaths: MutableMap<Memo, Flow> = mutableMapOf()

        // returns <path> to <flow>
        fun findMaxPath(valveName: String, minutes: Int, opened: OpenedValves): Flow {
            if (minutes <= 0) {
                return "" to 0
            }

            val myMaxPath = valvePaths[Memo(valveName, minutes, opened)]
            if (myMaxPath != null) {
                return myMaxPath
            }


            val valve = valves[valveName]!!
            val (nextMax, maxFlow) = shortestPaths[valve]!!
                .filterNot { (next, _) -> next.name in opened }
                .filter { (_, path) -> minutes - path.second - 1 > 0 }
                .map { (next, path) ->
                    val minutesRemaining = minutes - path.second - 1
                    val (p, f) = findMaxPath(next.name, minutesRemaining, opened + next.name)
                    Flow("${path.first} -> $p", f + (next.flow * minutesRemaining))
                }
                .maxByOrNull { (_, f) -> f } ?: ("" to 0)


            valvePaths[Memo(valveName, minutes, opened)] = nextMax to maxFlow
            return nextMax to maxFlow
        }
        val maxPath = findMaxPath("AA", 30, setOf())
        return maxPath.second
    }

    fun part2(input: List<String>): Int {
        val valves = parseValves(input)
        val shortestPaths = shortestPaths(valves)
        val valvePaths: MutableMap<MultiMemo, DoubleFlow> = mutableMapOf()

        fun findMaxPath(me: String, elephant: String, minutes: Int, minutesElephant: Int, opened: OpenedValves): DoubleFlow {
            if (minutes <= 0 || minutesElephant <= 0) {
                return DoubleFlow("", "", 0)
            }
            val myMaxPath = valvePaths[MultiMemo(setOf(me, elephant), setOf(minutes, minutesElephant), opened)]
            if (myMaxPath != null) {
                return myMaxPath
            }

            val valve = valves[me]!!
            val elephantValve = valves[elephant]!!

            val (nextMax, nextMaxElephant, maxFlow) = (shortestPaths[valve]!!.entries times shortestPaths[elephantValve]!!.entries)
                .asSequence()
                .filterNot { (valveEntry, elephantEntry) -> valveEntry.key.name == elephantEntry.key.name }
                .filter { (valveEntry, elephantEntry) -> valveEntry.key.name !in opened && elephantEntry.key.name !in opened }
                .filter { (valveEntry, elephantEntry) -> minutes - valveEntry.value.second - 1 > 0 && minutesElephant - elephantEntry.value.second - 1 > 0 }
                .map { (valveEntry, elephantEntry) ->
                    val minutesRemaining = minutes - valveEntry.value.second - 1
                    val minutesRemainingElephant = minutesElephant - elephantEntry.value.second - 1
                    val (path, pathElephant, f) = findMaxPath(valveEntry.key.name, elephantEntry.key.name, minutesRemaining, minutesRemainingElephant, opened + valveEntry.key.name + elephantEntry.key.name)
                    DoubleFlow(
                        "${valveEntry.key.name} -> $path",
                        "${elephantEntry.key.name} -> $pathElephant",
                        f + (valveEntry.key.flow * minutesRemaining) + (elephantEntry.key.flow * minutesRemainingElephant)
                    )
                }
                .maxByOrNull { (_, _, f) -> f } ?: DoubleFlow("", "", 0)


            val flow = DoubleFlow(nextMax, nextMaxElephant, maxFlow)
            valvePaths[MultiMemo(setOf(me, elephant), setOf(minutes, minutesElephant), opened)] = flow
            return flow
        }

        val maxPath = findMaxPath("AA", "AA", 26, 26, setOf())
        return maxPath.third
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check("Part 1", 1651) { part1(testInput) }
    check("Part 2", 1707) { part2(testInput) }

    val input = readInput("Day16")
//    check("Part 1", 2265) { part1(input) }
//    check("Part 2", 2811) { part2(input) }
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
