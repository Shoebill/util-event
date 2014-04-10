/**
 * Copyright (C) 2011-2014 MK124
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

/**
 * Provide interface for managing event handlers and dispatching events.
 * 
 * @author MK124
 */
public interface EventManager
{
	/**
	 * Register a global event handler.
	 * 
	 * @param type Related event type
	 * @param priority Priority of event handler
	 * @param handler Instance of event handler
	 *  
	 * @return Entry of event handler
	 *  
	 * @see HandlerPriority
	 */
	default
	<E extends Event>
	HandlerEntry registerHandler(Class<E> type, HandlerPriority priority, EventHandler<E> handler)
	{
		return registerHandler(type, priority.getValue(), null, handler);
	}
	
	/**
	 * Register a global event handler.
	 * 
	 * @param type Related event type
	 * @param priority Custom priority of event handler
	 * @param handler Instance of event handler
	 *  
	 * @return Entry of event handler
	 */
	default
	<E extends Event>
	HandlerEntry registerHandler(Class<E> type, short priority, EventHandler<E> handler)
	{
		return registerHandler(type, priority, null, handler);
	}
	
	/**
	 * Register a event handler that related with an instance.<p>
	 * Only monitor the event that related with this instance.
	 * 
	 * @param type Related event type
	 * @param priority Priority of event handler
	 * @param concerns Concerns instance
	 * @param handler Instance of event handler
	 *  
	 * @return Entry of event handler
	 *  
	 * @see HandlerPriority
	 */
	default
	<E extends Event>
	HandlerEntry registerHandler(Class<E> type, HandlerPriority priority, Concerns concerns, EventHandler<E> handler)
	{
		return registerHandler(type, priority.getValue(), null, handler);
	}
	
	/**
	 * Register a event handler that related with an instance.<p>
	 * Only monitor the event that related with this instance.
	 * 
	 * @param type Related event type
	 * @param priority Custom priority of event handler
	 * @param concerns Concerns instance
	 * @param handler Instance of event handler
	 *  
	 * @return Entry of event handler
	 */
	<E extends Event>
	HandlerEntry registerHandler(Class<E> type, short priority, Concerns concerns, EventHandler<E> handler);
	
	/**
	 * Dispatch events according to handler's priority.
	 * It might be interrupted if event allowed.
	 * If the handler throw exception, it will be print out and keep dispatching.
	 * 
	 * @param event Instance of event to be dispatch
	 * @param objects Related objects
	 */
	default
	<E extends Event>
	void dispatchEvent(E event, Object... objects)
	{
		dispatchEvent(null, event, objects);
	}
	
	/**
	 * Dispatch events according to handler's priority.
	 * It might be interrupted if event allowed.
	 * 
	 * @param handler Instance of exception handler. Print out the exception directly if it's {@code null}.
	 * @param event Instance of event to be dispatch
	 * @param objects Related objects
	 */
	<E extends Event>
	void dispatchEvent(ThrowableHandler handler, E event, Object... objects);
	
	/**
	 * 
	 * 
	 * @return
	 */
	EventManagerNode createChildNode();
}
