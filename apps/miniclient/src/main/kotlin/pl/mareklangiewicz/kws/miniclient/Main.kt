package pl.mareklangiewicz.kws.miniclient

import pl.mareklangiewicz.kws.WSClient
import pl.mareklangiewicz.kws.send

fun main(args: Array<String>) {
    val client = WSClient()
    client.connect("ws://localhost:7777")
    Thread.sleep(500)
    client.eventS.subscribe { println(it) }
    client.send("hello server")
    for (i in 1..5) {
        client.send("cool message nr $i of 5")
        Thread.sleep(500)
    }
    Thread.sleep(500)
    client.close()
}

