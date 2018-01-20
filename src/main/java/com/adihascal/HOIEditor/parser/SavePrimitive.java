package com.adihascal.HOIEditor.parser;

import com.adihascal.HOIEditor.IndentedFileWriter;

public class SavePrimitive<T> implements SaveElement
{
	private T value;
	
	SavePrimitive(T val)
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
	
	@Override
	public void write(IndentedFileWriter writer)
	{
		writer.write(value);
	}
	
}
