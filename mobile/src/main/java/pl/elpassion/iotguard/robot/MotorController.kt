package pl.elpassion.iotguard.robot

import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService

class MotorController {

    private val manager = PeripheralManagerService()
    private val leftMotorForward = manager.openGpio("BCM2").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorBackward = manager.openGpio("BCM3").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorPower = manager.openPwm("PWM0").apply { setPwmFrequencyHz(120.0);setEnabled(true) }
    private val rightMotorForward = manager.openGpio("BCM23").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorBackward = manager.openGpio("BCM24").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorPower = manager.openPwm("PWM1").apply { setPwmFrequencyHz(120.0); setEnabled(true) }

    fun moveForward() {
        leftMotorForward.value = true
        leftMotorBackward.value = false
        rightMotorForward.value = true
        rightMotorBackward.value = false
    }

    fun moveBackward() {
        leftMotorForward.value = false
        leftMotorBackward.value = true
        rightMotorForward.value = false
        rightMotorBackward.value = true
    }

    fun moveLeft() {
        leftMotorForward.value = true
        leftMotorBackward.value = false
        rightMotorForward.value = false
        rightMotorBackward.value = true
    }

    fun moveRight() {
        leftMotorForward.value = false
        leftMotorBackward.value = true
        rightMotorForward.value = true
        rightMotorBackward.value = false
    }

    fun stop() {
        leftMotorForward.value = false
        leftMotorBackward.value = false
        rightMotorForward.value = false
        rightMotorBackward.value = false
    }

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
        leftMotorPower.setEnabled(true)
        rightMotorPower.setPwmDutyCycle(right.toDouble())
        rightMotorPower.setEnabled(true)
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