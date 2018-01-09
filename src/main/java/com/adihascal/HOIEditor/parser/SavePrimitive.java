package com.adihascal.HOIEditor.parser;

public class SavePrimitive<T> implements SaveElement
{
	private T value;
	
	public SavePrimitive(T val)
	{
		this.value = val;
	}
	
	public T getValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return value.toString();
	}
}
