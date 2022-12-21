package day20

import utils.check
import utils.readInput

data class FileNumber(var idx: Int, val value: Long)

fun main() {
    fun decrypt(input: List<String>, decryptionKey: Long = 1, repeat: Int = 1): Long =
        input.mapIndexed { idx, element -> FileNumber(idx, element.toLong() * decryptionKey) }
            .apply {
                repeat(repeat) {
                    for (fileNumber in this) {
                        val (idx, value) = fileNumber

                        val newIdx = (idx + value.mod(size - 1)).let {
                            when {
                                it >= size - 1 -> it + 1
                                else -> it
                            }
                        }.mod(size)

                        fileNumber.idx = newIdx

                        val (rangeToCorrect, pushBy) =
                            if (idx < newIdx) idx..newIdx to -1
                            else newIdx..idx to 1

                        for (pair in this) {
                            if (pair !== fileNumber && pair.idx in rangeToCorrect) {
                                pair.idx = (pair.idx + pushBy).mod(size)
                            }
                        }
                    }
                }
            }
            .run { this to (find { (_, v) -> v == 0L })!!.idx }
            .let { (file, zeroIdx) ->
                (1..3).sumOf { i ->
                    (file.find { (idx, _) -> (i * 1000 + zeroIdx).mod(file.size) == idx })!!.value
                }
            }


    fun part1(input: List<String>): Long = decrypt(input)
    fun part2(input: List<String>): Long = decrypt(input, decryptionKey = 811589153, repeat = 10)


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check("Part 1", 3) { part1(testInput) }
    check("Part 2", 1623178306) { part2(testInput) }

    val input = readInput("Day20")
    check("Part 1", 3700) { part1(input) }
    check("Part 2", 10626948369382) { part2(input) }
}
