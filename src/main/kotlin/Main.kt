fun main() {
    println("Pawns-Only Chess")

    print("First Player's name: ")
    val whiteName = readLine()?.trim().orEmpty()

    print("Second Player's name: ")
    val blackName = readLine()?.trim().orEmpty()

    val game = Game(whiteName.ifEmpty { "White" }, blackName.ifEmpty { "Black" })
    game.run()
}