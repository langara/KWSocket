package pl.elpassion.iotguard

import android.app.Application
import pl.elpassion.iotguard.api.Client
import pl.elpassion.iotguard.api.ClientImpl
import pl.elpassion.iotguard.api.Server
import pl.elpassion.iotguard.api.ServerImpl
import pl.elpassion.iotguard.commander.Commander
import pl.elpassion.iotguard.commander.CommanderImpl
import pl.elpassion.iotguard.robot.Babbler
import pl.elpassion.iotguard.robot.Robot
import pl.elpassion.iotguard.robot.RobotImpl

object DI {

    private val commander by lazy { CommanderImpl(provideNewClient(), provideLogger()) }

    private val robot by lazy { RobotImpl(provideNewServer(), provideBabbler(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    private val babbler by lazy { Babbler(provideApplication(), provideLogger()) }

    var provideCommander: () -> Commander = { commander }

    var provideRobot: () -> Robot = { robot }

    var provideBabbler: () -> Babbler = { babbler }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: () -> Server = { ServerImpl() }

    var provideNewClient: () -> Client = { ClientImpl() }
}