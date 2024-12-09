data class FileBlock(
    val id: Int?,
    val size: Int,
)

fun main() {

    fun MutableMap<Long, FileBlock>.debug() {
        val addresses = this.keys.toList().sortedBy { it }
        for (addr in addresses) {
            val block = this[addr]
            println("$addr: ${block?.id} ${block?.size}")
        }
    }

    fun parseMemory(input: String): MutableMap<Long, FileBlock> {
        val memory = mutableMapOf<Long, FileBlock>()

        var idx = 0
        var id = 0
        var pos = 0L
        do {
            val next = input[idx].digitToInt()

            if (idx % 2 == 1) {
                // free space
                memory[pos] = FileBlock(null, next)
            } else {
                // occupied space
                memory[pos] = FileBlock(id, next)
                id += 1
            }
            pos += next
            idx++
        } while (idx < input.length)

        return memory
    }

    fun findFirstHole(memory: MutableMap<Long, FileBlock>, size: Int): Long? {
        memory
            .asSequence()
            .filter { it.value.id == null }
            .map { it.key }
            .sorted()
            .forEach { addr ->
                val block = memory[addr]
                check(block != null) { "Block at address $addr is null" }
                if (block.size >= size) {
                    return addr
                }
            }

        return null
    }

    fun verifyMemory(memory: MutableMap<Long, FileBlock>): Boolean {
        var addr = 0L
        val maxAddr = memory.keys.maxOrNull() ?: return false
        while (true) {
            val block = memory[addr]
            if (block == null) {
                println("verifyMem: no block at $addr")
                return false
            }

            addr += block.size
            if (addr > maxAddr) break
        }

        return true
    }

    fun compressMemory(memory: MutableMap<Long, FileBlock>): Boolean {
        val newMemory = mutableMapOf<Long, FileBlock>()

        var didCompress = false

        var pBlock: FileBlock? = null
        var pAddr = 0L

        val listOfAddrs = memory.keys.sortedBy { it }.toList()

        var idx = 0
        while (idx < listOfAddrs.size) {
            val tAddr = listOfAddrs[idx]
            val tBlock = memory[tAddr]

            if (pBlock == null) {
                pBlock = tBlock
                pAddr = tAddr
            } else {
                if (pBlock.id == null && tBlock!!.id == null) {
                    // we can compress
                    newMemory[pAddr] = FileBlock(null, pBlock.size + tBlock.size)
                    // skip one
                    pBlock = null

                    didCompress = true
                } else {
                    newMemory[pAddr] = pBlock
                    pBlock = tBlock
                    pAddr = tAddr
                }
            }

            idx++
        }

        // last insert
        if (pBlock != null) {
            newMemory[pAddr] = pBlock
        }

        memory.clear()
        newMemory.forEach { (k, v) ->
            memory[k] = v
        }

        return didCompress
    }

    fun moveBlock(memory: MutableMap<Long, FileBlock>, from: Long, to: Long) {
        val fromBlock = memory[from]!!
        val toBlock = memory[to]!!

        check(toBlock.id == null) { "Block at address $to is already occupied" }
        check(toBlock.size >= fromBlock.size) { "Block at address $to is smaller than the block at address $from" }

        // mark memory as free
        memory[from] = FileBlock(null, fromBlock.size)
        memory[to] = FileBlock(null, toBlock.size)

        if (toBlock.size == fromBlock.size) {
            // simple insert
            memory[to] = fromBlock
        } else {
            // insert from block and new space block
            memory[to] = fromBlock
            check((to + fromBlock.size) !in memory) { "Block at address ${(to + fromBlock.size)} is already occupied" }
            memory[to + fromBlock.size] = FileBlock(null, toBlock.size - fromBlock.size)
        }

        //if (compressMemory(memory))
        //    compressMemory(memory)

        /*
        check(verifyMemory(memory)) {
            memory.debug()
            "memory corruption!"
        }
        */
    }

    fun checksumMemory(memory: MutableMap<Long, FileBlock>): Long {
        var sum = 0L
        for (addr in memory.keys) {
            val block = memory[addr] ?: continue
            if (block.id == null) continue

            for (i in 0 until block.size) {
                sum += (addr + i) * block.id
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val memory = parseMemory(input[0])

        // val get all ids
        val fileIds = memory.filter { it.value.id != null }.map { it.value.id!! }.toList().sortedByDescending { it }

        for (fileId in fileIds) {
            // find addr for fileId
            val addr = memory.filter { it.value.id == fileId }.map { it.key }.first()

            val block = memory[addr]
            check(block != null) { "Block at address $addr is null" }

            val hole = findFirstHole(memory, block.size) ?: continue

            if (hole > addr) continue

            val holeBlock = memory[hole]
            check(holeBlock != null) { "Block at address $hole is null" }

            moveBlock(memory, addr, hole)
        }

        return checksumMemory(memory)
    }

    val day = 9

    // Read Inputs
    val testInput = readInput("day%02d_test".format(day))
    val input = readInput("day%02d".format(day))
    checkSolution("Part2", part2(testInput), 2858L)
    checkSolution("Part2", part2(input), 6488291456470L)
}
