/**
 * Copyright (C) 2011-2016 MK124

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.util.event

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Standard implementation class of event manager.
 * @author MK124
 * @author Marvin Haschker
 */
class EventManagerRoot : EventManager {

    override var throwableHandler: ThrowableHandler = DEFAULT_THROWABLE_HANDLER
    private val handlerEntryContainersMap: MutableMap<Class<out Event>, MutableMap<Any, Queue<HandlerEntry>>> = mutableMapOf()

    override fun <E : Event> registerHandler(type: Class<E>, handler: EventHandler<E>,
                                             priority: HandlerPriority, concerns: Attentions): HandlerEntry {
        val entry = object : AbstractHandlerEntry(type, concerns, handler, priority.value) {
            override val eventManager: EventManager
                get() = this@EventManagerRoot

            override fun cancel() {
                removeHandler(this)
                isCanceled = true
            }
        }
        return addHandlerEntry(entry)
    }

    private fun addHandlerEntry(entry: HandlerEntry): HandlerEntry {
        val type = entry.type
        val attentedObjects = entry.attentions.objects

        for (attentedObject in attentedObjects) {
            var objectEntriesMap = handlerEntryContainersMap[type]
            if (objectEntriesMap == null) {
                objectEntriesMap = ConcurrentHashMap<Any, Queue<HandlerEntry>>()
                handlerEntryContainersMap.put(type, objectEntriesMap)
            }

            var entries: Queue<HandlerEntry>? = objectEntriesMap[attentedObject]
            if (entries == null) {
                entries = ConcurrentLinkedQueue<HandlerEntry>()
                objectEntriesMap.put(attentedObject, entries)
            }

            entries.add(entry)
        }
        return entry
    }

    private fun removeHandler(entry: HandlerEntry?) {
        if (entry == null) return

        val type = entry.type
        val attentedObjects = entry.attentions.objects

        for (attentedObject in attentedObjects) {
            val objectEntriesMap = handlerEntryContainersMap[type] ?: return

            val entries = objectEntriesMap[attentedObject] ?: return

            entries.remove(entry)

            if (entries.size == 0) objectEntriesMap.remove(attentedObject)
            if (objectEntriesMap.isEmpty()) handlerEntryContainersMap.remove(type)
        }
    }

    override fun <T : Event> dispatchEvent(handler: ThrowableHandler?, event: T, vararg objects: Any?) {
        val throwableHandler = handler ?: DEFAULT_THROWABLE_HANDLER

        val type = event.javaClass
        val handlerEntryQueue = PriorityQueue<HandlerEntry>(16, HANDLER_ENTRY_PRIORITY_COMPARATOR)

        val objectEntriesMap = handlerEntryContainersMap[type] ?: return

        for (`object` in objects.filterNotNull()) {
            val cls = `object`.javaClass

            val entries = objectEntriesMap[`object`]
            if (entries != null) {
                handlerEntryQueue.addAll(entries.toList())
            }

            val interfaces = cls.interfaces
            interfaces
                    .mapNotNull { objectEntriesMap[it] }
                    .forEach { handlerEntryQueue.addAll(it.toList()) }

            var clz = cls
            while (clz != Any::class.java) {
                val classEntries = objectEntriesMap[clz]
                if (classEntries != null) {
                    handlerEntryQueue.addAll(classEntries.toList())
                }
                clz = clz.superclass as Class<Any>
            }
        }

        val entries = objectEntriesMap[Any::class.java]
        if (entries != null) {
            handlerEntryQueue.addAll(entries.toList())
        }

        while (!handlerEntryQueue.isEmpty() && !event.isInterrupted) {
            val entry = handlerEntryQueue.poll()
            val handler = entry.handler as EventHandler<T>

            try {
                handler.handleEvent(event)
            } catch (e: Throwable) {
                throwableHandler.handleThrowable(e)
            }

        }
    }

    override fun createChildNode(): EventManagerNode = EventManagerChild(this)

    companion object {

        val DEFAULT_THROWABLE_HANDLER = object : ThrowableHandler {
            override fun handleThrowable(throwable: Throwable) {
                if (throwable is AssertionError) throw throwable
                throwable.printStackTrace()
            }

        }

        private val HANDLER_ENTRY_PRIORITY_COMPARATOR = { o1: HandlerEntry, o2: HandlerEntry ->
            o2.priority - o1.priority }
    }
}
