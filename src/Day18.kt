import java.util.*

fun main() {
    fun parseMemoryList(input: List<String>): List<Point> {
        return input.map { val (x, y) = it.split(","); Point(x.toInt(), y.toInt()) }
    }

    fun placeMemory(grid: Grid<Char>, memory: List<Point>, numBlocks: Int) {
        for (i in 0 until numBlocks) {
            grid[memory[i]] = '#'
        }
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
                if (neighbor.x in 0..end.x && neighbor.y in 0..end.y
                    && neighbor !in visited && grid[neighbor] != '#'
                ) {
                    visited.add(neighbor)
                    queue.add(path + neighbor)
                }
            }
        }

        // No path found
        return null
    }

    fun part1(input: List<String>, memoryBlocksFallen: Int, size: Int): Long {
        val memory = parseMemoryList(input)
        val grid = mutableMapOf<Point, Char>()

        placeMemory(grid, memory, memoryBlocksFallen)
        val path = findShortestPath(grid, Point(0, 0), Point(size - 1, size - 1))

        return if (path == null) {
            -1L
        } else {
            path.size.toLong() - 1
        }
    }

    fun part2(input: List<String>, size: Int): String {
        val memory = parseMemoryList(input)
        for (i in 0 until memory.size) {
            println("================= $i =================")
            val grid = mutableMapOf<Point, Char>()
            placeMemory(grid, memory, i + 1)

            val path = findShortestPath(grid, Point(0, 0), Point(size - 1, size - 1))
            path?.forEach { grid[it] = 'O' }
            grid[memory[i]] = '@'

            //grid.debug('.', size-1, size-1)
            if (path == null) {
                // blocked
                return "${memory[i].x},${memory[i].y}"
            }
        }

        return "--"
    }

    val day = 18

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput, 12, 7), 22L)
    checkSolution("Part1 [Full]", part1(input, 1024, 71), 380L)
    checkSolution("Part2 [Test]", part2(testInput, 7), "6,1")
    checkSolution("Part2 [Full]", part2(input, 71), "26,50")
}
