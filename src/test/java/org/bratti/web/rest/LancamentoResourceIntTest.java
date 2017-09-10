package org.bratti.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.bratti.MinhasfinancasApp;
import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.Local;
import org.bratti.domain.Recorrencia;
import org.bratti.domain.RecorrenciaLancamentoGerado;
import org.bratti.domain.enumeration.TipoFrequencia;
import org.bratti.repository.LancamentoRepository;
import org.bratti.repository.LocalRepository;
import org.bratti.service.UserService;
import org.bratti.web.rest.errors.ExceptionTranslator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.MvcNamespaceHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinhasfinancasApp.class)
public class LancamentoResourceIntTest {

    private static final LocalDate DEFAULT_DATA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_VALOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR = new BigDecimal(2);

    private static final Boolean DEFAULT_EFETIVADA = false;
    private static final Boolean UPDATED_EFETIVADA = true;

    @Autowired
    private LancamentoRepository lancamentoRepository;
    
    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;
    
    @Autowired
    private UserService userService;

    private MockMvc restLancamentoMockMvc;

    private Lancamento lancamento;
	private Conta conta;
	private Local local;
	private Categoria categoria;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LancamentoResource lancamentoResource = new LancamentoResource(lancamentoRepository, localRepository, userService);
        this.restLancamentoMockMvc = MockMvcBuilders.standaloneSetup(lancamentoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        conta = new Conta().nome("conta").saldoInicial(BigDecimal.ZERO);
		local = new Local().nome("local");
		categoria = new Categoria().nome("categoria");
		
		em.persist(conta);
		em.persist(local);
		em.persist(categoria);
		
		Lancamento lancamento1 = new Lancamento()
			.conta(conta)
			.local(local)
			.categoria(categoria)
		    .data(DEFAULT_DATA)
		    .valor(DEFAULT_VALOR)
		    .efetivada(DEFAULT_EFETIVADA);
		lancamento = lancamento1;
    }

    @Test
    @Transactional
    public void createLancamento() throws Exception {
        int databaseSizeBeforeCreate = lancamentoRepository.findAll().size();

        lancamento.local(new Local().nome("nome"));
        // Create the Lancamento
        restLancamentoMockMvc.perform(post("/api/lancamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lancamento)))
            .andExpect(status().isCreated());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeCreate + 1);
        Lancamento testLancamento = lancamentoList.get(lancamentoList.size() - 1);
        assertThat(testLancamento.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testLancamento.getValor()).isEqualTo(DEFAULT_VALOR);
        assertThat(testLancamento.isEfetivada()).isEqualTo(DEFAULT_EFETIVADA);
    }

    @Test
    @Transactional
    public void createLancamentoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lancamentoRepository.findAll().size();

        // Create the Lancamento with an existing ID
        lancamento.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLancamentoMockMvc.perform(post("/api/lancamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lancamento)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllLancamentos() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        // Get all the lancamentoList
        restLancamentoMockMvc.perform(get("/api/lancamentos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lancamento.getId().intValue())))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())))
            .andExpect(jsonPath("$.[*].efetivada").value(hasItem(DEFAULT_EFETIVADA.booleanValue())));
    }

    @Test
    @Transactional
    public void getLancamento() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        // Get the lancamento
        restLancamentoMockMvc.perform(get("/api/lancamentos/{id}", lancamento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(lancamento.getId().intValue()))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.valor").value(DEFAULT_VALOR.intValue()))
            .andExpect(jsonPath("$.efetivada").value(DEFAULT_EFETIVADA.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingLancamento() throws Exception {
        // Get the lancamento
        restLancamentoMockMvc.perform(get("/api/lancamentos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLancamento() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();

        // Update the lancamento
        Lancamento updatedLancamento = lancamentoRepository.findOne(lancamento.getId());
        updatedLancamento
            .data(UPDATED_DATA)
            .valor(UPDATED_VALOR)
            .efetivada(UPDATED_EFETIVADA);

        restLancamentoMockMvc.perform(put("/api/lancamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLancamento)))
            .andExpect(status().isOk());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
        Lancamento testLancamento = lancamentoList.get(lancamentoList.size() - 1);
        assertThat(testLancamento.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testLancamento.getValor()).isEqualTo(UPDATED_VALOR);
        assertThat(testLancamento.isEfetivada()).isEqualTo(UPDATED_EFETIVADA);
    }

    @Test
    @Transactional
    public void updateNonExistingLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();

        lancamento.local(new Local().nome("nome"));
        
        restLancamentoMockMvc.perform(put("/api/lancamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lancamento)))
            .andExpect(status().isCreated());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLancamento() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);
        int databaseSizeBeforeDelete = lancamentoRepository.findAll().size();

        // Get the lancamento
        restLancamentoMockMvc.perform(delete("/api/lancamentos/{id}", lancamento.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lancamento.class);
        Lancamento lancamento1 = new Lancamento();
        lancamento1.setId(1L);
        Lancamento lancamento2 = new Lancamento();
        lancamento2.setId(lancamento1.getId());
        assertThat(lancamento1).isEqualTo(lancamento2);
        lancamento2.setId(2L);
        assertThat(lancamento1).isNotEqualTo(lancamento2);
        lancamento1.setId(null);
        assertThat(lancamento1).isNotEqualTo(lancamento2);
    }
    
    @Test
    @Transactional
    public void efetivaLancamentoDeRecorrencia() throws IOException, Exception {
    	Recorrencia recorrencia = criaRecorrenciaPersistida();
		
		Lancamento lancamentoProjecao = recorrencia.projecaoAte(LocalDate.now().plus(1, ChronoUnit.YEARS)).get(1);
		
		int lancamentosAntes = lancamentoRepository.findAll().size();
		assertEquals(0, lancamentosAntes);
		
		restLancamentoMockMvc.perform(put("/api/lancamentos")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
		        .content(TestUtil.convertObjectToJsonBytes(lancamentoProjecao)))
	            .andExpect(status().isCreated());
		
		List<Lancamento> lancamentos = lancamentoRepository.findAll();
		int lancamentosDepois = lancamentos.size();
		assertEquals(1, lancamentosDepois);
		
		em.refresh(recorrencia);
		
		assertEquals(1, recorrencia.getRecorrenciaLancamentos().size());
		RecorrenciaLancamentoGerado lancamentoGerado = recorrencia.getRecorrenciaLancamentos().get(0);
		assertEquals(lancamentoProjecao.getData(), lancamentoGerado.getData());
    }

	private Recorrencia criaRecorrenciaPersistida() {
		Recorrencia recorrencia = new Recorrencia();
    	
		recorrencia
    		.aCada(1)
    		.categoria(categoria)
    		.conta(conta)
    		.local(local)
    		.partirDe(LocalDate.now())
    		.tipoFrequencia(TipoFrequencia.MES)
    		.valor(BigDecimal.TEN);
		
		em.persist(recorrencia);
		
		return recorrencia;
	}
    
    @Test
    @Transactional
    public void deletaLancamentoDeProjecaoDeRecorrenciaMasSalvaMotivo() throws IOException, Exception {
    	Recorrencia recorrencia = criaRecorrenciaPersistida();
		
		Lancamento lancamentoProjecao = recorrencia.projecaoAte(LocalDate.now().plus(1, ChronoUnit.YEARS)).get(2);
		
		LocalDate dataProjecao = ((RecorrenciaLancamentoGerado)lancamentoProjecao.getMotivo()).getData();
		
		int lancamentosAntes = lancamentoRepository.findAll().size();
		assertEquals(0, lancamentosAntes);
		assertEquals(0, em.createQuery("from LancamentoMotivo").getResultList().size());
		
		restLancamentoMockMvc.perform(delete("/api/lancamentos")
				.contentType(TestUtil.APPLICATION_JSON_UTF8)
		        .content(TestUtil.convertObjectToJsonBytes(lancamentoProjecao)))
	            .andExpect(status().isOk());
		
		assertEquals(0, lancamentosAntes);
		List<?> motivos = em.createQuery("from LancamentoMotivo").getResultList();
		assertEquals(dataProjecao, ((RecorrenciaLancamentoGerado)motivos.get(0)).getData());
		assertEquals(1, motivos.size());

    }
}
