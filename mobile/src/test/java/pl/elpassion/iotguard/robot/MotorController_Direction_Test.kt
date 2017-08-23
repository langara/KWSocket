package pl.elpassion.iotguard.robot

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
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

    private fun setMotorDirections(
            degree: Int,
            changeLeftMotorToBackward: () -> Unit,
            changeLeftMotorToForward: () -> Unit,
            changeRightMotorToForward: () -> Unit,
            changeRightMotorToBackward: () -> Unit) {

        if (degree.isBetween(45, 180) or degree.isBetween(-45, 0)) {
            changeRightMotorToForward()
        } else {
            changeRightMotorToBackward()
        }
        if (degree.isBetween(-135, 0) or degree.isBetween(135, 180)) {
            changeLeftMotorToBackward()
        } else {
            changeLeftMotorToForward()
        }
    }

    fun Int.isBetween(from: Int, to: Int) = from <= this && this < to

}