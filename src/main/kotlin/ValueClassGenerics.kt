// Generics on Value classes in Kotlin
// See corresponding test file for usage

// Value classes will exploit Project Valhalla in the future to give stack based performance
// with custom value classes, hence using parameterised value class as the wrapping type (only
// available with config: kotlinOptions.languageVersion = "1.8"

sealed class Vehicle
data class Car(val wheels: Int) : Vehicle()
data class Truck(val wheels: Int, val haulLimit: Int) : Vehicle()

@JvmInline
value class HasEngine<T>(val engineRelated: T)

// You don't have to use a value class as below, but you'll benefit from Valhalla in future
fun startVehicleEngine(vehicle: Vehicle){
    when(vehicle){
        is Car -> println("Starting Car with [${vehicle.wheels}] wheels")
        is Truck -> println("Starting Truck which can haul [${vehicle.haulLimit}] grams")
    }
}

// hasEngine in a value class, hence it will be more performant, especially with Valhallla
fun startVehicleEngine(hasEngine : HasEngine<Vehicle>) {
    when(val vehicle = hasEngine.engineRelated){
        is Car -> println("Starting Car with [${vehicle.wheels}] wheels")
        is Truck -> println("Starting Truck with [${vehicle.wheels}] wheels hauling [${vehicle.haulLimit}] grams")
    }
}

