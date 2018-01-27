package pl.mareklangiewicz.kws.commander

import io.reactivex.Observable
import pl.mareklangiewicz.kws.Client
import pl.mareklangiewicz.kws.Connection
import pl.mareklangiewicz.kws.Event


class MultiClient(private val webRtcClient: Client, private val wsClient: Client) : Client {

    private var activeClient: Client? = null

    override val connections: List<Connection>
        get() = activeClient?.connections ?: emptyList()

    override val eventS: Observable<Event>
        get() = activeClient?.eventS ?: Observable.never()

    override fun close() {
        activeClient?.close()
        activeClient = null
    }

    override fun connect(address: String) {
        activeClient = if (address.startsWith("ws://")) wsClient else webRtcClient
        activeClient?.connect(address)
    }
}
