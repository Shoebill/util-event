package net.gtaun.util.event;

public interface EventManagerNode extends EventManager
{
	void destroy();
	
	boolean isDestroy();
	
	EventManager getParent();
}
