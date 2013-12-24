package main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import usuario.Usuario;
import util.DAO;
import util.DAOGenerico;




public class Teste {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
//			DAOGenerico<Usuario, Integer> daoU = new DAOGenerico<Usuario,Integer>(Usuario.class);
//			Usuario usu = dao.search(1);
//			dao.beginTransaction();
//			dao.remove(usu);
//			dao.commitTransaction();
//			usu = new Usuario();
//			usu.setNome("rogerio");
//			usu.setEmail("Rogerio Brito");
//			usu.setDataNascimento(new Date());
//			dao.beginTransaction();
//			dao.persist(usu);
//			dao.commitTransaction();

//			List<Usuario> lista = dao.search();
//			for (Usuario u : lista) {
//				System.out.println("id: " +u.getId() + "   Nome: " + u.getNome()+ "   email: " + u.getEmail() + "   data: " 
//			+ u.getDataNascimento());
//			}
//			
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/YYYY");
//			Date data = new Date();
//			String dataS = sdf.format(data);
//			DAO dao = new DAO();
//			dao.createFactory();
//			
//			String dataP =  sdf.toLocalizedPattern();
//			System.out.println("Data P" + dataP);
//			System.out.println("Data F" + dataS);
//			System.out.println("Fim...");
			
			System.out.println("0".matches("^[1-9][0-9]*|^[1-9][0-9]*[,][0-9]*|^[0][,]|^[0][,][0-9]*[1-9]$"));//False = 0
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public boolean teste(String texto, String operador){
		boolean res = false;
		for (String e : texto.split(operador)) {
			if(texto.matches("^[1-9][0-9]*|^[1-9][0-9]*[,][0-9]*|^[0][,][0-9]*[1-9]$")){
				res = true;
			}
		}
		return res;
	}
}

