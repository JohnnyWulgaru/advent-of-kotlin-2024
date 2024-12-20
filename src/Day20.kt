import java.util.*
import kotlin.math.abs

fun main() {
    data class Input20(
        val grid: Grid<Char>,
        val start: Point,
        val end: Point,
    )

    fun parseGrid(input: List<String>): Input20 {
        val grid = mutableMapOf<Point, Char>()
        var start: Point? = null
        var end: Point? = null

        for (y in input.indices) {
            for (x in input[y].indices) {
                val char = input[y][x]
                when (char) {
                    '#' -> grid[Point(x, y)] = char
                    'S' -> {
                        start = Point(x, y)
                    }

                    'E' -> {
                        end = Point(x, y)
                    }
                }
            }
        }

        require(start != null) { "Start point ('S') is missing in the input." }
        require(end != null) { "End point ('E') is missing in the input." }

        return Input20(grid, start, end)
    }

    fun findShortestPath(grid: Grid<Char>, start: Point, end: Point): List<Point>? {
        val queue = ArrayDeque<List<Point>>()
        val visited = mutableSetOf<Point>()

        queue.add(listOf(start))
        visited.add(start)

        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val current = path.last()

            // Check if we've reached the destination
            if (current == end) {
                return path
            }

            // Explore neighbors
            for (direction in Direction.entries) {
                val neighbor = current + direction
                if (neighbor !in visited && grid[neighbor] != '#'
                ) {
                    visited.add(neighbor)
                    queue.add(path + neighbor)
                }
            }
        }

        // No path found
        return null
    }

    fun part1(input: List<String>): Long {
        val (map, start, end) = parseGrid(input)
        val defaultPath = findShortestPath(map, start, end) ?: return -1L

        // add costs to the path
        val costPath = defaultPath.mapIndexed { index, point ->
            point to index
        }.toMap()

        // now we go through the path and hop over a wall
        // if the cost is higher then 102 we count it
        var count = 0L
        for (point in defaultPath) {
            for (direction in Direction.entries) {
                // skip same direction twice
                val skip = point + direction + direction
                if (map[skip] == '#') continue

                val startCost = costPath[point] ?: continue
                val skipCost = costPath[skip] ?: continue

                if (skipCost - startCost >= 102) {
                    count += 1
                }
            }
        }

        return count
    }

    fun getAllSkipPoints(map: Map<Point, Char>, point: Point, steps: Int): Set<Point> {
        val result = mutableSetOf<Point>()

        for (x in -steps..steps) {
            val distanceRemaining = steps - abs(x)
            for (y in -distanceRemaining..distanceRemaining) {
                val skip = point + Point(x, y)
                if (map[skip] == '#') continue

                result.add(skip)
            }
        }

        return result
    }

    fun manhattanDistance(point1: Point, point2: Point): Int {
        return abs(point1.x - point2.x) + abs(point1.y - point2.y)
    }


    fun part2(input: List<String>): Long {
        val (map, start, end) = parseGrid(input)
        val defaultPath = findShortestPath(map, start, end) ?: return -1L

        // add costs to the path
        val costPath = defaultPath.mapIndexed { index, point ->
            point to index
        }.toMap()

        // get all skip points and calculate the difference
        // we also need to remove the travel time
        var count = 0L
        for (point in defaultPath) {
            for (skip in getAllSkipPoints(map, point, 20)) {
                // skip same direction twice
                if (map[skip] == '#') continue

                val startCost = costPath[point] ?: continue
                val skipCost = costPath[skip] ?: continue

                // basic difference in cost on path
                var costDifference = skipCost - startCost

                // remove distance in travel time
                costDifference -= manhattanDistance(point, skip)

                if (costDifference >= 100) {
                    count += 1
                }
            }
        }

        return count
    }

    val day = 20

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 0L)
    checkSolution("Part1 [Full]", part1(input), 1363L)
    checkSolution("Part2 [Test]", part2(testInput), 0L)
    checkSolution("Part2 [Full]", part2(input), 0)
}
