
// Generics / Patameterised Classes in Kotlin (Declaration Site Variance)
// See corresponding test file for usage


// out T - later use (upper bound)
// --------------------------------
// The general rule is this: when a type parameter T of a class C is declared out,
// it may occur only in the out-position in the members of C.
//
// out == <? extends T>. It means we can later use an (upper bound) supertype ref/var to reference this type
// (covariant, because the upper bound ref could actually be of multiple subtypes)
// == produces an element (can't set/add as you don't know the actual type of T, T or an unknown subtype of T)
// (basically, it avoids trouble due to subtyping on the use site as you can return/use a wider covariant ref)
interface Source<out T> {
    fun nextT(): T
}

fun covarianceDemo(strs: Source<String>) {
    val objects: Source<Any> = strs // This is OK, since T is an out-param (C<SuperType> can ref C<SubType>)
    var v : Any = objects.nextT()
}

// Another covariance demo
class OutProducer<out T>(private val t: T) {
    fun get(): T {
        return t
    }
}


// in T - later use (lower bound)
// ------------------------------
// in == <? super T>. It means we can later use a lower bound subtype ref/var to reference this type
// (contravariant, because the lower bound ref can only be of a single type)
// == consume element (can set/add as you know the type ? is a supertype of T)
interface Comparable<in T> {
    operator fun compareTo(other: T): Int
}

fun contravarienceDemo(x: Comparable<Number>) {
    x.compareTo(1.0) // 1.0 has type Double, which is a subtype of Number
    // Thus, you can assign x to a variable of type Comparable<Double>
    val y: Comparable<Double> = x // This is OK, since T is an in-param (C<SubType> can ref C<SuperType>)
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


