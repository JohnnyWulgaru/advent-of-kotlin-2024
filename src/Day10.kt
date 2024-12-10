fun main() {
    fun directions(row: Int, col: Int): List<Pair<Int, Int>> {
        return listOf(
            Pair(row - 1, col),   // Up
            Pair(row + 1, col),   // Down
            Pair(row, col - 1),   // Left
            Pair(row, col + 1)    // Right
        )
    }

    fun parseMap(input: List<String>): List<List<Int>> {
        val map = mutableListOf<MutableList<Int>>()

        for (inputLine in input) {
            val line = mutableListOf<Int>()
            for (char in inputLine) {
                line.add(char.digitToInt())
            }
            map.add(line)
        }

        return map
    }

    fun findTrailHeads(map: List<List<Int>>) = map.flatMapIndexed { r, row ->
        row.mapIndexedNotNull { c, value ->
            if (value == 0) Pair(r, c) else null
        }
    }

    fun searchAllNines(
        map: List<List<Int>>,
        visited: Array<BooleanArray>,
        start: Pair<Int, Int>
    ): Int {
        val ninePositions = mutableSetOf<Pair<Int, Int>>()
        val toExplore = ArrayDeque<Pair<Int, Int>>()

        toExplore.add(Pair(start.first, start.second))
        visited[start.first][start.second] = true

        while (toExplore.isNotEmpty()) {
            val (r, c) = toExplore.removeFirst()

            if (map[r][c] == 9) {
                ninePositions.add(Pair(r, c))
            }

            for ((nextR, nextC) in directions(r, c)) {
                if (nextR in map.indices &&
                    nextC in map[0].indices &&
                    !visited[nextR][nextC] &&
                    map[nextR][nextC] - map[r][c] == 1
                ) {
                    toExplore.add(Pair(nextR, nextC))
                    visited[nextR][nextC] = true
                }
            }
        }

        return ninePositions.size
    }

    fun findTrails(map: List<List<Int>>, start: Pair<Int, Int>): List<List<Pair<Int, Int>>> {
        val trailsToNine = mutableListOf<List<Pair<Int, Int>>>()
        val visited = mutableSetOf<List<Pair<Int, Int>>>()

        fun dfs(current: List<Pair<Int, Int>>) {
            val (r, c) = current.last()

            if (map[r][c] == 9) {
                if (current !in visited) {
                    trailsToNine.add(current)
                    visited.add(current)
                }
                return
            }

            for ((nextRow, nextCol) in directions(r, c)) {
                if (nextRow in map.indices &&
                    nextCol in map[0].indices &&
                    map[nextRow][nextCol] == map[r][c] + 1
                ) {
                    dfs(current + Pair(nextRow, nextCol))
                }
            }
        }

        dfs(listOf(start))

        return trailsToNine
    }

    fun part1(input: List<String>): Long {
        val map = parseMap(input)

        val visited = Array(map.size) { BooleanArray(map[0].size) }
        return findTrailHeads(map).sumOf { trailhead ->
            visited.forEach { it.fill(false) }
            searchAllNines(map, visited, trailhead)
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        val map = parseMap(input)
        return findTrailHeads(map).sumOf { trailhead ->
            findTrails(map, trailhead).size
        }.toLong()
    }

    val day = 10

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 36L)
    checkSolution("Part1", part1(input), 682L)
    checkSolution("Part2", part2(testInput), 81L)
    checkSolution("Part2", part2(input), 1511L)
}
