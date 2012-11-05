package net.gtaun.util.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import net.gtaun.util.event.EventManager.Entry;
import net.gtaun.util.event.EventManager.Priority;
import net.gtaun.util.event.events.UselessEvent;
import net.gtaun.util.event.events.UselessEventHandler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EntryTest
{
	private EventManager eventManager;


	public EntryTest()
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
	public void testEntryProperties()
	{
		UselessEventHandler handler = new UselessEventHandler()
		{
		};
		
		Entry entry = eventManager.addHandler(UselessEvent.class, EntryTest.class, handler, Priority.LOWEST);

		assertSame(UselessEvent.class, entry.getType());
		assertSame(EntryTest.class, entry.getRelatedObject());
		assertSame(EntryTest.class, entry.getRelatedClass());
		assertSame(handler, entry.getHandler());
		assertEquals(Priority.LOWEST.getValue(), entry.getPriority());
	}
}
