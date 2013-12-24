package main;



import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import usuario.Usuario;
import util.DAO;

public class Main extends Application {
	
	@Override
	public void start(Stage palco) {
		palco.setTitle("Aplicação com JavaFX");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
		AnchorPane pane;
		try {
			
			pane = (AnchorPane) loader.load();
			Scene cena = new Scene(pane);
			DAO dao = new DAO();
			dao.createFactory();
			palco.setScene(cena);
			cena.getStylesheets().add("/util/style.css"); 
			palco.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {
		launch(args);
	}

	

}

