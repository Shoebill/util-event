/**
 * Copyright (C) 2011-2012 MK124
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The abstract class of event, should be inheritance for defining new event type.
 * 
 * @author MK124
 */
public abstract class Event
{
	private boolean interrupted;
	
	
	protected Event()
	{
		
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	/**
	 * Check whether the dispatch is interrupted or not.
	 * 
	 * @return If the dispatch is interrupted, return {@code true}
	 */
	protected boolean isInterrupted()
	{
		return interrupted;
	}
	
	/**
	 * Override this as a public method if it can be interrupted.
	 * 
	 * @see Interruptable
	 */
	protected void interrupt()
	{
		interrupted = true;
	}
}
