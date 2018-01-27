package pl.mareklangiewicz.kws.miniserver

import pl.mareklangiewicz.kws.WSServer

fun main(args: Array<String>) {
    val server = WSServer(7777)
    server.start()
    server.eventS.subscribe { println(it) }
    Thread.sleep(1_000_000)
}

