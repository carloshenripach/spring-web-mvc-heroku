package br.com.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import br.com.springboot.model.Pessoa;
import br.com.springboot.model.Telefone;
import br.com.springboot.repository.PessoaRepository;
import br.com.springboot.repository.ProfissaoRepository;
import br.com.springboot.repository.TelefoneRepository;
import net.sf.jasperreports.engine.JRException;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
		andView.addObject("pessoas", pessoaIt);
		andView.addObject("profissoes",profissaoRepository.findAll());
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5) Pageable pageable,
			ModelAndView modelAndView,@RequestParam("nomepesquisa") String nomepesquisa ) {
		Page<Pessoa> page = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		modelAndView.addObject("pessoas",page);
		modelAndView.addObject("nomepesquisa",nomepesquisa);
		modelAndView.addObject("pessoaobj",new Pessoa());
		modelAndView.addObject("profissoes",profissaoRepository.findAll());
		modelAndView.setViewName("cadastro/cadastropessoa");
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa",consumes = { "multipart/form-data" })
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult,@RequestPart("file") MultipartFile file) throws IOException {
		
		if (bindingResult.hasErrors()) {
			ModelAndView modelandView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
			modelandView.addObject("pessoas", pessoaIt);
			modelandView.addObject("pessoaobj", pessoa);
			modelandView.addObject("profissoes",profissaoRepository.findAll());
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage());
			}
			modelandView.addObject("msg", msg);
			return modelandView;
		}
		if(file.getSize()>0) {
			pessoa.setCurriculo(file.getBytes());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
			pessoa.setTipoCurriculo(file.getContentType());
		}else {
			if(pessoa.getId()!=null && pessoa.getId()>0) {
				Pessoa pessoaTemp = pessoaRepository.
								findById(pessoa.getId()).get();
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
				pessoa.setTipoCurriculo(pessoaTemp.getTipoCurriculo());
			}
		}
		pessoa.setTelefones(telefoneRepository.findTelefoneByPessoa(pessoa.getId()));
		pessoaRepository.save(pessoa);
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
		andView.addObject("pessoas", pessoaIt);
		andView.addObject("pessoaobj", new Pessoa());
		andView.addObject("profissoes",profissaoRepository.findAll());
		return andView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
		andView.addObject("pessoas", pessoaIt);
		andView.addObject("pessoaobj", new Pessoa());
		andView.addObject("profissoes",profissaoRepository.findAll());

		return andView;
	}

	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editarPessoa(@PathVariable("idpessoa") Long idpessoa) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
		
		modelAndView.addObject("pessoas", pessoaIt);
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("profissoes",profissaoRepository.findAll());
		return modelAndView;
	}

	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView removerPessoa(@PathVariable("idpessoa") Long idpessoa) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		pessoaRepository.deleteById(idpessoa);
		modelAndView.addObject("pessoaobj", new Pessoa());
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
		modelAndView.addObject("pessoas", pessoaIt);
		modelAndView.addObject("profissoes",profissaoRepository.findAll());
		return modelAndView;
	}

	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("sexopesquisa") String sexopesquisa, @PageableDefault(size=5,sort = {"nome"}) Pageable pageable){
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("profissoes",profissaoRepository.findAll());
		modelAndView.addObject("nomepesquisa",nomepesquisa);
		if(sexopesquisa == null || sexopesquisa.isEmpty()) {
			modelAndView.addObject("pessoas", pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable));
		}else {
			modelAndView.addObject("pessoas", pessoaRepository.findPessoaBySexoPage(nomepesquisa, sexopesquisa, pageable));
		}
		return modelAndView;

	}
	@GetMapping("**/pesquisarpessoa")
	public void gerarPDF(HttpServletRequest req,HttpServletResponse response) throws JRException, IOException {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		String sexo = req.getParameter("sexopesquisa");
		String  nome = req.getParameter("nomepesquisa");
		if(sexo==null||sexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByName(nome);
		}else {
			pessoas = pessoaRepository.findPessoaByNameAndSexo(nome,sexo);
		}
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", req.getServletContext());
		response.setContentLength(pdf.length);
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		response.getOutputStream().write(pdf);
	}

	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefones");

		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		modelAndView.addObject("pessoaobj", pessoa.get());

		modelAndView.addObject("telefoneobj", new Telefone());
		modelAndView.addObject("telefones", telefoneRepository.findTelefoneByPessoa(idpessoa));
		return modelAndView;
	}

	@PostMapping("/salvartelefone/{idpessoa}")
	public ModelAndView salvarTelefones(Telefone telefone, @PathVariable("idpessoa") Long idpessoa) {

		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		if (telefone != null && (telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()
				|| telefone.getNumero().isBlank() || telefone.getTipo().isBlank())) {
			ModelAndView andView = new ModelAndView("cadastro/cadastrotelefones");

			andView.addObject("telefones", telefoneRepository.findTelefoneByPessoa(idpessoa));
			andView.addObject("pessoaobj", pessoa.get());
			andView.addObject("telefoneobj", telefone);
			List<String> msg = new ArrayList<String>();
			if (telefone.getNumero().isEmpty() || telefone.getNumero().isBlank()) {
				msg.add("Número deve ser informado");
			}
			if (telefone.getTipo().isEmpty() || telefone.getTipo().isBlank()) {
				msg.add("Tipo deve ser informado");
			}
			andView.addObject("msg",msg);
			return andView;

		}

		telefone.setPessoa(pessoa.get());
		telefoneRepository.save(telefone);

		ModelAndView andView = new ModelAndView("cadastro/cadastrotelefones");

		andView.addObject("telefones", telefoneRepository.findTelefoneByPessoa(idpessoa));
		andView.addObject("pessoaobj", pessoa.get());
		andView.addObject("telefoneobj", new Telefone());
		return andView;
	}

	@GetMapping("/listartelefones/{idpessoa}")
	public ModelAndView telefonesListar(@PathVariable("idpessoa") Long idpessoa) {
		ModelAndView andView = new ModelAndView("cadastro/cadastrotelefones");
		andView.addObject("pessoaobj", pessoaRepository.findById(idpessoa).get());
		andView.addObject("telefones", telefoneRepository.findTelefoneByPessoa(idpessoa));
		andView.addObject("telefoneobj", new Telefone());
		return andView;
	}

	@GetMapping("/editartelefone/{idtelefone}")
	public ModelAndView editarTelefone(@PathVariable("idtelefone") Long idtelefone) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefones");
		Optional<Telefone> telefone = telefoneRepository.findById(idtelefone);

		Pessoa pessoa = pessoaRepository.findById(telefone.get().getPessoa().getId()).get();
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefoneobj", telefone.get());
		modelAndView.addObject("telefones", telefoneRepository.findTelefoneByPessoa(pessoa.getId()));
		return modelAndView;
	}

	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removerTelefone(@PathVariable("idtelefone") Long idtelefone) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefones");
		Optional<Telefone> telefone = telefoneRepository.findById(idtelefone);
		telefoneRepository.deleteById(idtelefone);
		Pessoa pessoa = pessoaRepository.findById(telefone.get().getPessoa().getId()).get();
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefoneobj", new Telefone());
		modelAndView.addObject("telefones", telefoneRepository.findTelefoneByPessoa(pessoa.getId()));

		return modelAndView;
	}

	@PostMapping("**/pesquisartelefone/{idpessoa}")
	public ModelAndView pesquisarTelefone(@RequestParam("telefonepesquisa") String telefonepesquisa,
			@PathVariable("idpessoa") Long idpessoa) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefones");
		modelAndView.addObject("pessoaobj", pessoaRepository.findById(idpessoa).get());
		modelAndView.addObject("telefoneobj", new Telefone());
		modelAndView.addObject("telefones", telefoneRepository.findTelefoneByNumero(telefonepesquisa));
		return modelAndView;

	}
	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public ModelAndView baixarCurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if(pessoa.getCurriculo()!=null) {
			response.setContentLength(pessoa.getCurriculo().length);
			response.setContentType(pessoa.getTipoCurriculo());
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			response.getOutputStream().write(pessoa.getCurriculo());
			
		}
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll(PageRequest.of(0, 5,Sort.by("nome")));
		andView.addObject("pessoas", pessoaIt);
		andView.addObject("pessoaobj", new Pessoa());
		andView.addObject("profissoes",profissaoRepository.findAll());
		return andView;
	}
}