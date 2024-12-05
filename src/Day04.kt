fun main() {
    data class MarkedNeedles(
        var used: Boolean = false,
        val letters: List<Point>,
    )

    val allDirections = listOf(
        Point(0, 1),
        Point(0, -1),
        Point(1, 0),
        Point(-1, 0),
        Point(1, 1),
        Point(1, -1),
        Point(-1, 1),
        Point(-1, -1)
    )

    val diagDirections = listOf(
        Point(1, 1),
        Point(1, -1),
        Point(-1, 1),
        Point(-1, -1)
    )


    fun findWordInDirection(
        inputs: List<String>,
        sx: Int,
        sy: Int,
        word: String,
        direction: Point
    ): List<Point> {
        val rows = inputs.size
        val cols = inputs[0].length
        val letters = mutableListOf<Point>()

        for (i in word.indices) {
            val x = sx + i * direction.x
            val y = sy + i * direction.y

            if (x !in 0 until rows || y !in 0 until cols) {
                return emptyList()
            }

            if (inputs[x][y] != word[i]) {
                return emptyList()
            }

            letters.add(Point(x, y))
        }

        return letters
    }

    fun findWord(input: List<String>, word: String, directions: List<Point>): List<MarkedNeedles> {
        val rows = input.size
        val cols = input[0].length
        val xmasOccurrences = mutableListOf<MarkedNeedles>()

        for (x in 0 until rows) {
            for (y in 0 until cols) {
                for (direction in directions) {
                    val occurrence = findWordInDirection(input, x, y, word, direction)
                    if (occurrence.isNotEmpty()) {
                        xmasOccurrences.add(MarkedNeedles(letters = occurrence))
                    }
                }
            }
        }

        return xmasOccurrences
    }

    fun part1(input: List<String>): Int {
        val findXmasOccurrences = findWord(input, "XMAS", allDirections)
        return findXmasOccurrences.size
    }

    fun part2(input: List<String>): Int {
        val listOfMas = findWord(input, "MAS", diagDirections)
        var overlaps = 0

        for (x in listOfMas.indices) {
            for (y in listOfMas.indices) {
                if (x == y) continue

                val first = listOfMas[x]
                val second = listOfMas[y]

                if (!first.used && !second.used && first.letters[1] == second.letters[1]) {
                    overlaps += 1
                    first.used = true
                    second.used = true
                }
            }
        }

        return overlaps
    }

    val day = 4

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    // Part 1 - test
    checkSolution("Part1", part1(testInput), 18)

    // Part 1 - real
    checkSolution("Part1", part1(input), 2633)

    // Part 2 - test
    checkSolution("Part2", part2(testInput), 9)

    // Part 2 - real
    checkSolution("Part2", part2(input), 1936)
}
