package net.gtaun.util.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		
		assertTrue(event.isInterruptable());
		assertFalse(event.isInterrupted());
	}
	
	public void testInterrupt()
	{
		Event event = new Event()
		{
		};
		
		assertTrue(event.interrupt());
		assertTrue(event.isInterrupted());
	}
	
	public void testInterrupt2()
	{
		Event event = new Event(false)
		{
		};
		
		assertFalse(event.interrupt());
		assertFalse(event.isInterrupted());
	}
}
