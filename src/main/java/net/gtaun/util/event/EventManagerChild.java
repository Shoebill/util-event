package net.gtaun.util.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

class EventManagerChild implements EventManagerNode
{
	private EventManager rootEventManager;
	private EventManagerNode parentEventManager;
	
	private Collection<HandlerEntry> handlerEntries;
	private Collection<EventManagerNode> childs;
	
	private boolean isDestroyed;
	
	
	EventManagerChild(EventManager root)
	{
		this(root, null);
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		if (!isDestroyed) destroy();
	}
	
	private EventManagerChild(EventManager root, EventManagerNode parent)
	{
		rootEventManager = root;
		parentEventManager = parent;
		handlerEntries = new HashSet<>();
		childs = new ArrayList<>();
	}
	
	private void checkDestroyState() throws IllegalStateException
	{
		if (isDestroyed) throw new IllegalStateException("EventManagerNode has been destroyed!");	
	}
	
	private void destroyChild(EventManagerNode node)
	{
		if (isDestroyed) return;
		childs.remove(node);
	}
	
	@Override
	public <E extends Event> HandlerEntry registerHandler(Class<E> type, short priority, Attentions concerns, EventHandler<E> handler)
	{
		checkDestroyState();
		
		final HandlerEntry rootEntry = rootEventManager.registerHandler(type, priority, concerns, handler);
		final HandlerEntry entry = new AbstractHandlerEntry(type, concerns, handler, priority)
		{
			@Override
			public EventManager getEventManager()
			{
				return EventManagerChild.this;
			}
			
			@Override
			public void cancel()
			{
				rootEntry.cancel();
				isCanceled = true;
				if (!isDestroyed) handlerEntries.remove(this);
			}
		};
		
		handlerEntries.add(entry);
		return entry;
	}
	
	@Override
	public <E extends Event> void dispatchEvent(ThrowableHandler handler, E event, Object... objects)
	{
		checkDestroyState();
		rootEventManager.dispatchEvent(handler, event, objects);
	}
	
	@Override
	public EventManagerNode createChildNode()
	{
		EventManagerNode node = rootEventManager.createChildNode();
		childs.add(node);
		return node;
	}
	
	public void cancelAll()
	{
		List<HandlerEntry> entries = new ArrayList<>(handlerEntries);
		for (HandlerEntry entry : entries) entry.cancel();
		handlerEntries.clear();
	}
	
	@Override
	public void destroy()
	{
		if (parentEventManager instanceof EventManagerChild && parentEventManager.isDestroy() == false)
		{
			((EventManagerChild)parentEventManager).destroyChild(this);
		}
		
		isDestroyed = true;
		
		for (EventManagerNode node : childs) node.destroy();
		childs.clear();
		
		for (HandlerEntry entry : handlerEntries) entry.cancel();
		handlerEntries.clear();
	}
	
	@Override
	public boolean isDestroy()
	{
		return isDestroyed;
	}
	
	@Override
	public EventManager getParent()
	{
		return rootEventManager;
	}
}
