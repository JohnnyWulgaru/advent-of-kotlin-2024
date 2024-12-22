fun main() {
    val pruneModulo = 16777216L // The modulus value for pruning
    val cache = mutableMapOf<Long, Long>()

    fun nextSecretNumber(secretNumber: Long): Long {
        if (cache[secretNumber] != null) return cache[secretNumber]!!


        fun prune(value: Long): Long {
            return value % pruneModulo
        }

        fun mix(value: Long, secret: Long): Long {
            return value xor secret
        }

        val step1Value = secretNumber * 64L
        var updatedSecret = prune(mix(step1Value, secretNumber))

        val step2Value = updatedSecret / 32L
        updatedSecret = prune(mix(step2Value, updatedSecret))

        val step3Value = updatedSecret * 2048L
        updatedSecret = prune(mix(step3Value, updatedSecret))

        cache[secretNumber] = updatedSecret
        return updatedSecret
    }

    fun test() {
        var start = 123L

        var next = nextSecretNumber(start)
        check(next == 15887950L) { "first test failed" }
        next = nextSecretNumber(next)
        check(next == 16495136L) { "second test failed" }
        next = nextSecretNumber(next)
        check(next == 527345L) { "third test failed" }
        next = nextSecretNumber(next)
        check(next == 704524L) { "fourth test failed" }
    }

    fun part1(input: List<String>): Long {
        return input.map { it.toLong() }.sumOf {
            var secretNumber = it
            repeat(2000) { secretNumber = nextSecretNumber(secretNumber) }
            println("Secret number: $secretNumber")
            secretNumber
        }
    }

    fun part2(input: List<String>): Long {
        val initialNumbers = input.map { it.toLong() }

        val sequenceValues = mutableMapOf<String, Long>()

        for (number in initialNumbers) {
            var currentNumber = number
            val sequences = mutableListOf<List<Long>>()
            val differences = mutableListOf<Long>()

            repeat(2000) {
                val newNumber = nextSecretNumber(currentNumber)
                val difference = (newNumber % 10) - (currentNumber % 10)

                if (differences.size == 4) {
                    differences.removeAt(0)
                }
                differences.add(difference)

                if (differences.size == 4 && !sequences.contains(differences.toList())) {
                    val cacheKey = differences.joinToString(",")
                    sequenceValues[cacheKey] = sequenceValues.getOrDefault(cacheKey, 0L) + (newNumber % 10)
                    sequences.add(differences.toList())
                }

                currentNumber = newNumber
            }
        }

        return sequenceValues.values.maxOrNull() ?: 0L
    }

    val day = 22

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val testInput2 = readInput("day%02d_test2".format(day))
    val input = readInput("day%02d".format(day))

    test()

    checkSolution("Part1 [Test]", part1(testInput), 37327623L)
    checkSolution("Part1 [Full]", part1(input), 15608699004L)
    checkSolution("Part2 [Test]", part2(testInput2), 23L)
    checkSolution("Part2 [Full]", part2(input), 1791L)
}
