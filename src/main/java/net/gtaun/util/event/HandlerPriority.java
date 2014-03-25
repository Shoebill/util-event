/**
 * Copyright (C) 2012-2013 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.util.event;

/**
 * Priority of event handler.
 */
public enum HandlerPriority
{
	/**
	 * The bottom priority. Handlers of this level will process events at the very last.
	 * Use {@code LOWEST} if not necessary.
	 * Don't use if you don't know what you are doing.
	 */
	BOTTOM((short) -32768),
	
	/**
	 * The lowest priority.
	 */
	LOWEST((short) -16384),
	
	/**
	 * The low priority.
	 */
	LOW((short) -8192),
	
	/**
	 * The normal priority.
	 */
	NORMAL((short) 0),
	
	/**
	 * The high priority.
	 */
	HIGH((short) 8192),
	
	/**
	 * The highest priority.
	 */
	HIGHEST((short) 16384),
	
	/**
	 * The monitor priority. Handlers of this level will process events first.
	 * Use {@code HIGHEST} if not necessary.
	 * Don't use if you don't know what you are doing.
	 */
	MONITOR((short) 32767);
	
	private final short value;
	
	
	private HandlerPriority(short value)
	{
		this.value = value;
	}
	
	/**
	 * Get the actual value of priority.
	 * 
	 * @return Value of priority
	 */
	public short getValue()
	{
		return value;
	}
}