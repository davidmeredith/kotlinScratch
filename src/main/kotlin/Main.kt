fun main(args: Array<String>) {
  println("Dave's scratch testing of some Kotlin concepts - see Tests")
}


sealed class Either<out L, out R>
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

fun parseInt(s: String) : Either<String, Int> = try {
    Right(Integer.parseInt(s))
} catch (exception: Exception){
    Left(exception.message ?: "No message")
}

fun calcInt(i: Int) : Either<String, Int> = Right(i * 2)
fun calcStr(i: Int) : Either<String, String> = Right("blah2: "+i * 2)
