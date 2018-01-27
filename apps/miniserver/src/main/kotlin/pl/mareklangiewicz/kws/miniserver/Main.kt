package pl.mareklangiewicz.kws.miniserver

import io.reactivex.rxkotlin.ofType
import pl.mareklangiewicz.kws.Message
import pl.mareklangiewicz.kws.WSServer

fun main(args: Array<String>) {
    val server = WSServer(7777)
    server.start()
    server.eventS.subscribe { println(it) }
    server.eventS.ofType<Message>()
        .filter { it.message.contains("hello", ignoreCase = true) }
        .subscribe { it.source?.send("Hi!") }
    Thread.sleep(1_000_000)
}

