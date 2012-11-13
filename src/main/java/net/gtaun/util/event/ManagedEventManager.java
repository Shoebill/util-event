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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.gtaun.util.event.events.EventHandlerRemovedEvent;
import net.gtaun.util.event.events.EventManagerEventHandler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 
 * @author MK124
 */
public class ManagedEventManager implements EventManager
{
	private EventManager eventManager;
	private EventManagerEventHandler eventManagerEventHandler;
	private HandlerEntry eventManagerEventHandlerEntry;
	private Set<HandlerEntry> managedHandlers;
	
	
	public ManagedEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
		managedHandlers = Collections.synchronizedSet(new HashSet<HandlerEntry>());
		eventManagerEventHandler = new EventManagerEventHandler()
		{
			@Override
			public void onEventHandlerRemoved(EventHandlerRemovedEvent event)
			{
				HandlerEntry entry = event.getEntry();
				managedHandlers.remove(entry);
			}
		};
		
		eventManagerEventHandlerEntry = eventManager.addHandler(EventHandlerRemovedEvent.class, eventManagerEventHandler, HandlerPriority.MONITOR);
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		eventManagerEventHandlerEntry.cancel();
	}
	
	@Override
	public String toString()
	{
		
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
	private void addHandlerEntry(HandlerEntry entry)
	{
		synchronized (managedHandlers)
		{
			managedHandlers.add(entry);
		}
	}
	
	public void removeAllHandler()
	{
		List<HandlerEntry> entries = new ArrayList<>(managedHandlers);
		for(HandlerEntry entry : entries)
		{
			entry.cancel();
		}
		
		managedHandlers.clear();
	}
	
	@Override
	public HandlerEntry addHandler(Class<? extends Event> type, EventHandler handler, HandlerPriority priority)
	{
		HandlerEntry entry = eventManager.addHandler(type, handler, priority);
		addHandlerEntry(entry);
		return entry;
	}
	
	@Override
	public HandlerEntry addHandler(Class<? extends Event> type, EventHandler handler, short priority)
	{
		HandlerEntry entry = eventManager.addHandler(type, handler, priority);
		addHandlerEntry(entry);
		return entry;
	}
	
	@Override
	public HandlerEntry addHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, HandlerPriority priority)
	{
		HandlerEntry entry = eventManager.addHandler(type, clz, handler, priority);
		addHandlerEntry(entry);
		return entry;
	}
	
	@Override
	public HandlerEntry addHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, short priority)
	{
		HandlerEntry entry = eventManager.addHandler(type, clz, handler, priority);
		addHandlerEntry(entry);
		return entry;
	}
	
	@Override
	public HandlerEntry addHandler(Class<? extends Event> type, Object object, EventHandler handler, HandlerPriority priority)
	{
		HandlerEntry entry = eventManager.addHandler(type, object, handler, priority);
		addHandlerEntry(entry);
		return entry;
	}
	
	@Override
	public HandlerEntry addHandler(Class<? extends Event> type, Object object, EventHandler handler, short priority)
	{
		HandlerEntry entry = eventManager.addHandler(type, object, handler, priority);
		addHandlerEntry(entry);
		return entry;
	}
	
	@Override
	public boolean hasHandler(Class<? extends Event> type, EventHandler handler)
	{
		return eventManager.hasHandler(type, handler);
	}
	
	@Override
	public boolean hasHandler(Class<? extends Event> type, Class<?> clz)
	{
		return eventManager.hasHandler(type, clz);
	}
	
	@Override
	public boolean hasHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler)
	{
		return eventManager.hasHandler(type, clz, handler);
	}
	
	@Override
	public boolean hasHandler(Class<? extends Event> type, Object object)
	{
		return eventManager.hasHandler(type, object);
	}
	
	@Override
	public boolean hasHandler(Class<? extends Event> type, Object object, EventHandler handler)
	{
		return eventManager.hasHandler(type, object, handler);
	}
	
	@Override
	public <T extends Event> void dispatchEvent(T event, Object... objects)
	{
		eventManager.dispatchEvent(event, objects);
	}
	
	@Override
	public <T extends Event> void dispatchEvent(ThrowableHandler handler, T event, Object... objects)
	{
		eventManager.dispatchEvent(handler, event, objects);
	}
}
