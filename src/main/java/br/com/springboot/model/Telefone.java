package br.com.springboot.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

@Entity
public class Telefone implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7791720770957941127L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tel_seq_gen")
	@SequenceGenerator(name = "tel_seq_gen", sequenceName = "tel_id_seq")
	private Long id;
	private String numero;
	private String tipo;
	@ManyToOne
	@ForeignKey(name = "pessoa_id")
	private Pessoa pessoa;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
}
