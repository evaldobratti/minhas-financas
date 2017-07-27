package org.bratti.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.bratti.domain.Recorrencia;

import org.bratti.repository.RecorrenciaRepository;
import org.bratti.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Recorrencia.
 */
@RestController
@RequestMapping("/api")
public class RecorrenciaResource {

    private final Logger log = LoggerFactory.getLogger(RecorrenciaResource.class);

    private static final String ENTITY_NAME = "recorrencia";

    private final RecorrenciaRepository recorrenciaRepository;

    public RecorrenciaResource(RecorrenciaRepository recorrenciaRepository) {
        this.recorrenciaRepository = recorrenciaRepository;
    }

    /**
     * POST  /recorrencias : Create a new recorrencia.
     *
     * @param recorrencia the recorrencia to create
     * @return the ResponseEntity with status 201 (Created) and with body the new recorrencia, or with status 400 (Bad Request) if the recorrencia has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/recorrencias")
    @Timed
    public ResponseEntity<Recorrencia> createRecorrencia(@Valid @RequestBody Recorrencia recorrencia) throws URISyntaxException {
        log.debug("REST request to save Recorrencia : {}", recorrencia);
        if (recorrencia.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new recorrencia cannot already have an ID")).body(null);
        }
        Recorrencia result = recorrenciaRepository.save(recorrencia);
        return ResponseEntity.created(new URI("/api/recorrencias/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recorrencias : Updates an existing recorrencia.
     *
     * @param recorrencia the recorrencia to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated recorrencia,
     * or with status 400 (Bad Request) if the recorrencia is not valid,
     * or with status 500 (Internal Server Error) if the recorrencia couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/recorrencias")
    @Timed
    public ResponseEntity<Recorrencia> updateRecorrencia(@Valid @RequestBody Recorrencia recorrencia) throws URISyntaxException {
        log.debug("REST request to update Recorrencia : {}", recorrencia);
        if (recorrencia.getId() == null) {
            return createRecorrencia(recorrencia);
        }
        Recorrencia result = recorrenciaRepository.save(recorrencia);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, recorrencia.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recorrencias : get all the recorrencias.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of recorrencias in body
     */
    @GetMapping("/recorrencias")
    @Timed
    public List<Recorrencia> getAllRecorrencias() {
        log.debug("REST request to get all Recorrencias");
        return recorrenciaRepository.findAllWithEagerRelationships();
    }

    /**
     * GET  /recorrencias/:id : get the "id" recorrencia.
     *
     * @param id the id of the recorrencia to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the recorrencia, or with status 404 (Not Found)
     */
    @GetMapping("/recorrencias/{id}")
    @Timed
    public ResponseEntity<Recorrencia> getRecorrencia(@PathVariable Long id) {
        log.debug("REST request to get Recorrencia : {}", id);
        Recorrencia recorrencia = recorrenciaRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(recorrencia));
    }

    /**
     * DELETE  /recorrencias/:id : delete the "id" recorrencia.
     *
     * @param id the id of the recorrencia to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/recorrencias/{id}")
    @Timed
    public ResponseEntity<Void> deleteRecorrencia(@PathVariable Long id) {
        log.debug("REST request to delete Recorrencia : {}", id);
        recorrenciaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
