package usuario;




import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.JOptionPane;

import util.DAOGenerico;

public class ControleUsuario implements Initializable {
	
	@FXML private static TableView<Usuario> tvUsuarios = new TableView<Usuario>();
	@FXML static TextField fieldId = new TextField() ;
	@FXML static TextField fieldNome = new TextField() ;
	@FXML static TextField fieldEmail = new TextField() ;
	@FXML static TextField fieldData = new TextField();
	@FXML static Button btnSalvar = new Button();
	private  ObservableList<Usuario> itens = null;
	
	DAOGenerico<Usuario, Integer> dao;
	
	private Usuario usu = null;
	private Stage palco;

	
	public ControleUsuario(){}
	public ControleUsuario(Usuario usu){
		this.usu = usu;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
			if(dao == null){
				dao = new DAOGenerico<Usuario, Integer>(Usuario.class);
			}
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
		btnSalvar.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event arg0) {
				Stage window = (Stage) btnSalvar.getScene().getWindow();
				try {
					salvar();
				} catch (Exception e) {
					window.close();
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
				btnSalvar.setOnMouseClicked(new EventHandler<Event>() {
					@Override
					public void handle(Event arg0) {
						Stage window = (Stage)  btnSalvar.getScene().getWindow();
						try {
							alterar();
						} catch (Exception e) {
							window.close();
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
				try {
					String msg = validarCampos(usuario, true);
					if(msg.trim().isEmpty()){
						dao.beginTransaction();
						dao.remove(usuario);
						dao.commitTransaction();
						carregarItens();
						JOptionPane.showMessageDialog(null,usuario.getNome() + " Excluido com sucesso!");
						window.close();
					}else{
						System.out.println("Linha: 170");
					}
				} catch (Exception e1) {
					window.close();
					e1.printStackTrace();
				}
				
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
	
	public void salvar(){
		Stage windows = (Stage) fieldNome.getScene().getWindow();
	    try {
			Usuario usu = carregarDadosTelaUsuario();
			String msg = validarCampos(usu, false);
			if(msg.trim().isEmpty()){
				dao.beginTransaction();
				dao.persist(usu);
				dao.commitTransaction();
				windows.close();
				carregarItens();
			}else{
				JOptionPane.showMessageDialog(null, msg);
			}
		} catch (ParseException e1) {
			windows.close();
			e1.printStackTrace();
		} catch (Exception e1) {
			windows.close();
			e1.printStackTrace();
		}
	}
	public void alterar(){
		Stage windows = (Stage) fieldNome.getScene().getWindow();
		try {
			Usuario usu = carregarDadosTelaUsuario();
			String msg = validarCampos(usu, true); 
			if(msg.trim().isEmpty()){
				dao.beginTransaction();
				dao.persist(usu);
				dao.commitTransaction();
				windows.close();
				carregarItens();
			}else{
				JOptionPane.showMessageDialog(null, msg);
			}
		} catch (ParseException e) {
			windows.close();
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			windows.close();
			e.printStackTrace();
		}
		
	}
	
	public void cancelar(ActionEvent e){
		Stage stage = (Stage) fieldNome.getScene().getWindow();
	    stage.close();
	}
	
	private Usuario carregarDadosTelaUsuario() throws Exception{
		Usuario usu = new Usuario();
		if(!fieldId.getText().trim().isEmpty()){
			usu.setId(Integer.parseInt(fieldId.getText()));
		}
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
		usu.setDataNascimento(data); 
		
		return usu;
	}
	
	private void carregarDadosUsuarioTela(Usuario usu) throws ParseException{
		fieldId.setText(usu.getId()+"");
		fieldNome.setText(usu.getNome());
		fieldEmail.setText(usu.getEmail());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/YYYY");
		
		String data = sdf.format(usu.getDataNascimento());
		fieldData.setText(data);
	}

	public String validarCampos(Usuario usu,boolean isAlter) throws Exception{
		StringBuilder msg = new StringBuilder("");
		if(isAlter){
			if(fieldId.getText().equals("0")){
				msg.append("Informe o ID do usuário \n");
			}
		}
		if(fieldNome.getText().trim().isEmpty()){
			msg.append("Informe o nome do usuário \n");
		}
		if(fieldEmail.getText().trim().isEmpty()){
			msg.append("Informe o email do usuário \n");
		}
		if(fieldData.getText().trim().isEmpty()){
			msg.append("Informe a data de nascimento do do usuário \n");
		}
		return msg.toString();
	}
	
	private void carregarColunas(){
		//if(tvUsuarios.getColumns().size() <= 0){
			//Coluna nome
			tvUsuarios.getColumns().clear();
			TableColumn<Usuario, String> colNome = new TableColumn<Usuario, String>("Nome");
			colNome.setCellValueFactory( new PropertyValueFactory<Usuario,String>("nome"));
			colNome.setPrefWidth(150);
			//Coluna Email
			TableColumn<Usuario, String> colEmail = new TableColumn<Usuario, String>("Email");
			colEmail.setCellValueFactory(new PropertyValueFactory<Usuario, String>("email"));
			colEmail.setPrefWidth(228);
			//Coluna Data de nascimento
			TableColumn<Usuario, String> colData = new TableColumn<Usuario, String>("Data de Nacimento");
			colData.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Usuario,String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<Usuario, String> usu) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
					String data;
					data = sdf.format(usu.getValue().getDataNascimento());
					System.out.println("Data: "+data);
					return new SimpleObjectProperty<String>(data);
					
				}
			});
			
			colData.setPrefWidth(120);
			tvUsuarios.getColumns().addAll(colNome, colEmail, colData);
		//}
	}
	
	public void carregarItens() throws Exception{
		carregarColunas();
		itens = FXCollections.observableArrayList(dao.search());
		tvUsuarios.setItems(itens);
		
	}
	
	
}
