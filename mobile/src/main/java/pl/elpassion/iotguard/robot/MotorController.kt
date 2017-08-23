package pl.elpassion.iotguard.robot

import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import pl.elpassion.iotguard.robot.MotorController.Direction.*

class MotorController {

    private val manager = PeripheralManagerService()
    private val leftMotorForward = manager.openGpio("BCM2").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorBackward = manager.openGpio("BCM3").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorPower = manager.openPwm("PWM0").apply { setPwmFrequencyHz(120.0);setEnabled(true) }
    private val rightMotorForward = manager.openGpio("BCM23").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorBackward = manager.openGpio("BCM24").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorPower = manager.openPwm("PWM1").apply { setPwmFrequencyHz(120.0); setEnabled(true) }

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

    private fun setupWheelsAndMove(leftDir: Direction, rightDir: Direction, leftPower: Int, rightPower: Int) {
        setupWheels(leftDir, rightDir)
        moveWheels(leftPower, rightPower)
    }

    fun moveForward() = setupWheelsAndMove(FORWARD, FORWARD, 100, 100)
    fun moveBackward() = setupWheelsAndMove(BACKWARD, BACKWARD, 100, 100)
    fun moveLeft() = setupWheelsAndMove(BACKWARD, FORWARD, 100, 100)
    fun moveRight() = setupWheelsAndMove(FORWARD, BACKWARD, 100, 100)
    fun stop() = setupWheels(STOP, STOP)

    fun releasePins() {
        leftMotorForward.close()
        leftMotorBackward.close()
        rightMotorForward.close()
        rightMotorBackward.close()
        leftMotorPower.close()
        rightMotorPower.close()
    }

    fun moveWheels(left: Int, right: Int) {
        leftMotorPower.setPwmDutyCycle(left.toDouble())
        rightMotorPower.setPwmDutyCycle(right.toDouble())
    }

    fun moveEngines(@IntRange(from = -180, to = 180) angle: Int, @FloatRange(from = 0.0, to = 1.0) power: Double) {
        if (angle < 0) {
            moveBackward()
            setMotorsPower(angle.unaryMinus(), power)
        } else {
            moveForward()
            setMotorsPower(angle, power)
        }
    }

    private fun setMotorsPower(angle: Int, power: Double) {
        if (angle < 90) {
            val rightPower: Double = 100.0 * power
            val leftPower: Double = power * 100.0 * (angle / 90.0)
            rightMotorPower.setPwmDutyCycle(rightPower)
            leftMotorPower.setPwmDutyCycle(leftPower)
        } else {
            val leftPower: Double = 100.0 * power
            val rightPower: Double = power * 100.0 * (-angle / 90.0 + 2)
            leftMotorPower.setPwmDutyCycle(leftPower)
            rightMotorPower.setPwmDutyCycle(rightPower)
        }
    }
}

fun setMotorDirections(
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

private fun Int.isBetween(from: Int, to: Int) = from <= this && this < to