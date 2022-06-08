import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EitherTest {

    @Test
    fun `assert that in a Monadic call chain, the return type of the last bind call (flatMap or map) dictates the final result type`() {
        val result1: Either<String, Int> = parseInt("6")
        assertTrue(result1 is Right)
        when(result1) {
            is Right -> println("Result number was ${result1.r}")
            is Left -> fail("Error was ${result1.l}")
        }
        val result2 = result1.map { it * 2 }.map { it * 2 } // monadic call chain (functional composition)
        assertTrue(result2 is Right && result2.r == 24) // notice the nice kotlin smart cast can be used in assertion
        // Applying toString() as our next bind call will change the type of our monad result from Int to String
        val tmpResult = result2.map { it.toString() }
        assertTrue(tmpResult is Right && tmpResult.r is String) // tmp's result should now be String

        // Continue call chain - result4's type is dictated by the return type of the *last* flatMap call to calcStr,
        // which is Either<String, String>. Changing the type to 'Either<String, Int>' as returned by calcInt
        // gives expected compile error.
        val result3 = result2 as Right
        val result4: Either<String, String> = result3.flatMap { calcInt(result3.r) }.flatMap { calcStr(result3.r) }
        if(result4 is Right){
            println("Result string was: ${result4.r}")
        } else {
            fail("Unexpected - result4 is not an instance of Right")
        }
    }

}