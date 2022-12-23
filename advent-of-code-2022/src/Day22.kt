package day22

import utils.*
import kotlin.math.abs
import kotlin.math.sqrt

enum class Direction(val vector: P, val value: Int) {
    RIGHT(P(1, 0), 0), DOWN(P(0, 1), 1), LEFT(P(-1, 0), 2), UP(P(0, -1), 3);

    operator fun inc(): Direction = when (this) {
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
        UP -> RIGHT
    }

    operator fun plus(steps: Int): Direction {
        var direction = this
        repeat(steps) { direction++ }
        return direction
    }

    operator fun dec(): Direction = when (this) {
        RIGHT -> UP
        DOWN -> RIGHT
        LEFT -> DOWN
        UP -> LEFT
    }

    operator fun minus(steps: Int): Direction {
        var direction = this
        repeat(steps) { direction-- }
        return direction
    }
}

data class P(val x: Int, val y: Int) {
    operator fun plus(other: P): P = P(x + other.x, y + other.y)
}

data class State(val boxFace: BoxFace, val point: P, var direction: Direction) {
    operator fun inc(): State {
        val newPoint = point + direction.vector
        val newState = when {
            newPoint.x < 0 -> boxFace.translate(boxFace.left, point, direction)
            newPoint.x >= boxFace.edgeSize -> boxFace.translate(boxFace.right, point, direction)
            newPoint.y < 0 -> boxFace.translate(boxFace.top, point, direction)
            newPoint.y >= boxFace.edgeSize -> boxFace.translate(boxFace.bottom, point, direction)
            else -> State(boxFace, newPoint, direction)
        }
        return if (newState.point in newState.boxFace.walls) this else newState
    }
}

typealias BoxEdge = Pair<BoxFace, Translation>

data class BoxFace(val id: P, val walls: Walls, val edgeSize: Int) {
    lateinit var top: BoxEdge
    lateinit var bottom: BoxEdge
    lateinit var left: BoxEdge
    lateinit var right: BoxEdge

    fun translate(edge: BoxEdge, point: P, direction: Direction): State {
        val (targetFace, translation) = edge
        val (pointInTargetFace, newDirection) = translation(point, direction)
        return State(targetFace, pointInTargetFace, newDirection)
    }
}

typealias Walls = Set<P>
typealias Translation = (P, Direction) -> Pair<P, Direction>

fun main() {
    fun parseBox(boxMap: List<String>): List<BoxFace> {
        val area = boxMap.sumOf { it.count { c -> !c.isWhitespace() } }
        val edge = sqrt(area.toDouble() / 6).toInt()
        return buildList {
            var startY = 0
            while (startY < boxMap.size) {
                var startX = boxMap[startY].indexOfFirst { !it.isWhitespace() }
                while (startX < boxMap[startY].length) {
                    val walls = buildSet {
                        for (y in startY until startY + edge) {
                            for (x in startX until startX + edge) {
                                if (boxMap[y][x] == '#') add(P(x - startX, y - startY))
                            }
                        }
                    }

                    add(BoxFace(P(startX, startY), walls, edge))
                    startX += edge
                }
                startY += edge
            }
        }
    }

    fun parseInput(input: List<String>): Pair<List<BoxFace>, String> =
        input.split(String::isBlank)
            .let { (map, path) -> parseBox(map) to path.single() }

    fun connectFacesPart1(faces: List<BoxFace>, isTest: Boolean = false) {
        val edgeSize = faces.first().edgeSize
        val moveTop: Translation = { (x, _), direction -> P(x, edgeSize - 1) to direction }
        val moveBottom: Translation = { (x, _), direction -> P(x, 0) to direction }
        val moveLeft: Translation = { (_, y), direction -> P(edgeSize - 1, y) to direction }
        val moveRight: Translation = { (_, y), direction -> P(0, y) to direction }
        if (isTest) {
            faces[0].apply {
                top = faces[4] to moveTop
                bottom = faces[3] to moveBottom
                left = this to moveLeft
                right = this to moveRight
            }
            faces[1].apply {
                top = this to moveTop
                bottom = this to moveBottom
                left = faces[3] to moveLeft
                right = faces[2] to moveRight
            }
            faces[2].apply {
                top = this to moveTop
                bottom = this to moveBottom
                left = faces[1] to moveLeft
                right = faces[3] to moveRight
            }
            faces[3].apply {
                top = faces[0] to moveTop
                bottom = faces[4] to moveBottom
                left = faces[2] to moveLeft
                right = faces[1] to moveRight
            }
            faces[4].apply {
                top = faces[3] to moveTop
                bottom = faces[0] to moveBottom
                left = faces[5] to moveLeft
                right = faces[5] to moveRight
            }
            faces[5].apply {
                top = this to moveTop
                bottom = this to moveBottom
                left = faces[4] to moveLeft
                right = faces[4] to moveRight
            }
        } else {
            faces[0].apply {
                top = faces[4] to moveTop
                bottom = faces[2] to moveBottom
                left = faces[1] to moveLeft
                right = faces[1] to moveRight
            }
            faces[1].apply {
                top = this to moveTop
                bottom = this to moveBottom
                left = faces[0] to moveLeft
                right = faces[0] to moveRight
            }
            faces[2].apply {
                top = faces[0] to moveTop
                bottom = faces[4] to moveBottom
                left = this to moveLeft
                right = this to moveRight
            }
            faces[3].apply {
                top = faces[5] to moveTop
                bottom = faces[5] to moveBottom
                left = faces[4] to moveLeft
                right = faces[4] to moveRight
            }
            faces[4].apply {
                top = faces[2] to moveTop
                bottom = faces[0] to moveBottom
                left = faces[3] to moveLeft
                right = faces[3] to moveRight
            }
            faces[5].apply {
                top = faces[3] to moveTop
                bottom = faces[3] to moveBottom
                left = this to moveLeft
                right = this to moveRight
            }
        }
    }

    fun connectFacesPart2(faces: List<BoxFace>, isTest: Boolean = false) {
        val edgeSize = faces.first().edgeSize
        val moveTop: Translation = { (x, _), direction -> P(x, edgeSize - 1) to direction }
        val moveBottom: Translation = { (x, _), direction -> P(x, 0) to direction }
        val moveLeft: Translation = { (_, y), direction -> P(edgeSize - 1, y) to direction }
        val moveRight: Translation = { (_, y), direction -> P(0, y) to direction }
        val switch = { c: Int -> abs(c - edgeSize + 1) }
        if (isTest) {
            faces[0].apply {
                top = faces[1] to { (x, y), direction -> P(switch(x), y) to direction + 2 }
                bottom = faces[3] to moveBottom
                left = faces[2] to { (x, y), direction -> P(y, x) to direction - 1 }
                right = faces[5] to { (x, y), direction -> P(x, switch(y)) to direction + 2 }
            }
            faces[1].apply {
                top = faces[0] to { (x, y), direction -> P(switch(x), y) to direction + 2 }
                bottom = faces[4] to { (x, y), direction -> P(switch(x), y) to direction + 2 }
                left = faces[5] to { (_, y), direction -> P(switch(y), edgeSize - 1) to direction + 1 }
                right = faces[2] to moveRight
            }
            faces[2].apply {
                top = faces[0] to { (x, y), direction -> P(y, x) to direction + 1 }
                bottom = faces[4] to { (x, _), direction -> P(0, switch(x)) to direction - 1 }
                left = faces[1] to moveLeft
                right = faces[3] to moveRight
            }
            faces[3].apply {
                top = faces[0] to moveTop
                bottom = faces[4] to moveBottom
                left = faces[2] to moveLeft
                right = faces[5] to { (_, y), direction -> P(switch(y), 0) to direction + 1 }
            }
            faces[4].apply {
                top = faces[3] to moveTop
                bottom = faces[1] to { (x, y), direction -> P(switch(x), y) to direction + 2 }
                left = faces[2] to { (_, y), direction -> P(switch(y), edgeSize - 1) to direction + 1 }
                right = faces[5] to moveRight
            }
            faces[5].apply {
                top = faces[3] to { (x, _), direction -> P(edgeSize - 1, switch(x)) to direction - 1 }
                bottom = faces[1] to { (x, _), direction -> P(0, switch(x)) to direction - 1 }
                left = faces[4] to moveLeft
                right = faces[0] to { (x, y), direction -> P(x, abs(y - edgeSize + 1)) to direction + 2 }
            }
        } else {
            faces[0].apply {
                top = faces[5] to { (x, _), direction -> P(0, x) to direction + 1 }
                bottom = faces[2] to moveBottom
                left = faces[3] to { (_, y), direction -> P(0, switch(y)) to direction + 2 }
                right = faces[1] to moveRight
            }
            faces[1].apply {
                top = faces[5] to { (x, _), direction -> P(x, edgeSize - 1) to direction }
                bottom = faces[2] to { (x, _), direction -> P(edgeSize - 1, x) to direction + 1 }
                left = faces[0] to moveLeft
                right = faces[4] to { (_, y), direction -> P(edgeSize - 1, switch(y)) to direction + 2 }
            }
            faces[2].apply {
                top = faces[0] to moveTop
                bottom = faces[4] to moveBottom
                left = faces[3] to { (_, y), direction -> P(y, 0) to direction - 1 }
                right = faces[1] to { (_, y), direction -> P(y, edgeSize - 1) to direction - 1 }
            }
            faces[3].apply {
                top = faces[2] to { (x, _), direction -> P(0, x) to direction + 1 }
                bottom = faces[5] to moveBottom
                left = faces[0] to { (_, y), direction -> P(0, switch(y)) to direction + 2 }
                right = faces[4] to moveRight
            }
            faces[4].apply {
                top = faces[2] to moveTop
                bottom = faces[5] to { (x, _), direction -> P(edgeSize - 1, x) to direction + 1 }
                left = faces[3] to moveLeft
                right = faces[1] to { (_, y), direction -> P(edgeSize - 1, switch(y)) to direction + 2 }
            }
            faces[5].apply {
                top = faces[3] to moveTop
                bottom = faces[1] to { (x, _), direction -> P(x, 0) to direction }
                left = faces[0] to { (_, y), direction -> P(y, 0) to direction - 1 }
                right = faces[4] to { (_, y), direction -> P(y, edgeSize - 1) to direction - 1 }
            }
        }
    }


    fun walk(input: List<String>, isTest: Boolean = false, connectFaces: (List<BoxFace>, Boolean) -> Unit) = parseInput(input)
        .also { (faces, _) -> connectFaces(faces, isTest) }
        .let { (faces, path) ->
            val number = """(\d+)""".toRegex()

            var pathIdx = 0
            var state = State(faces[0], P(0, 0), Direction.RIGHT)
            while (pathIdx < path.length) {
                if (path[pathIdx].isLetter()) {
                    when (path[pathIdx]) {
                        'R' -> state.direction++
                        'L' -> state.direction--
                        else -> throw IllegalArgumentException("Incorrect direction!")
                    }
                    pathIdx++
                    continue
                }
                val (steps) = number.matchAt(path, pathIdx)
                    ?.destructured
                    ?: throw IllegalArgumentException("Incorrect path!")

                repeat(steps.toInt()) { state++ }

                pathIdx += steps.length
            }

            val (face, point, direction) = state
            val position = face.id + point

            1000 * (position.y + 1) + 4 * (position.x + 1) + direction.value
        }

    fun part1(input: List<String>, isTest: Boolean = false): Int = walk(input, isTest, ::connectFacesPart1)
    fun part2(input: List<String>, isTest: Boolean = false): Int = walk(input, isTest, ::connectFacesPart2)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check("Part 1", 6032) { part1(testInput, true) }
    check("Part 2", 5031) { part2(testInput, true) }

    val input = readInput("Day22")
    check("Part 1", 159034) { part1(input) }
    check("Part 2", 147245) { part2(input) }
}
