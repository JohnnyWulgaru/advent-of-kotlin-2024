fun main() {
    val rMul = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
    val rDo = """do\(\)""".toRegex()
    val rDont = """don't\(\)""".toRegex()
    val rAllStates = """mul\((\d+),(\d+)\)|do\(\)|don't\(\)""".toRegex()

    fun part1(input: List<String>): Int {
        return rMul.findAll(input.joinToString(""))
            .map { matchResult ->
                val (num1, num2) = matchResult.destructured
                num1.toInt() * num2.toInt()
            }
            .sum()
    }

    fun reusePart1Part2(lines: List<String>): Int {
        val input = "do()" + lines.joinToString("") + "don't()"
        val r = """do\(\)(.*?)don't\(\)""".toRegex()
        return r.findAll(input).sumOf {
            part1(listOf(it.value))
        }
    }

    fun properRegexPart2(lines: List<String>): Int {
        val input = lines.joinToString("")
        var mulOn = true

        return rAllStates.findAll(input).fold(0) { acc, matchResult ->
            when (matchResult.value) {
                "do()" -> {
                    mulOn = true
                    acc
                }

                "don't()" -> {
                    mulOn = false
                    acc
                }

                else -> {
                    if (mulOn) {
                        val (num1, num2) = matchResult.destructured
                        acc + (num1.toInt() * num2.toInt())
                    } else {
                        acc
                    }
                }
            }
        }
    }

    fun naivePart2(lines: List<String>): Int {
        val input = lines.joinToString("")
        var mulOn = true
        var result = 0

        var idx = 0
        while (idx < input.length) {
            var match = rDo.find(input, idx)
            if (match != null && match.range.first == idx) {
                mulOn = true
                idx = match.range.last + 1
                continue
            }

            match = rDont.find(input, idx)
            if (match != null && match.range.first == idx) {
                mulOn = false
                idx = match.range.last + 1
                continue
            }

            match = rMul.find(input, idx)
            if (match != null && match.range.first == idx) {
                if (mulOn) {
                    val (num1, num2) = match.destructured
                    result += num1.toInt() * num2.toInt()
                }
                idx = match.range.last + 1
                continue
            }

            idx++
        }

        return result
    }

    fun part2(input: List<String>): Int {
        //return naivePart2(input)
        // return properRegexPart2(input)
        return reusePart1Part2(input)
    }

    // Or read a large test input from the file:
    val testInput = readInput("day03_test")
    check(part1(testInput) == 161)

    val testInput2 = readInput("day03_test_2")
    check(part2(testInput2) == 48)

    // Read the input from the file.
    val input = readInput("day03")
    val result1 = part1(input)
    println("Part1: $result1")
    check(result1 == 174960292)

    val result2 = part2(input)
    println("Part2 : $result2")
    check(result2 == 56275602)
}
