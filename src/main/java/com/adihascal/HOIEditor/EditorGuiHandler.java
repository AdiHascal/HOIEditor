package com.adihascal.HOIEditor;

import com.adihascal.HOIEditor.parser.SaveObject;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

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
	
	private TitledPane getCategoryPane(Entry<String, HashMap<String, SaveObject>> entry)
	{
		TitledPane pane = new TitledPane(entry.getKey(), new ScrollPane(new AnchorPane()));
		ScrollPane scroll = (ScrollPane) pane.getContent();
		AnchorPane inside = (AnchorPane) scroll.getContent();
		pane.setText(entry.getKey());
		pane.setMinWidth(200);
		scroll.setMaxWidth(220);
		inside.setMinWidth(200);
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
		int i = 0;
		for(Entry<String, SaveObject> e : entry.getValue().entrySet())
		{
			Text label = new Text(5, 20 + 20 * i++, e.getKey());
			label.setOnMouseClicked(this);
			label.setUserData(e.getValue());
			inside.getChildren().add(label);
		}
		return pane;
	}
	
	@Override
	public void handle(Event event)
	{
		displayObject((SaveObject) ((Node) event.getSource()).getUserData());
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
	
	private void displayObject(SaveObject obj)
	{
	
	}
}
