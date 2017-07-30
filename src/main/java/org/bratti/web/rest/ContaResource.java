package org.bratti.web.rest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.Recorrencia;
import org.bratti.repository.ContaRepository;
import org.bratti.repository.LancamentoRepository;
import org.bratti.repository.RecorrenciaRepository;
import org.bratti.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Conta.
 */
@RestController
@RequestMapping("/api")
public class ContaResource {

    private final Logger log = LoggerFactory.getLogger(ContaResource.class);

    private static final String ENTITY_NAME = "conta";

    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;
    private final RecorrenciaRepository recorrenciaRepository;
    
    public ContaResource(ContaRepository contaRepository, LancamentoRepository lancamentoRepository,
    		RecorrenciaRepository recorrenciaRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
        this.recorrenciaRepository = recorrenciaRepository;
    }

    @PostMapping("/contas")
    @Timed
    public ResponseEntity<Conta> createConta(@RequestBody Conta conta) throws URISyntaxException {
        log.debug("REST request to save Conta : {}", conta);
        if (conta.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new conta cannot already have an ID")).body(null);
        }
        Conta result = contaRepository.save(conta);
        return ResponseEntity.created(new URI("/api/contas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/contas")
    @Timed
    public ResponseEntity<Conta> updateConta(@RequestBody Conta conta) throws URISyntaxException {
        log.debug("REST request to update Conta : {}", conta);
        if (conta.getId() == null) {
            return createConta(conta);
        }
        Conta result = contaRepository.save(conta);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, conta.getId().toString()))
            .body(result);
    }

    @GetMapping("/contas")
    @Timed
    public List<Map> getAllContas() {
        log.debug("REST request to get all Contas");
        List<Conta> contas = contaRepository.findAll();
        return contas.stream().map(c -> {
        	Map<String, Object> conta = new HashMap<>();
        	conta.put("id", c.getId());
        	conta.put("nome", c.getNome());
        	conta.put("saldoInicial", c.getSaldoInicial());
        	conta.put("saldoAtual", saldoNoDia(LocalDate.now(), c));
        	return conta;
        }).collect(Collectors.toList());
    }

    @GetMapping("/contas/{id}")
    @Timed
    public ResponseEntity<Conta> getConta(@PathVariable Long id) {
        log.debug("REST request to get Conta : {}", id);
        Conta conta = contaRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(conta));
    }

    @DeleteMapping("/contas/{id}")
    @Timed
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        log.debug("REST request to delete Conta : {}", id);
        contaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/contas/{id}/lancamentos")
    @Timed
    public List<Lancamento> getLancamentos(@PathVariable("id") Long contaId, 
        @RequestParam("mes") Integer mes, @RequestParam("ano") Integer ano){
    	
    	List<Lancamento> lancamentosReais = lancamentoRepository.findByContaMesAno(contaId, mes, ano);

    	List<Lancamento> lancamentosProjetados = projecoesAte(LocalDate.of(ano, mes, 1).plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS));

    	List<Lancamento> projetadosDoMes = lancamentosProjetados.stream().filter(l -> l.getData().getMonth().ordinal() + 1 == mes && l.getData().getYear() == ano).collect(Collectors.toList());
    	
    	lancamentosReais.addAll(projetadosDoMes);
    	
		return lancamentosReais;
    }
    
    private List<Lancamento> projecoesAte(LocalDate ate) {
    	return recorrenciaRepository.findAllWithEagerRelationships().stream().map(r -> {
    		return r.projecaoAte(ate);
    	}).flatMap(List::stream).collect(Collectors.toList());
    }
    
    @GetMapping("/contas/{id}/saldo")
    @Timed
    public Object getSaldoFimDoDiaEm(@PathVariable("id") Long contaId, @RequestParam("data") LocalDate data) {
    	Conta conta = contaRepository.findOne(contaId);
		BigDecimal saldo = saldoNoDia(data, conta);
		BigDecimal saldoProjecoes = projecoesAte(data).stream().map(i -> i.getValor()).reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
    	    	
		HashMap<String, Object> retorno = new HashMap<>();
		
		retorno.put("data", data);
		retorno.put("saldo", saldo.add(saldoProjecoes));
		
		return retorno;
    }

    private BigDecimal saldoNoDia(LocalDate data, Conta conta) {
    	BigDecimal saldoLancamentos = lancamentoRepository.findByContaAteDia(conta, data).orElse(BigDecimal.ZERO);
		BigDecimal saldo = saldoLancamentos.add(conta.getSaldoInicial());
		return saldo;
    }

}
