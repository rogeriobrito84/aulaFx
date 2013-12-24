package util;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.Query;

/**
 * Classe genérica para manipulação das entidades
 * através das operações básicas para consulta,
 * inclusão, atualização e exclusão.
 * 
 * @author gustav.monteiro
 */
public class DAOGenerico<T, ID extends Serializable> extends DAO {
	/**
	 * Instância da classe referenciada por este DAO
	 * através de Generics
	 */
	protected Class<T> persistentClass;
	/**
	 * Construtor privado
	 * Inicializa a estrutura básica do JPA
	 * @throws Exception 
	 */
	public DAOGenerico(Class<T> classe) throws Exception {
		super();
		this.persistentClass = classe;
	}
	/**
	 * Persiste a entidade no banco de dados.
	 * @param object Objeto a ser persistido
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public T persist(T object) throws Exception {
		try {
			Object mergeResult = getEntityManager().merge(object);
			getEntityManager().persist(mergeResult);
			return (T) mergeResult;
		} catch (EntityExistsException e) {
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception("Erro ao persistir o objeto: " + persistentClass.getSimpleName() + e.getMessage());
		}
	}
	/**
	 * Busca genérica por chave primária
	 * @param key A chave primária do objeto
	 * @return Objeto to tipo especificado
	 * @throws ExcecaoObjetoInexistente
	 */
	public T search(ID key) throws Exception {
		try {
			T result = getEntityManager().find(persistentClass, key);
//			if (result == null) {
//				throw new Exception("Nenhum objeto encontrado do tipo: " + persistentClass.getSimpleName());
//			}
			return result;
		} catch (Exception e) {
			throw new Exception("Erro ao efetuar consulta do objeto: " + persistentClass.getSimpleName() + e.getMessage());
		} 
	}
	
	public List<T> searchAdvance(){
		
		return null;
	}
	
	/**
	 * Retorna a lista de todos os objetos do tipo
	 * @return Lista tipificada do objeto
	 * @throws ExcecaoObjetoInexistente
	 */
	@SuppressWarnings("unchecked")
	public List<T> search() throws Exception {
		try {
			List<T> result = getEntityManager().createQuery(
					"SELECT T FROM " + persistentClass.getSimpleName() + " T").getResultList(); 

//			if (result == null || result.size() == 0) {
//				throw new Exception();
//			}
			
			return result;
		} catch (Exception e) {
			throw new Exception("Erro ao efetuar consulta do objeto: " + persistentClass.getSimpleName() + e.getMessage());
		}
	}
	
	/**
	 * Retorna uma lista filtrada pelo valor do 
	 * atributo especificado
	 * @param attribute O atributo a ser filtrado
	 * @param value Valor do atributo
	 * @return Lista tipificada do objeto filtrado
	 * pelo valor do atributo especificado
	 * @throws ExcecaoObjetoInexistente
	 */
	@SuppressWarnings("unchecked")
	public List<T> search(String attribute, Object value) throws Exception {
		try {
			Query query = getEntityManager().createQuery(
					"SELECT T FROM "+ persistentClass.getSimpleName() + " T WHERE T."
							+ attribute + " = :atributo");
			query.setParameter("atributo", value);
			
			List<T> result = query.getResultList();

//			if (result == null | result.size() == 0) {
//				throw new Exception();
//			}
			return result; 
		} catch (Exception e) {
			throw new Exception("Erro ao efetuar consulta do objeto: " + persistentClass.getSimpleName() + e.getMessage());
		}
	}
	
	
	/**
	 * Remove o objeto especificado.
	 * @param object O objeto a ser removido.
	 * @throws Exception
	 */
	public void remove(T object) throws Exception {
		try {
			getEntityManager().remove(object);
		} catch (Exception e) {
			throw new Exception("Erro ao remover o objeto: " + persistentClass.getSimpleName() + e.getMessage());
		}
	}

	public void merge(T object) throws Exception {
		try {
			getEntityManager().merge(object);
		} catch (Exception e) {
			throw new Exception("Erro ao efetuar merger no objeto: " + persistentClass.getSimpleName() + e.getMessage());
		}
	}

}
