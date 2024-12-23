fun main() {
    fun parse(input: List<String>): Pair<Set<Pair<String, String>>, Set<String>> {
        val connections = mutableSetOf<Pair<String, String>>()
        val computers = mutableSetOf<String>()
        for (line in input) {
            val (a, b) = line.split("-").map { it.trim() }
            connections.add(a to b)
            connections.add(b to a)
            computers.add(a)
            computers.add(b)
        }

        return connections to computers
    }

    fun part1(input: List<String>): Int {
        val (connections, computers) = parse(input)
        val sortedComputers = computers.sorted()

        var count = 0
        sortedComputers.forEachIndexed { ai, a ->
            sortedComputers.take(ai).forEachIndexed { bi, b ->
                if (connections.contains(a to b)) {

                    sortedComputers.take(bi).forEachIndexed { ci, c ->
                        if (connections.contains(b to c) && connections.contains(a to c)
                            && (a[0] == 't' || b[0] == 't' || c[0] == 't')
                        ) {
                            count++
                        }
                    }
                }
            }
        }

        return count
    }

    fun part2(input: List<String>): String {
        val (connections, computers) = parse(input)
        val sortedComputers = computers.sorted()

        val queue = ArrayDeque<List<String>>().apply {
            // create lists of computers in the lan party
            // initialize with the first computer
            addAll(sortedComputers.map { listOf(it) })
        }
        var biggestLanParty = emptyList<String>()

        while (queue.isNotEmpty()) {
            val current = queue.removeLast()
            if (current.size > biggestLanParty.size) {
                biggestLanParty = current
            }

            // Speed optimization since our list is sorted
            val lastComputer = current.last()
            val lastComputerIndex = sortedComputers.indexOf(lastComputer)
            for (x in sortedComputers.drop(lastComputerIndex)) {

                // try to add all computers to the initial `current` list computers
                // if there is a valid connection between them
                var isValid = true
                inner@ for (y in current) {
                    if ((x to y) !in connections) {
                        isValid = false
                        break@inner
                    }
                }
                if (isValid) {
                    queue.add(current + x)
                }
            }
        }
        return biggestLanParty.sorted().joinToString(",")
    }

    val day = 23

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 7)
    checkSolution("Part1 [Full]", part1(input), 1467)
    checkSolution("Part2 [Test]", part2(testInput), "co,de,ka,ta")
    checkSolution("Part2 [Full]", part2(input), "di,gs,jw,kz,md,nc,qp,rp,sa,ss,uk,xk,yn")
}
