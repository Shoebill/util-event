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

import java.util.HashSet;
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
	private Entry eventManagerEventHandlerEntry;
	private Set<Entry> managedHandlers;
	
	
	public ManagedEventManager( EventManager eventManager )
	{
		this.eventManager = eventManager;
		managedHandlers = new HashSet<>();
		eventManagerEventHandler = new EventManagerEventHandler()
		{
			@Override
			public void onEventHandlerRemoved( EventHandlerRemovedEvent event )
			{
				Entry entry = event.getEntry();
				if( managedHandlers.contains(entry) ) managedHandlers.remove( entry );
			}
		};
		
		eventManagerEventHandlerEntry = eventManager.addHandler( EventHandlerRemovedEvent.class, eventManagerEventHandler, Priority.MONITOR );
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		eventManager.removeHandler( eventManagerEventHandlerEntry );
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
	private void addHandlerEntry( Entry entry )
	{
		managedHandlers.add( entry );
	}
	
	public void removeAllHandler()
	{
		for( Entry entry : managedHandlers ) removeHandler( entry );
	}
	
	
	@Override
	public Entry addHandler( Class<? extends Event> type, EventHandler handler, Priority priority )
	{
		Entry entry = eventManager.addHandler( type, handler, priority );
		addHandlerEntry( entry );
		return entry;
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, EventHandler handler, short priority )
	{
		Entry entry = eventManager.addHandler( type, handler, priority );
		addHandlerEntry( entry );
		return entry;
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Class<?> clz, EventHandler handler, Priority priority )
	{
		Entry entry = eventManager.addHandler( type, clz, handler, priority );
		addHandlerEntry( entry );
		return entry;
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Class<?> clz, EventHandler handler, short priority )
	{
		Entry entry = eventManager.addHandler( type, clz, handler, priority );
		addHandlerEntry( entry );
		return entry;
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Object object, EventHandler handler, Priority priority )
	{
		Entry entry = eventManager.addHandler( type, object, handler, priority );
		addHandlerEntry( entry );
		return entry;
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Object object, EventHandler handler, short priority )
	{
		Entry entry = eventManager.addHandler( type, object, handler, priority );
		addHandlerEntry( entry );
		return entry;
	}

	@Override
	public void removeHandler( Class<? extends Event> type, EventHandler handler )
	{
		eventManager.removeHandler( type, handler );
	}

	@Override
	public void removeHandler( Class<? extends Event> type, Class<?> clz, EventHandler handler )
	{
		eventManager.removeHandler( type, clz, handler );
	}

	@Override
	public void removeHandler( Class<? extends Event> type, Object object, EventHandler handler )
	{
		eventManager.removeHandler( type, object, handler );
	}
	
	@Override
	public void removeHandler( Entry entry )
	{
		eventManager.removeHandler( entry );
	}

	@Override
	public boolean hasHandler(Class<? extends Event> type, EventHandler handler)
	{
		return eventManager.hasHandler(type, handler);
	}
	
	@Override
	public boolean hasHandler( Class<? extends Event> type, Class<?> clz )
	{
		return eventManager.hasHandler( type, clz );
	}

	@Override
	public boolean hasHandler( Class<? extends Event> type, Class<?> clz, EventHandler handler )
	{
		return eventManager.hasHandler( type, clz, handler );
	}

	@Override
	public boolean hasHandler( Class<? extends Event> type, Object object )
	{
		return eventManager.hasHandler( type, object );
	}

	@Override
	public boolean hasHandler( Class<? extends Event> type, Object object, EventHandler handler )
	{
		return eventManager.hasHandler( type, object, handler );
	}
	
	@Override
	public boolean hasHandler( Entry entry )
	{
		return eventManager.hasHandler( entry );
	}

	@Override
	public <T extends Event> void dispatchEvent( T event, Object... objects )
	{
		eventManager.dispatchEvent( event, objects );
	}

	@Override
	public <T extends Event> void dispatchEvent(ThrowableHandler handler, T event, Object... objects)
	{
		eventManager.dispatchEvent(handler, event, objects);
	}
}
