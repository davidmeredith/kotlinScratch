import org.junit.jupiter.api.Test

internal class ValueClassGenericsKtTest {

    @Test
    fun startVehicleEngine() {
        startVehicleEngine(HasEngine(Car(5)))
        startVehicleEngine(HasEngine(Truck(5, 5)))
    }

    @Test
    fun testStartVehicleEngine() {
        startVehicleEngine(Car(5))
        startVehicleEngine(Truck(5, 5))

    }
}