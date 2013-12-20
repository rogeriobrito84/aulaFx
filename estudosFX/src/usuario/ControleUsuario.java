package usuario;




import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.JOptionPane;

import util.DAOGenerico;

public class ControleUsuario implements Initializable {
	
	@FXML private static TableView<Usuario> tvUsuarios = new TableView<Usuario>();
	@FXML static TextField fieldId = new TextField() ;
	@FXML static TextField fieldNome = new TextField() ;
	@FXML static TextField fieldEmail = new TextField() ;
	@FXML static TextField fieldData = new TextField();
	@FXML static Button btnSalvar = new Button();
	ObservableList<Usuario> itens;
	
	DAOGenerico<Usuario, Integer> dao = new DAOGenerico<Usuario,Integer>(Usuario.class);
	
	private Usuario usu = null;
	private Stage palco;

	
	public ControleUsuario(){}
	public ControleUsuario(Usuario usu){
		this.usu = usu;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		carregarColunas();
		try {
			carregarItens();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addNovo() throws IOException{
		mostrarCadastro();
		palco.setTitle("Cadastrando Usuário");
		btnSalvar.setText("Salvar");
		//Setando o evendo no botão da tela.
		btnSalvar.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Stage window = (Stage)  btnSalvar.getScene().getWindow();
				try {
					Usuario usu =  carregarDadosTelaUsuario();
					dao.beginTransaction();
					dao.persist(usu);
					dao.commitTransaction();
					carregarItens();
					window.close();
				} catch (ParseException e) {
					window.close();
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		palco.show();
	}
	
	public void editarUsuario(ActionEvent e){
		int linha = tvUsuarios.getSelectionModel().getSelectedIndex();
		if(linha > -1){
			int id = tvUsuarios.getItems().get(linha).getId();
			
			
			try {
				usu = dao.search(id);
				mostrarCadastro();
				palco.setTitle("Editando Usuário");
				carregarDadosUsuarioTela(usu);
				btnSalvar.setText("Alterar");
				//Setando o evendo
				btnSalvar.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						Stage window = (Stage)  btnSalvar.getScene().getWindow();
						try {
							usu = carregarDadosTelaUsuario();
							dao.beginTransaction();
							dao.persist(usu);
							dao.commitTransaction();
							carregarItens();
							window.close();
						} catch (ParseException e) {
							window.close();
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				palco.show();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else{
			JOptionPane.showMessageDialog(null, "Selecione um usuário para fazer alterações!");
		}
	}
	
	public void excluir(ActionEvent e){
		int linha =  tvUsuarios.getSelectionModel().getSelectedIndex();
		
		if(linha > -1){
			Stage window = (Stage)  btnSalvar.getScene().getWindow();
			Usuario usuario = tvUsuarios.getItems().get(linha);
			if(JOptionPane.showConfirmDialog(null, "Deseja excluir " + usuario.getNome() + " ?") == 0){
				removerUsuario(indexUsuario(usuario));
				carregarItens();
				JOptionPane.showMessageDialog(null,usuario.getNome() + " Excluido com sucesso!");
				window.close();
			}
		}else{
			JOptionPane.showMessageDialog(null, "Selecione um usuário para excluir!");
		}
	}
	
	private void mostrarCadastro() throws IOException{
		palco = new Stage();
		palco.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		AnchorPane pane = loader.load(getClass().getResource("cadastro.fxml"));
		Scene cena = new Scene(pane);
		palco.setScene(cena);
	}
	
	public void salvar(ActionEvent e){
		Stage windows = (Stage) fieldNome.getScene().getWindow();
	    try {
			adicionarUsuario(carregarDadosTelaUsuario());
			carregarItens();
			windows.close();
			
		} catch (ParseException e1) {
			windows.close();
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public void alterar(){
		Stage windows = (Stage) fieldNome.getScene().getWindow();
		try {
			alterarUsuario(carregarDadosTelaUsuario());
			windows.close();
		} catch (ParseException e) {
			windows.close();
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void cancelar(ActionEvent e){
		Stage stage = (Stage) fieldNome.getScene().getWindow();
	    stage.close();
	}
	
	private Usuario carregarDadosTelaUsuario() throws Exception{
		Usuario usu = new Usuario();
		StringBuilder msg = new StringBuilder("");
		if(fieldNome.getText().trim().isEmpty()){
			msg.append("Informe o nome do usuário \n");
		}
		if(fieldEmail.getText().trim().isEmpty()){
			msg.append("Informe o email do usuário \n");
		}
		if(fieldData.getText().trim().isEmpty()){
			msg.append("Informe a data de nascimento do do usuário \n");
		}
		 if(!msg.toString().trim().isEmpty()){
				JOptionPane.showMessageDialog(null, msg.toString());
				throw new Exception(msg.toString());
		 }else{
			usu.setId(Integer.parseInt(fieldId.getText()));
			usu.setNome(fieldNome.getText());
			usu.setEmail(fieldEmail.getText());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
			Calendar dataC = Calendar.getInstance();
			String dataS = fieldData.getText();
			Date data;
			if((dataS != null) & !dataS.trim().isEmpty()){
				data = sdf.parse(dataS);
			}else{
				data = (Date) dataC.getTime();
			}
			usu.setDataNascimento(fieldData.getText()); 
		 }
		 	
		
		return usu;
	}
	
	private void carregarDadosUsuarioTela(Usuario usu){
		fieldId.setText(usu.getId()+"");
		fieldNome.setText(usu.getNome());
		fieldEmail.setText(usu.getEmail());
		
		fieldData.setText(usu.getDataNascimento());
	}

	
	
	private void carregarColunas(){
		if(tvUsuarios.getColumns().size() <= 0){
			//Coluna nome
			TableColumn<Usuario, String> colNome = new TableColumn<Usuario, String>("Nome");
			colNome.setCellValueFactory( new PropertyValueFactory<Usuario,String>("nome"));
			colNome.setPrefWidth(150);
			//Coluna Email
			TableColumn<Usuario, String> colEmail = new TableColumn<Usuario, String>("Email");
			colEmail.setCellValueFactory(new PropertyValueFactory<Usuario, String>("email"));
			colEmail.setPrefWidth(228);
			//Coluna Data de nascimento
			TableColumn<Usuario, String> colData = new TableColumn<Usuario, String>("Data de Nacimento");
			colData.setCellValueFactory(new PropertyValueFactory<Usuario, String>("dataNascimento"));
			colData.setPrefWidth(120);
			tvUsuarios.getColumns().addAll(colNome, colEmail, colData);
		}
	}
	
	public void carregarItens() throws Exception{
		itens = FXCollections.observableArrayList(dao.search());
		System.out.println("Contidade Usuários: " + itens.size());
		tvUsuarios.setItems(itens);
	}
	
	
}
