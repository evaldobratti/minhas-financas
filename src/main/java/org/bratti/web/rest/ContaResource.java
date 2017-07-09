package org.bratti.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.bratti.domain.Conta;

import org.bratti.repository.ContaRepository;
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

    public ContaResource(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    /**
     * POST  /contas : Create a new conta.
     *
     * @param conta the conta to create
     * @return the ResponseEntity with status 201 (Created) and with body the new conta, or with status 400 (Bad Request) if the conta has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
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
     *
     * @param conta the conta to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated conta,
     * or with status 400 (Bad Request) if the conta is not valid,
     * or with status 500 (Internal Server Error) if the conta couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
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
     *
     * @return the ResponseEntity with status 200 (OK) and the list of contas in body
     */
    @GetMapping("/contas")
    @Timed
    public List<Conta> getAllContas() {
        log.debug("REST request to get all Contas");
        return contaRepository.findAll();
    }

    /**
     * GET  /contas/:id : get the "id" conta.
     *
     * @param id the id of the conta to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the conta, or with status 404 (Not Found)
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
     *
     * @param id the id of the conta to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/contas/{id}")
    @Timed
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        log.debug("REST request to delete Conta : {}", id);
        contaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
