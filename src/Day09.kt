fun main() {
    fun MutableList<Int?>.debug() {
        for (i in this.indices) {
            if (this[i] == null) {
                print(" |")
            } else {
                print("${this[i]}|")
            }
        }
        print("\n")
    }

    fun parseMemory(input: String): MutableList<Int?> {
        val memory = mutableListOf<Int?>()

        var idx = 0
        var id = 0
        do {
            val next = input[idx].digitToInt()

            if (idx % 2 == 1) {
                // free space
                for (i in 0 until next) memory.add(null)
            } else {
                // occupied space
                for (i in 0 until next) memory.add(id)
                id += 1
            }
            idx++
        } while (idx < input.length)
        return memory
    }

    fun checkMemory(memory: MutableList<Int?>): Long {
        var sum = 0L
        for (i in memory.indices) {
            val num = memory[i]
            sum += i * num!!
        }
        return sum
    }

    fun part1(input: List<String>): Long {
        val memory = parseMemory(input[0])
        while (true) {
            // find first hole
            val hole = memory.indexOfFirst { it == null }
            if (hole == -1) break

            // find last block
            val lastBlockAddr = memory.indexOfLast { it != null }
            val lastBlock = memory[lastBlockAddr]

            // remove last block
            memory.removeAt(lastBlockAddr)
            memory[hole] = lastBlock
        }

        return checkMemory(memory)
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val day = 9

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 1928L)
    checkSolution("Part1", part1(input), 6461289671426L)
    checkSolution("Part2", part2(testInput), 0)
    checkSolution("Part2", part2(input), 0)
}
