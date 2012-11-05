package net.gtaun.util.event.events;

import net.gtaun.util.event.Event;

public class UselessEvent extends Event
{
	private boolean isProcessed = false;
	
	
	public UselessEvent()
	{
		
	}
	
	public void setProcessed(boolean isProcessed)
	{
		this.isProcessed = isProcessed;
	}
	
	public boolean isProcessed()
	{
		return isProcessed;
	}
}
