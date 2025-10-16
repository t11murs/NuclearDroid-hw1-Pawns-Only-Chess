class Game(
    private val whiteName: String,
    private val blackName: String
) {
    private val board = Board()
    private var current = Color.WHITE
    private val ep = EnPassant()

    init {
        board.setup()
    }

    fun run() {
        board.printAscii()

        while (true) {
            val name = if (current == Color.WHITE) whiteName else blackName
            print("${name}'s turn: ")
            val input = readLine()?.trim().orEmpty()

            if (input.equals("exit", ignoreCase = true)) {
                println("Bye!")
                return
            }

            val move = parseMove(input)
            if (move == null) {
                println("Invalid Input")
                continue
            }

            // попытка применить ход
            val err = tryApplyMove(board, move, current, ep)
            if (err != null) {
                println(err)
                continue
            }

            board.printAscii()

            if (winByLastRank(board, current) ||
                !board.anyPawns(if (current == Color.WHITE) Color.BLACK else Color.WHITE)
            ) {
                println(if (current == Color.WHITE) "White Wins!" else "Black Wins!")
                println("Bye!")
                return
            }

            // проверка пата для следующего игрока
            val next = if (current == Color.WHITE) Color.BLACK else Color.WHITE
            if (!hasAnyLegalMove(board, next, ep)) {
                println("Stalemate!")
                println("Bye!")
                return
            }

            current = next
        }
    }
}