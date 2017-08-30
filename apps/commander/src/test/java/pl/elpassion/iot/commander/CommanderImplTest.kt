@file:Suppress("IllegalIdentifier")

package pl.elpassion.iot.commander

import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.elpassion.iot.api.*
import pl.elpassion.iot.commander.CommanderAction.*
import pl.elpassion.iot.commander.CommanderState.Connected
import pl.elpassion.iot.commander.CommanderState.Disconnected
import pl.elpassion.loggers.SimpleLogger

class CommanderImplTest {

    val events = PublishRelay.create<Event>()
    val socket = mock<Connection>()
    val client = mock<Client>()
    val observer = TestObserver<CommanderState>()

    init {
        whenever(client.connections).thenReturn(listOf(socket))
        whenever(client.events).thenReturn(events)
    }

    val logger = SimpleLogger()

    val commander = CommanderImpl(client, logger)

    @Before
    fun setup() {
        commander.states.subscribe(observer)
    }

    @Test
    fun `Connect to websocket client`() {
        val url = "ws://1.2.3.4"
        commander.actions.accept(Connect(url))
        verify(client).connect(url)
    }

    @Test
    fun `Send move forward command`() {
        commander.actions.accept(MoveForward)
        verify(socket).send("move forward")
    }

    @Test
    fun `Send move backward command`() {
        commander.actions.accept(MoveBackward)
        verify(socket).send("move backward")
    }

    @Test
    fun `Send move left command`() {
        commander.actions.accept(MoveLeft)
        verify(socket).send("move left")
    }

    @Test
    fun `Send move right command`() {
        commander.actions.accept(MoveRight)
        verify(socket).send("move right")
    }

    @Test
    fun `Send stop command`() {
        commander.actions.accept(Stop)
        verify(socket).send("stop")
    }

    @Test
    fun `Disconnected state by default`() {
        observer.assertValue(Disconnected)
    }

    @Test
    fun `Change to connected state when connection opens`() {
        events.accept(Open())
        observer.assertLastValue(Connected)
    }

    @Test
    fun `Change to disconnected state when connection closes`() {
        events.accept(Open())
        events.accept(Close(0))
        observer.assertLastValue(Disconnected)
    }

    @Test
    fun `Send say command`() {
        val sentence = "Alice has a cat."
        commander.actions.accept(Say(sentence))
        verify(socket).send("say $sentence")
    }
}


fun <T> TestObserver<T>.assertLastValue(value: T) {
    assertEquals(value, values().last())
}
