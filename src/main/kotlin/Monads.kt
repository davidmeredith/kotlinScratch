

sealed class Either<out L, out R>() {
    companion object {
        fun <R1> of(param: R1): Either<Nothing, R1> = Right(param)
    }
}
data class Left<out L>(val l: L) : Either<L, Nothing>()
data class Right<out R>(val r: R) : Either<Nothing, R>()

inline fun <L, R1, R2> Either<L, R1>.map(f: (R1) -> R2): Either<L, R2> =
    when(this) {
        is Right -> Right(f(this.r))
        is Left -> this
    }

inline fun <L, R1, R2> Either<L, R1>.flatMap(f: (R1) -> Either<L, R2>): Either<L, R2> =
    when(this) {
        is Right -> f(this.r)
        is Left -> this
    }
