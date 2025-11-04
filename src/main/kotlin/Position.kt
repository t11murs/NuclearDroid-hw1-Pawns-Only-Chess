data class Position(
    val x: Int, val y: Int
) {
    init {
        require(x in 0..7 && y in 0..7) {
            "Position is out of bounds"
        }
    }

    companion object {
        const val FILES = "abcdefgh"
        const val RANKS = "12345678 "

        // типа мапперов что ли
        fun fileIndex(ch: Char) = FILES.indexOf(ch)
        fun rankIndex(ch: Char) = RANKS.indexOf(ch)
        fun fileChar(x: Int) = FILES[x]
        fun rankChar(y: Int) = RANKS[y]

        fun fromString(cell: String): Position? {
            if (cell.length != 2) return null
            val fileX = fileIndex(cell[0])
            val rankY = rankIndex(cell[1])
            if (fileX == -1 || rankY == -1) return null
            return Position(fileX, rankY)
        }
    }

    fun toAlg(): String = "${fileChar(x)}${rankChar(y)}"
}