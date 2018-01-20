package com.adihascal.HOIEditor;

import java.io.FileWriter;
import java.io.IOException;

public class IndentedFileWriter extends FileWriter
{
	private int indent = 0;
	
	IndentedFileWriter(String fileName) throws IOException
	{
		super(fileName);
	}
	
	public void endTag()
	{
		deIndent();
		write("}\r\n");
	}
	
	private void deIndent()
	{
		indent--;
	}
	
	@Override
	public void write(String str)
	{
		try
		{
			StringBuilder result = new StringBuilder();
			for(int i = 0; i < indent; i++)
			{
				result.append('\t');
			}
			result.append(str);
			super.write(result.toString());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void startTag(String name)
	{
		write(name + "={\r\n");
		indent();
	}
	
	private void indent()
	{
		indent++;
	}
	
	public void write(Object o)
	{
		write(String.valueOf(o) + "\r\n");
	}
	
	public void writeNoIndent(String str)
	{
		try
		{
			super.write(str);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
