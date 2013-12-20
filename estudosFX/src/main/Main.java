package main;



import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import usuario.Usuario;

public class Main extends Application {
	
	@Override
	public void start(Stage palco) throws Exception {
		palco.setTitle("Aplicação com JavaFX");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
		AnchorPane  pane = (AnchorPane) loader.load();
		Scene cena = new Scene(pane);
		palco.setScene(cena);
		palco.show();
	}

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		launch(args);
	}

	

}

