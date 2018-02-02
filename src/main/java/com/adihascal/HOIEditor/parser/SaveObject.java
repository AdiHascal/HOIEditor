package com.adihascal.HOIEditor.parser;

import com.adihascal.HOIEditor.IndentedFileWriter;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SaveObject implements SaveElement, Iterable<Entry<String, SaveElement>>
{
	@SuppressWarnings("ComparatorMethodParameterNotUsed")
	private Map<String, SaveElement> members = new TreeMap<>((o1, o2) -> 1);
	private String selfName;
	
	public void add(String key, SaveElement value)
	{
		members.put(key, value);
	}
	
	public SaveElement remove(String key)
	{
		return members.remove(key);
	}
	
	public Map<String, SaveElement> getMembers()
	{
		return members;
	}
	
	public SaveElement getFirstByName(String name)
	{
		Optional<Entry<String, SaveElement>> result = members.entrySet().stream().filter(e -> e.getKey().equals(name))
				.findFirst();
		if(result.isPresent())
		{
			return result.get().getValue();
		}
		return null;
	}
	
	public List<SaveElement> getAllByName(String name)
	{
		return members.entrySet().stream().filter(e -> e.getKey().equals(name)).map(Entry::getValue)
				.collect(Collectors.toList());
	}
	
	public String getSelfName()
	{
		return selfName;
	}
	
	public void setSelfName(String selfName)
	{
		this.selfName = selfName;
	}
	
	@Override
	public String toString()
	{
		return "";
	}
	
	@Override
	public void write(IndentedFileWriter writer)
	{
		members.forEach((key, value) ->
		{
			if(!(value instanceof SavePrimitive))
			{
				writer.startTag(key);
				value.write(writer);
				writer.endTag();
			}
			else
			{
				writer.write(key + "=" + value + "\r\n");
			}
		});
	}
	
	@Override
	public Iterator<Entry<String, SaveElement>> iterator()
	{
		return members.entrySet().iterator();
	}
	
	@Override
	public void forEach(Consumer<? super Entry<String, SaveElement>> action)
	{
		members.entrySet().forEach(action);
	}
}
