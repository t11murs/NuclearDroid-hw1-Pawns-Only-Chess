class EnPassant (
    var target: Position? = null,
    var victim: Position? = null,
){
    fun clear(){
        target = null
        victim = null
    }

    fun setEnPassant(mid: Position, to: Position){
        target = mid
        victim = to
    }
}