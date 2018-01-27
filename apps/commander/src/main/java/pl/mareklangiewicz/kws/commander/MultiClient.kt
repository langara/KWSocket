package pl.mareklangiewicz.kws.commander

import io.reactivex.Observable
import pl.mareklangiewicz.kws.api.Client
import pl.mareklangiewicz.kws.api.Connection
import pl.mareklangiewicz.kws.api.Event


class MultiClient(private val webRtcClient: Client, private val wsClient: Client) : Client {

    private var activeClient: Client? = null

    override val connections: List<Connection>
        get() = activeClient?.connections ?: emptyList()

    override val events: Observable<Event>
        get() = activeClient?.events ?: Observable.never()

    override fun disconnect() {
        activeClient?.disconnect()
        activeClient = null
    }

    override fun connect(address: String) {
        activeClient = if (address.startsWith("ws://")) wsClient else webRtcClient
        activeClient?.connect(address)
    }
}
