package com.adihascal.HOIEditor;

import com.adihascal.HOIEditor.parser.FileParser;
import com.adihascal.HOIEditor.parser.SaveObject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class Main extends Application
{
	public static Stage stage;
	public static SaveObject root;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		stage = primaryStage;
		FileChooser fc = new FileChooser();
		fc.setSelectedExtensionFilter(new ExtensionFilter("Hearts of Iron save", "hoi4"));
		fc.setTitle("Select save file");
		fc.setInitialDirectory(new File(System
				.getProperty("user.home") + "\\Documents\\Paradox Interactive\\Hearts of Iron IV\\save games"));
		try
		{
			File result = fc.showOpenDialog(null);
			if(result == null)
			{
				return;
			}
			root = new FileParser(new String(Files.readAllBytes(result.toPath())), 9).parse();
			primaryStage.setTitle(result.getPath() + " - HOIEditor");
			EditableContent.initEditor(root);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/editor.fxml"));
			primaryStage.setScene(new Scene(loader.load()));
			primaryStage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception
	{
		super.stop();
		System.exit(0);
	}
}
