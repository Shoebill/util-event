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

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Standard implementation class of event manager.
 * 
 * @author MK124
 */
public class EventManagerRoot implements EventManager
{
	private static final ThrowableHandler DEFAULT_THROWABLE_HANDLER = new ThrowableHandler()
	{
		@Override
		public void handleThrowable(Throwable throwable)
		{
			if(throwable instanceof AssertionError) throw (AssertionError) throwable;
			throwable.printStackTrace();
		}
	};
	
	private static final Comparator<HandlerEntry> HANDLER_ENTRY_PRIORITY_COMPARATOR = new Comparator<HandlerEntry>()
	{
		@Override
		public int compare(HandlerEntry o1, HandlerEntry o2)
		{
			return o2.getPriority() - o1.getPriority();
		}
	};
	
	
	private Map<Class<? extends Event>, Map<Object, Queue<HandlerEntry>>> handlerEntryContainersMap;
	
	
	public EventManagerRoot()
	{
		handlerEntryContainersMap = new ConcurrentHashMap<>();
	}
	
	@Override
	public <E extends Event> HandlerEntry registerHandler(Class<E> type, short priority, Attentions concerns, EventHandler<E> handler)
	{
		HandlerEntry entry = new AbstractHandlerEntry(type, concerns, handler, priority)
		{
			@Override
			public EventManager getEventManager()
			{
				return EventManagerRoot.this;
			}
			
			@Override
			public void cancel()
			{
				removeHandler(this);
				isCanceled = true;
			}
		};
		return addHandlerEntry(entry);
	}
	
	private HandlerEntry addHandlerEntry(HandlerEntry entry)
	{
		Class<? extends Event> type = entry.getType();
		Collection<Object> attentedObjects = entry.getAttentions().getObjects();
		
		for (Object attentedObject : attentedObjects)
		{
			Map<Object, Queue<HandlerEntry>> objectEntriesMap = handlerEntryContainersMap.get(type);
			if (objectEntriesMap == null)
			{
				objectEntriesMap = new ConcurrentHashMap<Object, Queue<HandlerEntry>>();
				handlerEntryContainersMap.put(type, objectEntriesMap);
			}
			
			Queue<HandlerEntry> entries = objectEntriesMap.get(attentedObject);
			if (entries == null)
			{
				entries = new ConcurrentLinkedQueue<HandlerEntry>();
				objectEntriesMap.put(attentedObject, entries);
			}
			
			entries.add(entry);	
		}
		return entry;
	}
	
	private void removeHandler(HandlerEntry entry)
	{
		if (entry == null) return;
		
		Class<? extends Event> type = entry.getType();
		Collection<Object> attentedObjects = entry.getAttentions().getObjects();

		for (Object attentedObject : attentedObjects)
		{
			Map<Object, Queue<HandlerEntry>> objectEntriesMap = handlerEntryContainersMap.get(type);
			if (objectEntriesMap == null) return;
			
			Queue<HandlerEntry> entries = objectEntriesMap.get(attentedObject);
			if (entries == null) return;
			
			entries.remove(entry);
		
			if (entries.size() == 0) objectEntriesMap.remove(attentedObject);
			if (objectEntriesMap.size() == 0) handlerEntryContainersMap.remove(type);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void dispatchEvent(ThrowableHandler throwableHandler, T event, Object... objects)
	{
		if (throwableHandler == null) throwableHandler = DEFAULT_THROWABLE_HANDLER;
		
		Class<? extends Event> type = event.getClass();
		PriorityQueue<HandlerEntry> handlerEntryQueue = new PriorityQueue<HandlerEntry>(16, HANDLER_ENTRY_PRIORITY_COMPARATOR);
		
		Map<Object, Queue<HandlerEntry>> objectEntriesMap = handlerEntryContainersMap.get(type);
		if (objectEntriesMap == null) return;
		
		for (Object object : objects)
		{
			if (object == null) continue;
			Class<?> cls = object.getClass();
			
			Queue<HandlerEntry> entries = objectEntriesMap.get(object);
			if (entries != null)
			{
				for (HandlerEntry entry : entries) handlerEntryQueue.add(entry);
			}
			
			Class<?>[] interfaces = cls.getInterfaces();
			for (Class<?> clz : interfaces)
			{
				Queue<HandlerEntry> classEntries = objectEntriesMap.get(clz);
				if (classEntries != null)
				{
					for (HandlerEntry entry : classEntries) handlerEntryQueue.add(entry);
				}
			}
			
			for (Class<?> clz = cls; clz != Object.class; clz = clz.getSuperclass())
			{
				Queue<HandlerEntry> classEntries = objectEntriesMap.get(clz);
				if (classEntries != null)
				{
					for (HandlerEntry entry : classEntries) handlerEntryQueue.add(entry);
				}
			}
		}
		
		Queue<HandlerEntry> entries = objectEntriesMap.get(Object.class);
		if (entries != null)
		{
			for (HandlerEntry entry : entries) handlerEntryQueue.add(entry);
		}
		
		while (handlerEntryQueue.isEmpty() == false && event.isInterrupted() == false)
		{
			HandlerEntry entry = handlerEntryQueue.poll();
			EventHandler<T> handler = (EventHandler<T>) entry.getHandler();
			
			if (handler == null) continue;
			
			try
			{
				handler.handleEvent(event);
			}
			catch (Throwable e)
			{
				throwableHandler.handleThrowable(e);
			}
		}
	}

	@Override
	public EventManagerNode createChildNode()
	{
		return new EventManagerChild(this);
	}
}
