package net.gtaun.util.event

internal open class EventManagerChild constructor(override val parent: EventManager,
                                                          private val parentEventManager: EventManagerNode? = null,
                                                          override var isDestroyed: Boolean = false) :
        EventManagerNode {

    override var throwableHandler: ThrowableHandler = EventManagerRoot.DEFAULT_THROWABLE_HANDLER

    private val handlerEntries: MutableList<HandlerEntry> = mutableListOf()
    private val children: MutableList<EventManagerNode> = mutableListOf()

    @Throws(Throwable::class)
    protected fun finalize() {
        if (!isDestroyed)
            destroy()
    }

    @Throws(IllegalStateException::class)
    private fun checkDestroyState() {
        if (isDestroyed)
            throw IllegalStateException("EventManagerNode has been destroyed!")
    }

    private fun destroyChild(node: EventManagerNode) {
        if (isDestroyed) return
        children.remove(node)
    }

    override fun <E : Event> registerHandler(type: Class<E>, handler: EventHandler<E>, priority: HandlerPriority,
                                             concerns: Attentions): HandlerEntry {
        checkDestroyState()

        val rootEntry = parent.registerHandler(type, handler, priority, concerns)
        val entry = object : AbstractHandlerEntry(type, concerns, handler, priority.value) {
            override val eventManager: EventManager
                get() = this@EventManagerChild

            override fun cancel() {
                rootEntry.cancel()
                isCanceled = true
                if (!isDestroyed)
                    handlerEntries.remove(this)
            }
        }

        handlerEntries.add(entry)
        return entry
    }

    override fun <E : Event> dispatchEvent(handler: ThrowableHandler?, event: E, vararg objects: Any?) {
        checkDestroyState()
        parent.dispatchEvent(handler, event, *objects)
    }

    override fun createChildNode(): EventManagerNode {
        val node = parent.createChildNode()
        children.add(node)
        return node
    }

    override fun cancelAll() {
        handlerEntries.forEach { it.cancel() }
        handlerEntries.clear()
    }

    override fun destroy() {
        if (parentEventManager is EventManagerChild && parentEventManager.isDestroyed == false) {
            parentEventManager.destroyChild(this)
        }

        isDestroyed = true

        children.forEach { it.destroy() }
        children.clear()

        handlerEntries.forEach { it.cancel() }
        handlerEntries.clear()
    }
}
