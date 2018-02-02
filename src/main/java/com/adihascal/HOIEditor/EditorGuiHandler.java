package com.adihascal.HOIEditor;

import com.adihascal.HOIEditor.parser.SaveArray;
import com.adihascal.HOIEditor.parser.SaveObject;
import com.adihascal.HOIEditor.parser.SavePrimitive;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class EditorGuiHandler implements Initializable, EventHandler<Event>
{
	public Accordion categories;
	public ScrollPane editPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		editPane.prefHeightProperty().bind(Main.stage.heightProperty());
		editPane.prefWidthProperty().bind(Main.stage.widthProperty());
		Main.stage.setOnCloseRequest(e ->
		{
			e.consume();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirm Exit");
			alert.setHeaderText("You may have unsaved changes");
			ButtonType save = new ButtonType("Save and Exit");
			ButtonType dontSave = new ButtonType("Exit Without Saving");
			ButtonType cancel = alert.getButtonTypes().remove(1);
			alert.getButtonTypes().setAll(save, dontSave, cancel);
			Optional<ButtonType> result = alert.showAndWait();
			if(result.isPresent())
			{
				if(result.get() == save)
				{
					writeToFile();
					Platform.exit();
				}
				else if(result.get() == dontSave)
				{
					Platform.exit();
				}
				else
				{
					alert.close();
				}
			}
		});
		categories.getPanes().clear();
		EditableContent.categories.entrySet().forEach(e -> categories.getPanes().add(getCategoryPane(e)));
	}
	
	private TitledPane getCategoryPane(Entry<String, TreeMap<String, SaveObject>> entry)
	{
		TitledPane pane = new TitledPane(entry.getKey(), new ScrollPane(new AnchorPane()));
		ScrollPane scroll = (ScrollPane) pane.getContent();
		AnchorPane inside = (AnchorPane) scroll.getContent();
		
		int width = 0;
		int i = 0;
		for(Entry<String, SaveObject> e : entry.getValue().entrySet())
		{
			Text label = new Text(5, 20 + 20 * i++, e.getKey());
			width = (int) Math.max(width, label.getLayoutBounds().getWidth());
			label.setOnMouseClicked(this);
			label.setUserData(e.getValue());
			inside.getChildren().add(label);
		}
		
		pane.setText(entry.getKey());
		pane.setMinWidth(200);
		scroll.setMaxWidth(200);
		inside.setMinWidth(width);
		inside.setMaxHeight(400);
		inside.setMinHeight(20);
		inside.setPrefHeight(20 * entry.getValue().size());
		inside.setMaxHeight(20 * entry.getValue().size());
		scroll.setPrefHeight(20 + 20 * entry.getValue().size());
		scroll.setMaxHeight(20 + 20 * entry.getValue().size());
		if(entry.getValue().size() < 15)
		{
			scroll.setTranslateY(Math.floor(-(145 - 7.5 * entry.getValue().size())));
		}
		return pane;
	}
	
	@Override
	public void handle(Event event)
	{
		((AnchorPane) editPane.getContent()).getChildren().clear();
		displayObject((SaveObject) ((Node) event.getSource()).getUserData(), new PositionTracker());
	}
	
	private void writeToFile()
	{
		try
		{
			Path out = Paths
					.get(new File(Main.stage.getTitle()).getParent(), Main.stage.getTitle()
							.substring(Main.stage.getTitle().lastIndexOf('\\'), Main.stage
									.getTitle()
									.lastIndexOf('.')) + " - edited.hoi4");
			Files.createFile(out);
			IndentedFileWriter writer = new IndentedFileWriter(out.toString());
			writer.write("HOI4txt\r\n");
			Main.root.write(writer);
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private int getStringWidth(String str, Font font)
	{
		return (int) Math.ceil(Toolkit.getToolkit().getFontLoader().computeStringWidth(str, font));
	}
	
	private void displayObject(SaveObject obj, PositionTracker tracker)
	{
		obj.forEach(entry ->
		{
			Label name = new Label(entry.getKey() + ":");
			name.setFont(Font.getDefault());
			name.setLayoutX(tracker.indent * 20);
			name.setLayoutY(20 * tracker.vPos);
			((AnchorPane) editPane.getContent()).getChildren().add(name);
			if(!(entry.getValue() instanceof SaveObject))
			{
				if(entry.getValue() instanceof SavePrimitive)
				{
					TextField value = new TextField(entry.getValue().toString());
					value.setOnKeyReleased(e -> ((SavePrimitive) entry.getValue()).updateValue(value.getText()));
					value.setFont(name.getFont());
					value.setLayoutX(tracker.indent * 20 + getStringWidth(name.getText(), name.getFont()) + 5);
					value.setLayoutY(tracker.vPos * 20);
					((AnchorPane) editPane.getContent()).getChildren().add(value);
					tracker.incV();
					tracker.incV();
				}
				else
				{
					tracker.incIndent();
					tracker.incV();
					((SaveArray) entry.getValue()).forEach(e ->
					{
						TextField value = new TextField(e.toString());
						value.setOnKeyReleased(ev -> e.updateValue(value.getText()));
						value.setLayoutX(tracker.indent * 20);
						value.setLayoutY(tracker.vPos * 20);
						((AnchorPane) editPane.getContent()).getChildren().add(value);
						tracker.incV();
					});
					tracker.incV();
					tracker.decIndent();
				}
			}
			else
			{
				tracker.incV();
				tracker.incIndent();
				displayObject((SaveObject) entry.getValue(), tracker);
				tracker.decIndent();
			}
		});
		if(tracker.indent == 1)
		{
			tracker.decIndent();
		}
	}
	
	private static class PositionTracker
	{
		int vPos = 0, indent = 0;
		
		void incV()
		{
			vPos++;
		}
		
		void incIndent()
		{
			indent++;
		}
		
		void decIndent()
		{
			if(indent > 0)
			{
				indent--;
			}
		}
	}
}
