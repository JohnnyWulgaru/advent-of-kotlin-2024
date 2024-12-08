typealias Grid = MutableMap<Point, Char>

fun main() {
    var maxX = 0
    var maxY = 0
    fun parseMap(input: List<String>): Grid {
        val grid: Grid = mutableMapOf()
        for (y in input.indices) {
            val line = input[y]
            for (x in line.indices) {
                grid[Point(x, y)] = line[x]
            }
        }

        maxX = grid.keys.maxOf { it.x }
        maxY = grid.keys.maxOf { it.y }
        return grid
    }

    fun inBounds(p: Point): Boolean {
        return !(p.x < 0 || p.x > maxX || p.y < 0 || p.y > maxY)
    }

    fun Grid.draw() {
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                print(this[Point(x, y)] ?: ' ')
            }
            System.out.println()
        }
    }


    fun markResonance(antinodeGrid: Grid, antennaLocations: List<Point>) {
        for (i in antennaLocations.indices) {
            for (j in antennaLocations.indices) {
                if (i == j) continue
                val antenna1 = antennaLocations[i]
                val antenna2 = antennaLocations[j]

                val diff1 = antenna1 - antenna2
                val diff2 = antenna2 - antenna1

                val r1 = antenna1 + diff1
                val r2 = antenna2 + diff2

                if (inBounds(r1)) {
                    antinodeGrid[r1] = '#'
                }
                if (inBounds(r2)) {
                    antinodeGrid[r2] = '#'
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val grid = parseMap(input)
        val antinodeGrid = mutableMapOf<Point, Char>()
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                antinodeGrid[Point(x, y)] = '.'
            }
        }

        val uniqueAntennas = grid.values.distinct().filter { it != '.' }

        for (antennaType in uniqueAntennas) {
            val antennaLocations = grid.filterValues { it == antennaType }.keys
            markResonance(antinodeGrid, antennaLocations.toList())
        }
        grid.draw()
        return antinodeGrid.values.count { it == '#' }.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val day = 8

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 14)
    checkSolution("Part1", part1(input), 323)
    checkSolution("Part2", part2(testInput), 34)
    checkSolution("Part2", part2(input), 0)
}
