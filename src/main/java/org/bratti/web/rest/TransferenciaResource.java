package org.bratti.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.bratti.domain.Transferencia;

import org.bratti.repository.TransferenciaRepository;
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
 * REST controller for managing Transferencia.
 */
@RestController
@RequestMapping("/api")
public class TransferenciaResource {

    private final Logger log = LoggerFactory.getLogger(TransferenciaResource.class);

    private static final String ENTITY_NAME = "transferencia";

    private final TransferenciaRepository transferenciaRepository;

    public TransferenciaResource(TransferenciaRepository transferenciaRepository) {
        this.transferenciaRepository = transferenciaRepository;
    }

    /**
     * POST  /transferencias : Create a new transferencia.
     *
     * @param transferencia the transferencia to create
     * @return the ResponseEntity with status 201 (Created) and with body the new transferencia, or with status 400 (Bad Request) if the transferencia has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/transferencias")
    @Timed
    public ResponseEntity<Transferencia> createTransferencia(@RequestBody Transferencia transferencia) throws URISyntaxException {
        log.debug("REST request to save Transferencia : {}", transferencia);
        if (transferencia.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new transferencia cannot already have an ID")).body(null);
        }
        Transferencia result = transferenciaRepository.save(transferencia);
        return ResponseEntity.created(new URI("/api/transferencias/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /transferencias : Updates an existing transferencia.
     *
     * @param transferencia the transferencia to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated transferencia,
     * or with status 400 (Bad Request) if the transferencia is not valid,
     * or with status 500 (Internal Server Error) if the transferencia couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/transferencias")
    @Timed
    public ResponseEntity<Transferencia> updateTransferencia(@RequestBody Transferencia transferencia) throws URISyntaxException {
        log.debug("REST request to update Transferencia : {}", transferencia);
        if (transferencia.getId() == null) {
            return createTransferencia(transferencia);
        }
        Transferencia result = transferenciaRepository.save(transferencia);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, transferencia.getId().toString()))
            .body(result);
    }

    /**
     * GET  /transferencias : get all the transferencias.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of transferencias in body
     */
    @GetMapping("/transferencias")
    @Timed
    public List<Transferencia> getAllTransferencias() {
        log.debug("REST request to get all Transferencias");
        return transferenciaRepository.findAll();
    }

    /**
     * GET  /transferencias/:id : get the "id" transferencia.
     *
     * @param id the id of the transferencia to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the transferencia, or with status 404 (Not Found)
     */
    @GetMapping("/transferencias/{id}")
    @Timed
    public ResponseEntity<Transferencia> getTransferencia(@PathVariable Long id) {
        log.debug("REST request to get Transferencia : {}", id);
        Transferencia transferencia = transferenciaRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(transferencia));
    }

    /**
     * DELETE  /transferencias/:id : delete the "id" transferencia.
     *
     * @param id the id of the transferencia to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/transferencias/{id}")
    @Timed
    public ResponseEntity<Void> deleteTransferencia(@PathVariable Long id) {
        log.debug("REST request to delete Transferencia : {}", id);
        transferenciaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
