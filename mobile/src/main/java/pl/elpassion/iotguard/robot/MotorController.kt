package pl.elpassion.iotguard.robot

import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import pl.elpassion.iotguard.robot.MotorController.Direction.*

class MotorController {

    private val manager = PeripheralManagerService()
    private val rightMotorForward = manager.openGpio("BCM2").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorBackward = manager.openGpio("BCM3").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorPower = manager.openPwm("PWM0").apply { setPwmFrequencyHz(120.0);setEnabled(true) }
    private val leftMotorForward = manager.openGpio("BCM23").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorBackward = manager.openGpio("BCM24").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
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

    fun setupWheelsAndMove(leftDir: Direction, rightDir: Direction, leftPower: Int, rightPower: Int) {
        setupWheels(leftDir, rightDir)
        moveWheels(leftPower, rightPower)
    }

    fun setupWheelsAndMove(leftPower: Int, rightPower: Int) = setupWheelsAndMove(
            leftDir = if (leftPower > 0) FORWARD else BACKWARD,
            rightDir = if (rightPower > 0) FORWARD else BACKWARD,
            leftPower = Math.abs(leftPower),
            rightPower = Math.abs(rightPower)
    )

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

    fun changeRightMotorDirectionToForward() {
        rightMotorForward.value = true
        rightMotorBackward.value = false
    }

    fun changeLeftMotorDirectionToForward() {
        leftMotorForward.value = true
        leftMotorBackward.value = false
    }

    fun changeRightMotorDirectionToBackward() {
        rightMotorForward.value = false
        rightMotorBackward.value = true
    }

    fun changeLeftMotorDirectionToBackward() {
        leftMotorForward.value = false
        leftMotorBackward.value = true
    }

    fun moveEngines(@IntRange(from = -180, to = 180) angle: Int, @FloatRange(from = 0.0, to = 1.0) power: Double) {
        setMotorDirections(angle, this::changeLeftMotorDirectionToBackward, this::changeLeftMotorDirectionToForward, this::changeRightMotorDirectionToForward, this::changeRightMotorDirectionToBackward)
        if (angle < 0) {
            setMotorsPower(angle.unaryMinus(), power)
        } else {
            setMotorsPower(angle, power)
        }
    }

    private fun setMotorsPower(angle: Int, power: Double) {
        if (angle < 90) {
            val leftPower: Double = 100.0 * power
            val rightPower: Double = power * 100.0 * Math.abs((angle / 45.0) - 1.0)
            rightMotorPower.setPwmDutyCycle(rightPower)
            leftMotorPower.setPwmDutyCycle(leftPower)
        } else {
            val rightPower: Double = 100.0 * power
            val leftPower: Double = power * 100.0 * Math.abs((-angle / 45.0) + 3.0)
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
