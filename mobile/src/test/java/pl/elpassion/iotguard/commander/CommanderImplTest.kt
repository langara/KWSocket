@file:Suppress("IllegalIdentifier")

package pl.elpassion.iotguard.commander

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import pl.elpassion.iotguard.SimpleLogger
import pl.elpassion.iotguard.api.*
import pl.elpassion.iotguard.commander.CommanderAction.*
import pl.elpassion.iotguard.commander.CommanderState.Connected
import pl.elpassion.iotguard.commander.CommanderState.Disconnected

class CommanderImplTest {

    val events = PublishSubject.create<Event>()
    val socket = mock<Socket>()
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
        commander.perform(Connect(url))
        verify(client).connect(url)
    }

    @Test
    fun `Send move forward command`() {
        commander.perform(MoveForward)
        verify(socket).send("move forward")
    }

    @Test
    fun `Send move backward command`() {
        commander.perform(MoveBackward)
        verify(socket).send("move backward")
    }

    @Test
    fun `Send move left command`() {
        commander.perform(MoveLeft)
        verify(socket).send("move left")
    }

    @Test
    fun `Send move right command`() {
        commander.perform(MoveRight)
        verify(socket).send("move right")
    }

    @Test
    fun `Send stop command`() {
        commander.perform(Stop)
        verify(socket).send("stop")
    }

    @Test
    fun `Disconnected state by default`() {
        observer.assertValue(Disconnected)
    }

    @Test
    fun `Change to connected state when connection opens`() {
        events.onNext(Open())
        observer.assertLastValue(Connected)
    }

    @Test
    fun `Change to disconnected state when connection closes`() {
        events.onNext(Open())
        events.onNext(Close(0))
        observer.assertLastValue(Disconnected)
    }
}


fun <T> TestObserver<T>.assertLastValue(value: T) {
    assertEquals(value, values().last())
}
