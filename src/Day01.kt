import kotlin.math.abs

fun main() {
    fun parseLists(input: String): Pair<MutableList<Int>, MutableList<Int>> {
        val lines = input.lines()
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        for (line in lines) {
            val (leftNum, rightNum) = line.split(" ").map { it.trim() }.filter { it != "" }.map { it.toInt() }
            left.add(leftNum)
            right.add(rightNum)
        }
        return left to right
    }

    fun part1(input: List<String>): Int {
        val (left, right) = parseLists(input.joinToString("\n"))
        left.sort()
        right.sort()

        var maxDistance = 0
        for (i in 0 until left.size) {
            maxDistance += abs(left[i] - right[i])
        }
        return maxDistance
    }

    fun part2(input: List<String>): Int {
        val (left, right) = parseLists(input.joinToString("\n"))
        left.sort()
        right.sort()


        var similarity = 0
        for (i in 0 until left.size) {
            val needle = left[i]

            val count = right.count { it == needle }
            similarity += needle * count
        }

        return similarity
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
