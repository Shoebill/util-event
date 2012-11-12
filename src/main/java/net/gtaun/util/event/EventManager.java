/**
 * Copyright (C) 2011-2012 MK124
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
 * 
 * 
 * @author MK124
 */
public interface EventManager
{
	public interface HandlerEntry
	{
		public EventManager getEventManager();
		public Class<? extends Event> getType();
		public Object getRelatedObject();
		public Class<?> getRelatedClass();
		public EventHandler getHandler();
		public short getPriority();
	}
	
	public static interface ThrowableHandler
	{
		void handleThrowable(Throwable throwable);
	}
	
	public enum EventHandlerPriority
	{
		BOTTOM((short) -32768),
		LOWEST((short) -16384),
		LOW((short) -8192),
		NORMAL((short) 0),
		HIGH((short) 8192),
		HIGHEST((short) 16384),
		MONITOR((short) 32767);
		
		private final short value;
		
		
		private EventHandlerPriority(short value)
		{
			this.value = value;
		}
		
		public short getValue()
		{
			return value;
		}
	}
	
	
	HandlerEntry addHandler(Class<? extends Event> type, EventHandler handler, EventHandlerPriority priority);
	HandlerEntry addHandler(Class<? extends Event> type, EventHandler handler, short priority);
	HandlerEntry addHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, EventHandlerPriority priority);
	HandlerEntry addHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, short priority);
	HandlerEntry addHandler(Class<? extends Event> type, Object object, EventHandler handler, EventHandlerPriority priority);
	HandlerEntry addHandler(Class<? extends Event> type, Object object, EventHandler handler, short priority);
	
	void removeHandler(Class<? extends Event> type, EventHandler handler);
	void removeHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler);
	void removeHandler(Class<? extends Event> type, Object object, EventHandler handler);
	void removeHandler(HandlerEntry entry);
	
	boolean hasHandler(Class<? extends Event> type, EventHandler handler);
	boolean hasHandler(Class<? extends Event> type, Class<?> clz);
	boolean hasHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler);
	boolean hasHandler(Class<? extends Event> type, Object object);
	boolean hasHandler(Class<? extends Event> type, Object object, EventHandler handler);
	boolean hasHandler(HandlerEntry entry);
	
	<T extends Event> void dispatchEvent(T event, Object... objects);
	<T extends Event> void dispatchEvent(ThrowableHandler handler, T event, Object... objects);
}
