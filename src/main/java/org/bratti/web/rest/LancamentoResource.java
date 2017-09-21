package org.bratti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.bratti.domain.Lancamento;
import org.bratti.domain.Local;
import org.bratti.repository.LancamentoRepository;
import org.bratti.repository.LocalRepository;
import org.bratti.service.UserService;
import org.bratti.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;

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

	private UserService userService;

    public LancamentoResource(LancamentoRepository lancamentoRepository,
        LocalRepository localRepository, 
        UserService userService) {
        this.lancamentoRepository = lancamentoRepository;
        this.localRepository = localRepository;
		this.userService = userService;
    }

    @PostMapping("/lancamentos")
    @Timed
    public ResponseEntity<Lancamento> createLancamento(@RequestBody @Validated Lancamento lancamentoDTO) throws URISyntaxException {
        log.debug("REST request to save Lancamento : {}", lancamentoDTO);
        if (lancamentoDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new lancamento cannot already have an ID")).body(null);
        }

        Lancamento lancamento = parse(lancamentoDTO);

        Lancamento result = lancamentoRepository.save(lancamento);
        return ResponseEntity.created(new URI("/api/lancamentos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

	private Lancamento parse(Lancamento lancamentoDTO) {
		Local local = localRepository.findOneByNomeIgnoreCase(lancamentoDTO.getLocal().getNome());
        if (local == null) {
        	local = localRepository.save(new Local().nome(lancamentoDTO.getLocal().getNome()).usuario(userService.getUserWithAuthorities()));
        }

        Lancamento lancamento = new Lancamento();
        lancamento.setId(lancamentoDTO.getId());
        lancamento.setCategoria(lancamentoDTO.getCategoria());
        lancamento.setConta(lancamentoDTO.getConta());
        lancamento.setData(lancamentoDTO.getData());
        lancamento.setEfetivada(lancamentoDTO.isEfetivada());
		lancamento.setLocal(local);
        lancamento.setValor(lancamentoDTO.getValor());
        lancamento.setMotivo(lancamentoDTO.getMotivo());
        lancamento.usuario(userService.getUserWithAuthorities());
		return lancamento;
	}

    @PutMapping("/lancamentos")
    @Timed
    public ResponseEntity<Lancamento> updateLancamento(@RequestBody Lancamento lancamento) throws URISyntaxException {
        log.debug("REST request to update Lancamento : {}", lancamento);
        if (lancamento.getId() == null) {
            return createLancamento(lancamento);
        }
        
        Lancamento result = lancamentoRepository.save(parse(lancamento));
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
        return lancamentoRepository.findByUsuarioIsCurrentUser();
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

    @DeleteMapping("/lancamentos/{id}")
    @Timed
    public ResponseEntity<Void> deleteLancamento(@PathVariable Long id) {
        log.debug("REST request to delete Lancamento : {}", id);
        lancamentoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @DeleteMapping("/lancamentos")
    @Timed
    public ResponseEntity<Void> deleteMaybeLancamento(@RequestBody Lancamento lancamento) throws URISyntaxException {
        log.debug("REST request to delete Lancamento : {}", lancamento.getId());
        
        //createLancamento(lancamento);
        log.debug("lancamento criado");
        lancamentoRepository.delete(lancamento);
        log.debug("lancamento apagado");
        return ResponseEntity.ok().build();
    }
}
