package net.gtaun.util.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.Queue;

import net.gtaun.util.event.EventManager.Entry;
import net.gtaun.util.event.EventManager.Priority;
import net.gtaun.util.event.event.EventHandlerAddedEvent;
import net.gtaun.util.event.event.EventHandlerRemovedEvent;
import net.gtaun.util.event.event.EventManagerEventHandler;
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

		eventManager.addHandler(EventHandlerAddedEvent.class, managerEventHandler, Priority.NORMAL);
		eventManager.addHandler(EventHandlerRemovedEvent.class, managerEventHandler, Priority.NORMAL);

		eventManager.addHandler(UselessEvent.class, handler, Priority.NORMAL);
		eventManager.removeHandler(UselessEvent.class, handler);

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

		eventManager.addHandler(EventHandlerAddedEvent.class, eventManager, managerEventHandler, Priority.NORMAL);
		Entry entry = eventManager.addHandler(UselessEvent.class, this, handler, Priority.HIGHEST);
		
		EventHandlerAddedEvent event = queue.poll();
		assertNotNull(event);

		assertSame(UselessEvent.class, event.getType());
		assertSame(this, event.getRelatedObject());
		assertSame(handler, event.getHandler());
		assertEquals(Priority.HIGHEST.getValue(), event.getPriority());
		assertNull(event.getRelatedClass());
		assertSame(entry, event.getEntry());
	}
}
