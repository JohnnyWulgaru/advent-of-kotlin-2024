fun main() {
    fun part1(input: List<String>): Long {
        return input.size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val day = 0

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 0)
    checkSolution("Part1 [Full]", part1(input), 0)
    checkSolution("Part2 [Test]", part2(testInput), 0)
    checkSolution("Part2 [Full]", part2(input), 0)
}
