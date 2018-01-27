@file:Suppress("IllegalIdentifier")

package pl.mareklangiewicz.kws.commander

import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.mareklangiewicz.kws.Client
import pl.mareklangiewicz.kws.Close
import pl.mareklangiewicz.kws.Connection
import pl.mareklangiewicz.kws.Event
import pl.mareklangiewicz.kws.Open
import pl.mareklangiewicz.kws.commander.CommanderAction.Connect
import pl.mareklangiewicz.kws.commander.CommanderAction.Disconnect
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveBackward
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveForward
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveLeft
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveRight
import pl.mareklangiewicz.kws.commander.CommanderAction.Say
import pl.mareklangiewicz.kws.commander.CommanderAction.Stop
import pl.mareklangiewicz.kws.commander.CommanderState.Connected
import pl.mareklangiewicz.kws.commander.CommanderState.Disconnected
import pl.mareklangiewicz.kws.loggers.SimpleLogger

class CommanderImplTest {

    private val eventS: PublishRelay<Event> = PublishRelay.create<Event>()
    private val connection = mock<Connection>()
    private val client = mock<Client>()
    private val observer = TestObserver<CommanderState>()

    init {
        whenever(client.connections).thenReturn(listOf(connection))
        whenever(client.eventS).thenReturn(eventS)
    }

    private val logger = SimpleLogger()

    private val commander = CommanderImpl(client, logger)

    @Before
    fun setup() {
        commander.stateS.subscribe(observer)
    }

    @Test
    fun `Connect to websocket client`() {
        val url = "ws://1.2.3.4"
        commander.actionS.accept(Connect(url))
        verify(client).connect(url)
    }

    @Test
    fun `Disconnect from client`() {
        commander.actionS.accept(Disconnect)
        verify(client).close()
    }

    @Test
    fun `Send move forward command`() {
        commander.actionS.accept(MoveForward)
        verify(connection).send("move forward")
    }

    @Test
    fun `Send move backward command`() {
        commander.actionS.accept(MoveBackward)
        verify(connection).send("move backward")
    }

    @Test
    fun `Send move left command`() {
        commander.actionS.accept(MoveLeft)
        verify(connection).send("move left")
    }

    @Test
    fun `Send move right command`() {
        commander.actionS.accept(MoveRight)
        verify(connection).send("move right")
    }

    @Test
    fun `Send stop command`() {
        commander.actionS.accept(Stop)
        verify(connection).send("stop")
    }

    @Test
    fun `Disconnected state by default`() {
        observer.assertValue(Disconnected)
    }

    @Test
    fun `Change to connected state when connection opens`() {
        eventS.accept(Open())
        observer.assertLastValue(Connected)
    }

    @Test
    fun `Change to disconnected state when connection closes`() {
        eventS.accept(Open())
        eventS.accept(Close(0))
        observer.assertLastValue(Disconnected)
    }

    @Test
    fun `Send say command`() {
        val sentence = "Alice has a cat."
        commander.actionS.accept(Say(sentence))
        verify(connection).send("say $sentence")
    }
}


fun <T> TestObserver<T>.assertLastValue(value: T) {
    assertEquals(value, values().last())
}
