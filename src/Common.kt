data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

fun <T> permutations(possibilities: List<T>, length: Int): List<List<T>> {
    val result = mutableListOf<List<T>>()
    fun generateCombination(current: List<T>) {
        if (current.size == length) {
            result.add(current)
            return
        }
        for (p in possibilities) {
            generateCombination(current + p)
        }
    }
    generateCombination(listOf())

    return result
}