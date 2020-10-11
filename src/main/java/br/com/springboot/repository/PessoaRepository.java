package br.com.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.springboot.model.Pessoa;
@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa,Long>{
	@Query("select p from Pessoa p where p.nome like %?1%")
	List<Pessoa> findPessoaByName(String nome);
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexopessoa like ?2")
	List<Pessoa> findPessoaByNameAndSexo(String nome,String sexo);
	
	default Page<Pessoa> findPessoaByNamePage(String nome, Pageable pageable){
		Pessoa pessoa= new Pessoa();
		pessoa.setNome(nome);
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome",GenericPropertyMatchers.contains().ignoreCase());
		Example<Pessoa> example = Example.of(pessoa,exampleMatcher);
		Page<Pessoa> pessoas = findAll(example,pageable);
		return pessoas;
		
	}
	default Page<Pessoa> findPessoaBySexoPage(String nome,String sexo, Pageable pageable){
		Pessoa pessoa= new Pessoa();
		pessoa.setNome(nome);
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome",GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("sexo",GenericPropertyMatchers.contains().ignoreCase());
		Example<Pessoa> example = Example.of(pessoa,exampleMatcher);
		Page<Pessoa> pessoas = findAll(example,pageable);
		return pessoas;
		
	}
}
