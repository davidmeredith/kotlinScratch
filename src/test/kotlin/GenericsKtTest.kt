import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GenericsKtTest {

    @Test
    fun `in Declaration-site variance consumer demo - accept wide`() {
        val inConsumer = InConsumer(Mammal()) // accept wide to allow contravariant references
        // in means that we can safely assign a reference to any of its subtypes,
        // it is contravariant because the references are not polymorphic
        val dogConsumerContravariantRef : InConsumer<Dog> = inConsumer
        val catConsumerContravariantRef : InConsumer<Cat> = inConsumer

        dogConsumerContravariantRef.consume(Dog())
        catConsumerContravariantRef.consume(Cat())
        // prints:
        // class value: [Mammal@28dcca0c] fun var [Dog@45d84a20]
        // class value: [Mammal@28dcca0c] fun var [Cat@4c163e3]
    }

    @Test
    fun `out Declaration-site variance producer demo - produce narrow`(){
        val outProducer = OutProducer("string")
        // out means that we can safely assign a reference to any of its supertypes
        // it is covariant because covariantRef is polymorphic.
        val covariantRef: OutProducer<Any> = outProducer
        assertTrue(covariantRef is OutProducer<Any>)
    }
}