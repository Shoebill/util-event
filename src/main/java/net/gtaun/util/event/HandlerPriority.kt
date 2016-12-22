/**
 * Copyright (C) 2012-2016 MK124

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
 * Priority of event handler.
 */
enum class HandlerPriority constructor(val value: Short) {
    /**
     * The bottom priority. Handlers of this level will process events at the very last.
     */
    BOTTOM((-32768).toShort()),

    /**
     * The lowest priority.
     */
    LOWEST((-16384).toShort()),

    /**
     * The low priority.
     */
    LOW((-8192).toShort()),

    /**
     * The normal priority.
     */
    NORMAL(0.toShort()),

    /**
     * The high priority.
     */
    HIGH(8192.toShort()),

    /**
     * The highest priority.
     */
    HIGHEST(16384.toShort()),

    /**
     * The monitor priority. Handlers of this level will process events first.
     */
    MONITOR(32767.toShort())
}