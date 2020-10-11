package br.com.springboot.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Profissao implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3359522931165308763L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prof_seq_gen")
	@SequenceGenerator(name = "prof_seq_gen", sequenceName = "prof_id_seq")
	private Long id;
	private String nome;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
