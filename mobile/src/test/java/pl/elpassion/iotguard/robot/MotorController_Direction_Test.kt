package pl.elpassion.iotguard.robot

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class MotorController_Direction_Test {

    private val changeLeftMotorToBackward = mock<() -> Unit>()
    private val changeLeftMotorToForward = mock<() -> Unit>()

    @Test
    fun `Should set left motor direction to backward in (135, 180) `() {
        setMotorDirections(
                degree = 140,
                changeLeftMotorToBackward = changeLeftMotorToBackward,
                changeLeftMotorToForward = mock())

        verify(changeLeftMotorToBackward).invoke()
    }

    @Test
    fun `Should set left motor direction to forward in (-180, -135) `() {

        setMotorDirections(
                degree = -140,
                changeLeftMotorToBackward = changeLeftMotorToBackward,
                changeLeftMotorToForward = changeLeftMotorToForward)

        verify(changeLeftMotorToForward).invoke()
    }

    private fun setMotorDirections(
            degree: Int,
            changeLeftMotorToBackward: () -> Unit,
            changeLeftMotorToForward: () -> Unit) {

        if (degree < 0) {
            changeLeftMotorToForward()
        } else {
            changeLeftMotorToBackward()
        }
    }

}