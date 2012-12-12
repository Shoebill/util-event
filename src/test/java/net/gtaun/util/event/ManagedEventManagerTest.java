package net.gtaun.util.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.gtaun.util.event.EventManager.HandlerEntry;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.events.InterruptableEvent;
import net.gtaun.util.event.events.UselessEvent;
import net.gtaun.util.event.events.UselessEventHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ManagedEventManagerTest
{
	private EventManager rootEventManager;
	private ManagedEventManager eventManager;
	
	
	public ManagedEventManagerTest()
	{

	}

	@Before
	public void setUp() throws Exception
	{
		rootEventManager = new EventManagerImpl();
		eventManager = new ManagedEventManager(rootEventManager);
	}

	@After
	public void tearDown() throws Exception
	{
		rootEventManager = null;
		eventManager = null;
	}
	
	@Test
	public void testRegisterHandlerAndDispatchEvent()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event)
			{
				event.setProcessed(true);
			}
		};
		
		eventManager.registerHandler(UselessEvent.class, handler, HandlerPriority.NORMAL);
		
		UselessEvent event = new UselessEvent();
		rootEventManager.dispatchEvent(event);
		
		assertTrue(event.isProcessed());
	}

	@Test
	public void testRegisterHandlerAndDispatchEvent2()
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
		
		eventManager.registerHandler(InterruptableEvent.class, this, handler, HandlerPriority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		rootEventManager.dispatchEvent(event, this);
		rootEventManager.dispatchEvent(event, new Object());
		rootEventManager.dispatchEvent(event);
		
		assertEquals(1, counter.getTaps());
	}

	@Test
	public void testRegisterHandlerAndDispatchEvent3()
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
		
		eventManager.registerHandler(InterruptableEvent.class, getClass(), handler, HandlerPriority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		rootEventManager.dispatchEvent(event, this);
		rootEventManager.dispatchEvent(event, new Object());
		rootEventManager.dispatchEvent(event);
		
		assertEquals(1, counter.getTaps());
	}
	
	@Test
	public void testRegisterHandlerAndDispatchEvent4()
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
		
		eventManager.registerHandler(InterruptableEvent.class, Cloneable.class, handler, HandlerPriority.NORMAL);
		
		InterruptableEvent event = new InterruptableEvent();
		rootEventManager.dispatchEvent(event, cloneable);
		rootEventManager.dispatchEvent(event, new Object());
		rootEventManager.dispatchEvent(event);
		
		assertEquals(1, counter.getTaps());
	}
	
	@Test
	public void testCancelHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event)
			{
				event.setProcessed(true);
			}
		};
		
		HandlerEntry entry = eventManager.registerHandler(UselessEvent.class, handler, HandlerPriority.NORMAL);
		entry.cancel();
		
		UselessEvent event = new UselessEvent();
		rootEventManager.dispatchEvent(event);
		
		assertFalse(event.isProcessed());
	}
	
	@Test
	public void testCancelAllHandler()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event)
			{
				event.setProcessed(true);
			}
		};
		
		eventManager.registerHandler(UselessEvent.class, handler, HandlerPriority.NORMAL);
		eventManager.cancelAll();
		
		UselessEvent event = new UselessEvent();
		rootEventManager.dispatchEvent(event);
		
		assertFalse(event.isProcessed());
	}
	
	@Test
	public void testGcCancelAllHandler() throws InterruptedException
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
			@Override
			public void onUselessEvent(UselessEvent event)
			{
				event.setProcessed(true);
			}
		};
		
		final Object waitObject = new Object();
		ManagedEventManager eventManager = new ManagedEventManager(rootEventManager)
		{
			@Override
			protected void finalize() throws Throwable
			{
				super.finalize();
				synchronized (waitObject)
				{
					waitObject.notify();
				}
			}
		};
		
		eventManager.registerHandler(UselessEvent.class, handler, HandlerPriority.NORMAL);
		eventManager = null;
		
		System.gc();
		
		synchronized (waitObject)
		{
			waitObject.wait();
		}
		
		UselessEvent event = new UselessEvent();
		rootEventManager.dispatchEvent(event);
		
		assertFalse(event.isProcessed());
	}
}
