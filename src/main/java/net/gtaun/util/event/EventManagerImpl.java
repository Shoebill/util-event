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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.gtaun.util.event.event.EventHandlerAddedEvent;
import net.gtaun.util.event.event.EventHandlerRemovedEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 
 * @author MK124
 */
public class EventManagerImpl implements EventManager
{
	private Map<Class<? extends Event>, Map<Object, Queue<Entry>>> handlerEntryContainersMap;
	
	
	public EventManagerImpl()
	{
		handlerEntryContainersMap = new ConcurrentHashMap<Class<? extends Event>, Map<Object, Queue<Entry>>>();
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, EventHandler handler, Priority priority )
	{
		return addHandler( type, Object.class, handler, priority.getValue() );
	}
	
	@Override
	public Entry addHandler( Class<? extends Event> type, EventHandler handler, short priority )
	{
		return addHandler( type, Object.class, handler, priority );
	}
	
	@Override
	public Entry addHandler( Class<? extends Event> type, Class<?> relatedClass, EventHandler handler, Priority priority )
	{
		return addHandler( type, (Object)relatedClass, handler, priority.getValue() );
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Class<?> relatedClass, EventHandler handler, short customPriority )
	{
		return addHandler( type, (Object)relatedClass, handler, customPriority );
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Object relatedObject, EventHandler handler, Priority priority )
	{
		return addHandler( type, relatedObject, handler, priority.getValue() );
	}

	@Override
	public Entry addHandler( Class<? extends Event> type, Object relatedObject, EventHandler handler, short customPriority )
	{
		Entry entry = new Entry( type, relatedObject, handler, customPriority );
		return addHandler(entry);
	}
	
	@Override
	public Entry addHandler( Entry entry )
	{
		Class<? extends Event> type = entry.getType();
		Object relatedObject = entry.getRelatedObject();
		EventHandler handler = entry.getHandler();
		
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null )
		{
			objectHandlerEntries = new ConcurrentHashMap<>();
			handlerEntryContainersMap.put( type, objectHandlerEntries );
		}
		
		Queue<Entry> entries = objectHandlerEntries.get(relatedObject);
		if( entries == null )
		{
			entries = new ConcurrentLinkedQueue<>();
			objectHandlerEntries.put( relatedObject, entries );
		}
		
		for( Entry e : entries )
		{
			if( e.getHandler() != handler ) continue;
			removeHandler( type, relatedObject, handler );
		}
		
		entries.add( entry );
		
		EventHandlerAddedEvent event = new EventHandlerAddedEvent(entry);
		dispatchEvent( event, this );
		
		return entry;
	}


	@Override
	public void removeHandler( Class<? extends Event> type, EventHandler handler )
	{
		removeHandler( type, Object.class, handler );
	}
	
	@Override
	public void removeHandler( Class<? extends Event> type, Class<?> clz, EventHandler handler )
	{
		removeHandler( type, (Object)clz, handler );
	}
	
	@Override
	public void removeHandler( Class<? extends Event> type, Object relatedObject, EventHandler handler )
	{
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null ) return;
		
		Queue<Entry> entries = objectHandlerEntries.get(relatedObject);
		if( entries == null ) return;
		
		for( Entry entry : entries )
		{
			if( entry.getHandler() != handler ) continue;
			entries.remove( entry );
			
			EventHandlerRemovedEvent event = new EventHandlerRemovedEvent(entry);
			dispatchEvent( event, this );
		}
		
		if( entries.size() == 0 ) objectHandlerEntries.remove( relatedObject );
		if( objectHandlerEntries.size() == 0 ) handlerEntryContainersMap.remove( type );
	}
	
	@Override
	public void removeHandler( Entry entry )
	{
		Class<? extends Event> type = entry.getType();
		Object relatedObject = entry.getRelatedObject();
		
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null ) return;
		
		Queue<Entry> entries = objectHandlerEntries.get(relatedObject);
		if( entries == null ) return;
		
		for( Entry e : entries )
		{
			if( e != entry ) continue;
			entries.remove( entry );
			
			EventHandlerRemovedEvent event = new EventHandlerRemovedEvent(entry);
			dispatchEvent( event, this );
			break;
		}
		
		if( entries.size() == 0 ) objectHandlerEntries.remove( relatedObject );
		if( objectHandlerEntries.size() == 0 ) handlerEntryContainersMap.remove( type );
	}
	

	@Override
	public boolean hasHandler( Class<? extends Event> type, Class<?> clz )
	{
		return hasHandler( type, (Object)clz );
	}
	
	@Override
	public boolean hasHandler( Class<? extends Event> type, Class<?> clz, EventHandler handler )
	{
		return hasHandler( type, (Object)clz, handler );
	}
	
	@Override
	public boolean hasHandler( Class<? extends Event> type, Object object )
	{
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null ) return false;
		
		Queue<Entry> entries = objectHandlerEntries.get(object);
		if( entries == null ) return false;
		
		return true;
	}

	@Override
	public boolean hasHandler( Class<? extends Event> type, Object object, EventHandler handler )
	{
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null ) return false;
		
		Queue<Entry> entries = objectHandlerEntries.get(object);
		if( entries == null ) return false;
		
		for( Entry entry : entries )
		{
			if( entry.getHandler() == handler ) return true;
		}
		
		return false;
	}
	
	@Override
	public boolean hasHandler( Entry entry )
	{
		Class<? extends Event> type = entry.getType();
		Object relatedObject = entry.getRelatedObject();
		
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null ) return false;
		
		Queue<Entry> entries = objectHandlerEntries.get(relatedObject);
		if( entries == null ) return false;
		
		for( Entry e : entries )
		{
			if( e == entry ) return true;
		}
		
		return false;
	}
	
	
	@Override
	public <T extends Event> void dispatchEvent( T event, Object ...objects )
	{
		if( objects.length == 1 && objects[0] instanceof Object[] ) objects = (Object[]) objects[0];
		
		Class<? extends Event> type = event.getClass();
		PriorityQueue<Entry> handlerEntryQueue = new PriorityQueue<>( 16,
			new Comparator<Entry>()
			{
				@Override
				public int compare( Entry o1, Entry o2 )
				{
					return o2.getPriority() - o1.getPriority();
				}
			}
		);
		
		Map<Object, Queue<Entry>> objectHandlerEntries = handlerEntryContainersMap.get(type);
		if( objectHandlerEntries == null ) return;
		
		for( Object object : objects )
		{
			Class<?> cls = object.getClass();
			
			Queue<Entry> entries = objectHandlerEntries.get( object );
			if( entries != null ) for( Entry entry : entries )
			{
				if( entry.getHandler() == null ) entries.remove( entry );
				else handlerEntryQueue.add( entry );
			}

			Class<?>[] interfaces = cls.getInterfaces();
			for( Class<?> clz : interfaces )
			{
				Queue<Entry> classHandlerEntries = objectHandlerEntries.get( clz );
				if( classHandlerEntries != null ) for( Entry entry : classHandlerEntries )
				{
					if( entry.getHandler() == null ) entries.remove( entry );
					else handlerEntryQueue.add( entry );
				}
			}
			
			for( Class<?> clz = cls; clz != null; clz = clz.getSuperclass() )
			{
				Queue<Entry> classHandlerEntries = objectHandlerEntries.get( clz );
				if( classHandlerEntries != null ) for( Entry entry : classHandlerEntries )
				{
					if( entry.getHandler() == null ) entries.remove( entry );
					else handlerEntryQueue.add( entry );
				}
			}
		}
		
		Set<Entry> processedHandler = new HashSet<>( handlerEntryQueue.size() );
		while( handlerEntryQueue.isEmpty() == false && event.isInterrupted() == false )
		{
			Entry entry = handlerEntryQueue.poll();
			EventHandler handler = entry.getHandler();
			
			if( handler == null ) continue;
			
			if( processedHandler.contains(entry) ) return;
			processedHandler.add( entry );
			
			try
			{
				handler.handleEvent( event );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
}
