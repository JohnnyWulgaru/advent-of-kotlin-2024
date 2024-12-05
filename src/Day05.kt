fun main() {
    class PageOrderRuleComparator(private val pageOrderRules: List<Pair<Int, Int>>) : Comparator<Int> {
        override fun compare(x: Int?, y: Int?): Int = when {
            x == y -> 0
            x to y in pageOrderRules -> -1
            else -> 1
        }
    }

    fun splitInput(input: List<String>): Pair<List<Pair<Int,Int>>, List<List<Int>>>{
        val pairs = mutableListOf<Pair<Int,Int>>()
        val numbers = mutableListOf<List<Int>>()
        var pairsMode = true
        for (line in input) {
            if(line.isBlank()) {
                pairsMode = false
                continue
            }

            if(pairsMode) {
                val (a, b) = line.split("|").map { it.toInt() }
                pairs.add(a to b)
            } else {
                val numbersLine = line.split(",").map { it.toInt() }
                numbers.add(numbersLine)
            }
        }

        return pairs to numbers
    }

    fun checkPairRule(input: List<Int>, pair: Pair<Int,Int>): Boolean {
        // make sure that pair.first is before pair.second if both are in the input
        if(input.contains(pair.first) && input.contains(pair.second)) {
            return input.indexOf(pair.first) < input.indexOf(pair.second)
        } else {
            // none of the numbers match, which is ok too
            return true
        }
    }

    fun part1(input: List<String>): Int {
        val (pairs, numbers) = splitInput(input)
        var count = 0

        for(number in numbers) {
            var rulesCorrect = true
            for(pair in pairs) {
                if(!checkPairRule(number, pair)) {
                    rulesCorrect = false
                    break
                }
            }

            if(rulesCorrect) {
                // find the middle number in the number list
                val idx = (number.size - 1) / 2
                val num = number[idx]
                count += num
            }
        }

        return count
    }

    fun part2(input: List<String>): Int {
        val (pairs, numbers) = splitInput(input)
        var count = 0
        for(i in numbers.indices) {
            val number = numbers[i]
            var rulesCorrect = true
            for(pair in pairs) {
                if(!checkPairRule(number, pair)) {
                    rulesCorrect = false
                    break
                }
            }

            if(!rulesCorrect) {
                val x = number.sortedWith(PageOrderRuleComparator(pairs))
                val idx = (number.size - 1) / 2
                val num = x[idx]
                count += num
            }
        }

        return count
    }

    val day = 5

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 143)
    checkSolution("Part1", part1(input), 6498)
    checkSolution("Part2", part2(testInput), 123)
    checkSolution("Part2", part2(input), 5017)
}
