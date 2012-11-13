package net.gtaun.util.event.events;

import net.gtaun.util.event.Event;
import net.gtaun.util.event.Interruptable;

public class InterruptableEvent extends Event implements Interruptable
{
	public InterruptableEvent()
	{
		super();
	}
	
	@Override
	public void interrupt()
	{
		super.interrupt();
	}
}
