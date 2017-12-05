package org.bratti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.bratti.domain.Lancamento;
import org.bratti.domain.Local;
import org.bratti.domain.User;
import org.bratti.repository.LancamentoRepository;
import org.bratti.repository.LocalRepository;
import org.bratti.repository.RecorrenciaRepository;
import org.bratti.service.UserService;
import org.bratti.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Lancamento.
 */
@RestController
@RequestMapping("/api")
public class LancamentoResource {

    private static final int ANOS_PROJECAO = 3;

	private final Logger log = LoggerFactory.getLogger(LancamentoResource.class);

    private static final String ENTITY_NAME = "lancamento";

    private final LancamentoRepository lancamentoRepository;

	private final LocalRepository localRepository;

	private UserService userService;

    private RecorrenciaRepository recorrenciaReposiory;
    
    private ProviderSignInUtils providerSignInUtils;

    private SignInAdapter signInAdapter;

    public LancamentoResource(LancamentoRepository lancamentoRepository,
        LocalRepository localRepository, 
        RecorrenciaRepository recorrenciaReposiory,
        UserService userService,
        ProviderSignInUtils providerSignInUtils, 
        SignInAdapter signInAdapter) {
        this.lancamentoRepository = lancamentoRepository;
        this.localRepository = localRepository;
		this.recorrenciaReposiory = recorrenciaReposiory;
        this.userService = userService;
        this.providerSignInUtils = providerSignInUtils;
        this.signInAdapter = signInAdapter;
    }

    @GetMapping("/socialAuth")
    public Map<String, String> getSocialAuthData(HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        
        ProviderSignInAttempt att = (ProviderSignInAttempt) request.getSession().getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE);
        if (att != null) {
            res.put("wtf", att.toString());

        }
        return res;
    }

    @PostMapping("/signup")
    public void login(ServletWebRequest request) {
        Connection<?> conn = providerSignInUtils.getConnectionFromSession(request);
        if (conn != null) {
            UserProfile profile = conn.fetchUserProfile();
            User user = userService.createUser(profile.getEmail(), "asdf##$@231", profile.getFirstName(), profile.getLastName(), profile.getEmail(), 
            conn.getImageUrl(), "pt");
            userService.activateRegistration(user.getActivationKey());
            signInAdapter.signIn(profile.getEmail(), conn, request);
            providerSignInUtils.doPostSignUp(profile.getEmail(), request);
        }

        
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

    private List<Lancamento> projecoesAte(LocalDate ate) {
    	return recorrenciaReposiory.findAllWithEagerRelationships().stream().map(r -> {
    		return r.projecaoAte(ate);
    	}).flatMap(List::stream).collect(Collectors.toList());
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
        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        
        lancamentos.addAll(projecoesAte(LocalDate.now().plus(Period.ofYears(ANOS_PROJECAO))));
        
		return lancamentos;
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
