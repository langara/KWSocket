package pl.elpassion.iotguard.robot

import org.junit.Assert
import org.junit.Test

class MotorController_Direction_Test {

    enum class DIRECTION {
        BACKWARD,
        FORWARD,
        NOT_SET
    }

    private var leftMotorDirection = DIRECTION.NOT_SET
    private val changeLeftMotorToBackward = { leftMotorDirection = DIRECTION.BACKWARD }
    private val changeLeftMotorToForward = { leftMotorDirection = DIRECTION.FORWARD }

    @Test
    fun `Should set left motor direction to backward in (135, 180) `() {
        setMotorDirections(changeLeftMotorToBackward)
        Assert.assertEquals(leftMotorDirection, DIRECTION.BACKWARD)
    }

    private fun setMotorDirections(changeLeftMotorToBackward: () -> Unit) {
        changeLeftMotorToBackward()
    }

}