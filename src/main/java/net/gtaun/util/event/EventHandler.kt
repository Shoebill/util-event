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

/**
 * Interface of event handler.
 * @author MK124
 */
@FunctionalInterface
interface EventHandler<in E : Event> {
    /**
     * Handle event.
     * @param event Instance of event.
     * @throws Throwable Exceptions that might be thrown.
     */
    @Throws(Throwable::class)
    fun handleEvent(event: E)
}

fun <E: Event> EventHandler(handler: (E) -> Unit) = object : EventHandler<E> {
    override fun handleEvent(event: E) {
        handler(event)
    }
}