package net.gtaun.util.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class AbstractHandlerEntry implements HandlerEntry
{
	private Class<? extends Event> type;
	private EventHandler<?> handler;
	private short priority;
	
	protected boolean isCanceled = false;
	
	
	public AbstractHandlerEntry(Class<? extends Event> type, EventHandler<?> handler, short priority)
	{
		this.type = type;
		this.handler = handler;
		this.priority = priority;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		
		if(isCanceled) return;
		cancel();
	}
	
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	@Override
	public Class<? extends Event> getType()
	{
		return type;
	}

	@Override
	public EventHandler<?> getHandler()
	{
		return handler;
	}

	@Override
	public short getPriority()
	{
		return priority;
	}
}
