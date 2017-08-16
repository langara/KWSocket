package pl.elpassion.iotguard.commander

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test
import pl.elpassion.iotguard.SimpleLogger
import pl.elpassion.iotguard.api.Client
import pl.elpassion.iotguard.api.Socket

class CommanderImplTest {

    val socket = mock<Socket>()

    val client = mock<Client>().apply {
        whenever(connections).thenReturn(listOf(socket))
        whenever(events).thenReturn(Observable.empty())
    }

    val logger = SimpleLogger()

    val commander = CommanderImpl(client, logger)

    @Test
    fun `Send move forward command`() {
        commander.perform(CommanderAction.MoveForward)
        verify(socket).send("move forward")
    }

    @Test
    fun `Send move backward command`() {
        commander.perform(CommanderAction.MoveBackward)
        verify(socket).send("move backward")
    }

    @Test
    fun `Send move left command`() {
        commander.perform(CommanderAction.MoveLeft)
        verify(socket).send("move left")
    }

    @Test
    fun `Send move right command`() {
        commander.perform(CommanderAction.MoveRight)
        verify(socket).send("move right")
    }

    @Test
    fun `Send stop command`() {
        commander.perform(CommanderAction.Stop)
        verify(socket).send("stop")
    }
}