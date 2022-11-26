import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class GenericsKtTest {

    @Test
    fun `in Declaration-site variance consumer demo`() {
        val inConsumer = InConsumer(Mammal())
        // in means that we can safely assign a reference to any of its subtypes.
        val dogConsumer : InConsumer<Dog> = inConsumer
        val catConsumer : InConsumer<Cat> = inConsumer
        assertTrue(dogConsumer is InConsumer<Dog>)
        assertTrue(catConsumer is InConsumer<Cat>)

        dogConsumer.consume(Dog())
        catConsumer.consume(Cat())
        // prints:
        // class value: [Mammal@28dcca0c] fun var [Dog@45d84a20]
        // class value: [Mammal@28dcca0c] fun var [Cat@4c163e3]
    }

    @Test
    fun `out Declaration-site variance produce demo`(){
        val outProducer = OutProducer("string")
        // out means that we can safely assign a reference to any of its supertypes.
        val ref: OutProducer<Any> = outProducer
        @Suppress
        assertTrue(ref is OutProducer<Any>)
    }
}