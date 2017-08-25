package pl.elpassion.iotguard

import android.app.Application
import pl.elpassion.api.Client
import pl.elpassion.iot.api.ClientImpl
import pl.elpassion.api.Server
import pl.elpassion.iot.api.ServerImpl
import pl.elpassion.iotguard.robot.Babbler
import pl.elpassion.iotguard.robot.Robot
import pl.elpassion.iotguard.robot.RobotImpl
import pl.elpassion.loggers.Logger
import pl.elpassion.loggers.SimpleLogger

object DI {

    private val robot by lazy { RobotImpl(provideNewServer(), provideBabbler(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    private val babbler by lazy { Babbler(provideApplication(), provideLogger()) }

    var provideRobot: () -> Robot = { robot }

    var provideBabbler: () -> Babbler = { babbler }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: () -> Server = { ServerImpl() }

    var provideNewClient: () -> Client = { ClientImpl() }
}