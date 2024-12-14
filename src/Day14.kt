val testSize = Point(11, 7)
val realSize = Point(101, 103)

data class Robot(val id: Int, var p: Point, val v: Point)

fun Robot.move(size: Point) {
    var x = (p.x + v.x)
    var y = (p.y + v.y)

    if (x < 0) x += size.x
    if (x >= size.x) x -= size.x
    if (y < 0) y += size.y
    if (y >= size.y) y -= size.y

    p = Point(x, y)
}

val robotRegex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()

fun main() {
    fun parseRobots(input: List<String>): List<Robot> = buildList {
        for (i in input.indices) {
            val line = input[i]
            val (pX, pY, vX, vY) = robotRegex.matchEntire(line)!!.destructured
            add(Robot(i, Point(pX.toInt(), pY.toInt()), Point(vX.toInt(), vY.toInt())))
        }
    }

    fun simRobots(robots: List<Robot>, steps: Int, size: Point): List<Robot> {
        return robots.map {
            var (x, y) = it.p
            for (i in 1..steps) {
                x += it.v.x
                y += it.v.y

                if (x < 0) x += size.x
                if (x >= size.x) x -= size.x
                if (y < 0) y += size.y
                if (y >= size.y) y -= size.y
            }
            Robot(it.id, Point(x, y), it.v)
        }
    }

    fun printRobots(size: Point, endRobots: List<Robot>) {
        for (y in 0..size.y) {
            for (x in 0..size.x) {
                val robot = endRobots.count { it.p == Point(x, y) }
                if (robot > 0) {
                    print(robot)
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    fun countQuadrants(robots: List<Robot>, size: Point): Int {
        val q1x = 0..<size.x / 2
        val q1y = 0..<size.y / 2

        val q2x = q1x.last + 2..<size.x
        val q2y = q1y.last + 2..<size.y

        val q1 = robots.count { it.p.x in q1x && it.p.y in q1y }
        val q2 = robots.count { it.p.x in q2x && it.p.y in q1y }
        val q3 = robots.count { it.p.x in q1x && it.p.y in q2y }
        val q4 = robots.count { it.p.x in q2x && it.p.y in q2y }

        return q1 * q2 * q3 * q4
    }

    /*
     * Can't resuse my code from part 1 since we need to do step by step, not robot by robot.
     */
    fun simRobotsPart2(robots: List<Robot>, size: Point): Long {
        var steps = 1L
        while (true) {
            robots.forEach { it.move(size) }
            val positionMap = robots.groupBy { it.p }
            if (positionMap.values.all { it.size == 1 }) {
                return steps
            }
            steps++
        }
    }

    fun part1(input: List<String>, test: Boolean = false): Long {
        val size = if (test) testSize else realSize
        val robots = parseRobots(input)
        val endPositions = simRobots(robots, 100, size)
        val results = countQuadrants(endPositions, size)
        return results.toLong()
    }

    fun part2(input: List<String>, test: Boolean = false): Long {
        val size = if (test) testSize else realSize
        val robots = parseRobots(input)
        val steps = simRobotsPart2(robots, size)
        return steps
    }

    val day = 14

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput, true), 12L)
    checkSolution("Part1 [Full]", part1(input), 233709840L)
    checkSolution("Part2 [Full]", part2(input), 6620L)
}
