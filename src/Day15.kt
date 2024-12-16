fun main() {
    fun Point.boxGps() = 100 * y + x

    fun printMap(map: Grid<Char>, robot: Point) {
        for (y in 0..map.keys.maxOf { it.y }) {
            for (x in 0..map.keys.maxOf { it.x }) {
                if (robot.x == x && robot.y == y) {
                    print('@')
                } else {
                    print(map[Point(x, y)] ?: ' ')
                }
            }
            println()
        }
    }

    fun parseMap(input: List<String>, big: Boolean = false): Grid<Char> {
        val map: Grid<Char> = mutableMapOf()

        for (y in input.indices) {
            val line = input[y]
            if (line.isBlank()) break
            for (x in line.indices) {
                if (line[x] == '.') continue
                if (big) {
                    when {
                        line[x] == '#' -> {
                            map[Point(x * 2, y)] = '#'
                            map[Point((x * 2) + 1, y)] = '#'
                        }

                        line[x] == 'O' -> {
                            map[Point((x * 2), y)] = '['
                            map[Point((x * 2) + 1, y)] = ']'
                        }

                        line[x] == '@' -> {
                            map[Point((x * 2), y)] = '@'
                        }
                    }
                } else {
                    map[Point(x, y)] = line[x]
                }
            }
        }

        return map
    }

    fun parseDirections(input: List<String>): List<List<Direction>> {
        val allDirections = mutableListOf<List<Direction>>()
        var found = false
        for (y in input.indices) {
            val line = input[y]
            if (line.isBlank()) {
                found = true
                continue
            }

            if (found) {
                allDirections.add(line.toCharArray().toList().map {
                    when (it) {
                        '^' -> Direction.N
                        'v' -> Direction.S
                        '>' -> Direction.E
                        '<' -> Direction.W
                        else -> throw IllegalArgumentException("Invalid direction: $it")
                    }
                })
            }
        }
        return allDirections
    }

    fun parseStuff(input: List<String>, big: Boolean = false): Pair<Grid<Char>, List<List<Direction>>> {
        return parseMap(input, big) to parseDirections(input)
    }

    fun recursivePush(map: Grid<Char>, start: Point, direction: Direction): Boolean {
        val next = start + direction
        return when {
            map[next] == null -> {
                // move this one in direction that return true
                map.remove(start)
                map[next] = 'O'
                true
            }

            map[next] == '#' -> {
                false
            }

            map[next] == 'O' -> {
                if (recursivePush(map, next, direction)) {
                    map.remove(start)
                    map[next] = 'O'
                    true
                } else {
                    false
                }
            }

            else -> {
                throw IllegalArgumentException("Invalid map value: ${map[next]}")
            }
        }
    }

    fun recursivePushBig(map: Grid<Char>, start: Point, direction: Direction): Boolean {
        val startWhat = map[start]
        val next = start + direction
        val nextWhat = map[next]

        if (direction == Direction.W || direction == Direction.E) {
            when (nextWhat) {
                in listOf('[', ']') -> {
                    return if (recursivePushBig(map, next, direction)) {
                        map.remove(start)
                        if (startWhat != null) map[next] = startWhat
                        true
                    } else {
                        false
                    }
                }

                '#' -> {
                    return false
                }

                null -> {
                    map.remove(start)
                    if (startWhat != null) map[next] = startWhat
                    return true
                }
            }
        } else {
            when (nextWhat) {
                '[', ']' -> {
                    val (left, right) = if (nextWhat == '[') {
                        Pair(next, next + Direction.E)
                    } else {
                        Pair(next + Direction.W, next)
                    }

                    val leftOk = recursivePushBig(map, left, direction)
                    val rightOk = recursivePushBig(map, right, direction)

                    return if (leftOk && rightOk) {
                        map.remove(left)
                        map.remove(right)
                        map[left + direction] = '['
                        map[right + direction] = ']'
                        true
                    } else {
                        false
                    }
                }

                '#' -> {
                    return false
                }

                null -> {
                    return true
                }
            }
        }

        throw IllegalArgumentException("Invalid map value: ${map[next]}")
    }

    fun part1(input: List<String>): Long {
        val (map, directions) = parseStuff(input)
        var robot = map.filterValues { it == '@' }.keys.first()
        map.remove(robot)

        //printMap(map, robot)

        for (directionLine in directions) {
            for (direction in directionLine) {
                val next = robot + direction
                val what = map[next]
                //println("--> Direction: $direction")
                when (what) {
                    '#' -> { /* wall - do nothing */
                    }

                    'O' -> {
                        // box - check if we can move the box in the same direction
                        if (recursivePush(map, next, direction)) {
                            robot += direction
                        }
                    }

                    null -> {
                        // move robot to new spot
                        robot += direction
                    }

                    else -> {
                        println("What unhandled: $what")
                    }
                }
                //printMap(map, robot)
            }
        }

        return map.filterValues { it == 'O' }.keys.sumOf { it.boxGps() }.toLong()

    }

    fun part2(input: List<String>): Long {
        var (map, directions) = parseStuff(input, true)
        var robot = map.filterValues { it == '@' }.keys.first()
        map.remove(robot)

        for (directionLine in directions) {
            for (direction in directionLine) {
                val next = robot + direction
                val what = map[next]
                //println("--> Direction: $direction")
                when (what) {
                    '#' -> { /* wall - do nothing */
                    }

                    '[', ']' -> {
                        // clone map
                        val clone = map.toMutableMap()
                        if (recursivePushBig(clone, robot, direction)) {
                            map = clone
                            robot += direction
                        }
                    }

                    null -> {
                        // move robot to new spot
                        robot += direction
                    }

                    else -> {
                        println("What unhandled: $what")
                    }
                }
                //printMap(map, robot)
            }
        }

        //printMap(map, robot)
        return map.filterValues { it == '[' }.keys.sumOf { it.boxGps() }.toLong()
    }

    val day = 15

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val testInputSmall = readInput("day%02d_test_small".format(day))
    val testInputSmall2 = readInput("day%02d_test_small2".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Small]", part1(testInputSmall), 2028L)
    checkSolution("Part1 [Test]", part1(testInput), 10092L)
    checkSolution("Part1 [Full]", part1(input), 1414416L)
    checkSolution("Part2 [Small]", part2(testInputSmall2), 618L)
    checkSolution("Part2 [Test]", part2(testInput), 9021L)
    checkSolution("Part2 [Full]", part2(input), 1386070L)
}
