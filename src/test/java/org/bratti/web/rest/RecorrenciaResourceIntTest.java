package org.bratti.web.rest;

import org.bratti.MinhasfinancasApp;

import org.bratti.domain.Recorrencia;
import org.bratti.domain.Conta;
import org.bratti.repository.RecorrenciaRepository;
import org.bratti.web.rest.errors.ExceptionTranslator;

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

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.bratti.domain.enumeration.TipoFrequencia;
/**
 * Test class for the RecorrenciaResource REST controller.
 *
 * @see RecorrenciaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinhasfinancasApp.class)
public class RecorrenciaResourceIntTest {

    private static final TipoFrequencia DEFAULT_TIPO_FREQUENCIA = TipoFrequencia.DIA;
    private static final TipoFrequencia UPDATED_TIPO_FREQUENCIA = TipoFrequencia.SEMANA;

    private static final Integer DEFAULT_A_CADA = 1;
    private static final Integer UPDATED_A_CADA = 2;

    private static final BigDecimal DEFAULT_VALOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR = new BigDecimal(2);

    private static final Integer DEFAULT_DIA = 1;
    private static final Integer UPDATED_DIA = 2;

    private static final LocalDate DEFAULT_PARTIR_DE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PARTIR_DE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private RecorrenciaRepository recorrenciaRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRecorrenciaMockMvc;

    private Recorrencia recorrencia;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecorrenciaResource recorrenciaResource = new RecorrenciaResource(recorrenciaRepository);
        this.restRecorrenciaMockMvc = MockMvcBuilders.standaloneSetup(recorrenciaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recorrencia createEntity(EntityManager em) {
        Recorrencia recorrencia = new Recorrencia()
            .tipoFrequencia(DEFAULT_TIPO_FREQUENCIA)
            .aCada(DEFAULT_A_CADA)
            .valor(DEFAULT_VALOR)
            .dia(DEFAULT_DIA)
            .partirDe(DEFAULT_PARTIR_DE);
        // Add required entity
        Conta conta = ContaResourceIntTest.createEntity(em);
        em.persist(conta);
        em.flush();
        recorrencia.setConta(conta);
        return recorrencia;
    }

    @Before
    public void initTest() {
        recorrencia = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecorrencia() throws Exception {
        int databaseSizeBeforeCreate = recorrenciaRepository.findAll().size();

        // Create the Recorrencia
        restRecorrenciaMockMvc.perform(post("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isCreated());

        // Validate the Recorrencia in the database
        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeCreate + 1);
        Recorrencia testRecorrencia = recorrenciaList.get(recorrenciaList.size() - 1);
        assertThat(testRecorrencia.getTipoFrequencia()).isEqualTo(DEFAULT_TIPO_FREQUENCIA);
        assertThat(testRecorrencia.getaCada()).isEqualTo(DEFAULT_A_CADA);
        assertThat(testRecorrencia.getValor()).isEqualTo(DEFAULT_VALOR);
        assertThat(testRecorrencia.getDia()).isEqualTo(DEFAULT_DIA);
        assertThat(testRecorrencia.getPartirDe()).isEqualTo(DEFAULT_PARTIR_DE);
    }

    @Test
    @Transactional
    public void createRecorrenciaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recorrenciaRepository.findAll().size();

        // Create the Recorrencia with an existing ID
        recorrencia.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecorrenciaMockMvc.perform(post("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTipoFrequenciaIsRequired() throws Exception {
        int databaseSizeBeforeTest = recorrenciaRepository.findAll().size();
        // set the field null
        recorrencia.setTipoFrequencia(null);

        // Create the Recorrencia, which fails.

        restRecorrenciaMockMvc.perform(post("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isBadRequest());

        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkaCadaIsRequired() throws Exception {
        int databaseSizeBeforeTest = recorrenciaRepository.findAll().size();
        // set the field null
        recorrencia.setaCada(null);

        // Create the Recorrencia, which fails.

        restRecorrenciaMockMvc.perform(post("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isBadRequest());

        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDiaIsRequired() throws Exception {
        int databaseSizeBeforeTest = recorrenciaRepository.findAll().size();
        // set the field null
        recorrencia.setDia(null);

        // Create the Recorrencia, which fails.

        restRecorrenciaMockMvc.perform(post("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isBadRequest());

        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPartirDeIsRequired() throws Exception {
        int databaseSizeBeforeTest = recorrenciaRepository.findAll().size();
        // set the field null
        recorrencia.setPartirDe(null);

        // Create the Recorrencia, which fails.

        restRecorrenciaMockMvc.perform(post("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isBadRequest());

        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecorrencias() throws Exception {
        // Initialize the database
        recorrenciaRepository.saveAndFlush(recorrencia);

        // Get all the recorrenciaList
        restRecorrenciaMockMvc.perform(get("/api/recorrencias?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recorrencia.getId().intValue())))
            .andExpect(jsonPath("$.[*].tipoFrequencia").value(hasItem(DEFAULT_TIPO_FREQUENCIA.toString())))
            .andExpect(jsonPath("$.[*].aCada").value(hasItem(DEFAULT_A_CADA)))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())))
            .andExpect(jsonPath("$.[*].dia").value(hasItem(DEFAULT_DIA)))
            .andExpect(jsonPath("$.[*].partirDe").value(hasItem(DEFAULT_PARTIR_DE.toString())));
    }

    @Test
    @Transactional
    public void getRecorrencia() throws Exception {
        // Initialize the database
        recorrenciaRepository.saveAndFlush(recorrencia);

        // Get the recorrencia
        restRecorrenciaMockMvc.perform(get("/api/recorrencias/{id}", recorrencia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(recorrencia.getId().intValue()))
            .andExpect(jsonPath("$.tipoFrequencia").value(DEFAULT_TIPO_FREQUENCIA.toString()))
            .andExpect(jsonPath("$.aCada").value(DEFAULT_A_CADA))
            .andExpect(jsonPath("$.valor").value(DEFAULT_VALOR.intValue()))
            .andExpect(jsonPath("$.dia").value(DEFAULT_DIA))
            .andExpect(jsonPath("$.partirDe").value(DEFAULT_PARTIR_DE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRecorrencia() throws Exception {
        // Get the recorrencia
        restRecorrenciaMockMvc.perform(get("/api/recorrencias/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecorrencia() throws Exception {
        // Initialize the database
        recorrenciaRepository.saveAndFlush(recorrencia);
        int databaseSizeBeforeUpdate = recorrenciaRepository.findAll().size();

        // Update the recorrencia
        Recorrencia updatedRecorrencia = recorrenciaRepository.findOne(recorrencia.getId());
        updatedRecorrencia
            .tipoFrequencia(UPDATED_TIPO_FREQUENCIA)
            .aCada(UPDATED_A_CADA)
            .valor(UPDATED_VALOR)
            .dia(UPDATED_DIA)
            .partirDe(UPDATED_PARTIR_DE);

        restRecorrenciaMockMvc.perform(put("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRecorrencia)))
            .andExpect(status().isOk());

        // Validate the Recorrencia in the database
        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeUpdate);
        Recorrencia testRecorrencia = recorrenciaList.get(recorrenciaList.size() - 1);
        assertThat(testRecorrencia.getTipoFrequencia()).isEqualTo(UPDATED_TIPO_FREQUENCIA);
        assertThat(testRecorrencia.getaCada()).isEqualTo(UPDATED_A_CADA);
        assertThat(testRecorrencia.getValor()).isEqualTo(UPDATED_VALOR);
        assertThat(testRecorrencia.getDia()).isEqualTo(UPDATED_DIA);
        assertThat(testRecorrencia.getPartirDe()).isEqualTo(UPDATED_PARTIR_DE);
    }

    @Test
    @Transactional
    public void updateNonExistingRecorrencia() throws Exception {
        int databaseSizeBeforeUpdate = recorrenciaRepository.findAll().size();

        // Create the Recorrencia

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRecorrenciaMockMvc.perform(put("/api/recorrencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recorrencia)))
            .andExpect(status().isCreated());

        // Validate the Recorrencia in the database
        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteRecorrencia() throws Exception {
        // Initialize the database
        recorrenciaRepository.saveAndFlush(recorrencia);
        int databaseSizeBeforeDelete = recorrenciaRepository.findAll().size();

        // Get the recorrencia
        restRecorrenciaMockMvc.perform(delete("/api/recorrencias/{id}", recorrencia.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Recorrencia> recorrenciaList = recorrenciaRepository.findAll();
        assertThat(recorrenciaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recorrencia.class);
        Recorrencia recorrencia1 = new Recorrencia();
        recorrencia1.setId(1L);
        Recorrencia recorrencia2 = new Recorrencia();
        recorrencia2.setId(recorrencia1.getId());
        assertThat(recorrencia1).isEqualTo(recorrencia2);
        recorrencia2.setId(2L);
        assertThat(recorrencia1).isNotEqualTo(recorrencia2);
        recorrencia1.setId(null);
        assertThat(recorrencia1).isNotEqualTo(recorrencia2);
    }
}
