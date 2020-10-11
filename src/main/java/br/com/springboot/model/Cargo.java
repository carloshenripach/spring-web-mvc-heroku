package br.com.springboot.model;

public enum Cargo {
	JUNIOR("Júnior"),
	PLENO("Pleno"),
	SENIOR("Sênior");
	
	private String nome;
	private String valor;
	private Cargo(String nome) {
		this.nome = nome;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getValor() {
		return this.name();
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	@Override
	public String toString() {
		
		return this.name();
	}
	
}
