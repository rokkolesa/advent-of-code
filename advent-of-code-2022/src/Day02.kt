enum class Opponent
{
    A, B, C
}

enum class Me(val score: Int, val outcome: Int)
{
    X(1, 0), Y(2, 3), Z(3, 6)
}

val outcomes: Array<IntArray> = arrayOf(
    intArrayOf(3, 0, 6),
    intArrayOf(6, 3, 0),
    intArrayOf(0, 6, 3)
)
val scores: Array<IntArray> = arrayOf(
    intArrayOf(3, 1, 2),
    intArrayOf(1, 2, 3),
    intArrayOf(2, 3, 1)
)

data class Input(val opponent: Opponent, val me: Me)

fun main()
{
    val inputLineRegex = """([ABC]) ([XYZ])""".toRegex()
    fun parseInput(input: List<String>): List<Input> = input.map {
        val (opponentSign, mySign) = inputLineRegex
            .matchEntire(it)
            ?.destructured
            ?: throw IllegalArgumentException("Incorrect input line $it")
        val opponent = Opponent.valueOf(opponentSign)
        val me = Me.valueOf(mySign)
        Input(opponent, me)
    }

    fun sumScores(
        input: List<String>,
        rightInput: (Me) -> Int,
        targetScore: Array<IntArray>
    ): Int
    {
        val parsedInput = parseInput(input)
        val rightScore = parsedInput.sumOf { (_, me) -> rightInput(me) }
        val targetScores = parsedInput.sumOf { (opponent, me) -> targetScore[me.ordinal][opponent.ordinal] }
        return rightScore + targetScores
    }

    fun part1(input: List<String>): Int = sumScores(input, Me::score, outcomes)
    fun part2(input: List<String>): Int = sumScores(input, Me::outcome, scores)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
