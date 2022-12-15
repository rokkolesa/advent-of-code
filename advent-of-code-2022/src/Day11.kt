package day11

import utils.*
import java.math.BigInteger

data class Item(val moduloItems: List<Int>)
data class Monkey(
    val id: Int,
    val startingItems: MutableList<Int>,
    val operation: (Int) -> Int,
    val modulo: Int,
    val throwTo: (Boolean) -> Int,
    var inspections: BigInteger = BigInteger.ZERO,
    var items: MutableList<Item>? = null,
) {
    fun throwItem(monkeys: List<Monkey>, item: Item) {
        val newWorries = item.moduloItems.mapIndexed { index, it ->
            operation(it).mod(monkeys[index].modulo)
        }
        val throwTo = throwTo(newWorries[id] == 0)
        monkeys[throwTo].items!!.add(Item(newWorries))
    }

    fun throwItem(monkeys: List<Monkey>, item: Int, divideWorry: Int) {
        val newWorry = operation(item) / divideWorry
        val throwTo = throwTo(newWorry.mod(modulo) == 0)
        monkeys[throwTo].startingItems.add(newWorry)
    }
}

val operationRegex = """(.*) ([*+]) (.*)""".toRegex()

fun main() {
    fun parseMonkey(input: List<String>): Monkey {
        val id = input[0].substringAfter(" ").substringBefore(":").toInt()
        val items = input[1].substringAfter(": ").split(", ").map(String::toInt).toMutableList()

        val (left, operand, right) = operationRegex
            .matchEntire(input[2].substringAfter("= "))
            ?.destructured
            ?: throw IllegalArgumentException("Incorrect operation definition!")
        val leftFactor = when (left) {
            "old" -> null
            else -> left.toInt()
        }
        val rightFactor = when (right) {
            "old" -> null
            else -> right.toInt()
        }

        val operation = when (operand) {
            "*" -> { it: Int -> (leftFactor ?: it) * (rightFactor ?: it) }
            "+" -> { it: Int -> (leftFactor ?: it) + (rightFactor ?: it) }
            else -> throw IllegalArgumentException("Incorrect operation definition!")
        }

        val div = input[3].substringAfterLast(" ").toInt()
        val trueBranch = input[4].substringAfterLast(" ").toInt()
        val falseBranch = input[5].substringAfterLast(" ").toInt()
        val throwTo = { it: Boolean -> if (it) trueBranch else falseBranch }

        return Monkey(id, items, operation, div, throwTo)
    }

    fun simulate(monkeys: List<Monkey>, rounds: Int, divideWorry: Int? = null) {
        for (i in 1..rounds) {
            monkeys.forEach { monkey ->
                if (divideWorry != null) {
                    with(monkey.startingItems.iterator()) {
                        forEach { item ->
                            monkey.inspections++
                            monkey.throwItem(monkeys, item, divideWorry)
                            remove()
                        }
                    }
                } else {
                    with(monkey.items!!.iterator()) {
                        forEach { item ->
                            monkey.inspections++
                            monkey.throwItem(monkeys, item)
                            remove()
                        }
                    }
                }
            }
        }
    }

    fun initItems(monkeys: List<Monkey>) {
        val allModulos = monkeys.map(Monkey::modulo)
        monkeys.forEach {
            it.items = it.startingItems
                .map { item -> Item(allModulos.map(item::mod)) }
                .toMutableList()
        }
    }

    fun runSimulation(input: List<String>, rounds: Int, divideWorry: Int? = null): BigInteger = input
        .split(String::isBlank)
        .map(::parseMonkey)
        .also(::initItems)
        .sortedBy(Monkey::id)
        .also { simulate(it, rounds, divideWorry) }
        .map(Monkey::inspections)
        .sorted()
        .reversed()
        .take(2)
        .reduce { acc, i -> acc * i }

    fun part1(input: List<String>): BigInteger = runSimulation(input, 20, 3)

    fun part2(input: List<String>): BigInteger = runSimulation(input, 10_000)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check("Part 1", "10605".toBigInteger()) { part1(testInput) }
    check("Part 2", "2713310158".toBigInteger()) { part2(testInput) }

    val input = readInput("Day11")
    simulate("Part 1") { part1(input) }
    simulate("Part 2") { part2(input) }
}
