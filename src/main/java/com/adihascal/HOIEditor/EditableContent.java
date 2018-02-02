package com.adihascal.HOIEditor;

import com.adihascal.HOIEditor.parser.FileParser;
import com.adihascal.HOIEditor.parser.SaveElement;
import com.adihascal.HOIEditor.parser.SaveObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class EditableContent
{
	static HashMap<String, TreeMap<String, SaveObject>> categories = new HashMap<>();
	
	public static void initEditor(SaveObject root)
	{
		try
		{
			SaveObject categoryRoot = new FileParser(new String(Files
					.readAllBytes(Paths.get(EditableContent.class.getResource("/categories").toURI()))), 0).parse();
			LinkedList<Category> cats = new LinkedList<>();
			categoryRoot.getMembers().forEach((key, value) -> cats.add(new Category((SaveObject) value)));
			cats.forEach(c -> categories.put(c.categoryName, new TreeMap<>(Comparator.naturalOrder())));
			traverseSave(cats, root);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void traverseSave(LinkedList<Category> cats, SaveObject obj)
	{
		if(obj.getSelfName() != null)
		{
			Optional<Category> cat = cats.stream().filter(c -> c.accepts(obj.getSelfName())).findFirst();
			if(cat.isPresent())
			{
				categories.get(cat.get().categoryName).putAll(cat.get().accept(obj));
				return;
			}
		}
		obj.getMembers().entrySet().stream().filter(e -> e.getValue() instanceof SaveObject)
				.forEach(e -> traverseSave(cats, (SaveObject) e.getValue()));
	}
	
	private static class Category
	{
		String categoryName, tag, policy, objName;
		
		private Category(SaveObject obj)
		{
			if(obj != null)
			{
				this.categoryName = obj.getSelfName();
				this.tag = obj.getFirstByName("tag").toString();
				this.policy = obj.getFirstByName("policy").toString();
				this.objName = obj.getFirstByName("name").toString();
			}
		}
		
		private boolean accepts(String identifier)
		{
			return tag.equals(identifier);
		}
		
		private HashMap<String, SaveObject> accept(SaveObject object)
		{
			return applyPolicy(object).entrySet().stream().filter(e -> e.getValue() instanceof SaveObject)
					.collect(Collectors
							.toMap(e -> getObjectName((SaveObject) e.getValue()), e -> (SaveObject) e
									.getValue(), (a, b) -> b, HashMap::new));
		}
		
		private Map<String, SaveElement> applyPolicy(SaveObject obj)
		{
			switch(policy)
			{
				case "parent":
					return Collections.singletonMap("", obj);
				case "child":
					return obj.getMembers();
				default:
					return Collections.emptyMap();
			}
		}
		
		private String getObjectName(SaveObject obj)
		{
			if(objName.startsWith("#"))
			{
				return obj.getFirstByName(objName.substring(1)).toString();
			}
			else if(objName.equals("identifier"))
			{
				return obj.getSelfName();
			}
			return null;
		}
	}
}
