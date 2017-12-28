@file:Suppress("IllegalIdentifier")

package pl.elpassion.iot.robot

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

class MotorController_Direction_Test {

    private val changeLeftMotorToBackward = mock<() -> Unit>()
    private val changeLeftMotorToForward = mock<() -> Unit>()
    private val changeRightMotorToForward = mock<() -> Unit>()
    private val changeRightMotorToBackward = mock<() -> Unit>()

    @Test
    fun `Should set left motor direction to backward in (135, 180) `() {
        setMotorDirections(degree = 140)
        verify(changeLeftMotorToBackward).invoke()
    }

    @Test
    fun `Should set left motor direction to forward in (-180, -135) `() {
        setMotorDirections(degree = -140)
        verify(changeLeftMotorToForward).invoke()
    }

    @Test
    fun `Should set left motor direction to backward in (-135, 0) `() {
        setMotorDirections(degree = -100)
        verify(changeLeftMotorToBackward).invoke()
    }

    @Test
    fun `Should set left motor direction to forward in (0, 135) `() {
        setMotorDirections(degree = 90)
        verify(changeLeftMotorToForward).invoke()
    }

    @Test
    fun `Should set right motor direction to backward in (0, 45) `() {
        setMotorDirections(degree = 30)
        verify(changeRightMotorToBackward).invoke()
    }

    @Test
    fun `Should set right motor direction to forward in (-45, 0) `() {
        setMotorDirections(degree = -30)
        verify(changeRightMotorToForward).invoke()
    }

    @Test
    fun `Should set right motor direction to backward in (-180, -45) `() {
        setMotorDirections(degree = -50)
        verify(changeRightMotorToBackward).invoke()
    }

    @Test
    fun `Should set right motor direction to forward in (45, 180) `() {
        setMotorDirections(degree = 90)
        verify(changeRightMotorToForward).invoke()
    }

    private fun setMotorDirections(degree: Int) =
            setMotorDirections(degree, changeLeftMotorToBackward, changeLeftMotorToForward, changeRightMotorToForward, changeRightMotorToBackward)
}