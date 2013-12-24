package main;


import java.awt.event.MouseAdapter;
import java.io.IOException;

import javax.swing.plaf.basic.BasicTabbedPaneUI.MouseHandler;
import javax.xml.ws.handler.Handler;

import com.sun.prism.impl.Disposer.Target;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
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
	
	public void pressBtn(ActionEvent e){
		
	}
	
	
	
}
