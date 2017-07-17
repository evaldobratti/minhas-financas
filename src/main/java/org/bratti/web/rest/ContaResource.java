package org.bratti.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.repository.ContaRepository;
import org.bratti.repository.LancamentoRepository;
import org.bratti.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

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

    public ContaResource(ContaRepository contaRepository, LancamentoRepository lancamentoRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    /**
     * POST  /contas : Create a new conta.
     */
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

    /**
     * PUT  /contas : Updates an existing conta.
     */
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

    /**
     * GET  /contas : get all the contas.
     */
    @GetMapping("/contas")
    @Timed
    public List<Conta> getAllContas() {
        log.debug("REST request to get all Contas");
        return contaRepository.findAll();
    }

    /**
     * GET  /contas/:id : get the "id" conta.
     */
    @GetMapping("/contas/{id}")
    @Timed
    public ResponseEntity<Conta> getConta(@PathVariable Long id) {
        log.debug("REST request to get Conta : {}", id);
        Conta conta = contaRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(conta));
    }

    /**
     * DELETE  /contas/:id : delete the "id" conta.
     */
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
        
        return lancamentoRepository.findByContaMesAno(contaId, mes, ano);
    }

}
