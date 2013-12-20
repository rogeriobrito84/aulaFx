package usuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="usuario")
public class Usuario {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String nome;
	@Column
	private String email;
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getDataNascimento(){
		String data = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		data = sdf.format(dataNascimento);
		return data;
	}
	
	public void setDataNascimento(String dataNascimento) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		Calendar data = Calendar.getInstance();
		
		if((dataNascimento != null) & !dataNascimento.trim().isEmpty()){
			this.dataNascimento = sdf.parse(dataNascimento);
		}else{
			this.dataNascimento = (Date) data.getTime();
		}
		
	}
	
	
	
	
}
