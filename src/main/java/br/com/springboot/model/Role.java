package br.com.springboot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6034838477869273305L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_gen")
	@SequenceGenerator(name = "role_seq_gen", sequenceName = "role_seq_gen")
	private Long id;
	private String nomeRole;

	@Override
	public String getAuthority() {   //ROLE_ADMIN, ROLE_GERENTE, ROLE_SECRETARIO 
		// TODO Auto-generated method stub
		return this.nomeRole;
	}

	public String getNomeRole() {
		return nomeRole;
	}

	public void setNomeRole(String nomeRole) {
		this.nomeRole = nomeRole;
	} 
}
