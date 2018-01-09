package com.adihascal.HOIEditor.parser;

import java.util.Map;
import java.util.TreeMap;

public class SaveObject implements SaveElement
{
	@SuppressWarnings("ComparatorMethodParameterNotUsed")
	private Map<String, SaveElement> members = new TreeMap<>((o1, o2) -> 1);
	
	public void add(String key, SaveElement value)
	{
		members.put(key, value);
	}
	
	public Map<String, SaveElement> getMembers()
	{
		return members;
	}
	
	public SaveElement getByName(String name)
	{
		return members.get(name);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		members.forEach((key, value) -> {
			if(!key.equals(""))
			{
				builder.append(key).append("=");
			}
			if(!(value instanceof SavePrimitive))
			{
				builder.append("{\n");
			}
			builder.append(value).append("\n");
			if(!(value instanceof SavePrimitive))
			{
				builder.append("}\n");
			}
		});
		return builder.toString();
	}
}
