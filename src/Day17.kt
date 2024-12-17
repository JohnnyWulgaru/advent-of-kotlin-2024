import kotlin.math.floor
import kotlin.math.pow

fun main() {
    data class Computer(
        val reg: MutableMap<Char, Long> = mutableMapOf(),
        val program: List<Long>,
        var pc: Int = 0,
        var out: MutableList<Long> = mutableListOf(),
    )

    fun Computer.copyInputs(): Computer {
        val reg = mutableMapOf<Char, Long>()
        reg.putAll(this.reg)
        val program = mutableListOf<Long>()
        program.addAll(this.program)

        return Computer(
            reg = reg,
            program = program,
        )
    }

    fun parseProgram(input: List<String>): Computer {
        val program = mutableListOf<Long>()
        val registers = mutableMapOf<Char, Long>()
        for (line in input) {
            if (line.startsWith("Register ")) {
                val id = line.substringAfter("Register ")[0]
                val value = line.substringAfter("Register ").substringAfter(": ").toLong()
                registers[id] = value
            } else if (line.startsWith("Program: ")) {
                program.addAll(line.substringAfter("Program: ").split(",").map { it.toLong() })
            }
        }
        return Computer(registers, program)
    }

    fun getRealOperand(registers: Map<Char, Long>, operand: Long): Long? {
        return when (operand) {
            0L -> 0
            1L -> 1
            2L -> 2
            3L -> 3
            4L -> registers['A']!!
            5L -> registers['B']!!
            6L -> registers['C']!!
            else -> {
                return null
            }
        }
    }

    fun step(computer: Computer): Boolean {
        if (computer.pc >= computer.program.size) {
            return false
        }

        val opcode = computer.program[computer.pc]
        val litOperand = computer.program[computer.pc + 1]
        val operand = getRealOperand(computer.reg, litOperand)
        computer.pc += 2

        when (opcode) {
            0L -> {
                //The adv instruction (opcode 0) performs division. The numerator is the value in the A register.
                // The denominator is found by raising 2 to the power of the instruction's combo operand.
                // (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
                // The result of the division operation is truncated to an integer and then written to the A register.
                val num = computer.reg['A']!!
                val denom = 2.0.pow(operand!!.toDouble())
                computer.reg['A'] = floor(num / denom).toLong()
            }

            1L -> {
                //The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand,
                // then stores the result in register B.
                computer.reg['B'] = computer.reg['B']!! xor litOperand
            }

            2L -> {
                //The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby keeping only its lowest 3 bits),
                // then writes that value to the B register.
                computer.reg['B'] = operand!! and 7
            }

            3L -> {
                //The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero,
                // it jumps by setting the instruction pointer to the value of its literal operand; if this instruction jumps,
                // the instruction pointer is not increased by 2 after this instruction.
                val a = computer.reg['A']!!
                if (a != 0L) {
                    computer.pc = litOperand.toInt()
                }
            }

            4L -> {
                //The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the
                // result in register B. (For legacy reasons, this instruction reads an operand but ignores it.)
                computer.reg['B'] = computer.reg['B']!! xor computer.reg['C']!!
            }

            5L -> {
                //The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value.
                // (If a program outputs multiple values, they are separated by commas.)
                computer.out.add(operand!! and 7)
            }

            6L -> {
                //The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is stored in the B register.
                // (The numerator is still read from the A register.)
                val num = computer.reg['A']!!
                val denom = 2.0.pow(operand!!.toDouble())
                computer.reg['B'] = floor(num / denom).toLong()
            }

            7L -> {
                //The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is stored in the C register.
                // (The numerator is still read from the A register.)
                val num = computer.reg['A']!!
                val denom = 2.0.pow(operand!!.toDouble())
                computer.reg['C'] = floor(num / denom).toLong()
            }

            else -> {
                throw IllegalArgumentException("Invalid opcode: $opcode")
            }
        }

        return true
    }

    fun run(computer: Computer) {
        var keepRunning = true
        while (keepRunning) {
            keepRunning = step(computer)
        }
    }

    fun part1(input: List<String>): String {
        val computer = parseProgram(input)
        run(computer)
        return computer.out.joinToString(",")
    }

    fun checkProgramToOutput(originalProgram: List<Long>, out: List<Long>): Boolean {
        for (i in out.indices) {
            if (i > originalProgram.size) {
                return false
            }
            if (out[i] != originalProgram[i]) {
                return false
            }
        }
        return true
    }

    fun bruteForcePart2(input: List<String>, start: Long = 0): Long {
        val computer = parseProgram(input)
        val originalProgram = computer.program.toMutableList()

        var initialRegA = start
        while (initialRegA < Integer.MAX_VALUE - 1) {
            val clonedComputer = computer.copyInputs()
            clonedComputer.reg['A'] = initialRegA

            var keepRunning = true
            run@ while (keepRunning) {
                keepRunning = step(clonedComputer)

                // check if output still matches
                if (!checkProgramToOutput(originalProgram, clonedComputer.out)) {
                    break@run
                }

            }

            // check at end of run
            if (checkProgramToOutput(originalProgram, clonedComputer.out)
                && originalProgram.size == clonedComputer.out.size
            ) {
                return initialRegA
            }

            initialRegA++
        }

        return -1
    }

    fun reverseEngineerPart2(input: List<String>): Long {
        /*
        2 4
            B = A mod 8
        1 1
            B = B xor 1
        7 5
            C = A / 2^B
        4 6
            B = B xor C
        1 4
            B = B xor 4
        0 3
            A = A / 2^3
        5 5
            out B mod 8 <- just one output per loop
        3 0
            jnz 0
         */

        // A = A / 2^3
        // out ((((A mod 8) xor 1) xor (A / 2^(A mod 8) xor 1)))) xor 4))

        val computer = parseProgram(input)
        val originalProgram = computer.program.toMutableList()

        fun findDigit(program: List<Long>, target: List<Long>): Long {
            var startingRegisterA = if (target.size > 1) {
                8L * findDigit(program, target.subList(1, target.size))
            } else {
                0L
            }

            while (true) {
                val clonedComputer = Computer(
                    reg = mutableMapOf('A' to startingRegisterA),
                    program = program,
                )

                run(clonedComputer)

                if (checkProgramToOutput(target, clonedComputer.out)) {
                    return startingRegisterA
                }

                startingRegisterA++
            }
        }

        return findDigit(originalProgram, originalProgram)
    }

    // test cases
    //If register C contains 9, the program 2,6 would set register B to 1.
    fun test1() {
        val computer = Computer(
            reg = mutableMapOf('C' to 9L),
            program = listOf(2L, 6L),
        )
        step(computer)
        check(computer.reg['B']!! == 1L) { "test 1 failed" }
    }

    //If register A contains 10, the program 5,0,5,1,5,4 would output 0,1,2.
    fun test2() {
        val computer = Computer(
            reg = mutableMapOf('A' to 10),
            program = listOf(5L, 0L, 5L, 1L, 5L, 4L),
        )
        run(computer)
        check(computer.out == listOf(0L, 1L, 2L)) { "test 2 failed" }
    }

    //If register A contains 2024, the program 0,1,5,4,3,0 would output 4,2,5,6,7,7,7,7,3,1,0 and leave 0 in register A.
    fun test3() {
        val computer = Computer(
            reg = mutableMapOf('A' to 2024L),
            program = listOf(0L, 1L, 5L, 4L, 3L, 0L),
        )
        run(computer)
        check(computer.out == listOf(4L, 2L, 5L, 6L, 7L, 7L, 7L, 7L, 3L, 1L, 0L)) { "test 3 failed" }
        check(computer.reg['A']!! == 0L) { "test 3 failed (reg)" }
    }

    //If register B contains 29, the program 1,7 would set register B to 26.
    fun test4() {
        val computer = Computer(
            reg = mutableMapOf('B' to 29L),
            program = listOf(1, 7),
        )
        run(computer)
        check(computer.reg['B']!! == 26L) { "test 4 failed" }
    }

    //If register B contains 2024 and register C contains 43690, the program 4,0 would set register B to 44354.
    fun test5() {
        val computer = Computer(
            reg = mutableMapOf('B' to 2024L, 'C' to 43690L),
            program = listOf(4L, 0L),
        )
        run(computer)
        check(computer.reg['B']!! == 44354L) { "test 5 failed" }
    }


    fun allTests() {
        test1()
        test2()
        test3()
        test4()
        test5()
    }
    allTests()

    // run real input / outputs
    val day = 17

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val testInput2 = readInput("day%02d_test2".format(day))
    val input = readInput("day%02d".format(day))

    checkSolution("Part1 [Test]", part1(testInput), "4,6,3,5,6,3,5,2,1,0")
    checkSolution("Part1 [Full]", part1(input), "4,6,1,4,2,1,3,1,6")
    checkSolution("Part2 [Test]", bruteForcePart2(testInput2), 117440)
    checkSolution("Part2 [Full]", reverseEngineerPart2(input), 202366627359274L)
}
