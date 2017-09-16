package org.bratti.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.apache.commons.io.IOUtils;
import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.Local;
import org.bratti.repository.LocalRepository;
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
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Local.
 */
@RestController
@RequestMapping("/api")
public class LocalResource {

    private final Logger log = LoggerFactory.getLogger(LocalResource.class);

    private static final String ENTITY_NAME = "local";

    private final LocalRepository localRepository;

    public LocalResource(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    /**
     * POST  /locals : Create a new local.
     *
     * @param local the local to create
     * @return the ResponseEntity with status 201 (Created) and with body the new local, or with status 400 (Bad Request) if the local has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/locals")
    @Timed
    public ResponseEntity<Local> createLocal(@Valid @RequestBody Local local) throws URISyntaxException {
        log.debug("REST request to save Local : {}", local);
        if (local.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new local cannot already have an ID")).body(null);
        }
        Local result = localRepository.save(local);
        return ResponseEntity.created(new URI("/api/locals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /locals : Updates an existing local.
     *
     * @param local the local to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated local,
     * or with status 400 (Bad Request) if the local is not valid,
     * or with status 500 (Internal Server Error) if the local couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/locals")
    @Timed
    public ResponseEntity<Local> updateLocal(@Valid @RequestBody Local local) throws URISyntaxException {
        log.debug("REST request to update Local : {}", local);
        if (local.getId() == null) {
            return createLocal(local);
        }
        Local result = localRepository.save(local);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, local.getId().toString()))
            .body(result);
    }

    /**
     * GET  /locals : get all the locals.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of locals in body
     */
    @GetMapping("/locais")
    @Timed
    public List<Local> getAllLocals() {
        log.debug("REST request to get all Locals");
        return localRepository.findAll();
    }

    /**
     * GET  /locals/:id : get the "id" local.
     *
     * @param id the id of the local to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the local, or with status 404 (Not Found)
     */
    @GetMapping("/locals/{id}")
    @Timed
    public ResponseEntity<Local> getLocal(@PathVariable Long id) {
        log.debug("REST request to get Local : {}", id);
        Local local = localRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(local));
    }

    /**
     * DELETE  /locals/:id : delete the "id" local.
     *
     * @param id the id of the local to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/locals/{id}")
    @Timed
    public ResponseEntity<Void> deleteLocal(@PathVariable Long id) {
        log.debug("REST request to delete Local : {}", id);
        localRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    
    @PostMapping("/upload")
    public List<Lancamento> upload(@RequestParam("extrato") MultipartFile file) throws IOException {
    	Conta conta = null;
    	
    	byte[] bytes = new byte[new Long(file.getSize()).intValue()];
    	
    	IOUtils.readFully(file.getInputStream(), bytes);
    	
    	int countUtf8 = countEspeciais(bytes, "UTF-8");
    	int countCp1252 = countEspeciais(bytes, "cp1252");
    	
    	String rightEncoding = countUtf8 > countCp1252 ? "UTF-8" : "cp1252"; 
    	
    	List<String> lines = IOUtils.readLines(new ByteArrayInputStream(bytes), rightEncoding);
    	return lines.stream()
    		.filter(linha -> !linha.contains("Dependencia Origem"))
    		.filter(linha -> !linha.contains("Saldo Anterior"))
    		.filter(linha -> !linha.contains("S A L D O"))
    		.map(linha -> linha.replaceAll("\"", ""))
    		.map(linha -> {
    			String[] colunas = linha.split(",");
    			String[] data = colunas[0].split("/");
    			
    			int mes = Integer.parseInt(data[0]);
    			int dia = Integer.parseInt(data[1]);
				int ano = Integer.parseInt(data[2]);
				
				String local = colunas[2];
    			
    			Lancamento l = new Lancamento();
    			l.categoria(new Categoria().nome("Não determinada"));
    			l.conta(conta);
    			l.data(LocalDate.of(ano, mes, dia));
    			l.efetivada(true);
    			l.local(new Local().nome(local));
    			l.motivo(null);
    			l.valor(new BigDecimal(colunas[5]));
    			
    			return l;
    		}).collect(Collectors.toList());

    }

	private int countEspeciais(byte[] bytes, String encoding) throws IOException {
		int caracteresEspeciais = 0;
		String stringUtf8 = IOUtils.toString(bytes, encoding);
    	List<Character> especiais = Arrays.asList('ç', 'ã', 'í', 'é', 'õ');
    	for (int i = 0; i < stringUtf8.length(); i++) {
			if (especiais.contains(stringUtf8.charAt(i)))
				caracteresEspeciais++;
		}
    	return caracteresEspeciais;
	}
}
