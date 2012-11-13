package net.gtaun.util.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.gtaun.util.event.events.InterruptableEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventTest
{
	public EventTest()
	{

	}

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception
	{

	}
	
	@Test
	public void testProperties()
	{
		Event event = new Event()
		{
		};
		
		assertFalse(event.isInterrupted());
	}
	
	public void testInterrupt()
	{
		InterruptableEvent event = new InterruptableEvent();
		
		event.interrupt();
		assertTrue(event.isInterrupted());
	}
}
