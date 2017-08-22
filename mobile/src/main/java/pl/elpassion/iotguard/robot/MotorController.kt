package pl.elpassion.iotguard.robot

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService

class MotorController {

    private val manager = PeripheralManagerService()
    private val leftMotorForward = manager.openGpio("BCM2").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val leftMotorBackward = manager.openGpio("BCM3").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorForward = manager.openGpio("BCM23").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }
    private val rightMotorBackward = manager.openGpio("BCM24").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH) }

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
    }

    fun moveEngines(left: Int, right: Int) {
        // TODO
    }

}