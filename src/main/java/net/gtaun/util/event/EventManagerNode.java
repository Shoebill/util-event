package net.gtaun.util.event;

public interface EventManagerNode extends EventManager
{
	/**
	 * 
	 */
	void destroy();
	
	/**
	 * 
	 * @return
	 */
	boolean isDestroy();
	
	/**
	 * 
	 * @return
	 */
	EventManager getParent();
}
