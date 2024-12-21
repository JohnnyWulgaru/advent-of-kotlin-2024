fun main() {
    /*
     +---+---+---+
     | 7 | 8 | 9 |
     +---+---+---+
     | 4 | 5 | 6 |
     +---+---+---+
     | 1 | 2 | 3 |
     +---+---+---+
         | 0 | A |
         +---+---+
      */
    val numPad = mutableMapOf<Char, Point>().apply {
        put('7', Point(0, 0))
        put('8', Point(1, 0))
        put('9', Point(2, 0))
        put('4', Point(0, 1))
        put('5', Point(1, 1))
        put('6', Point(2, 1))
        put('1', Point(0, 2))
        put('2', Point(1, 2))
        put('3', Point(2, 2))
        put('X', Point(0, 3))
        put('0', Point(1, 3))
        put('A', Point(2, 3))
    }

    /*
        +---+---+
        | ^ | A |
    +---+---+---+
    | < | v | > |
    +---+---+---+
     */
    val dirPad = mutableMapOf<Char, Point>().apply {
        put('X', Point(0, 0))
        put('^', Point(1, 0))
        put('A', Point(2, 0))
        put('<', Point(0, 1))
        put('v', Point(1, 1))
        put('>', Point(2, 1))
    }


    // Directions for BFS traversal
    val directions = mapOf(
        "^" to Point(0, -1),
        ">" to Point(1, 0),
        "v" to Point(0, 1),
        "<" to Point(-1, 0)
    )

    data class State(val p: Point, val path: String)

    fun findAllNextMoves(pad: Map<Char, Point>, start: Char, end: Char): List<String> {
        if (start == end) return listOf("A")

        val queue = ArrayDeque<State>()
        val distances = mutableMapOf<String, Int>()
        val allPaths = mutableListOf<String>()

        val endPos = pad[end]!!
        val avoidPos = pad['X']!!

        queue.add(State(pad[start]!!, ""))

        while (queue.isNotEmpty()) {
            val (currentPos, currentPath) = queue.removeFirst()

            if (currentPos == endPos) {
                allPaths.add(currentPath + "A")
            }

            directions.forEach { (directionChar, direction) ->
                val position = currentPos + direction

                // skip invalid
                if (position == avoidPos) return@forEach

                val button = pad.values.find { it.x == position.x && it.y == position.y }
                if (button != null) {
                    val newPath = currentPath + directionChar
                    val posKey = "${position.x},${position.y}"

                    // only add shorted paths to queue
                    if (distances[posKey] == null || distances[posKey]!! >= newPath.length) {
                        queue.add(State(position, newPath))
                        distances[posKey] = newPath.length
                    }
                }
            }
        }

        // Sort from smallest to largest paths
        return allPaths.sortedBy { it.length }
    }

    // Find the smallest amount of button presses
    fun numKeyPressesForCode(
        input: Map<Char, Point>,
        code: String,
        robotLayers: Int,
        cache: MutableMap<String, Long>
    ): Long {
        val key = "$code,$robotLayers"
        cache[key]?.let { return it }

        var current = 'A'
        var length = 0L

        for (c in code) {
            val moves = findAllNextMoves(input, current, c)
            length += if (robotLayers == 0) {
                moves[0].length.toLong()
            } else {
                moves.minOf { move -> numKeyPressesForCode(dirPad, move, robotLayers - 1, cache) }
            }
            current = c
        }

        cache[key] = length
        return length
    }

    val cache = mutableMapOf<String, Long>()

    fun part1(input: List<String>): Long {
        return input.sumOf { code ->
            val numerical = code.filter { it.isDigit() }.toLong()
            numerical * numKeyPressesForCode(numPad, code, 2, cache)
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { code ->
            val numerical = code.filter { it.isDigit() }.toInt()
            numerical * numKeyPressesForCode(numPad, code, 25, cache)
        }
    }

    val day = 21

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 126384L)
    checkSolution("Part1 [Full]", part1(input), 184716L)
    //checkSolution("Part2 [Test]", part2(testInput), 0L)
    checkSolution("Part2 [Full]", part2(input), 229403562787554L)
}
