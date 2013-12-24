package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.sessions.Session;
import org.h2.fulltext.FullTextLucene;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Classe concentradora dos m�todos gen�ricos de acesso ao banco H2.
 * 
 * O uso da classe da classe esta condicionado a configura��o de uma
 * propriedade do Sistema Operacional com a nomenclatura PERSISTENCE_UNIT.
 * Esta constante representa as informa��es de acesso ao banco de dados H2,
 * como URL, usu�rio e senha, pr�- configuradas no arquivo de configura��o
 * do DAO. 
 * 
 * Os valores poss�veis para a propriedade PERSISTENCE_UNIT s�o:
 * 
 * dao_componente
 * 			Configura��es de acesso para os componentes P2K
 *  		(PDV, AV e MB)
 * dao_sp
 * 			Configura��es de aceddo para o componente SP.
 * 
 * @author edmo e alterada por rogerio 
 *
 */
public class DAO {
	
	
	
	/**
	 * F�brica para cria��o dos Entity Managers
	 */
	protected static EntityManagerFactory emf = null;
	
	/**
	 * Inst�ncia do logger
	 */
	//private static Logger logger = LoggerFactory.getLogger(DAO.class.getName());
	
	/**
	 * Inst�ncia do entity manager
	 */
	private EntityManager entityManager;
	
	/**
	 * Constante que identifica o par�metro do sistema que define
	 * qual persistence unit ser� utilizado
	 */
	protected static  String PERSISTENCE_UNIT = "estudosFX";
	
	/**
	 * Refer�ncia est�tica para Data Source JDBC 
	 */
	protected static JdbcDataSource jdbcDataSource = null;
	
	/**
	 * Refer�ncia est�tica para pool de conex�es JDBC
	 */
	protected static JdbcConnectionPool connectionPool = null;
	
	private Session sessao = null;
	
	/**
	 * Senha para usu�rio do banco H2
	 */
	private static String senhaH2 = "12345";
	/**
	 * Construtor padr�o respons�vel por inicializar a f�brica criado de Entity Managers.
	 * @throws Exception 
	 */
	public DAO() throws Exception {
		try {
			createFactory();
		} catch (Exception e) {
			throw new Exception("Erro acessando a base relacional!" + e.getMessage());
		}
		
		this.entityManager = this.emf.createEntityManager();		
	}
	
	
	
	/**
	 * Retorna refer�ncia para o objeto de Log da biblioteca.
	 */
//	protected static Logger getLogger() {
//		if(logger == null) {
//			logger = LoggerFactory.getLogger(DAO.class);
//		}
//		return logger;
//	}
	
	/**
	 * M�todo que fornece acesso ao Entity Manager relacionado ao DAO.
	 * Este m�todo verificar se o Entity Manager est� fechado ou nulo
	 * e, nessas situa��es, encarrega-se de solicitar uma nova inst�ncia
	 * ao EntityManagerFactory.
	 * @throws Exception 
	 */
	protected EntityManager getEntityManager() throws Exception {
		if(this.entityManager == null || !this.entityManager.isOpen()) {
			this.entityManager = null;
			try {
				createFactory();
			} catch (Exception e) {
				throw new Exception("Erro no getEntityManager()" + e.getMessage());
			}
			this.entityManager = this.emf.createEntityManager();
		}
		return this.entityManager;
	}
	
	/**
	 * Executa comando SHUTDOWN no banco relacional H2.
	 * 
	 * @return	<code>true</code> caso processo de shutdown ocorra 
	 * 			com sucesso, <code>false</code> em caso contr�rio.
	 * @throws Exception 
	 */
	public  boolean shutdown() throws Exception {
		boolean result = false;
		Connection connection = null;
		try {
			connection = getConnection();
			connection.createStatement().execute("SHUTDOWN");
			result = true;
		} catch (SQLException e) {
			throw new Exception("Erro ao executar SHUTDOWN do banco de dados!" + e.getMessage());
		} finally {
			closeConnection(connection);
		}
		return result;
	}
	
	/**
	 * Realiza comunica��o com o banco relacional H2 estabelecendo uma conex�o
	 * para verifica��o do servi�o e fechando-a em seguida.
	 * 
	 * @return	<code>true</code> caso processo de shutdown ocorra 
	 * 			com sucesso, <code>false</code> em caso contr�rio.
	 * @throws ExcecaoConexaoBanco
	 */
	public boolean connect() throws Exception {
		boolean result = false;
		Connection connection = null;
		try {
			connection = getInternalConnection();
			result = true;
		} catch (SQLException e) {
			throw new Exception("Erro ao conectar ao banco de dados!" + e.getMessage());
			
		} finally {
			closeConnection(connection);
		}
		return result;
	}	
	
	/**
	 * Executa comando expl�citio para inicializa �ndices FULLTEXT SEARCH
	 * na base de dados H2.
	 * 
	 * @throws Exception
	 */
	public boolean iniciaIndices() throws Exception {
		boolean result = false;
		Connection connection = getConnection();
		try {
			FullTextLucene.init(connection);
			result = true;
		} catch (Exception e) {
			result = false;
			throw new Exception("Erro iniciando �ndices!" + e.getMessage());
		} finally{
			if(connection != null) {
				connection.close();
			}
		}
		return result;
	}	
	/**
	 * Cria uma Sess�o caso a da classe esteja nula.
	 * @return Session
	 * @throws Exception 
	 */
	public Session getSession() throws Exception{
		if(this.sessao == null) {
			try {
				EntityManager em = getEntityManager();
				this.sessao = em.unwrap(Session.class);
			} catch (Exception e) {
				throw new Exception("Erro na recupera��o da sess�o da conex�o!" + e.getMessage());
			}
		}
		return this.sessao;
	}
	
	/**
	 * Obt�m conex�o com o banco relacional H2 de forma est�tica
	 * para uso interno no DAO.
	 * 
	 * @return
	 * @throws Exception 
	 */
	private  Connection getInternalConnection() throws Exception {
		Session sessao = getSession();

		if ((sessao == null) || (sessao.getLogin() == null)) {
			throw new SQLException();
		}
		JdbcConnectionPool pool = getPoolConnection(sessao);
		
		return pool.getConnection();		
	}
	/**
	 * Criar um novo pool caso o da classe esteja null
	 * @param sessao
	 * @return
	 */
	public JdbcConnectionPool getPoolConnection(Session sessao){
		if(this.connectionPool == null){
			if (jdbcDataSource == null) {
				jdbcDataSource = new JdbcDataSource();
				
				jdbcDataSource.setURL(sessao.getLogin().getDatabaseURL());
				jdbcDataSource.setUser(sessao.getLogin().getUserName());
				jdbcDataSource.setPassword(sessao.getLogin().getPassword());
				jdbcDataSource.setPassword(senhaH2);
				connectionPool = JdbcConnectionPool.create(jdbcDataSource);	
			}
		}
		return this.connectionPool;
	}
	
	/**
	 * Obt�m a conex�o com o banco de dados a partir da sess�o do Entity Manager.
	 * @throws Exception 
	 */
	public Connection getConnection() throws Exception {
		Session sessao = getSession();
		JdbcConnectionPool pool = getPoolConnection(sessao);
		return pool.getConnection();
	}	
	
	/**
	 * Encerra a conex�o com o banco de dados
	 * 
	 * @param connection
	 * 		Refer�ncia � conex�o que ter� os recursos liberados no banco de dados.
	 * @throws Exception 
	 */
	private static void closeConnection(Connection connection) throws Exception {
		if(connection != null) {
			try {
				connection.close();
			} catch (SQLException sqle) {
				throw new Exception("Erro na finaliza��o da conex�o!" + sqle.getMessage());
			}
		}
	}	
	
	/**
	 * Processa comandos SQL definidos em um arquivo do sistema.
	 * Cada comando deve estar em uma linha do arquivo e delimitado pelo caracter ';'
	 * 
	 * @param scriptFile
	 * 		Refer�ncia ao <code>File>/code> que indica localiza��o f�sica do arquivo de script.
	 * @throws Exception 
	 */
	public void executeScriptSQLFromFileSystem(File scriptFile) throws Exception {
		BufferedReader br = null;
		FileReader is = null;
		try {
			is = new FileReader(scriptFile);
			br = new BufferedReader(is);
			this.processaScript(br);
		} catch (Exception e) {
			throw new Exception("Erro na cria��o da base de dados!" + e.getMessage());
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (Exception e) {}
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {}
		}
	}	
	
	/**
	 * Processa comandos SQL definidos como resource da biblioteca linx-dao.jar
	 * Cada comando deve estar em uma linha do arquivo e delimitado pelo caracter ';'
	 * 
	 * @param scriptFile
	 * 			String que indica nome do arquivo configurado como resource	da lib.
	 * @throws Exception 
	 */
	public void executeScriptSQLFromResource(String scriptFile) throws Exception {
		BufferedReader br = null;
		InputStreamReader is = null;
		
		try {
			is = new InputStreamReader(getClass().getResourceAsStream(scriptFile));
			br = new BufferedReader(is);
			this.processaScript(br);
		} catch (Exception e) {
			throw new Exception("Erro na cria��o da base de dados!" + e.getMessage());
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (Exception e) {}
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {}
		}
	}	

	/**
	 * M�todo gen�rico que recebe como par�metro um objeto <code>BufferedReader</code>
	 * que aponta para um arquivo com os comando SQL a serem executados.
	 * 
	 * O m�todo percorre as linhas lidas do arquivo e a cada carater ';' encontrado,
	 * executa o comando SQL no banco de dados.
	 * 
	 * @param br
	 * 			Refer�ncia ao imputstrean que representa o arquivo a ser lido para
	 * 			execu��o dos comandos SQL.
	 * @throws Exception 
	 */
	private void processaScript(BufferedReader br) throws Exception {
		try {
			String registro = "";
			String comando  = "";
			
			while (!((registro = br.readLine()) == null)) {
				if (!registro.startsWith("--")) {
					comando += registro;
				}
				
				if (comando !=null && comando.trim().endsWith(";")) {
					this.executeInternalSQL(comando);
					comando = "";
				}
			}
		} catch (Exception e) {
			throw new Exception("Erro no m�todo processaScript!" + e.getMessage());
		}
	}
	

	/**
	 * M�todo que executa comando SQL e registra poss�vel exce��o
	 * sem impedir continuidade do processsamento.
	 * 
	 * @param comando
	 * @throws Exception 
	 */
	private void executeInternalSQL(String comando) throws Exception {
		Connection conn = null;
		try {
			conn = getConnection();
			if(conn != null) {
				conn.createStatement().execute(comando);
			}
		} catch(SQLException sqle) {
			throw new Exception("Erro iniciando �ndices!" + "  comando: "+ comando + " "+ sqle.getMessage());
		} finally {
			closeConnection(conn);			
		}		
	}
	
	/**
	 * Executa uma comando SQL no banco de dados.
	 * @param comando
	 */
	public void executeSQL(String comando) throws Exception {
		Connection conn = null;
		
		try {
			conn = getConnection();
			if(conn != null) {
				conn.createStatement().execute(comando);
			}
		} catch(SQLException sqle) {
			throw new Exception(sqle.getMessage() +  comando);
		} finally {
			closeConnection(conn);			
		}
	}
	
	/**
	 * Executa todos os comando SQL parametrizados.
	 * @param comandos
	 * @throws ExcecaoExecucaoComandoSQL
	 */
	public void executeSQL(String[] comandos) throws Exception {
		Connection conn = null;
		
		try {
			conn = getConnection();
			if(conn != null && comandos != null) {
				for (int i = 0; i < comandos.length; i++) {
					conn.createStatement().execute(comandos[i]);
				}
			}
		} catch(SQLException sqle) {
			throw new Exception(sqle.getMessage());
		} finally {
			closeConnection(conn);					
		}
	}
	
	/**
	 * Inicia uma transa��o 
	 * @throws Exception
	 */
	public void beginTransaction() throws Exception {
		try {
			EntityManager em = getEntityManager();
			if(!isTransactionActive()){
				em.getTransaction().begin();
			}
		} catch (Exception e) {
			throw new Exception("Erro ao iniciar Transa��o!" + e.getMessage());
		}
	}
	
	/**
	 * Verifica se existe transa��o e se a mesma est� ativa
	 * @throws Exception
	 */
	public boolean isTransactionActive() throws Exception {
		boolean result = false;
		
		try {
			if(entityManager != null) {
				result = entityManager.getTransaction() != null && entityManager.getTransaction().isActive(); 
			}
		} catch (Exception e) {
			result = false;
			throw new Exception("Erro ao verificar status da transa��a!" + e.getMessage());
		}
		return result;
	}

	/**
	 * Realiza commit das transa��es realizadas no banco de dados.
	 * @throws ExcecaoCommitTransacao
	 */
	public void commitTransaction() throws Exception {
		try {
			EntityManager em = getEntityManager();
			if(isTransactionActive()){
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			throw new Exception("Erro ao efetuar commit na transa��o!" + e.getMessage());
		}
	}	
	
	/**
	 * Realiza rollback das transa��es pendentes no banco de dados.
	 * @throws ExcecaoRollbackTransacao
	 */
	public void rollbackTransacation() throws Exception {
		try {
			EntityManager em = getEntityManager();
			em.getTransaction().rollback();
		} catch (Exception e) {
			throw new Exception("Erro ao efetuar rollback na transa��o!" + e.getMessage());
		}
	}
	
	/**
	 * Inicializa a f�brica de Entity Managers, o que representa acesso ao banco
	 * de dados H2.
	 * 
	 * @throws Exception
	 */
	public void createFactory() throws Exception {
		try {
			if (emf==null) {
				emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
			}
			this.entityManager = emf.createEntityManager();
		} catch (Exception e) {
			throw new Exception("Erro ao criar EntityManagerFactory!" + e.getMessage());
		}
	}
	
	/**
	 * Fecha o EntityManager associado ao DAO
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		try {
			if (this.entityManager != null && this.entityManager.isOpen()) {
				this.entityManager.close();
				this.entityManager = null;
			}
		} catch (Exception e) {
			throw new Exception("Erro ao fechar o EntityManager" + e.getMessage());
		}
	}	
	
	/**
	 * Libera os recursos associados ao banco de dados como a f�brica de Entity
	 * Managers e Pool de conex�es JDBC.
	 * 
	 * @throws Exception
	 */
	public void closeEntityManagerFactory() throws Exception {
		try {
			if (emf != null) {
				emf.close();
				emf = null;
			}
			jdbcDataSource = null;
			connectionPool = null;
		} catch (Exception e) {
			throw new Exception("Erro fechando o EntityManagerFactory!" + e.getMessage());
			
		}
	}	
	

}
