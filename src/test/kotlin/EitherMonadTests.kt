import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EitherMonadTests {

    /*
     - Monad is a concept, a functional pattern.
     - Monads can be implemented in any statically typed language that supports generics.
     - Implemented as classes that encapsulate a ‘context’ that wraps a parameterized input type (eg R1) and
       which declares higher-order functions to transform the value of the wrapped type.
     - Technically, a Monad implements:
         * A higher order bind function: flatmap()  (and optionally map())
         * A unit function:  of()
         * Obeys the laws of Right and Left associativity
     - unit() function creates a Monad by wrapping the parameterized type
     - flatMap/map allows passing a ‘mapper’ function to our wrapped type in order to transform the wrapped val.
     - flatMap/map allows call chaining – you can repeatedly apply multiple ‘mapper’ functions in order to
       successively transform results in a pipeline.
     - Transformation in a pipeline successively creates new Monads and the type of the wrapped param can change,
       eg from R1 to R2.
     - Important: flatMap/map won't call the mapper function if its parameterised input value is null/empty
       (eg Either’s error implementation is an instance of Left, which uses Nothing for the input type)
     - A null/empty input value aborts the call chain - subsequent mappers in the chain can simply shortcut
       and return the error-Monad unchanged. Alternatively, the Monad could create a new error monad in order to
       update the stack trace for example.
     - The end-result of a long call chain will be our transformed parameter result with the type specified
       by the last Monad in the chain, eg for the Either Monad, this is an instance of Right for success OR
       Left for error.

       When to use map, when to use flatMap ?
       The need to return a new wrapping Monad (map) or flatten/unwrap (flatMap) depends on the
       passed mapper function:
        - If the mapper fn you want to pass specifies  `A -> B` apply map
        - If the mapper fn you want to pass specifies `A -> SomeMonad of B` apply flatmap

     */
    @Test
    fun `assert Left identity`(){
        // If we create a new monad and call its bind function, the result should be the same as
        // applying the function to the value eg:
        // Optional.of(value).flatMap(F).equals(F.apply(value))
        Either.Companion.of(6).flatMap { doubleIt( it * 2)  }.equals(12)
    }
    @Test
    fun `assert Right identity`(){
        // The result of calling a unit (of) function should be the same as the creation of a new monad eg:
        // Optional.of(value).flatMap(Optional::of).equals(Optional.of(value))
        Either.Companion.of(6).flatMap {  Either.Companion.of(6) }.equals(  Either.Companion.of(6))
    }
    @Test
    fun `assert associativity`(){
        // In a monadic call chain, it should not matter how functions are nested (because of bind unpacking):
        val r1 = Either.Companion.of(6).flatMap { doubleIt(it) }.flatMap { doubleIt(it) }
        val r2 = Either.Companion.of(6).map { it * 2 }.flatMap {  doubleIt( it ) }
        assertEquals(24, (r1 as Right).r)
        assertEquals(24, (r2 as Right).r)
        assertEquals(r1, r2)
    }
    @Test
    fun `assert unit call's return type is the same as method return type`(){
        val of = Either.Companion.of(6) as Right
        val parseInt = parseIntReturnMonad("6") as Right
        assertEquals(of.r, parseInt.r)
    }
    @Test
    fun `assert the return type of last bind (flatMap or map) dictates the end result type in a Monadic call chain`() {
        val result1: Either<String, Int> = parseIntReturnMonad("6")
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
        val result4: Either<String, String> = result3.flatMap { doubleIt(result3.r) }.flatMap { doubleItAsStr(result3.r) }
        if(result4 is Right){
            println("Result string was: ${result4.r}")
        } else {
            fail("Unexpected - result4 is not an instance of Right")
        }
    }

}