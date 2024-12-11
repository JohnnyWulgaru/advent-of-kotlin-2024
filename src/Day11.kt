import arrow.core.MemoizationCache
import arrow.core.MemoizedDeepRecursiveFunction
import kotlin.time.measureTime

fun main() {

    fun numberOfDigits(i: Long): Int {
        return i.toString().length
    }

    /**
     * If the stone is engraved with the number 0, it is replaced by a stone engraved with the number 1.
     * If the stone is engraved with a number that has an even number of digits, it is replaced by two stones. The left half of the digits are engraved on the new left stone, and the right half of the digits are engraved on the new right stone. (The new numbers don't keep extra leading zeroes: 1000 would become stones 10 and 0.)
     * If none of the other rules apply, the stone is replaced by a new stone; the old stone's number multiplied by 2024 is engraved on the new stone.
     */
    fun blinkList(i: List<Long>): List<Long> {
        val newList = mutableListOf<Long>()
        for (number in i) {
            if (number == 0L) {
                newList.add(1L)
            } else if (numberOfDigits(number) % 2 == 0) {
                val s = number.toString()
                // val split in half
                val left = s.substring(0, s.length / 2)
                val right = s.substring(s.length / 2)

                newList.add(left.toLong())
                newList.add(right.toLong())
            } else {
                newList.add(number * 2024L)
            }
        }
        return newList
    }

    fun part1(input: List<String>): Long {
        var stones = input[0].split(" ").map { it.toLong() }
        repeat(25) {
            stones = blinkList(stones)
        }
        return stones.size.toLong()
    }

    val blinkMemoizedSimpleCache = mutableMapOf<Pair<Long, Int>, Long>()
    fun blinkMemoizedSimple(number: Long, iter: Int): Long {
        if (iter == 0) return 1

        val cacheKey = number to iter
        blinkMemoizedSimpleCache[cacheKey]?.let { return it }

        val totalStones = if (number == 0L) {
            blinkMemoizedSimple(1L, iter - 1)
        } else if (number.toString().length % 2 == 0) {
            val s = number.toString()
            // val split in half
            val left = s.substring(0, s.length / 2).toLong()
            val right = s.substring(s.length / 2).toLong()

            blinkMemoizedSimple(left, iter - 1) + blinkMemoizedSimple(right, iter - 1)
        } else {
            blinkMemoizedSimple(number * 2024, iter - 1)
        }

        blinkMemoizedSimpleCache[cacheKey] = totalStones
        return totalStones
    }

    val arrowCache = object : MemoizationCache<Pair<Long, Int>, Long> {
        val cache = mutableMapOf<Pair<Long, Int>, Long>()
        override fun get(key: Pair<Long, Int>): Long? = cache[key]
        override fun set(key: Pair<Long, Int>, value: Long): Long {
            cache[key] = value; return value
        }

    }
    val blinkMemoized = MemoizedDeepRecursiveFunction(arrowCache) { (number, iter) ->
        if (iter == 0) {
            1
        } else {
            if (number == 0L) {
                callRecursive(1L to iter - 1)
            } else if (number.toString().length % 2 == 0) {
                val s = number.toString()
                // val split in half
                val left = s.substring(0, s.length / 2).toLong()
                val right = s.substring(s.length / 2).toLong()

                callRecursive(left to iter - 1) + callRecursive(right to iter - 1)
            } else {
                callRecursive(number * 2024 to iter - 1)
            }
        }
    }

    fun part2(input: List<String>): Long {
        val stones = input[0].split(" ").map { it.toLong() }

        var sum = 0L
        val kotlinDuration = measureTime {
            for (stone in stones) {
                sum += blinkMemoizedSimple(stone, 75)
            }
        }
        println("Kotlin: $kotlinDuration")

        sum = 0L
        val arrowDuration = measureTime {
            for (stone in stones) {
                sum += blinkMemoized(stone to 75)
            }
        }
        println("Arrow: $arrowDuration")

        return sum
    }

    val day = 11

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1", part1(testInput), 55312L)
    checkSolution("Part1", part1(input), 197357L)
    //checkSolution("Part2", part2(testInput), 0)
    checkSolution("Part2", part2(input), 234568186890978L)
}
