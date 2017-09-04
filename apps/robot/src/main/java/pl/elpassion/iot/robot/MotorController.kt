package pl.elpassion.iot.robot

import android.support.annotation.IntRange
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import pl.elpassion.iot.robot.MotorController.Direction.*

class MotorController {

    private val manager = PeripheralManagerService()
    private val rightMotorForward = manager.openGpio("BCM2").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorBackward = manager.openGpio("BCM3").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorPower = manager.openPwm("PWM0").apply { setPwmFrequencyHz(120.0);setEnabled(true) }
    private val leftMotorForward = manager.openGpio("BCM23").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorBackward = manager.openGpio("BCM24").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val upCameraServo = manager.openGpio("BCM7").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val downCameraServo = manager.openGpio("BCM8").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorPower = manager.openPwm("PWM1").apply { setPwmFrequencyHz(120.0); setEnabled(true) }

    enum class Direction { FORWARD, BACKWARD, STOP }

    private fun setupLeftWheel(direction: Direction) {
        leftMotorForward.value = direction == FORWARD
        leftMotorBackward.value = direction == BACKWARD
    }

    private fun setupRightWheel(direction: Direction) {
        rightMotorForward.value = direction == FORWARD
        rightMotorBackward.value = direction == BACKWARD
    }

    private fun setupWheels(left: Direction, right: Direction) {
        setupLeftWheel(left)
        setupRightWheel(right)
    }

    fun setupWheelsAndMove(leftDir: Direction, rightDir: Direction, @IntRange(from = 0, to = 100) leftPower: Int, @IntRange(from = 0, to = 100) rightPower: Int) {
        setupWheels(leftDir, rightDir)
        moveWheels(leftPower, rightPower)
    }

    fun setupWheelsAndMove(@IntRange(from = -100, to = 100) leftPower: Int, @IntRange(from = -100, to = 100) rightPower: Int) = setupWheelsAndMove(
            leftDir = if (leftPower > 0) FORWARD else BACKWARD,
            rightDir = if (rightPower > 0) FORWARD else BACKWARD,
            leftPower = Math.abs(leftPower),
            rightPower = Math.abs(rightPower)
    )

    fun moveForward() = setupWheelsAndMove(FORWARD, FORWARD, 100, 100)
    fun moveBackward() = setupWheelsAndMove(BACKWARD, BACKWARD, 100, 100)
    fun moveLeft() = setupWheelsAndMove(BACKWARD, FORWARD, 20, 20)
    fun moveRight() = setupWheelsAndMove(FORWARD, BACKWARD,20, 20)
    fun stop() = setupWheels(STOP, STOP)

    fun releasePins() {
        leftMotorForward.close()
        leftMotorBackward.close()
        rightMotorForward.close()
        rightMotorBackward.close()
        leftMotorPower.close()
        rightMotorPower.close()
    }

    fun lookUp() {
        upCameraServo.value = true
        downCameraServo.value = false
    }

    fun lookDown() {
        upCameraServo.value = false
        downCameraServo.value = true
    }

    fun lookAhead() {
        upCameraServo.value = false
        downCameraServo.value = false
    }

    fun moveWheels(left: Int, right: Int) {
        leftMotorPower.setPwmDutyCycle(left.toDouble())
        rightMotorPower.setPwmDutyCycle(right.toDouble())
    }
}

fun setMotorDirections(
        degree: Int,
        changeLeftMotorToBackward: () -> Unit,
        changeLeftMotorToForward: () -> Unit,
        changeRightMotorToForward: () -> Unit,
        changeRightMotorToBackward: () -> Unit) {

    if (degree in 45..180 || degree in -45..0) {
        changeRightMotorToForward()
    } else {
        changeRightMotorToBackward()
    }
    if (degree in -135..0 || degree in 135..180) {
        changeLeftMotorToBackward()
    } else {
        changeLeftMotorToForward()
    }
}
