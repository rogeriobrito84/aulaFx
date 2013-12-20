package main;

import java.awt.Event;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import usuario.ControleUsuario;

public class MainControle {
   
	
	
	public void listarUsuarios() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		AnchorPane pane = (AnchorPane) loader.load(ControleUsuario.class.getResource("usuario.fxml"));
		ControleUsuario uControle = loader.getController();
		Scene cena = new Scene(pane);
		Stage palco = new Stage();
		palco.setResizable(false);
		palco.setTitle("Lista de Usuários");
		palco.initModality(Modality.APPLICATION_MODAL);
		
		palco.setScene(cena);
		palco.show();
		
		
	}
	
	
}
