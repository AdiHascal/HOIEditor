package com.adihascal.HOIEditor.parser;

import com.adihascal.HOIEditor.IndentedFileWriter;

@SuppressWarnings("unchecked")
public class SavePrimitive<T> implements SaveElement
{
	private T value;
	private Class<T> type;
	
	SavePrimitive(T val)
	{
		this.value = val;
		this.type = (Class<T>) val.getClass();
	}
	
	public void updateValue(String newVal)
	{
		if(type == String.class)
		{
			value = (T) newVal;
		}
		else if(type == Integer.class)
		{
			value = (T) (Integer) Integer.parseInt(newVal);
		}
		else
		{
			value = (T) (Double) Double.parseDouble(newVal);
		}
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
