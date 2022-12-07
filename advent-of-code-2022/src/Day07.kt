class Dir(val path: String, var size: Long, val dirs: MutableSet<Dir> = mutableSetOf(), val parent: Dir? = null) {
    operator fun plusAssign(fileSize: Long) {
        this.size += fileSize
        parent?.plusAssign(fileSize)
    }

    override fun toString(): String {
        return "Dir $path, $size" + (if (dirs.isNotEmpty()) " dirs: $dirs" else "")
    }
}

val diskSpace = 70_000_000L
val neededFreeSpace = 30_000_000L

fun main() {

    fun parseInput(input: List<String>): Pair<Dir, MutableList<Dir>> {
        var currentDir = Dir("/", 0L)
        val root = currentDir
        val allDirs = mutableListOf(root)

        fun addDir(dirName: String): Dir {
            val newDirPath = "${if(root == currentDir) "" else currentDir.path}/$dirName"

            val find = allDirs.find { d -> d.path == newDirPath }
            return if (find != null) find else {
                val newDir = Dir(newDirPath, 0L, parent = currentDir)
                allDirs.add(newDir)
                newDir
            }
        }

        input.forEach { line ->
            // commands
            if (line.startsWith("$ ")) {
                val command = line.substringAfter("$ ")
                currentDir = when {
                    command == "cd .." -> currentDir.parent!!
                    command == "cd /" -> root
                    command.startsWith("cd ") -> addDir(command.substringAfter("cd "))
                    command == "ls" -> currentDir
                    else -> throw RuntimeException("Unknown cd: $command")
                }
            } else {
                val (sizeOrDir, _) = line.split(" ")
                when (sizeOrDir) {
                    "dir" -> {}
                    else -> currentDir += sizeOrDir.toLong()
                }
            }
        }
        return root to allDirs
    }

    fun part1(input: List<String>): Long =
        parseInput(input)
            .second
            .asSequence()
            .map { it.size }
            .filter { it < 100_000 }
            .sum()


    fun part2(input: List<String>): Long {
        val (root, allDirs) = parseInput(input)
        return allDirs
            .asSequence()
            .map { it.size }
            .filter { it > (neededFreeSpace - diskSpace + root.size) }
            .min()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInput("Day07")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}