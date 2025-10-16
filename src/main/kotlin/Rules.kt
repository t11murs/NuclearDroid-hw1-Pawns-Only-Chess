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
    //будем обнулять взятие на проходе, установим заново, если будет двойной шаг
    var newEpTarget: Position? = null
    var newEpVictim: Position? = null

    //ход на 1 вперед
    if (to.x == from.x && to.y == from.y + dir) {
        if (board.inside(to.x, to.y) && board.isEmpty(to)) {
            board.move(from, to)
            ep.clear()
            return null
        }
    }

    //ход на 2 вперед (только первый ход этой пешки)

    val startRank = if (current == Color.WHITE) 1 else 6
    if (from.y == startRank && to.x == from.x && to.y == from.y + 2 * dir) {
        val mid = Position(from.x, from.y + dir)
        if (board.inside(to.x, to.y) && board.isEmpty(to) && board.isEmpty(mid)) {
            board.move(from, to)
            // по правилам такая пешка может быть взята на проходе слудующим ходом
            newEpTarget = mid
            newEpVictim = to
            ep.setEnPassant(newEpTarget, newEpVictim)
            return null
        }
    }

    //взятие по диагонали
    if ((to.x == from.x + 1 || to.x == from.x - 1) && to.y == from.y + dir) {
        if (board.inside(to.x, to.y) && board.get(to) == opponentChar) {
            board.move(from, to)
            ep.clear()
            return null
        }
    }

    //взятие на проходе
    val epTarget = ep.target
    val epVictim = ep.victim

    if (epTarget != null && epVictim != null) {
        if ((to.x == from.x + 1 || to.x == from.x - 1) &&
            to.y == from.y + dir && to == epTarget && board.isEmpty(to)
        ) {
            if (board.get(epVictim) == opponentChar && epTarget.x == epVictim.x && epVictim.y == epTarget.y - dir) {
                board.set(epVictim, '.')
                board.move(from, to)
                ep.clear()
                return null
            }
        }
    }

    /* после каждого хода пишу ep.clear() тк по правилам взятие на проходе можно сделать только после двойного шага пешки*/

    return "Invalid input"
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