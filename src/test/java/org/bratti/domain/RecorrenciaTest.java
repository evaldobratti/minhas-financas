package org.bratti.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.bratti.domain.enumeration.TipoFrequencia;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
				.dia(10)
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 5));
		
		assertTrue(projecoes.isEmpty());
	}
	
	@Test
	public void naoDeveGerarProjecoesParaADataDeInicioDaRecorrencia() {
		LocalDate inicio = LocalDate.of(1900, 1, 10);
		
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(inicio)
				.tipoFrequencia(TipoFrequencia.DIA)
				.dia(10)
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(inicio);
		
		assertTrue(projecoes.isEmpty());
	}
	
	@Test
	public void deveGerarUmaProjecaoDiariaParaDiaPosteriorAoInicioDaRecorrencia() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.dia(10)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 11));
		
		assertEquals(1, projecoes.size());
		
		Lancamento lancamento = projecoes.get(0);
		assertEquals(LocalDate.of(1900, 1, 11), lancamento.getData());
		assertEquals(10, lancamento.getValor().intValue());
		assertEquals(categoria, lancamento.getCategoria());
		assertEquals(conta, lancamento.getConta());
	}
	

	@Test
	public void deveGerarDuasProjecoesDiariasPara2DiasPosterioresAoInicioDaRecorrencia() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.dia(10)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 12));
		
		assertEquals(2, projecoes.size());
		
		Lancamento lancamento1 = projecoes.get(0);
		assertEquals(LocalDate.of(1900, 1, 11), lancamento1.getData());
		assertEquals(10, lancamento1.getValor().intValue());
		assertEquals(categoria, lancamento1.getCategoria());
		assertEquals(conta, lancamento1.getConta());
		
		Lancamento lancamento2 = projecoes.get(1);
		assertEquals(LocalDate.of(1900, 1, 12), lancamento2.getData());
		assertEquals(10, lancamento2.getValor().intValue());
		assertEquals(categoria, lancamento2.getCategoria());
		assertEquals(conta, lancamento2.getConta());
	}
	
	@Test
	public void deveGerarProjecaoDiaSimDiaNaoParaRecorrenciaACadaDoisDias() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.dia(10)
				.valor(BigDecimal.valueOf(10))
				.aCada(2)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 15));
		
		//assertEquals(2, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 1, 12), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 1, 14), projecoes.get(1).getData());
	}
	

	@Test
	public void deveGerarProjecaoACada3Dias() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.dia(12)
				.valor(BigDecimal.valueOf(10))
				.aCada(3)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 18));
		
		assertEquals(3, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 1, 12), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 1, 15), projecoes.get(1).getData());
		assertEquals(LocalDate.of(1900, 1, 18), projecoes.get(2).getData());
	}
	
	@Test
	public void deveGerarTerceiraProjecaoDiariaPara3DiasPosterioresAoInicioDaRecorrencia() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.DIA)
				.dia(10)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);;
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 1, 13));
		
		assertEquals(3, projecoes.size());
		
		Lancamento lancamento = projecoes.get(2);
		assertEquals(LocalDate.of(1900, 1, 13), lancamento.getData());
		assertEquals(10, lancamento.getValor().intValue());
		assertEquals(categoria, lancamento.getCategoria());
		assertEquals(conta, lancamento.getConta());
	}
	
	@Test
	public void naoDeveGerarProjecaoMensalSeDataDeProjecaoForAnteriorAoVencimento() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.dia(5)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 2, 4));
		
		assertEquals(0, projecoes.size());
	}
	
	@Test
	public void deveGerarDuasProjecoesMensaisSeDataDeProjecaoForDoisMesesAdiante() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.dia(5)
				.valor(BigDecimal.valueOf(10))
				.aCada(1)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 3, 15));
		
		assertEquals(2, projecoes.size());
		
		Lancamento lancamento1 = projecoes.get(0);
		assertEquals(LocalDate.of(1900, 2, 5), lancamento1.getData());
		assertEquals(10, lancamento1.getValor().intValue());
		assertEquals(categoria, lancamento1.getCategoria());
		assertEquals(conta, lancamento1.getConta());
		
		Lancamento lancamento2 = projecoes.get(1);
		assertEquals(LocalDate.of(1900, 3, 5), lancamento2.getData());
		assertEquals(10, lancamento2.getValor().intValue());
		assertEquals(categoria, lancamento2.getCategoria());
		assertEquals(conta, lancamento2.getConta());
	}
	
	@Test
	public void deveGerar3ProjecoesBimestraisDuranteUmSemestreComInicioFuturo() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.dia(15)
				.valor(BigDecimal.valueOf(10))
				.aCada(2)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 6, 28));
		
		assertEquals(LocalDate.of(1900, 2, 15), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 4, 15), projecoes.get(1).getData());
		assertEquals(LocalDate.of(1900, 6, 15), projecoes.get(2).getData());
	}
	
	@Test
	public void deveGerar3ProjecoesBimestraisDuranteUmSemestreComInicioPassado() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.dia(5)
				.valor(BigDecimal.valueOf(10))
				.aCada(2)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 6, 28));
		
		assertEquals(3, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 2, 5), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 4, 5), projecoes.get(1).getData());
		assertEquals(LocalDate.of(1900, 6, 5), projecoes.get(2).getData());
	}
	
	@Test
	public void deveGerar3ProjecoesBimestraisDuranteUmSemestreComInicioNoDia() {
		Recorrencia recorrencia = new Recorrencia()
				.partirDe(LocalDate.of(1900, 1, 10))
				.tipoFrequencia(TipoFrequencia.MES)
				.dia(10)
				.valor(BigDecimal.valueOf(10))
				.aCada(2)
				.categoria(categoria)
				.conta(conta);
		
		List<Lancamento> projecoes = recorrencia.projecaoAte(LocalDate.of(1900, 6, 28));
		
		assertEquals(2, projecoes.size());
		
		assertEquals(LocalDate.of(1900, 3, 10), projecoes.get(0).getData());
		assertEquals(LocalDate.of(1900, 5, 10), projecoes.get(1).getData());
	}
}
