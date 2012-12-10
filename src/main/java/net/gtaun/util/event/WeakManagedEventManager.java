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
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * Keeping the reference of entry's instance is necessary, or it will be recycled by GC.
 * 
 * @author MK124
 */
public class WeakManagedEventManager extends AbstractManagedEventManager implements EventManager
{
	private Collection<Reference<HandlerEntry>> handlerEntries;
	
	
	public WeakManagedEventManager(EventManager eventManager)
	{
		super(eventManager);
		handlerEntries = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	protected HandlerEntry register(HandlerEntry originalEntry)
	{
		HandlerEntry entry = new HandlerEntryImpl(this, originalEntry);
		handlerEntries.add(new WeakReference<>(entry));
		return entry;
	}

	@Override
	protected void remove(HandlerEntry entry)
	{
		for (Iterator<Reference<HandlerEntry>> iter = handlerEntries.iterator(); iter.hasNext();)
		{
			Reference<HandlerEntry> ref = iter.next();
			HandlerEntry e = ref.get();
			
			if (e == null || e == entry) iter.remove();
		}
	}

	@Override
	public void cancelAll()
	{
		for (Reference<HandlerEntry> ref : handlerEntries)
		{
			HandlerEntry entry = ref.get();
			if (entry != null) entry.cancel();	
		}
	}
}
