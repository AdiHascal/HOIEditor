package com.adihascal.HOIEditor.parser;

import com.adihascal.HOIEditor.IndentedFileWriter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class SaveArray implements SaveElement, Iterable<SavePrimitive>
{
	private SavePrimitive[] array;
	
	SaveArray(Collection<SavePrimitive> arr)
	{
		this.array = arr.toArray(new SavePrimitive[0]);
	}
	
	public SaveElement get(int i)
	{
		return array[i];
	}
	
	@Override
	public String toString()
	{
		return "";
	}
	
	@Override
	public Iterator<SavePrimitive> iterator()
	{
		return new Iterator<SavePrimitive>()
		{
			int index = 0;
			
			@Override
			public boolean hasNext()
			{
				return index < array.length;
			}
			
			@Override
			public SavePrimitive next()
			{
				return array[index++];
			}
		};
	}
	
	@Override
	public void forEach(Consumer<? super SavePrimitive> action)
	{
		Arrays.stream(array).forEach(action);
	}
	
	@Override
	public void write(IndentedFileWriter writer)
	{
		if(array.length > 0)
		{
			writer.write("");
			forEach(a -> writer.writeNoIndent(a + " "));
			writer.write("\r\n");
		}
	}
	
}
