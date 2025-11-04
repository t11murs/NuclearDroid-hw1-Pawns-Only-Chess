private fun charOf(color: Color) = if (color == Color.WHITE) 'W' else 'B'
private fun dirOf(color: Color) = if (color == Color.WHITE) 1 else -1

fun tryApplyMove(board: Board, move: Move, current: Color, ep: EnPassant): String? {
    val currentChar = charOf(current)
    val opponentChar = charOf(if (current == Color.WHITE) Color.BLACK else Color.WHITE)
    val dir = dirOf(current)
    val from = move.from
    val to = move.to

    val startPiece = board.get(from)
    if (startPiece != currentChar) {
        return if (current == Color.WHITE) "No white pawn at ${from.toAlg()}" else "No black pawn at ${from.toAlg()}"
    }

    return when {
        canForward1(board, from, to, dir) ->
            doForward1(board, from, to, ep)

        canForward2(board, from, to, current, dir) ->
            doForward2(board, from, to, current, dir, ep)

        canCapture(board, from, to, dir, opponentChar) ->
            doCapture(board, from, to, opponentChar, ep)

        canEnPassant(board, from, to, dir, ep, opponentChar) ->
            doEnPassant(board, from, to, dir, ep)

        else -> "Invalid input"
    }
}

private fun canForward1(board: Board, from: Position, to: Position, dir: Int): Boolean {
    return to.x == from.x && to.y == from.y + dir &&
            board.inside(to.x, to.y) && board.isEmpty(to)
}

private fun canForward2(board: Board, from: Position, to: Position, current: Color, dir: Int): Boolean {
    val startRank = if (current == Color.WHITE) 1 else 6
    if (!(from.y == startRank && to.x == from.x && to.y == from.y + 2 * dir)) return false
    val mid = Position(from.x, from.y + dir)
    return board.inside(to.x, to.y) && board.isEmpty(to) && board.isEmpty(mid)
}

private fun canCapture(board: Board, from: Position, to: Position, dir: Int, opponentChar: Char): Boolean {
    val diag = (to.x == from.x + 1 || to.x == from.x - 1) && to.y == from.y + dir
    return diag && board.inside(to.x, to.y) && board.get(to) == opponentChar
}

private fun canEnPassant(
    board: Board,
    from: Position,
    to: Position,
    dir: Int,
    ep: EnPassant,
    opponentChar: Char
): Boolean {
    val epTarget = ep.target ?: return false
    val epVictim = ep.victim ?: return false
    val diag = (to.x == from.x + 1 || to.x == from.x - 1) && to.y == from.y + dir
    if (!(diag && to == epTarget && board.isEmpty(to))) return false
    return board.get(epVictim) == opponentChar &&
            epTarget.x == epVictim.x &&
            epVictim.y == epTarget.y - dir
}

private fun doForward1(board: Board, from: Position, to: Position, ep: EnPassant): String? {
    board.move(from, to)
    ep.clear()
    return null
}

private fun doForward2(
    board: Board,
    from: Position,
    to: Position,
    current: Color,
    dir: Int,
    ep: EnPassant
): String? {
    board.move(from, to)
    // право взятия на проходе — цель (клетка "через"), жертва — клетка назначения
    val mid = Position(from.x, from.y + dir)
    ep.setEnPassant(mid, to)
    return null
}

private fun doCapture(board: Board, from: Position, to: Position, opponentChar: Char, ep: EnPassant): String? {
    board.move(from, to)
    ep.clear()
    return null
}

private fun doEnPassant(board: Board, from: Position, to: Position, dir: Int, ep: EnPassant): String? {
    // ep.victim гарантированно не null по предикату
    val victim = ep.victim!!
    board.set(victim, '.')
    board.move(from, to)
    ep.clear()
    return null
}


//нужно для проверки на ничью
fun hasAnyLegalMove(board: Board, color: Color, ep: EnPassant): Boolean {
    val currentChar = charOf(color)
    val opponentChar = charOf(if (color == Color.WHITE) Color.BLACK else Color.WHITE)
    val dir = dirOf(color)

    for (y in 0..7) {
        for (x in 0..7) {
            val from = Position(x, y)
            if (board.get(from) != currentChar) continue

            // обычный шаг
            val p1 = Position(x, y + dir)
            if (board.inside(p1.x, p1.y) && board.isEmpty(p1)) return true

            //двойной шаг
            val startRank = if (color == Color.WHITE) 1 else 6
            val p2 = Position(x, y + 2 * dir)
            if (y == startRank) {
                val mid = Position(x, y + dir)
                if (board.inside(p2.x, p2.y) && board.isEmpty(mid) && board.isEmpty(p2)) return true
            }
            //взятие пешки
            val captureLeft = Position(x - 1, y + dir)
            val captureRight = Position(x + 1, y + dir)
            if (board.inside(captureLeft.x, captureLeft.y) && board.get(captureLeft) == opponentChar) return true
            if (board.inside(captureRight.x, captureRight.y) && board.get(captureLeft) == opponentChar) return true

            //взятие не проходе
            val epTarget = ep.target
            val epVictim = ep.victim
            if (epTarget != null && epVictim != null) {
                if ((epTarget.x == from.x + 1 || epTarget.x == from.x - 1) && epTarget.y == y + dir) {
                    if (board.get(epVictim) == opponentChar && board.isEmpty(epTarget)) return true
                }
            }

        }
    }

    return false
}

fun winByLastRank(board: Board, color: Color): Boolean {
    val currentChar = charOf(color)
    val lastY = if (color == Color.WHITE) 7 else 0
    for (x in 0..7) if (board.get(Position(x, lastY)) == currentChar) return true
    return false
}