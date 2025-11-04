data class Move(val from: Position, val to: Position) {
    companion object {
        fun parseMove(moveStr: String): Move? {
            val t = moveStr.trim()
            if (t.length != 4) return null
            val from = Position.fromString(t.substring(0, 2)) ?: return null
            val to = Position.fromString(t.substring(2, 4)) ?: return null
            return Move(from, to)
        }
    }
}