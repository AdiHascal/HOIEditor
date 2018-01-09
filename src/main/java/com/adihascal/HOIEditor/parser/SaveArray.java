package com.adihascal.HOIEditor.parser;

import java.util.Collection;

public class SaveArray implements SaveElement
{
	private SaveElement[] array;
	
	public SaveArray(Collection<SaveElement> arr)
	{
		this.array = arr.toArray(new SaveElement[0]);
	}
	
	public SaveElement get(int i)
	{
		return array[i];
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(SaveElement se : array)
		{
			sb.append(se).append("\n");
		}
		return sb.toString();
	}
}
