package org.bratti.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.Local;
import org.bratti.repository.LancamentoRepository;
import org.bratti.repository.LocalRepository;
import org.bratti.web.rest.dto.LancamentoRequestDTO;
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
 * REST controller for managing Lancamento.
 */
@RestController
@RequestMapping("/api")
public class LancamentoResource {

    private final Logger log = LoggerFactory.getLogger(LancamentoResource.class);

    private static final String ENTITY_NAME = "lancamento";

    private final LancamentoRepository lancamentoRepository;

	private final LocalRepository localRepository;
    
    public LancamentoResource(LancamentoRepository lancamentoRepository,
        LocalRepository localRepository) {
        this.lancamentoRepository = lancamentoRepository;
        this.localRepository = localRepository;
    }

    @PostMapping("/lancamentos")
    @Timed
    public ResponseEntity<Lancamento> createLancamento(@RequestBody LancamentoRequestDTO lancamentoDTO) throws URISyntaxException {
        log.debug("REST request to save Lancamento : {}", lancamentoDTO);
        if (lancamentoDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new lancamento cannot already have an ID")).body(null);
        }

        Local local = localRepository.findOneByNomeIgnoreCase(lancamentoDTO.getLocal());
        if (local == null) {
        	local = localRepository.save(new Local().nome(lancamentoDTO.getLocal()));
        }
        
        Lancamento lancamento = new Lancamento();
        lancamento.setCategoria(lancamentoDTO.getCategoria());
        lancamento.setConta(lancamentoDTO.getConta());
        lancamento.setData(lancamentoDTO.getData());
        lancamento.setEfetivada(lancamentoDTO.getEfetivada());
		lancamento.setLocal(local);
        lancamento.setValor(lancamentoDTO.getValor());

        Lancamento result = lancamentoRepository.save(lancamento);
        return ResponseEntity.created(new URI("/api/lancamentos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/lancamentos")
    @Timed
    public ResponseEntity<Lancamento> updateLancamento(@RequestBody Lancamento lancamento) throws URISyntaxException {
        log.debug("REST request to update Lancamento : {}", lancamento);
        if (lancamento.getId() == null) {
            return createLancamento(null);
        }
        Lancamento result = lancamentoRepository.save(lancamento);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, lancamento.getId().toString()))
            .body(result);
    }

    /**
     * GET  /lancamentos : get all the lancamentos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of lancamentos in body
     */
    @GetMapping("/lancamentos")
    @Timed
    public List<Lancamento> getAllLancamentos() {
        log.debug("REST request to get all Lancamentos");
        return lancamentoRepository.findAll();
    }

    /**
     * GET  /lancamentos/:id : get the "id" lancamento.
     *
     * @param id the id of the lancamento to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the lancamento, or with status 404 (Not Found)
     */
    @GetMapping("/lancamentos/{id}")
    @Timed
    public ResponseEntity<Lancamento> getLancamento(@PathVariable Long id) {
        log.debug("REST request to get Lancamento : {}", id);
        Lancamento lancamento = lancamentoRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(lancamento));
    }

    /**
     * DELETE  /lancamentos/:id : delete the "id" lancamento.
     *
     * @param id the id of the lancamento to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/lancamentos/{id}")
    @Timed
    public ResponseEntity<Void> deleteLancamento(@PathVariable Long id) {
        log.debug("REST request to delete Lancamento : {}", id);
        lancamentoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
