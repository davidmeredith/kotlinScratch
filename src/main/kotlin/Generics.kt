
// Generics / parameterized Classes in Kotlin for Declaration Site Variance:
// See the corresponding test file for usage.
//
// - Covariance: Producing narrower elements of type T in the 'out' position to facilitate 'upper-bound' (polymorphic)
// references at the call-site within fun bodies (e.g. type T can be a subtype - covariant).
//
// - Contravariance: Accepting wider elements of type T in the 'in' position to allower lower-bound
// references at the call-site within fun bodies.


// Covariant <out T> params.
// Used to produce narrower elements of Type T in the 'out position' to subsequently allow upper-bound references
// to be used later at the call-site within a fun body.
//------------------------------------------------------------------------------------
// The general rule is this: when a type parameter T of a class is declared 'out',
// it may occur only in the out-position in the members of the class.
//
// Equivalent java is: '<? extends T>' (our java type '?' is T or an unknown SUBTYPE of T).
// <out T> means we can later use an upper-bound supertype ref/var to reference this type.
// It is 'covariant' because the upper-bound reference could actually be OF MULTIPLE SUBTYPES.
// Note, we can't set/add using T as you don't know the actual type of T - it is T or an unknown subtype of T.
// Basically, it avoids trouble due to subtyping at the use site as you can return/use wider covariant references.
interface Source<out T> {
    fun nextT(): T
}

// A covariance demo
fun covarianceConsumerDemo(strings: Source<String>) {
    val objects: Source<Any> = strings //OK, T is a 'covariant' out-param (Source<Any> can reference Source<String>)
    val v : Any = objects.nextT()
}

// Another covariance demo
class OutProducer<out T>(private val t: T) {
    fun get(): T {
        return t
    }
}


// Contravariant <in T> params.
// Used to accept wider elements of type T in the 'in position' to subsequently allow lower-bound references
// to be used later at the call-sie within a fun body.
// ----------------------------------------------------------------------------------
// The general rule is this: when a type parameter T of a class is declared 'in',
// it may occur only in the in-position in the members of the class.
//
// Equivalent Java is: '<? super T>' (our java type '?' Is a SUPERTYPE of T, so '?' can later be a reference to
// a lower-bound var (narrowing conversion).
// In Kotlin <in T> means we can later use a lower-bound subtype ref/var to reference this type (lower-bound refers to
// the call site).
// It is 'contravariant' because the lower-bound reference can only be of a single type.
// Note, we CAN set/add as you know the type ? is a supertype of T.
interface Comparable<in T> {
    operator fun compareTo(other: T): Int
}

fun contravarienceDemo(x: Comparable<Number>) {
    x.compareTo(1.0) // 1.0 has type Double, which is a subtype of Number
    // Thus, you can assign x to a variable of type Comparable<Double>
    val y: Comparable<Double> = x // This is OK, since T is an in-param (y<SubType> can ref x<SuperType>)
}

// Another in variance demo
class InConsumer<in T>(private val value : T) {
    fun consume(t : T) {
       println("class value: [$value] fun var [$t]")
    }
}

// these classes are used to demonstrate the concepts with the help of the tests
open class Mammal { }
class Cat: Mammal() { }
class Dog: Mammal() { }


