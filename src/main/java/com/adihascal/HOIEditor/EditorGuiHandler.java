package com.adihascal.HOIEditor;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditorGuiHandler implements Initializable
{
	public Accordion categories;
	public ScrollPane editPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		categories.prefHeightProperty().bind(Main.stage.heightProperty());
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
			alert.getButtonTypes().setAll(save, dontSave);
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
		//TODO fill categories
		EditableContent.categories.forEach((key, value) -> System.out.println(key + " " + value));
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
}
