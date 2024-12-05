fun main() {

    fun isGraduallyIncreasing(levels: List<Int>, maxChange: Int = 3) =
        levels.windowed(2).all { (a, b) -> (b - a) in (1..maxChange) }

    fun isGraduallyDecreasing(levels: List<Int>, maxChange: Int = 3) =
        levels.windowed(2).all { (a, b) -> (a - b) in (1..maxChange) }


    fun isReportSafe(levels: List<Int>) = !(!isGraduallyIncreasing(levels) && !isGraduallyDecreasing(levels))

    fun isReportSafe2(levels: List<Int>): Boolean {
        // First, check if the report is already safe
        if (isGraduallyIncreasing(levels) || isGraduallyDecreasing(levels)) {
            return true
        }

        // Try removing each level and check if the resulting list is safe
        for (i in levels.indices) {
            val modifiedLevels = levels.take(i) + levels.drop(i+1)
            if (isGraduallyIncreasing(modifiedLevels) || isGraduallyDecreasing(modifiedLevels)) {
                return true
            }
        }

        return false
    }

    fun part1(input: List<String>): Int {
        // Read input from standard input
        val reports = input
            .map { it.split(" ").map { num -> num.toInt() } }
            .toList()

        // Count safe reports
        val safeReportCount = reports.count { isReportSafe(it) }
        return safeReportCount
    }

    fun part2(input: List<String>): Int {
        val reports = input
            .map { it.split(" ").map { num -> num.toInt() } }
            .toList()

        // Count safe reports
        val safeReportCount = reports.count { isReportSafe2(it) }
        return safeReportCount
    }

    // Or read a large test input from the file:
    val testInput = readInput("day02_test")
    check(part1(testInput) == 2)

    val testInput2 = readInput("day02_test")
    check(part2(testInput2) == 4)

    // Read the input from the file.
    val input = readInput("day02")
    val result1 = part1(input)
    println("Part1: $result1")
    check(result1 == 306)

    val result2 = part2(input)
    println("Part2 : $result2")
    check(result2 == 366)
}
