package net.gtaun.util.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import net.gtaun.util.event.EventManager.Entry;
import net.gtaun.util.event.EventManager.Priority;
import net.gtaun.util.event.EventManager.ThrowableHandler;
import net.gtaun.util.event.events.InterruptableEvent;
import net.gtaun.util.event.events.UselessEvent;
import net.gtaun.util.event.events.UselessEventHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventManagerTest
{
	private EventManager eventManager;


	public EventManagerTest()
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
	public void testAddHandlerAndDispatchEvent()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event)
			{
				event.setProcessed(true);
			}
		};
		
		eventManager.addHandler(UselessEvent.class, handler, Priority.NORMAL);
		
		UselessEvent event = new UselessEvent();
		eventManager.dispatchEvent(event);
		
		assertTrue(event.isProcessed());
	}

	@Test
	public void testAddHandlerAndDispatchEvent2()
	{
		final TapableCounter counter = new TapableCounter();
		
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onInterruptableEvent(InterruptableEvent event)
			{
				counter.tap();
			}
		};
		
		eventManager.addHandler(InterruptableEvent.class, this, handler, Priority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		eventManager.dispatchEvent(event, this);
		eventManager.dispatchEvent(event, new Object());
		eventManager.dispatchEvent(event);
		
		assertEquals(1, counter.getTaps());
	}

	@Test
	public void testAddHandlerAndDispatchEvent3()
	{
		final TapableCounter counter = new TapableCounter();
		
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onInterruptableEvent(InterruptableEvent event)
			{
				counter.tap();
			}
		};
		
		eventManager.addHandler(InterruptableEvent.class, EventManagerTest.class, handler, Priority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		eventManager.dispatchEvent(event, this);
		eventManager.dispatchEvent(event, new Object());
		eventManager.dispatchEvent(event);
		
		assertEquals(1, counter.getTaps());
	}
	
	@Test
	public void testAddHandlerAndDispatchEvent4()
	{
		final TapableCounter counter = new TapableCounter();
		
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onInterruptableEvent(InterruptableEvent event)
			{
				counter.tap();
			}
		};
		
		Cloneable cloneable = new Cloneable()
		{
		};
		
		eventManager.addHandler(InterruptableEvent.class, Cloneable.class, handler, Priority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		eventManager.dispatchEvent(event, cloneable);
		eventManager.dispatchEvent(event, new Object());
		eventManager.dispatchEvent(event);
		
		assertEquals(1, counter.getTaps());
	}
	
	@Test
	public void testHasAndRemoveEventHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		eventManager.addHandler(UselessEvent.class, handler, Priority.NORMAL);
		assertTrue(eventManager.hasHandler(UselessEvent.class, handler));
		eventManager.removeHandler(UselessEvent.class, handler);
		assertFalse(eventManager.hasHandler(UselessEvent.class, handler));
		
		eventManager.addHandler(UselessEvent.class, this, handler, Priority.NORMAL);
		assertTrue(eventManager.hasHandler(UselessEvent.class, this, handler));
		eventManager.removeHandler(UselessEvent.class, this, handler);
		assertFalse(eventManager.hasHandler(UselessEvent.class, this, handler));
		
		eventManager.addHandler(UselessEvent.class, EventManagerTest.class, handler, Priority.NORMAL);
		assertTrue(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class, handler));
		eventManager.removeHandler(UselessEvent.class, EventManagerTest.class, handler);
		assertFalse(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class, handler));
	}
	
	@Test
	public void testRemoveHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event)
			{
				event.setProcessed(true);
			}
		};
		
		eventManager.addHandler(UselessEvent.class, handler, Priority.NORMAL);
		eventManager.removeHandler(UselessEvent.class, handler);
		
		UselessEvent event = new UselessEvent();
		eventManager.dispatchEvent(event);
		
		assertFalse(event.isProcessed());
	}
	
	@Test
	public void testInterruptEvent()
	{
		final TapableCounter counter = new TapableCounter();
		
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onInterruptableEvent(InterruptableEvent event)
			{
				counter.tap();
				event.interrupt();
			}
		};
		
		UselessEventHandler handler2 = new UselessEventHandler()
		{
			@Override
			public void onInterruptableEvent(InterruptableEvent event)
			{
				counter.tap();
			}
		};

		eventManager.addHandler(InterruptableEvent.class, handler, Priority.NORMAL);
		eventManager.addHandler(InterruptableEvent.class, handler2, Priority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		eventManager.dispatchEvent(event);
		
		assertTrue(event.isInterruptable());
		assertTrue(counter.getTaps() == 1);
	}

	@Test
	public void testHasGlobalHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		assertFalse(eventManager.hasHandler(UselessEvent.class, handler));
		
		Entry entry = eventManager.addHandler(UselessEvent.class, handler, Priority.NORMAL);
		
		assertTrue(eventManager.hasHandler(entry));
		assertTrue(eventManager.hasHandler(UselessEvent.class, handler));
		assertTrue(eventManager.hasHandler(UselessEvent.class, Object.class));
		assertTrue(eventManager.hasHandler(UselessEvent.class, Object.class, handler));
		assertFalse(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class));
		assertFalse(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class, handler));
		assertFalse(eventManager.hasHandler(UselessEvent.class, this));
		assertFalse(eventManager.hasHandler(UselessEvent.class, this, handler));
	}
	
	@Test
	public void testTypeFilteredHasHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		assertFalse(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class, handler));
		
		Entry entry = eventManager.addHandler(UselessEvent.class, EventManagerTest.class, handler, Priority.NORMAL);
		
		assertTrue(eventManager.hasHandler(entry));
		assertFalse(eventManager.hasHandler(UselessEvent.class, handler));
		assertFalse(eventManager.hasHandler(UselessEvent.class, Object.class));
		assertFalse(eventManager.hasHandler(UselessEvent.class, Object.class, handler));
		assertTrue(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class));
		assertTrue(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class, handler));
		assertFalse(eventManager.hasHandler(UselessEvent.class, this));
		assertFalse(eventManager.hasHandler(UselessEvent.class, this, handler));
	}
	
	@Test
	public void testInstanceFilteredHasHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		assertFalse(eventManager.hasHandler(UselessEvent.class, this, handler));
		
		Entry entry = eventManager.addHandler(UselessEvent.class, this, handler, Priority.NORMAL);
		
		assertTrue(eventManager.hasHandler(entry));
		assertFalse(eventManager.hasHandler(UselessEvent.class, handler));
		assertFalse(eventManager.hasHandler(UselessEvent.class, Object.class));
		assertFalse(eventManager.hasHandler(UselessEvent.class, Object.class, handler));
		assertFalse(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class));
		assertFalse(eventManager.hasHandler(UselessEvent.class, EventManagerTest.class, handler));
		assertTrue(eventManager.hasHandler(UselessEvent.class, this));
		assertTrue(eventManager.hasHandler(UselessEvent.class, this, handler));
	}
	
	@Test
	public void testCatchException()
	{
		final Exception exception = new Exception();
		final Queue<Throwable> queue = new LinkedList<Throwable>();
		
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event) throws Exception
			{
				throw exception;
			}
		};
		
		ThrowableHandler throwableHandler = new ThrowableHandler()
		{
			@Override
			public void handleThrowable(Throwable throwable)
			{
				queue.offer(throwable);
			}
		};
		
		eventManager.addHandler(UselessEvent.class, handler, Priority.NORMAL);
		eventManager.dispatchEvent(throwableHandler, new UselessEvent());
		
		assertSame(exception, queue.poll());
	}
	
	@Test
	public void testIllgalHasHandler()
	{
		eventManager.hasHandler(null);
		eventManager.hasHandler(UselessEvent.class, (EventHandler)null);
		eventManager.hasHandler(UselessEvent.class, (Class<?>)null);
		eventManager.hasHandler(UselessEvent.class, (Class<?>)null, null);
		eventManager.hasHandler(UselessEvent.class, new Object());
		eventManager.hasHandler(UselessEvent.class, new Object(), null);
	}
	
	@Test
	public void testIllgalRemoveHandler()
	{
		eventManager.removeHandler(null);
		eventManager.removeHandler(UselessEvent.class, (EventHandler)null);
		eventManager.removeHandler(UselessEvent.class, (Class<?>)null, null);
		eventManager.removeHandler(UselessEvent.class, new Object(), null);
	}
}