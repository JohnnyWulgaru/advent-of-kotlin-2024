fun main() {
    data class Input19(
        val towels: List<String>,
        val patterns: List<String>,
    )

    fun parseInput(input: List<String>): Input19 {
        val towels = input[0].split(", ").toList()
        val patterns = input.drop(2).toList()
        return Input19(towels, patterns)
    }

    // Helper function with memoization
    fun findAllCombinations(towels: List<String>, design: String, cache: MutableMap<String, Long>): Long {
        // Check cache first
        cache[design]?.let { return it }

        // Base case: empty string
        if (design.isEmpty()) {
            return 1
        }

        // Try all patterns
        var combinations = 0L
        for (pattern in towels) {
            if (design.startsWith(pattern)) {
                combinations += findAllCombinations(towels, design.substring(pattern.length), cache)
            }
        }

        // Store result in cache and return
        cache[design] = combinations
        return combinations
    }


    fun part1(input: List<String>): Int {
        val data = parseInput(input)
        val cache = mutableMapOf<String, Long>()
        return data.patterns.filter {
            findAllCombinations(data.towels, it, cache) > 0
        }.size
    }

    fun part2(input: List<String>): Long {
        val data = parseInput(input)
        val cache = mutableMapOf<String, Long>()
        return data.patterns.sumOf {
            findAllCombinations(data.towels, it, cache)
        }
    }

    val day = 19

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 6)
    checkSolution("Part1 [Full]", part1(input), 347)
    checkSolution("Part2 [Test]", part2(testInput), 16L)
    checkSolution("Part2 [Full]", part2(input), 919219286602165L)
}
