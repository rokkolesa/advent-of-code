package day21

import utils.*
import kotlin.IllegalStateException

typealias Num = Long
typealias Operations = List<Operation>

class Operation(private val name: String, val constant: Boolean = false, val op: (Num, Num) -> Num) {
    lateinit var leftInv: Operation
    lateinit var rightInv: Operation

    fun rightPartial(left: Num) = Operation(rightInv.name) { _, r -> rightInv(left, r) }
    fun leftPartial(right: Num) = Operation(leftInv.name) { l, _ -> leftInv(l, right) }
    fun singleton() = listOf(this)
    operator fun invoke(left: Num, right: Num): Num = op(left, right)
    operator fun invoke(coeff: Num) = invoke(coeff, coeff)
    operator fun invoke() = invoke(0, 0)

    override fun toString(): String = name.ifEmpty { invoke().toString() }
}

data class Monkey(val name: String, val operation: Operation, val subMonkeys: List<String>)

fun Operations.toConstant() = this[0]()
fun Operations.isConstant() = all(Operation::constant)
fun Num.toOperation() = Operation("", constant = true) { _, _ -> this }.apply { leftInv = this; rightInv = this }

fun main() {
    val plusOperation =
        // a + b = c
        Operation("+") { left, right -> left + right }
            .apply {
                // a = b - c
                leftInv = Operation("-") { left, right -> left - right }
                // b = c - a
                rightInv = Operation("-") { left, right -> right - left }
            }

    val minusOperation =
        // a - b = c
        Operation("-") { left, right -> left - right }
            .apply {
                // a = c + b
                leftInv = Operation("+") { left, right -> left + right }
                // b = a - c
                rightInv = Operation("-") { left, right -> left - right }
            }

    val multiplyOperation =
        // a * b = c
        Operation("*") { left, right -> left * right }
            .apply {
                // a = c / b
                leftInv = Operation("/") { left, right -> left / right }
                // b = c / a
                rightInv = Operation("/") { left, right -> right / left }
            }

    val divisionOperation =
        // a / b = c
        Operation("/") { left, right -> left / right }
            .apply {
                // a = c * b
                leftInv = Operation("*") { left, right -> left * right }
                // b = c / a
                rightInv = Operation("/") { left, right -> right / left }
            }

    fun parseMonkeys(input: List<String>): List<Monkey> = input.map {
        val (name, operationDefinition) = it.split(": ")
        val (operation, subMonkeys) = when {
            operationDefinition.contains("+") -> operationDefinition.split(" + ").let { (left, right) -> plusOperation to listOf(left, right) }
            operationDefinition.contains("-") -> operationDefinition.split(" - ").let { (left, right) -> minusOperation to listOf(left, right) }
            operationDefinition.contains("*") -> operationDefinition.split(" * ").let { (left, right) -> multiplyOperation to listOf(left, right) }
            operationDefinition.contains("/") -> operationDefinition.split(" / ").let { (left, right) -> divisionOperation to listOf(left, right) }
            else -> operationDefinition.toLong().toOperation() to listOf()
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
            val cache = mutableMapOf<String, Operations>()

            fun findOperations(monkey: String): Operations {
                val queue = ArrayDeque<String>()
                queue.addFirst(monkey)
                while (queue.isNotEmpty()) {
                    val monkeyName = queue.removeFirst()
                    if (cache[monkeyName] != null) continue

                    val current = monkeys[monkeyName]!!
                    val subMonkeys = current.subMonkeys

                    when {
                        subMonkeys.isEmpty() -> cache[monkeyName] = current.operation().toOperation().singleton()
                        subMonkeys.all { it in cache } -> {
                            cache[monkeyName] = subMonkeys.map { cache[it]!! }
                                .let { (leftOperations, rightOperations) ->
                                    when {
                                        leftOperations.isConstant() && rightOperations.isConstant() -> current.operation(leftOperations.toConstant(), rightOperations.toConstant()).toOperation().singleton()
                                        leftOperations.isConstant() -> rightOperations + current.operation.rightPartial(leftOperations.toConstant())
                                        rightOperations.isConstant() -> leftOperations + current.operation.leftPartial(rightOperations.toConstant())
                                        else -> throw IllegalStateException()
                                    }
                                }
                        }

                        subMonkeys.any { it == "humn" } && subMonkeys.find { it != "humn" } in cache -> {
                            cache[monkeyName] = subMonkeys
                                .let { (left, right) ->
                                    when (left) {
                                        "humn" -> current.operation.leftPartial(cache[right]!!.toConstant()).singleton()
                                        else -> current.operation.rightPartial(cache[left]!!.toConstant()).singleton()
                                    }
                                }
                        }

                        else -> {
                            subMonkeys.filter { it !in cache }.filter { it != "humn" }.forEach { queue.addFirst(it) }
                            queue.addLast(monkeyName)
                        }
                    }
                }

                return cache[monkey]!!
            }

            monkeys["root"]!!.subMonkeys.map(::findOperations)
                .let { (leftOperation, rightOperation) ->
                    when {
                        leftOperation.isConstant() -> rightOperation to leftOperation.toConstant()
                        rightOperation.isConstant() -> leftOperation to rightOperation.toConstant()
                        else -> throw IllegalStateException()
                    }
                }
                .let { (operations, constant) -> operations.foldRight(constant) { operation, acc -> operation(acc) } }
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")
    check("Part 1", 152) { part1(testInput) }
    check("Part 1", 324122188240430) { part1(input) }
    check("Part 2", 301) { part2(testInput) }
    check("Part 2", 3412650897405) { part2(input) }
}
