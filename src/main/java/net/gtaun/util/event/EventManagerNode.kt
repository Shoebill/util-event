package net.gtaun.util.event

interface EventManagerNode : EventManager {
    fun cancelAll()

    fun destroy()

    val isDestroyed: Boolean

    val parent: EventManager
}
