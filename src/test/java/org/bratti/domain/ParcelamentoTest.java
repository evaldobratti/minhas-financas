package org.bratti.domain;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.bratti.domain.enumeration.TipoFrequencia;
import org.junit.Assert;
import org.junit.Test;

public class ParcelamentoTest {

	@Test
	public void criaParcelamento_Com_ParcelaInicialMensalEmSeisVezes() {
		Parcelamento parcelamento = (Parcelamento) new Parcelamento()
			.quantidadeParcelas(6)
			.tipoFrequencia(TipoFrequencia.MES)
			.local(new Local().nome("Local"))
			.valor(BigDecimal.valueOf(10))
			.categoria(new Categoria())
			.partirDe(LocalDate.of(2010, 1, 10))
			.aCada(1)
			.conta(new Conta());
		
		List<Lancamento> projecoes = parcelamento.projecaoAte(LocalDate.of(2011, 1, 1));
		
		assertEquals(LocalDate.of(2010, 1, 10), projecoes.get(0).getData());
		assertEquals("Local (1/6)", projecoes.get(0).descricao());
		
		assertEquals(LocalDate.of(2010, 2, 10), projecoes.get(1).getData());
		assertEquals("Local (2/6)", projecoes.get(1).descricao());
		
		assertEquals(LocalDate.of(2010, 3, 10), projecoes.get(2).getData());
		assertEquals("Local (3/6)", projecoes.get(2).descricao());
		
		assertEquals(LocalDate.of(2010, 4, 10), projecoes.get(3).getData());
		assertEquals("Local (4/6)", projecoes.get(3).descricao());
		
		assertEquals(LocalDate.of(2010, 5, 10), projecoes.get(4).getData());
		assertEquals("Local (5/6)", projecoes.get(4).descricao());
		
		assertEquals(LocalDate.of(2010, 6, 10), projecoes.get(5).getData());
		assertEquals("Local (6/6)", projecoes.get(5).descricao());
		
		assertEquals(6, projecoes.size());
	}
	
	@Test
	public void criaParcelamento_Com_ParcelaInicialMensalEmSeisVezesIniciandoNaTerceiraParcela() {
		Parcelamento parcelamento = (Parcelamento) new Parcelamento()
			.quantidadeParcelas(6)
			.iniciandoNaParcela(3)
			.tipoFrequencia(TipoFrequencia.MES)
			.local(new Local().nome("Local"))
			.valor(BigDecimal.valueOf(10))
			.categoria(new Categoria())
			.partirDe(LocalDate.of(2010, 1, 10))
			.aCada(1)
			.conta(new Conta());
		
		List<Lancamento> projecoes = parcelamento.projecaoAte(LocalDate.of(2011, 1, 1));
		
		assertEquals(LocalDate.of(2010, 1, 10), projecoes.get(0).getData());
		assertEquals("Local (3/6)", projecoes.get(0).descricao());
		
		assertEquals(LocalDate.of(2010, 2, 10), projecoes.get(1).getData());
		assertEquals("Local (4/6)", projecoes.get(1).descricao());
		
		assertEquals(LocalDate.of(2010, 3, 10), projecoes.get(2).getData());
		assertEquals("Local (5/6)", projecoes.get(2).descricao());
		
		assertEquals(LocalDate.of(2010, 4, 10), projecoes.get(3).getData());
		assertEquals("Local (6/6)", projecoes.get(3).descricao());
		
		assertEquals(4, projecoes.size());
	}
	
	@Test
	public void naoDeveGerarProjecaoSePrimeiraParcelaJaForFornecida() {
		Parcelamento parcelamento = (Parcelamento) new Parcelamento()
				.quantidadeParcelas(6)
				.iniciandoNaParcela(3)
				.tipoFrequencia(TipoFrequencia.MES)
				.local(new Local().nome("Local"))
				.valor(BigDecimal.valueOf(10))
				.categoria(new Categoria())
				.partirDe(LocalDate.of(2010, 1, 10))
				.aCada(1)
				.conta(new Conta());
		
		parcelamento.efetivaCom(new Lancamento());
	}
	
}
