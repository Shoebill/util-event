package net.gtaun.util.event

import kotlin.reflect.KClass

class Attentions {

    val objects: MutableList<Any> = mutableListOf()

    fun `object`(target: Any): Attentions {
        objects.add(target)
        return this
    }

    fun clazz(clz: KClass<*>): Attentions =
            clazz(clz.java)

    fun clazz(clz: Class<*>): Attentions {
        objects.add(clz)
        return this
    }

    companion object {
        @JvmStatic
        fun create(): Attentions = Attentions()

        @JvmStatic
        fun all(): Attentions = create().clazz(Any::class.java)
    }
}
