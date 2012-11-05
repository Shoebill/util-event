package net.gtaun.util.event.events;

import net.gtaun.util.event.AbstractEventHandler;

public abstract class UselessEventHandler extends AbstractEventHandler
{
	protected UselessEventHandler()
	{
		super(UselessEventHandler.class);
	}

	public void onUselessEvent(UselessEvent event) throws Exception
	{
		
	}
	
	public void onInterruptableEvent(InterruptableEvent event) throws Exception
	{
		
	}
}
