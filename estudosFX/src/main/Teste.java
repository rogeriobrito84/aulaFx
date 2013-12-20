package main;

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
		DAOGenerico<Usuario, Integer> dao = new DAOGenerico<Usuario,Integer>(Usuario.class);
		try {
			
			Usuario usu = dao.search(1);
//			usu = new Usuario();
//			usu.setNome("rogerio");
//			usu.setEmail("Rogerio Brito");
//			dao.beginTransaction();
//			dao.persist(usu);
//			dao.commitTransaction();
			
			List<Usuario> lista = dao.search();
			for (Usuario u : lista) {
				System.out.println("id: " +u.getId() + "   Nome: " + u.getNome()+ "   email: " + u.getEmail() + "   data: " 
			+ u.getDataNascimento());
			}
			System.out.println("Fim...");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

