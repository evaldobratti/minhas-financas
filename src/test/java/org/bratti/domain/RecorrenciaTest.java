package org.bratti.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.bratti.domain.enumeration.TipoFrequencia;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

public class RecorrenciaTest {

	@Mock
	Conta conta;
	
	@Mock
	Categoria categoria;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void naoDeveGerarProjecoesParaDataAnteriorADataDeInicioDaRecorrencia() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 5));
		
		assertTrue(projecoes.isEmpty());
	}
	
	@Test
	public void deveGerarProjecoesParaADataDeInicioDaRecorrencia() {
		LocalDate inicio = LocalDate.of(1900, 1, 10);
		
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(inicio)
				.tipoFrequencia(TipoFrequencia.DIA)
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(inicio);
		
		assertEquals(1, projecoes.size());
	}

	@Test
	public void deveGerarTresProjecoesDiarias() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 12));
		
		assertEquals(3, projecoes.size());
		
		Lancamento lancamento1 = projecoes.get(0);
		assertEquals(LocalDate.of(1900, 1, 10), lancamento1.getData());
		assertEquals(10, lancamento1.getValor().intValue());
		assertEquals(categoria, lancamento1.getCategoria());
		assertEquals(conta, lancamento1.getConta());
		
		Lancamento lancamento2 = projecoes.get(1);
		assertEquals(LocalDate.of(1900, 1, 11), lancamento2.getData());
		assertEquals(10, lancamento2.getValor().intValue());
		assertEquals(categoria, lancamento2.getCategoria());
		assertEquals(conta, lancamento2.getConta());
		
		assertEquals(LocalDate.of(1900, 1, 12), projecoes.get(2).getData());
	}
	
	@Test
	public void deveGerarProjecaoDiaSimDiaNaoParaRecorrenciaACadaDoisDias() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.valor(BigDecimal.valueOf(10))
				.aCada(2)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 15));
		
		assertEquals(3, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 1, 10), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 1, 12), projecoes.get(1).getData());
		assertEquals(LocalDate.of(1900, 1, 14), projecoes.get(2).getData());
	}
	
	@Test
	public void deveGerar4ProjecoesDiarias() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 13));
		
		assertEquals(4, projecoes.size());
		
		Lancamento lancamento = projecoes.get(2);
		assertEquals(LocalDate.of(1900, 1, 12), lancamento.getData());
		assertEquals(10, lancamento.getValor().intValue());
		assertEquals(categoria, lancamento.getCategoria());
		assertEquals(conta, lancamento.getConta());
	}
	
	@Test
	public void deveGerarLancamentoInicialParaProjecaoMensal() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 2, 4));
		
		assertEquals(1, projecoes.size());
	}
	
	@Test
	public void deveGerar3ProjecoesBimestraisDuranteUmSemestreComInicioNoDia() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.valor(BigDecimal.valueOf(10))
				.aCada(2)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 6, 28));
		
		assertEquals(3, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 1, 10), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 3, 10), projecoes.get(1).getData());
		assertEquals(LocalDate.of(1900, 5, 10), projecoes.get(2).getData());
	}
	
	@Test
	public void naoDeveProjetarLancamentosComDatasJaEfetivadas() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta)
				.addRecorrenciaLancamentos(new RecorrenciaLancamentoGerado()
						.data(LocalDate.of(1900, 3, 10)));
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 4, 10));
		
		assertEquals(3, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 1, 10), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 2, 10), projecoes.get(1).getData());
		assertEquals(LocalDate.of(1900, 4, 10), projecoes.get(2).getData());
		
	}
	
}
