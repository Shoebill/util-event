/**
 * Copyright (C) 2012 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.util.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * 
 * @author MK124
 */
abstract class AbstractManagedEventManager implements EventManager
{
	protected static class HandlerEntryImpl implements HandlerEntry
	{
		private Reference<AbstractManagedEventManager> eventManagerReference;
		private HandlerEntry handlerEntry;
		
		protected HandlerEntryImpl(AbstractManagedEventManager eventManager, HandlerEntry handlerEntry)
		{
			eventManagerReference = new WeakReference<>(eventManager);
			this.handlerEntry = handlerEntry;
		}
		
		@Override
		protected void finalize() throws Throwable
		{
			super.finalize();
			cancel();
		}
		
		@Override
		public EventManager getEventManager()
		{
			return eventManagerReference.get();
		}

		@Override
		public void cancel()
		{
			if(handlerEntry == null) return;
			
			AbstractManagedEventManager eventManager = eventManagerReference.get();
			if (eventManager != null) eventManager.remove(this);
			
			handlerEntry.cancel();
			
			handlerEntry = null;
			eventManager = null;
		}

		@Override
		public Class<? extends Event> getType()
		{
			if (handlerEntry == null) return null;
			return handlerEntry.getType();
		}

		@Override
		public Object getRelatedObject()
		{
			if (handlerEntry == null) return null;
			return handlerEntry.getRelatedObject();
		}

		@Override
		public Class<?> getRelatedClass()
		{
			if (handlerEntry == null) return null;
			return handlerEntry.getRelatedClass();
		}

		@Override
		public EventHandler getHandler()
		{
			if (handlerEntry == null) return null;
			return handlerEntry.getHandler();
		}

		@Override
		public short getPriority()
		{
			if (handlerEntry == null) return 0;
			return handlerEntry.getPriority();
		}
	}
	
	
	private final EventManager eventManager;
	
	
	protected AbstractManagedEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		cancelAll();
	}
	
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
	protected abstract HandlerEntry register(HandlerEntry originalEntry);
	protected abstract void remove(HandlerEntry handlerEntryImpl);
	
	public abstract void cancelAll();

	@Override
	public HandlerEntry registerHandler(Class<? extends Event> type, EventHandler handler, HandlerPriority priority)
	{
		HandlerEntry entry = eventManager.registerHandler(type, handler, priority);
		return register(entry);
	}
	
	@Override
	public HandlerEntry registerHandler(Class<? extends Event> type, EventHandler handler, short priority)
	{
		HandlerEntry entry = eventManager.registerHandler(type, handler, priority);
		return register(entry);
	}
	
	@Override
	public HandlerEntry registerHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, HandlerPriority priority)
	{
		HandlerEntry entry = eventManager.registerHandler(type, clz, handler, priority);
		return register(entry);
	}
	
	@Override
	public HandlerEntry registerHandler(Class<? extends Event> type, Class<?> clz, EventHandler handler, short priority)
	{
		HandlerEntry entry = eventManager.registerHandler(type, clz, handler, priority);
		return register(entry);
	}
	
	@Override
	public HandlerEntry registerHandler(Class<? extends Event> type, Object object, EventHandler handler, HandlerPriority priority)
	{
		HandlerEntry entry = eventManager.registerHandler(type, object, handler, priority);
		return register(entry);
	}
	
	@Override
	public HandlerEntry registerHandler(Class<? extends Event> type, Object object, EventHandler handler, short priority)
	{
		HandlerEntry entry = eventManager.registerHandler(type, object, handler, priority);
		return register(entry);
	}
	
	@Override
	public <T extends Event> void dispatchEvent(T event, Object... objects)
	{
		eventManager.dispatchEvent(event, objects);
	}
	
	@Override
	public <T extends Event> void dispatchEvent(ThrowableHandler handler, T event, Object... objects)
	{
		eventManager.dispatchEvent(handler, event, objects);
	}
}
