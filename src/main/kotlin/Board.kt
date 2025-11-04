class Board() {
    private val cells: Array<CharArray> = Array(8) { CharArray(8) { '.' } }

    fun setup() {
        for (i in 0..7) cells[1][i] = 'W'
        for (i in 0..7) cells[6][i] = 'B'
    }

    fun get(pos: Position) = cells[pos.y][pos.x]
    fun set(pos: Position, ch: Char) {
        cells[pos.y][pos.x] = ch
    }

    fun isEmpty(pos: Position) = get(pos) == '.'
    fun inside(x: Int, y: Int) = x in 0..7 && y in 0..7
    fun anyPawns(color: Color): Boolean {
        val target = if (color == Color.WHITE) 'W' else 'B'
        for (y in 0..7)
            for (x in 0..7)
                if (cells[y][x] == target) return true
        return false
    }

    fun printAscii() {
        println()
        println("  +---+---+---+---+---+---+---+---+")
        for (y in 7 downTo 0) {
            print("${y + 1} |")
            for (x in 0..7) {
                val ch = cells[y][x]
                print(" $ch |")
            }
            println()
            println("  +---+---+---+---+---+---+---+---+")
        }
        println("    a   b   c   d   e   f   g   h")
        println()
    }

    fun move(from: Position, to: Position) {
        val piece = get(from)
        set(from, '.')
        set(to, piece)
    }

}