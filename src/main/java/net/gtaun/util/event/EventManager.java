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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 
 * @author MK124
 */
public interface EventManager
{
	public static class Entry
	{
		private Class<? extends Event> type;
		private Object relatedObject;
		private EventHandler handler;
		private short priority;
		
		
		public Entry(Class<? extends Event> type, Object relatedObject, EventHandler handler, short priority)
		{
			this.type = type;
			this.relatedObject = relatedObject;
			this.handler = handler;
			this.priority = priority;
		}
		
		@Override
		public String toString()
		{
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
		
		public Class<? extends Event> getType()
		{
			return type;
		}
		
		public Object getRelatedObject()
		{
			return relatedObject;
		}
		
		public Class<?> getRelatedClass()
		{
			if (relatedObject instanceof Class) return (Class<?>) relatedObject;
			return null;
		}
		
		public EventHandler getHandler()
		{
			return handler;
		}
		
		public short getPriority()
		{
			return priority;
		}
	}
	
	public static interface ThrowableHandler
	{
		void handleThrowable(Throwable throwable);
	}
	
	public enum Priority
	{
		BOTTOM((short) -32768),
		LOWEST((short) -16384),
		LOW((short) -8192),
		NORMAL((short) 0),
		HIGH((short) 8192),
		HIGHEST((short) 16384),
		MONITOR((short) 32767);
		
		private final short value;
		
		
		private Priority(short value)
		{
			this.value = value;
		}
		
		public short getValue()
		{
			return value;
		}
	}
	
	
	Entry addHandler(Class<? extends Event> type, EventHandler handler, Priority priority);
	Entry addHandler(Class<? extends Event> type, EventHandler handler, short priority);
	Entry addHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, Priority priority);
	Entry addHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, short priority);
	Entry addHandler(Class<? extends Event> type, Object object, EventHandler handler, Priority priority);
	Entry addHandler(Class<? extends Event> type, Object object, EventHandler handler, short priority);
	
	void removeHandler(Class<? extends Event> type, EventHandler handler);
	void removeHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler);
	void removeHandler(Class<? extends Event> type, Object object, EventHandler handler);
	void removeHandler(Entry entry);
	
	boolean hasHandler(Class<? extends Event> type, EventHandler handler);
	boolean hasHandler(Class<? extends Event> type, Class<?> clz);
	boolean hasHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler);
	boolean hasHandler(Class<? extends Event> type, Object object);
	boolean hasHandler(Class<? extends Event> type, Object object, EventHandler handler);
	boolean hasHandler(Entry entry);
	
	<T extends Event> void dispatchEvent(T event, Object... objects);
	<T extends Event> void dispatchEvent(ThrowableHandler handler, T event, Object... objects);
}
