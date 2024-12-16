import java.util.*

fun main() {
    data class Input(val map: MutableMap<Point, Char>, val start: Point, val end: Point)
    data class Reindeer(
        val pos: Point,
        val dir: Direction,
    )

    data class ReindeerCost(
        val reindeer: Reindeer,
        val cost: Int,
    )

    data class ReindeerCostPath(
        val reindeer: Reindeer,
        val cost: Int,
        val previousPath: List<Reindeer>,
    )

    fun parseMap(input: List<String>): Input {
        val map = mutableMapOf<Point, Char>()
        var start = Point(0, 0)
        var end = Point(0, 0)
        for (y in input.indices) {
            val line = input[y]
            if (line.isBlank()) break
            for (x in line.indices) {
                when {
                    line[x] == '.' -> {
                        //nothing
                    }

                    line[x] == '#' -> {
                        map[Point(x, y)] = '#'
                    }

                    line[x] == 'S' -> {
                        start = Point(x, y)
                    }

                    line[x] == 'E' -> {
                        end = Point(x, y)
                    }
                }
            }
        }

        return Input(map, start, end)
    }

    fun part1(input: List<String>): Int {
        val (map, start, end) = parseMap(input)
        val visited = mutableSetOf<Reindeer>()
        val queue = PriorityQueue<ReindeerCost>(compareBy { it.cost })

        // Start facing East (0)
        queue.offer(ReindeerCost(Reindeer(start, Direction.E), 0))

        while (queue.isNotEmpty()) {
            val (state, cost) = queue.remove()
            visited += state

            // Check if reached end
            if (state.pos == end) {
                return cost
            }

            // list all possible next moves
            val nextMoves = listOf(
                ReindeerCost(Reindeer(state.pos, state.dir.left()), cost + 1000),
                ReindeerCost(Reindeer(state.pos, state.dir.right()), cost + 1000),
                ReindeerCost(Reindeer(state.pos + state.dir, state.dir), cost + 1),
            ).filter { it.reindeer !in visited && map[it.reindeer.pos] != '#' }

            queue += nextMoves
        }

        return -1
    }

    fun part2(input: List<String>): Int {
        val (map, start, end) = parseMap(input)

        val visited = mutableSetOf<Reindeer>()
        val queue = PriorityQueue<ReindeerCostPath>(compareBy { it.cost })
        queue.add(ReindeerCostPath(Reindeer(start, Direction.E), 0, emptyList()))

        val bestPathSpots = mutableSetOf(end)
        var bestCost = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            val (state, cost, previousPath) = queue.remove()
            visited += state

            if (state.pos == end) {
                if (cost <= bestCost) {
                    bestCost = cost
                    bestPathSpots += previousPath.map { it.pos }
                } else {
                    break
                }
            }

            val nextMoves = listOf(
                ReindeerCostPath(Reindeer(state.pos, state.dir.left()), cost + 1000, previousPath + state),
                ReindeerCostPath(Reindeer(state.pos, state.dir.right()), cost + 1000, previousPath + state),
                ReindeerCostPath(Reindeer(state.pos + state.dir, state.dir), cost + 1, previousPath + state),
            ).filter { it.reindeer !in visited && map[it.reindeer.pos] != '#' }

            queue += nextMoves
        }


        return bestPathSpots.size
    }

    val day = 16

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 7036)
    checkSolution("Part1 [Full]", part1(input), 90460)
    checkSolution("Part2 [Test]", part2(testInput), 45)
    checkSolution("Part2 [Full]", part2(input), 575)
}
