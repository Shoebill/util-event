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

import kotlin.reflect.KClass

/**
 * Provide interface for managing event handlers and dispatching events.
 * @author MK124
 */
interface EventManager {

    /**
     * Register a event handler that related with an instance.
     * Only monitor the event that related with this instance.
     * @param <E> Event type
     * @param type Instance of Event class
     * @param handler Instance of event handler
     * @return Entry of event handler
     */
    fun <E : Event> registerHandler(type: KClass<E>, handler: (E) -> Unit) =
            registerHandler(type, handler, HandlerPriority.NORMAL)

    /**
     * Register a event handler that related with an instance.
     * Only monitor the event that related with this instance.
     * @param <E> Event type
     * @param type Instance of Event class
     * @param priority Custom priority of event handler
     * @param handler Instance of event handler
     * @return Entry of event handler
     */
    fun <E : Event> registerHandler(type: KClass<E>, handler: (E) -> Unit,
                                    priority: HandlerPriority) =
            registerHandler(type, handler, priority, Attentions.all())

    /**
     * Register a event handler that related with an instance.
     * Only monitor the event that related with this instance.
     * @param <E> Event type
     * @param type Instance of Event class
     * @param priority Custom priority of event handler
     * @param concerns Concerns instance
     * @param handler Instance of event handler
     * @return Entry of event handler
     */
    fun <E : Event> registerHandler(type: KClass<E>, handler: (E) -> Unit,
                                    priority: HandlerPriority, concerns: Attentions) : HandlerEntry =
            registerHandler(type.java, object : EventHandler<E> {
                override fun handleEvent(event: E) {
                    handler(event)
                }
            }, priority, concerns)

    /**
     * Register a event handler that related with an instance.
     * Only monitor the event that related with this instance.
     * @param <E> Event type
     * @param type Instance of Event class
     * @param handler Instance of event handler
     * @return Entry of event handler
     */
    fun <E : Event> registerHandler(type: Class<E>, handler: EventHandler<E>) =
            registerHandler(type, handler, HandlerPriority.NORMAL)

    /**
     * Register a event handler that related with an instance.
     * Only monitor the event that related with this instance.
     * @param <E> Event type
     * @param type Instance of Event class
     * @param priority Custom priority of event handler
     * @param handler Instance of event handler
     * @return Entry of event handler
     */
    fun <E : Event> registerHandler(type: Class<E>, handler: EventHandler<E>,
                                    priority: HandlerPriority) =
        registerHandler(type, handler, priority, Attentions.all())

    /**
     * Register a event handler that related with an instance.
     * Only monitor the event that related with this instance.
     * @param <E> Event type
     * @param type Instance of Event class
     * @param priority Custom priority of event handler
     * @param concerns Concerns instance
     * @param handler Instance of event handler
     * @return Entry of event handler
     */
    fun <E : Event> registerHandler(type: Class<E>, handler: EventHandler<E>,
                                    priority: HandlerPriority, concerns: Attentions) : HandlerEntry

    /**
     * Dispatch events according to handler's priority.
     * It might be interrupted if event allowed.
     * If the handler throw exception, it will be print out and keep dispatching.
     * @param <E> Event type
     * @param event Instance of event to be dispatch
     * @param objects Related objects
     */
    fun <E : Event> dispatchEvent(event: E, vararg objects: Any?) = dispatchEvent(throwableHandler, event, *objects)

    /**
     * Dispatch events according to handler's priority.
     * It might be interrupted if event allowed.
     * @param <E> Event type
     * @param handler Instance of exception handler. Print out the exception directly if it's `null`.
     * @param event Instance of event to be dispatch
     * @param objects Related objects
     */
    fun <E : Event> dispatchEvent(handler: ThrowableHandler?, event: E, vararg objects: Any?)

    /**
     * @return Child node
     */
    fun createChildNode(): EventManagerNode

    /**
     * Throwable handler
     */
    var throwableHandler: ThrowableHandler
}
