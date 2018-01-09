package com.adihascal.HOIEditor.parser;

import java.io.IOException;
import java.util.ArrayList;

public class FileParser
{
	private final String source;
	private final int length;
	private int pos = 9;
	
	public FileParser(String source)
	{
		this.source = source;
		this.length = source.length();
	}
	
	private static String addQuotes(String value)
	{
		StringBuilder product = new StringBuilder();
		product.append("\"");
		char[] var2 = value.toCharArray();
		
		for(char ch : var2)
		{
			switch(ch)
			{
				case '\b':
					product.append("\\b");
					break;
				case '\t':
					product.append("\\t");
					break;
				case '\n':
					product.append("\\n");
					break;
				case '\f':
					product.append("\\f");
					break;
				case '\r':
					product.append("\\r");
					break;
				case '"':
					product.append("\\\"");
					break;
				case '\\':
					product.append("\\\\");
					break;
				default:
					if(ch < ' ')
					{
						product.append(unicodeEscape(ch));
					}
					else
					{
						product.append(ch);
					}
			}
		}
		
		product.append("\"");
		return product.toString();
	}
	
	private static String toString(int c)
	{
		return c == -1 ? "eof" : String.valueOf((char) c);
	}
	
	private static String unicodeEscape(char ch)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\\u");
		String hex = Integer.toHexString(ch);
		
		for(int i = hex.length(); i < 4; ++i)
		{
			sb.append('0');
		}
		
		sb.append(hex);
		return sb.toString();
	}
	
	public SaveElement parse() throws Exception
	{
		SaveElement value = this.parseObject(true);
		this.skipWhiteSpace();
		if(this.pos < this.length)
		{
			throw this.expectedError(this.pos, "eof", toString(this.peek()));
		}
		else
		{
			return value;
		}
	}
	
	private SaveElement parseLiteral() throws Exception
	{
		this.skipWhiteSpace();
		int c = this.peek();
		if(c == -1)
		{
			throw this.expectedError(this.pos, "json literal", "eof");
		}
		else
		{
			switch(c)
			{
				case '\"':
					return this.parseString(true);
				case '{':
					return this.parseArrayOrObject();
				default:
					if(!this.isDigit(c) && c != 45)
					{
						if(c == 46)
						{
							throw this.numberError(this.pos);
						}
						else
						{
							return parseString(false);
						}
					}
					else
					{
						return this.parseNumber();
					}
			}
		}
	}
	
	private SaveElement parseArrayOrObject() throws Exception
	{
		int start = pos;
		while(peek() != '}')
		{
			if(next() == '=')
			{
				pos = start;
				return parseObject(false);
			}
		}
		pos = start;
		return parseArray();
	}
	
	private SaveObject parseObject(boolean root) throws Exception
	{
		SaveObject object = new SaveObject();
		
		assert root || this.peek() == '{';
		
		if(!root)
		{
			++this.pos;
		}
		
		while(this.pos < this.length)
		{
			this.skipWhiteSpace();
			int c = this.peek();
			switch(c)
			{
				case '"':
					String id = this.parseString(true).getValue();
					this.expectEqual();
					SaveElement value = this.parseLiteral();
					object.add(id, value);
					break;
				case ' ':
					++this.pos;
					break;
				case '}':
					++this.pos;
					return object;
				case '{':
					++this.pos;
					object.add("", this.parseObject(false));
					break;
				default:
					id = this.parseString(false).getValue();
					this.expectEqual();
					value = this.parseLiteral();
					object.add(id, value);
					break;
			}
		}
		
		if(!root)
		{
			throw this.expectedError(this.pos, ", or }", "eof");
		}
		else
		{
			return object;
		}
	}
	
	private void expectEqual() throws Exception
	{
		this.skipWhiteSpace();
		int n = this.next();
		if(n != '=')
		{
			throw this.expectedError(this.pos - 1, "=", toString(n));
		}
	}
	
	private SaveArray parseArray() throws Exception
	{
		ArrayList<SaveElement> list = new ArrayList<>();
		assert this.peek() == '{';
		
		++this.pos;
		
		while(this.pos < this.length)
		{
			this.skipWhiteSpace();
			int c = this.peek();
			switch(c)
			{
				case '}':
					++this.pos;
					return new SaveArray(list);
				default:
					list.add(this.parseLiteral());
			}
		}
		
		throw this.expectedError(this.pos, ", or ]", "eof");
	}
	
	private SavePrimitive<String> parseString(boolean quotes) throws Exception
	{
		int start = quotes ? ++this.pos : this.pos;
		StringBuilder sb = null;
		
		while(this.pos < this.length)
		{
			int c = this.next();
			if(c == '\\')
			{
				if(sb == null)
				{
					sb = new StringBuilder(this.pos - start + 16);
				}
				
				sb.append(this.source, start, this.pos - 1);
				sb.append(this.parseEscapeSequence());
				start = this.pos;
			}
			else if((quotes && c == '"') || (!quotes && (c <= ' ' || c == '=')))
			{
				if(sb != null)
				{
					sb.append(this.source, start, this.pos - 1);
					if(!quotes)
					{
						pos--;
					}
					return new SavePrimitive<>(quotes ? addQuotes(sb.toString()) : sb.toString());
				}
				String resultString = this.source.substring(start, this.pos - 1);
				SavePrimitive<String> result = new SavePrimitive<>(quotes ? addQuotes(resultString) : resultString);
				if(!quotes)
				{
					pos--;
				}
				return result;
			}
		}
		
		throw this.error("Missing close quote", this.pos, this.length);
	}
	
	private char parseEscapeSequence() throws Exception
	{
		int c = this.next();
		switch(c)
		{
			case 34:
				return '"';
			case 47:
				return '/';
			case 92:
				return '\\';
			case 98:
				return '\b';
			case 102:
				return '\f';
			case 110:
				return '\n';
			case 114:
				return '\r';
			case 116:
				return '\t';
			case 117:
				return this.parseUnicodeEscape();
			default:
				throw this.error("Invalid escape character", this.pos - 1, this.length);
		}
	}
	
	private char parseUnicodeEscape() throws Exception
	{
		return (char) (this.parseHexDigit() << 12 | this.parseHexDigit() << 8 | this.parseHexDigit() << 4 | this
				.parseHexDigit());
	}
	
	private int parseHexDigit() throws Exception
	{
		int c = this.next();
		if(c >= 48 && c <= 57)
		{
			return c - 48;
		}
		else if(c >= 65 && c <= 70)
		{
			return c + 10 - 65;
		}
		else if(c >= 97 && c <= 102)
		{
			return c + 10 - 97;
		}
		else
		{
			throw this.error("Invalid hex number", this.pos - 1, this.length);
		}
	}
	
	private boolean isDigit(int c)
	{
		return c >= 48 && c <= 57;
	}
	
	private void skipDigits()
	{
		while(true)
		{
			if(this.pos < this.length)
			{
				int c = this.peek();
				if(this.isDigit(c))
				{
					++this.pos;
					continue;
				}
			}
			
			return;
		}
	}
	
	private SavePrimitive<? extends Number> parseNumber() throws Exception
	{
		int start = this.pos;
		int c = this.next();
		if(c == '-')
		{
			c = this.next();
		}
		
		if(!this.isDigit(c))
		{
			throw this.numberError(start);
		}
		else
		{
			if(c != '0')
			{
				this.skipDigits();
			}
			
			if(this.peek() == '.')
			{
				++this.pos;
				if(!this.isDigit(this.next()))
				{
					throw this.numberError(this.pos - 1);
				}
				
				this.skipDigits();
			}
			
			c = this.peek();
			if(c == 'e' || c == 'E')
			{
				++this.pos;
				c = this.next();
				if(c == '-' || c == '+')
				{
					c = this.next();
				}
				
				if(!this.isDigit(c))
				{
					throw this.numberError(this.pos - 1);
				}
				
				this.skipDigits();
			}
			
			Double d = Double.parseDouble(this.source.substring(start, this.pos));
			if(d.intValue() == d)
			{
				return new SavePrimitive<>(d.intValue());
			}
			else
			{
				return new SavePrimitive<>(d);
			}
		}
	}
	
	private int peek()
	{
		return this.pos >= this.length ? -1 : this.source.charAt(this.pos);
	}
	
	private int next()
	{
		int next = this.peek();
		++this.pos;
		return next;
	}
	
	private void skipWhiteSpace()
	{
		while(this.pos < this.length)
		{
			switch(this.peek())
			{
				case 9:
				case 10:
				case 13:
				case 32:
					++this.pos;
					break;
				default:
					return;
			}
		}
		
	}
	
	private Exception error(String message, int start, int length)
	{
		return new IOException(message + " from " + start + " to " + length);
	}
	
	private Exception numberError(int start)
	{
		return new IOException("Invalid number at " + start);
	}
	
	private Exception expectedError(int start, String expected, String found)
	{
		return new IOException(String.format("expected %s, but found %s at %d", expected, found, start));
	}
}
