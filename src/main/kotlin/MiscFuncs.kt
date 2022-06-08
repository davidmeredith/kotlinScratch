fun parseIntReturnMonad(s: String) : Either<String, Int> = try {
    Right(Integer.parseInt(s))
} catch (exception: Exception){
    Left(exception.message ?: "No message")
}

fun doubleIt(i: Int) : Either<String, Int> = Right(i * 2)
fun doubleItAsStr(i: Int) : Either<String, String> = Right(""+i * 2)
