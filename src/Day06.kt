fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val day = 6

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 0)
    checkSolution("Part1", part1(input), 0)
    checkSolution("Part2", part2(testInput), 0)
    checkSolution("Part2", part2(input), 0)
}
