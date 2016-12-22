package net.gtaun.util.event

abstract class AbstractHandlerEntry(override val type: Class<out Event>,
                                    override val attentions: Attentions,
                                    override val handler: EventHandler<*>,
                                    override val priority: Short) : HandlerEntry {

    protected var isCanceled = false

    @Throws(Throwable::class)
    protected fun finalize() {
        if (!isCanceled)
            cancel()
    }

    override fun toString(): String =
            "AbstractHandlerEntry(type=$type, attentions=$attentions, handler=$handler," +
                    " priority=$priority, isCanceled=$isCanceled)"

}
