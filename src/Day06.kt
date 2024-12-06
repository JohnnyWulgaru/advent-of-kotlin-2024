fun main() {
    var maxX = 0
    var maxY = 0

    data class PointDir(
        var x: Int,
        var y: Int,
        var direction: Point,
    )

    data class Guard(
        var x: Int,
        var y: Int,
        var direction: Point = Point(0, -1),
    )

    fun Guard.toPointDir() = PointDir(x, y, direction)
    fun Guard.nextStep(): Point = Point(x + direction.x, y + direction.y)
    fun Guard.moveToPosition(point: Point) {
        x = point.x
        y = point.y
    }

    fun Guard.turnRight() {
        direction = when (direction) {
            Point(0, -1) -> Point(1, 0)   // Up -> Right
            Point(1, 0) -> Point(0, 1)   // Right -> Down
            Point(0, 1) -> Point(-1, 0)  // Down -> Left
            Point(-1, 0) -> Point(0, -1)  // Left -> Up
            else -> direction  // Fallback case, though shouldn't occur with valid input
        }
    }

    fun Point.outOfBounds() = x < 0 || x > maxX || y < 0 || y > maxY

    fun canMove(p: Point, map: Map<Point, Char>): Boolean {
        val cell = map[p] ?: return true
        return cell != '#'
    }

    fun doWalk(
        initMap: MutableMap<Point, Char>,
        guard: Guard,
        detectLoops: Boolean = false
    ): MutableSet<PointDir>? {
        val map = initMap.toMutableMap()
        val visits = mutableSetOf<PointDir>().apply {
            add(guard.toPointDir())
        }

        while (true) {
            val nextStep = guard.nextStep()
            if (nextStep.outOfBounds()) {
                break
            }

            if (canMove(nextStep, map)) {
                guard.moveToPosition(nextStep)

                val currentPointDir = guard.toPointDir()
                if (detectLoops && currentPointDir in visits) {
                    return null
                }
                visits.add(currentPointDir)
            } else {
                guard.turnRight()
            }
        }
        return visits
    }

    fun parseMap(input: List<String>, guard: Guard): MutableMap<Point, Char> {
        val initMap: MutableMap<Point, Char> = mutableMapOf()
        for (y in input.indices) {
            val line = input[y]
            for (x in line.indices) {
                when (line[x]) {
                    '^' -> {
                        guard.x = x
                        guard.y = y
                    }

                    '#' -> initMap[Point(x, y)] = line[x]
                    else -> {
                        /* dots and others are ignored */
                    }
                }
            }
        }
        maxX = initMap.keys.maxOf { it.x }
        maxY = initMap.keys.maxOf { it.y }
        return initMap
    }

    fun part1(input: List<String>): Int {
        val guard = Guard(0, 0)
        val map: MutableMap<Point, Char> = parseMap(input, guard)
        val visits = doWalk(map, guard)

        // extract just the locations since the gaurd might walk through the same spot in different directions
        return visits!!.map { it.x to it.y }.toSet().size
    }

    fun part2(input: List<String>): Int {
        val guard = Guard(0, 0)
        val map: MutableMap<Point, Char> = parseMap(input, guard)
        val visits = doWalk(map, guard.copy())

        val visitCoords = visits!!.map { it.x to it.y }.toSet()
        val guardStart = Pair(guard.x, guard.y)

        // go through all obstructions without the guard's starting position
        return (visitCoords - guardStart).count { (x, y) ->
            // clone map and add our obstruction
            val testMap = map.toMutableMap().also {
                it[Point(x, y)] = '#'
            }
            doWalk(testMap, guard.copy(), true) == null
        }
    }

    val day = 6

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 41)
    checkSolution("Part1", part1(input), 5516)
    checkSolution("Part2", part2(testInput), 6)
    checkSolution("Part2", part2(input), 2008)
}
