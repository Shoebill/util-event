package net.gtaun.util.event;

public interface EventManagerNode extends EventManager
{
	void cancelAll();
	
	void destroy();
	
	boolean isDestroy();
	
	EventManager getParent();
}
