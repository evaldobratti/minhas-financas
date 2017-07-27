package org.bratti.web.rest;

import org.bratti.MinhasfinancasApp;

import org.bratti.domain.Transferencia;
import org.bratti.repository.TransferenciaRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TransferenciaResource REST controller.
 *
 * @see TransferenciaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinhasfinancasApp.class)
public class TransferenciaResourceIntTest {

    private static final BigDecimal DEFAULT_VALOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR = new BigDecimal(2);

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTransferenciaMockMvc;

    private Transferencia transferencia;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TransferenciaResource transferenciaResource = new TransferenciaResource(transferenciaRepository);
        this.restTransferenciaMockMvc = MockMvcBuilders.standaloneSetup(transferenciaResource)
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
    public static Transferencia createEntity(EntityManager em) {
        Transferencia transferencia = new Transferencia()
            .valor(DEFAULT_VALOR);
        return transferencia;
    }

    @Before
    public void initTest() {
        transferencia = createEntity(em);
    }

    @Test
    @Transactional
    public void createTransferencia() throws Exception {
        int databaseSizeBeforeCreate = transferenciaRepository.findAll().size();

        // Create the Transferencia
        restTransferenciaMockMvc.perform(post("/api/transferencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transferencia)))
            .andExpect(status().isCreated());

        // Validate the Transferencia in the database
        List<Transferencia> transferenciaList = transferenciaRepository.findAll();
        assertThat(transferenciaList).hasSize(databaseSizeBeforeCreate + 1);
        Transferencia testTransferencia = transferenciaList.get(transferenciaList.size() - 1);
        assertThat(testTransferencia.getValor()).isEqualTo(DEFAULT_VALOR);
    }

    @Test
    @Transactional
    public void createTransferenciaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = transferenciaRepository.findAll().size();

        // Create the Transferencia with an existing ID
        transferencia.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransferenciaMockMvc.perform(post("/api/transferencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transferencia)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Transferencia> transferenciaList = transferenciaRepository.findAll();
        assertThat(transferenciaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTransferencias() throws Exception {
        // Initialize the database
        transferenciaRepository.saveAndFlush(transferencia);

        // Get all the transferenciaList
        restTransferenciaMockMvc.perform(get("/api/transferencias?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transferencia.getId().intValue())))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())));
    }

    @Test
    @Transactional
    public void getTransferencia() throws Exception {
        // Initialize the database
        transferenciaRepository.saveAndFlush(transferencia);

        // Get the transferencia
        restTransferenciaMockMvc.perform(get("/api/transferencias/{id}", transferencia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(transferencia.getId().intValue()))
            .andExpect(jsonPath("$.valor").value(DEFAULT_VALOR.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTransferencia() throws Exception {
        // Get the transferencia
        restTransferenciaMockMvc.perform(get("/api/transferencias/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransferencia() throws Exception {
        // Initialize the database
        transferenciaRepository.saveAndFlush(transferencia);
        int databaseSizeBeforeUpdate = transferenciaRepository.findAll().size();

        // Update the transferencia
        Transferencia updatedTransferencia = transferenciaRepository.findOne(transferencia.getId());
        updatedTransferencia
            .valor(UPDATED_VALOR);

        restTransferenciaMockMvc.perform(put("/api/transferencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTransferencia)))
            .andExpect(status().isOk());

        // Validate the Transferencia in the database
        List<Transferencia> transferenciaList = transferenciaRepository.findAll();
        assertThat(transferenciaList).hasSize(databaseSizeBeforeUpdate);
        Transferencia testTransferencia = transferenciaList.get(transferenciaList.size() - 1);
        assertThat(testTransferencia.getValor()).isEqualTo(UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void updateNonExistingTransferencia() throws Exception {
        int databaseSizeBeforeUpdate = transferenciaRepository.findAll().size();

        // Create the Transferencia

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTransferenciaMockMvc.perform(put("/api/transferencias")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(transferencia)))
            .andExpect(status().isCreated());

        // Validate the Transferencia in the database
        List<Transferencia> transferenciaList = transferenciaRepository.findAll();
        assertThat(transferenciaList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTransferencia() throws Exception {
        // Initialize the database
        transferenciaRepository.saveAndFlush(transferencia);
        int databaseSizeBeforeDelete = transferenciaRepository.findAll().size();

        // Get the transferencia
        restTransferenciaMockMvc.perform(delete("/api/transferencias/{id}", transferencia.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Transferencia> transferenciaList = transferenciaRepository.findAll();
        assertThat(transferenciaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transferencia.class);
        Transferencia transferencia1 = new Transferencia();
        transferencia1.setId(1L);
        Transferencia transferencia2 = new Transferencia();
        transferencia2.setId(transferencia1.getId());
        assertThat(transferencia1).isEqualTo(transferencia2);
        transferencia2.setId(2L);
        assertThat(transferencia1).isNotEqualTo(transferencia2);
        transferencia1.setId(null);
        assertThat(transferencia1).isNotEqualTo(transferencia2);
    }
}
