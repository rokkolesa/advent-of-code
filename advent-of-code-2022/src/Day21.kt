package day21

import utils.*
import kotlin.IllegalStateException

typealias Num = Long

class Operation(val name: String, val constant: Boolean = false, val op: (Num, Num) -> Num) {
    lateinit var leftInv: Operation
    lateinit var rightInv: Operation

    operator fun invoke(left: Num, right: Num): Num = op(left, right)
    operator fun invoke(coeff: Num) = invoke(coeff, coeff)
    operator fun invoke() = invoke(0, 0)

    override fun toString(): String = name.ifEmpty { invoke().toString() }
}

data class Monkey(val name: String, val operation: Operation, val subMonkeys: List<String>)

fun List<Operation>.toConstant() = this[0]()
fun List<Operation>.isConstant() = all(Operation::constant)

fun main() {
    val plusOperation = Operation("+") { left, right -> left + right }
        .apply {
            leftInv = Operation("-") { left, right -> left - right }
            rightInv = Operation("-") { left, right -> right - left }
        }

    val minusOperation = Operation("-") { left, right -> left - right }
        .apply {
            leftInv = Operation("+") { left, right -> left + right }
            rightInv = Operation("-") { left, right -> left - right }
        }

    val multiplyOperation =
        Operation("*") { left, right -> left * right }
            .apply {
                leftInv = Operation("/") { left, right -> left / right }
                rightInv = Operation("/") { left, right -> right / left }
            }

    val divisionOperation =
        Operation("/") { left, right -> left / right }
            .apply {
                leftInv = Operation("*") { left, right -> left * right }
                rightInv = Operation("/") { left, right -> right / left }

            }
    val constantOperation = { l: Num -> Operation("", constant = true) { _, _ -> l }.apply { leftInv = this; rightInv = this } }

    fun parseMonkeys(input: List<String>): List<Monkey> = input.map {
        val (name, operationDefinition) = it.split(": ")
        val (operation, subMonkeys) = when {
            operationDefinition.contains("+") -> operationDefinition.split(" + ").let { (left, right) -> plusOperation to listOf(left, right) }
            operationDefinition.contains("-") -> operationDefinition.split(" - ").let { (left, right) -> minusOperation to listOf(left, right) }
            operationDefinition.contains("*") -> operationDefinition.split(" * ").let { (left, right) -> multiplyOperation to listOf(left, right) }
            operationDefinition.contains("/") -> operationDefinition.split(" / ").let { (left, right) -> divisionOperation to listOf(left, right) }
            else -> constantOperation(operationDefinition.toLong()) to listOf()
        }

        Monkey(name, operation, subMonkeys)
    }

    fun part1(input: List<String>): Num = parseMonkeys(input).associateBy(Monkey::name)
        .let { monkeys ->
            val cache = mutableMapOf<String, Num>()

            val queue = ArrayDeque<String>()
            queue.addFirst("root")
            while (queue.isNotEmpty()) {
                val monkeyName = queue.removeFirst()
                val monkey = monkeys[monkeyName]!!
                val subMonkeys = monkey.subMonkeys
                if (subMonkeys.isEmpty()) {
                    cache[monkeyName] = monkey.operation()
                } else if (subMonkeys.all { it in cache }) {
                    cache[monkeyName] = monkey.operation(cache[subMonkeys.first()]!!, cache[subMonkeys.last()]!!)
                } else {
                    subMonkeys.filter { it !in cache }.forEach { queue.addFirst(it) }
                    queue.addLast(monkeyName)
                }

            }

            cache["root"]!!
        }

    fun part2(input: List<String>): Num = parseMonkeys(input).associateBy(Monkey::name)
        .let { monkeys ->
            val cache = mutableMapOf<String, List<Operation>>()


            fun findOperation(monkey: String): List<Operation> {
                val queue = ArrayDeque<String>()
                queue.addFirst(monkey)
                while (queue.isNotEmpty()) {
                    val monkeyName = queue.removeFirst()
                    val current = monkeys[monkeyName]!!
                    val subMonkeys = current.subMonkeys

                    if (subMonkeys.isEmpty()) {
                        cache[monkeyName] = listOf(constantOperation(current.operation()))
                    } else if (subMonkeys.all { it in cache }) {
                        val leftOperations = cache[subMonkeys.first()]!!
                        val rightOperations = cache[subMonkeys.last()]!!

                        if (leftOperations.isConstant() && rightOperations.isConstant()) {
                            cache[monkeyName] = listOf(constantOperation(current.operation(leftOperations.toConstant(), rightOperations.toConstant())))
                        } else if (leftOperations.isConstant()) {
                            cache[monkeyName] = rightOperations + Operation(current.operation.name) { _, r ->
                                current.operation.rightInv(leftOperations.toConstant(), r)
                            }

                        } else if (rightOperations.isConstant()) {
                            cache[monkeyName] = leftOperations + Operation(current.operation.name) { l, _ ->
                                current.operation.leftInv(l, rightOperations.toConstant())
                            }
                        } else {
                            throw IllegalStateException()
                        }
                    } else if (subMonkeys.any { it == "humn" } && subMonkeys.find { it != "humn" } in cache) {
                        val (left, right) = subMonkeys
                        if (left == "humn") {
                            val rightOperations = cache[right]!!
                            cache[monkeyName] = listOf(Operation(current.operation.name) { l, _ ->
                                current.operation.leftInv(l, rightOperations.toConstant())
                            })

                        } else {
                            val leftOperations = cache[left]!!
                            cache[monkeyName] = listOf(Operation(current.operation.name) { _, r ->
                                current.operation.rightInv(leftOperations.toConstant(), r)
                            })
                        }

                    } else {
                        subMonkeys.filter { it !in cache }.filter { it != "humn" }.forEach { queue.addFirst(it) }
                        queue.addLast(monkeyName)
                    }
                }

                return cache[monkey]!!
            }
            val (leftMonkey, rightMonkey) = monkeys["root"]!!.subMonkeys
            val leftOperation = findOperation(leftMonkey)
            val rightOperation = findOperation(rightMonkey)

            val (function, constant) =
                if (leftOperation.isConstant()) rightOperation to leftOperation.toConstant()
                else leftOperation to rightOperation.toConstant()

            return function.foldRight(constant) { operation, acc -> operation(acc) }
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")
    check("Part 1", 152) { part1(testInput) }
    check("Part 1", 324122188240430) { part1(input) }
    check("Part 2", 301) { part2(testInput) }
    check("Part 2", 3412650897405) { part2(input) }
}
