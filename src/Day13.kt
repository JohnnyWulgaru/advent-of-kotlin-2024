fun main() {
    data class Button(val id: Char, val x: Long, val y: Long)
    data class Prize(val x: Long, val y: Long)
    data class Machine(val buttons: Map<Char, Button>, val prize: Prize)

    val buttonRegex = """Button ([AB]): X\+(\d+), Y\+(\d+)""".toRegex()
    val prizeRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()

    fun parseMachines(lines: List<String>): List<Machine> = buildList {
        var currentButtons = mutableMapOf<Char, Button>()
        var currentPrize: Prize? = null

        for (line in lines) {
            if (line.startsWith("Button ")) {
                val (id, x, y) = buttonRegex.find(line)!!.destructured
                currentButtons[id.first()] = Button(id.first(), x.toLong(), y.toLong())
            } else if (line.startsWith("Prize: ")) {
                val (x, y) = prizeRegex.find(line)!!.destructured
                check(currentPrize == null) { "Prize already set" }
                currentPrize = Prize(x.toLong(), y.toLong())
            } else if (line.trim().isBlank()) {
                add(Machine(currentButtons, currentPrize!!))
                currentButtons = mutableMapOf()
                currentPrize = null
            }
        }
    }

    /*
        na * ax + nb * bx = px
        na * ay + nb * by = py

        na * ax = px - nb * bx
        na * ay = py - nb * by

        (px - nb * bx) / ax = (py - nb * by) / ay
        (px − nb * bx) * ay = (py − nb * by) * ax

        px * ay − nb * bx * ay = py * ax − nb * by * ax

        nb * by * ax − nb * bx * ay = py * ax − px * ay
        nb * (by * ax − bx * ay) = py * ax − px * ay

        nb = (py * ax - px * ay) / (by * ax - ay * bx)
        na = (py - by * nb) / ay

     */

    fun checkMachine(machine: Machine): Long {
        val buttonA = machine.buttons['A']!!
        val buttonB = machine.buttons['B']!!
        val prize = machine.prize

        val nb = (buttonA.x * prize.y - buttonA.y * prize.x) / (buttonA.x * buttonB.y - buttonA.y * buttonB.x)
        val na = (prize.y - buttonB.y * nb) / buttonA.y

        return if (
            buttonA.y * na + buttonB.y * nb == prize.y &&
            buttonA.x * na + buttonB.x * nb == prize.x
        ) {
            (3 * na) + nb
        } else {
            0L
        }
    }

    fun part1(input: List<String>): Long {
        val machines = parseMachines(input)
        return machines.sumOf { machine -> checkMachine(machine) }
    }

    fun part2(input: List<String>): Long {
        val machines = parseMachines(input)
        return machines
            .map { machine ->
                Machine(
                    buttons = machine.buttons,
                    prize = Prize(machine.prize.x + 10000000000000L, machine.prize.y + 10000000000000L)
                )
            }
            .sumOf { checkMachine(it) }
    }

    val day = 13

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), 480L)
    checkSolution("Part1 [Full]", part1(input), 40369L)
    //checkSolution("Part2 [Test]", part2(testInput), 0)
    checkSolution("Part2 [Full]", part2(input), 72034767575307L)
}
