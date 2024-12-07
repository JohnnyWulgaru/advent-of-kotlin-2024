enum class Operator {
    ADD, MULTIPLY, CONCAT
}

val OPERATORS = listOf(Operator.ADD, Operator.MULTIPLY)
val OPERATORS2 = listOf(Operator.ADD, Operator.MULTIPLY, Operator.CONCAT)

fun main() {

    fun evaluate(numbers: List<Long>, operators: List<Operator>): Long =
        numbers.drop(1).foldIndexed(numbers.first()) { index, acc, number ->
            when (operators[index]) {
                Operator.ADD -> acc + number
                Operator.MULTIPLY -> acc * number
                Operator.CONCAT -> "$acc$number".toLong()
            }
        }

    fun isLineSolvable(testValue: Long, numbers: List<Long>, operators: List<Operator>): Boolean {
        val possOps = permutations(operators, numbers.size - 1)
        for (o in possOps) {
            if (evaluate(numbers, o) == testValue) {
                return true
            }
        }
        return false
    }

    fun part1(input: List<String>): Long {
        var totalSum = 0L
        for (line in input) {
            val parts = line.split(": ")
            val testValue = parts[0].toLong()
            val numbers = parts[1].split(" ").map { it.toLong() }

            if (isLineSolvable(testValue, numbers, OPERATORS)) {
                totalSum += testValue
            }
        }
        return totalSum
    }

    fun part2(input: List<String>): Long {
        var totalSum = 0L
        for (line in input) {
            val parts = line.split(": ")
            val testValue = parts[0].toLong()
            val numbers = parts[1].split(" ").map { it.toLong() }

            if (isLineSolvable(testValue, numbers, OPERATORS2)) {
                totalSum += testValue
            }
        }
        return totalSum
    }

    val day = 7

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 3749)
    checkSolution("Part1", part1(input), 21572148763543)
    checkSolution("Part2", part2(testInput), 11387)
    checkSolution("Part2", part2(input), 581941094529163)
}
