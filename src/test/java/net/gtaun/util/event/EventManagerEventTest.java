package net.gtaun.util.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.Queue;

import net.gtaun.util.event.EventManager.HandlerEntry;
import net.gtaun.util.event.EventManager.EventHandlerPriority;
import net.gtaun.util.event.events.EventHandlerAddedEvent;
import net.gtaun.util.event.events.EventHandlerRemovedEvent;
import net.gtaun.util.event.events.EventManagerEventHandler;
import net.gtaun.util.event.events.UselessEvent;
import net.gtaun.util.event.events.UselessEventHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventManagerEventTest
{
	private EventManager eventManager;


	public EventManagerEventTest()
	{

	}

	@Before
	public void setUp() throws Exception
	{
		eventManager = new EventManagerImpl();
	}

	@After
	public void tearDown() throws Exception
	{
		eventManager = null;
	}
	
	@Test
	public void testHandlerAddedAndRemovedEvent()
	{
		final Queue<Event> queue = new LinkedList<Event>();
		
		final UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		EventManagerEventHandler managerEventHandler = new EventManagerEventHandler()
		{
			@Override
			public void onEventHandlerAdded(EventHandlerAddedEvent event)
			{
				if(event.getHandler() == handler) queue.offer(event);
			}
			
			@Override
			public void onEventHandlerRemoved(EventHandlerRemovedEvent event)
			{
				if(event.getHandler() == handler) queue.offer(event);
			}
		};

		HandlerEntry addedHandlerEntry = eventManager.addHandler(EventHandlerAddedEvent.class, managerEventHandler, EventHandlerPriority.NORMAL);
		HandlerEntry removedHandlerEntry = eventManager.addHandler(EventHandlerRemovedEvent.class, managerEventHandler, EventHandlerPriority.NORMAL);

		HandlerEntry entry = eventManager.addHandler(UselessEvent.class, handler, EventHandlerPriority.NORMAL);
		entry.cancel();
		
		addedHandlerEntry.cancel();
		removedHandlerEntry.cancel();
		
		assertSame(EventHandlerAddedEvent.class, queue.poll().getClass());
		assertSame(EventHandlerRemovedEvent.class, queue.poll().getClass());
	}
	
	@Test
	public void testHandlerAddedEventProperties()
	{
		final Queue<EventHandlerAddedEvent> queue = new LinkedList<EventHandlerAddedEvent>();
		
		final UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		EventManagerEventHandler managerEventHandler = new EventManagerEventHandler()
		{
			@Override
			public void onEventHandlerAdded(EventHandlerAddedEvent event)
			{
				if(event.getHandler() == handler) queue.offer(event);
			}
		};

		HandlerEntry addedHandlerEntry = eventManager.addHandler(EventHandlerAddedEvent.class, eventManager, managerEventHandler, EventHandlerPriority.NORMAL);
		HandlerEntry entry = eventManager.addHandler(UselessEvent.class, this, handler, EventHandlerPriority.HIGHEST);
		
		entry.cancel();
		addedHandlerEntry.cancel();
		
		EventHandlerAddedEvent event = queue.poll();
		assertNotNull(event);

		assertSame(eventManager, event.getEventManager());
		assertSame(UselessEvent.class, event.getType());
		assertSame(this, event.getRelatedObject());
		assertSame(handler, event.getHandler());
		assertEquals(EventHandlerPriority.HIGHEST.getValue(), event.getPriority());
		assertNull(event.getRelatedClass());
		assertSame(entry, event.getEntry());
	}
}
