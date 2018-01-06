package com.adihascal.HOIEditor.elements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ElementBase
{
	private String name;
	private LinkedList<ElementBase> children;
	
	public ElementBase(String name, ElementBase... children)
	{
		this.name = name;
		this.children.addAll(Arrays.asList(children));
	}
	
	public LinkedList<ElementBase> getChildren()
	{
		return children;
	}
	
	public LinkedList<ElementBase> getChildrenByName(String name)
	{
		return (LinkedList<ElementBase>) this.children.stream().filter(e -> e.getName().equals(name))
				.collect(Collectors.toList());
	}
	
	public String getName()
	{
		return name;
	}
}
