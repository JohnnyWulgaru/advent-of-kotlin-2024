data class Line(val start: Point, val end: Point)

data class Area(
    var id: Char,
    var plots: Set<Point>
)

fun Area.isInside(point: Point): Boolean {
    return plots.contains(point)
}

fun Area.area() = plots.size.toLong()

fun Area.outline() = plots.sumOf {
    // find the neighbors of the point and count how many of them are inside the plot
    val neighbors = listOf(Point(it.x + 1, it.y), Point(it.x - 1, it.y), Point(it.x, it.y + 1), Point(it.x, it.y - 1))
    4L - neighbors.count { plot -> plot in plots }
}

fun Area.sides(): Int {
    val s = mutableSetOf<Pair<Point, Point>>()

    for (plot in plots) {
        val directions = listOf(Point(-1, 0), Point(0, 1), Point(1, 0), Point(0, -1))

        for (d in directions) {
            // Check if the opposite (90 degree) neighbor is not in the set
            if (Point(plot.x + d.y, plot.y + d.x) in plots) {
                continue
            }

            // set the starting point to walk this direction
            var current = plot

            // Walk along the direction while conditions are met
            while (true) {
                val nextOnCheck = Point(current.x + d.y, current.y + d.x) // 90 degree check
                val nextOnWalk = current + d
                if (nextOnWalk !in plots) break
                if (nextOnCheck in plots) break
                current = nextOnWalk
            }

            s.add(current to d)
        }
    }

    return s.size
}

fun Area.debug() = "Area(id=$id, area=${area()}, outline=${outline()} sides=${sides()})"

fun MutableMap<Point, Char>.at(point: Point): Char? = this[point]

fun main() {
    fun parseMap(input: List<String>): MutableMap<Point, Char> {
        val map = mutableMapOf<Point, Char>()

        for (y in input.indices) {
            val line = input[y]
            for (x in 0 until input[y].length) {
                map[Point(x, y)] = line[x]
            }
        }

        return map
    }

    fun createArea(map: MutableMap<Point, Char>, start: Point, id: Char): Area {
        // flood fill map with same character from start point to find outline
        val points = mutableSetOf<Point>()
        val visited = mutableSetOf<Point>()
        val queue = ArrayDeque<Point>()
        queue.add(start)

        while (queue.isNotEmpty()) {
            val entry = queue.size

            val current = queue.removeFirst()
            points.add(current)

            // check all neighbors
            for (direction in listOf(Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1))) {
                val next = current + direction
                if (next in visited) continue

                val at = map.at(next)
                if (at == null || at != id) continue
                if (next !in points) {
                    queue.add(next)
                    visited.add(next)
                }
            }

            //println("Queue: $entry / ${queue.size}")
        }

        return Area(id = id, plots = points)
    }

    fun part1(input: List<String>): Long {
        val map = parseMap(input)
        val areas = mutableListOf<Area>()

        map.forEach { (k, v) ->
            if (areas.none { it.isInside(k) }) {
                val area = createArea(map, k, v)
                areas.add(area)
            }
        }

        return areas.sumOf {
            it.area() * it.outline()
        }
    }

    fun part2(input: List<String>): Long {
        val map = parseMap(input)
        val areas = mutableListOf<Area>()

        map.forEach { (k, v) ->
            if (areas.none { it.isInside(k) }) {
                val area = createArea(map, k, v)
                areas.add(area)
            }
        }

        return areas.sumOf {
            it.area() * it.sides()
        }
    }

    val day = 12

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val testInputSmall = readInput("day%02d_test_small".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 (Test) ", part1(testInput), 1930L)
    checkSolution("Part1        ", part1(input), 1374934L)
    checkSolution("Part2 (TestS)", part2(testInputSmall), 236L)
    checkSolution("Part2 (Test) ", part2(testInput), 1206L)
    checkSolution("Part2        ", part2(input), 841078L)
}
