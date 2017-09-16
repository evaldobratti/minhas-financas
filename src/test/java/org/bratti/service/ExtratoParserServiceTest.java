package org.bratti.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.LinhaExtrato;
import org.bratti.domain.Local;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ExtratoParserServiceTest {

	ExtratoParserService subject;

	Categoria categoria = new Categoria().nome("Categoria Qualquer");
	Conta conta = new Conta().nome("Conta");

	ExtratoDadosProvider extratoParserProvider;
	
	@Before
	public void setup() {
		extratoParserProvider = Mockito.mock(ExtratoDadosProvider.class);
		
		subject = new ExtratoParserService(extratoParserProvider);
		subject.setConta(conta);
	}

	@Test
	public void deveGerarLancamentoBaseadoEmRecarga() {
		Local local = new Local().nome("Telefone Pre-Pago - 99999999999-TIM - Parana");
		setupMock(local);
		
		String linhaRecarga = "\"08/01/2017\",\"\",\"Telefone Pre-Pago - 99999999999-TIM - Parana\",\"\",\"999999999999999\",\"-15.00\",";

		Lancamento lancamento = subject.parseLinha(linhaRecarga);
		
		assertNotNull(lancamento);
		assertEquals(LocalDate.of(2017, 8, 1) , lancamento.getData());
		assertEquals(local, lancamento.getLocal());
		assertEquals(categoria, lancamento.getCategoria());
		assertEquals(conta, lancamento.getConta());
		assertEquals("Telefone Pre-Pago - 99999999999-TIM - Parana ", lancamento.getDescricao());
		assertEquals(-15, lancamento.getValor().intValue());

		assertTrue(lancamento.getMotivo() instanceof LinhaExtrato);
		LinhaExtrato motivo = (LinhaExtrato) lancamento.getMotivo();
		
		assertEquals(linhaRecarga, motivo.getLinha());
		assertEquals("af83ca8e52fd2a3fe81a2713538cece7d6cf35ececa8241254f868b46d3a8b7b", motivo.getHash());
		assertEquals("Telefone Pre-Pago - 99999999999-TIM - Parana", motivo.getLocalOriginal());
	}

	private void setupMock(Local local) {
		Mockito.when(extratoParserProvider.getLocal(local.getNome())).thenReturn(local);
		Mockito.when(extratoParserProvider.getUltimaCategoriaDoLocal(local)).thenReturn(categoria);
	}
	
	@Test
	public void deveGerarLancamentoDeTED() {
		Local local = new Local().nome("Proventos TED - 999 9999     9999999999 JOSEEE EEEEEE");
		setupMock(local);
		
		String linha = "\"08/04/2017\",\"\",\"Proventos TED - 999 9999     9999999999 JOSEEE EEEEEE\",\"\",\"9999999\",\"1000.00\",";
		
		Lancamento lancamento = subject.parseLinha(linha);
		
		assertNotNull(lancamento);
		assertEquals(LocalDate.of(2017, 8, 4) , lancamento.getData());
		assertEquals(local, lancamento.getLocal());
		assertEquals(categoria, lancamento.getCategoria());
		assertEquals(conta, lancamento.getConta());
		assertEquals("Proventos TED - 999 9999     9999999999 JOSEEE EEEEEE ", lancamento.getDescricao());
		assertEquals(1000, lancamento.getValor().intValue());

		assertTrue(lancamento.getMotivo() instanceof LinhaExtrato);
		LinhaExtrato motivo = (LinhaExtrato) lancamento.getMotivo();
		
		assertEquals(linha, motivo.getLinha());
		assertEquals("19e30eef89440f7492e9f8bf879449332a70a74aa28387098ab2aed580f93ca7", motivo.getHash());
		assertEquals("Proventos TED - 999 9999     9999999999 JOSEEE EEEEEE", motivo.getLocalOriginal());
	}
	
	@Test
	public void deveUtilizarDadosDeCompraDeCartaoComoDataDeLancamento() {
		Local local = new Local().nome("SPOLETO MARINGA PARK");
		setupMock(local);
		
		String linha = "\"08/12/2017\",\"9999-9\",\"Compra com Cartão - 06/08 12:00 SPOLETO MARINGA PARK\",\"\",\"999999\",\"-43.30\",";
		
		Lancamento lancamento = subject.parseLinha(linha);
		
		assertNotNull(lancamento);
		assertEquals(LocalDate.of(2017, 8, 6) , lancamento.getData());
		assertEquals(local, lancamento.getLocal());
		assertEquals(categoria, lancamento.getCategoria());
		assertEquals(conta, lancamento.getConta());
		assertEquals("SPOLETO MARINGA PARK ", lancamento.getDescricao());
		assertEquals(new BigDecimal("-43.30"), lancamento.getValor());

		assertTrue(lancamento.getMotivo() instanceof LinhaExtrato);
		LinhaExtrato motivo = (LinhaExtrato) lancamento.getMotivo();
		
		assertEquals(linha, motivo.getLinha());
		assertEquals("10ba93557197d9df6abf71ce0bc8fff4b7c912fd0ea25e1342c4196585b80324", motivo.getHash());
		assertEquals("SPOLETO MARINGA PARK", motivo.getLocalOriginal());
	}
}
