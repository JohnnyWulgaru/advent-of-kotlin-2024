typealias Grid<T> = MutableMap<Point, T>
typealias CharGrid = Grid<Char>

fun <T> Grid<T>.debug(emptyChar: Char? = ' ', maxXIn: Int? = null, maxYIn: Int? = null) {
    val maxX = maxXIn ?: keys.maxOf { it.x }
    val maxY = maxYIn ?: keys.maxOf { it.y }
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            print(this[Point(x, y)] ?: emptyChar)
        }
        println()
    }
}

enum class Direction(val x: Int, val y: Int) {
    N(0, -1), S(0, 1), E(1, 0), W(-1, 0)
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun plus(dir: Direction) = plus(Point(dir.x, dir.y))
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
    operator fun minus(dir: Direction) = minus(Point(dir.x, dir.y))
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

fun Direction.id() = when (this) {
    Direction.N -> 0
    Direction.S -> 1
    Direction.E -> 2
    Direction.W -> 3
}

fun Direction.left() = when (this) {
    Direction.N -> Direction.W
    Direction.S -> Direction.E
    Direction.E -> Direction.N
    Direction.W -> Direction.S
}

fun Direction.right() = when (this) {
    Direction.N -> Direction.E
    Direction.S -> Direction.W
    Direction.E -> Direction.S
    Direction.W -> Direction.N
}
