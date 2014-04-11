package net.gtaun.util.event;

import java.util.ArrayList;
import java.util.Collection;

public class Attentions
{
	public static Attentions create()
	{
		return new Attentions();
	}
	
	public static Attentions all()
	{
		return create().clazz(Object.class);
	}
	
	
	private Collection<Object> objects;
	
	
	private Attentions()
	{
		objects = new ArrayList<>();
	}

	Collection<Object> getObjects()
	{
		return objects;
	}
	
	public Attentions object(Object target)
	{
		objects.add(target);
		return this;
	}
	
	public Attentions clazz(Class<?> clz)
	{
		objects.add(clz);
		return this;
	}
}
