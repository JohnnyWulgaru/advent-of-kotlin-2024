import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.substringBefore(' ').toInt() }.sorted()
            .zip(input.map { it.substringAfterLast(' ').toInt() }.sorted())
            .sumOf { abs(it.second - it.first) }
    }

    fun part2(input: List<String>): Int {
        val nums2 = input.map { it.substringAfterLast(' ').toInt() }
        return input.map { it.substringBefore(' ').toInt() }.sumOf { num1 -> num1 * nums2.count { it == num1 } }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("day01_test")
    check(part1(testInput) == 11)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("day01")
    val result1 = part1(input)
    println("Part1: $result1")
    check(result1 == 1580061)

    val result2 = part2(input)
    println("Part2 : $result2")
    check(result2 == 23046913)
}
