package org.bratti.service;

import static java.lang.Integer.parseInt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.LinhaExtrato;
import org.bratti.domain.Local;
import org.springframework.stereotype.Service;

@Service
public class ExtratoParserService {

	private static final Pattern COMPRA_CARTAO = Pattern.compile("Compra com Cartão - (\\d{2})/(\\d{2}) \\d{2}:\\d{2} (.*)");
	
	private ExtratoDadosProvider extratoParserProvider;
	private Conta conta;

	public ExtratoParserService(ExtratoDadosProvider extratoParserProvider) {
		this.extratoParserProvider = extratoParserProvider;
	}
	
	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public Lancamento parseLinha(String linha) {
		String[] splitted = linha.replaceAll("\"", "").split(",");
		
		String[] dataStr = splitted[0].split("/");
		String historico = splitted[2];
		Matcher matcher = COMPRA_CARTAO.matcher(historico);
		
		LocalDate data;
		String localStr;
		if (matcher.matches()) {
			data = LocalDate.of(parseInt(dataStr[2]), parseInt(matcher.group(2)), parseInt(matcher.group(1)));
			localStr = matcher.group(3);
		} else {
			data = LocalDate.of(parseInt(dataStr[2]), parseInt(dataStr[0]), parseInt(dataStr[1]));
			localStr = historico;
		}
		
		Local local = extratoParserProvider.getLocal(localStr);
		if (local == null)
			local = new Local().nome(localStr);
		Categoria categoria = extratoParserProvider.getUltimaCategoriaDoLocal(local.getNome());
		BigDecimal valor = new BigDecimal(splitted[5]);
		
		LinhaExtrato linhaExtrato = new LinhaExtrato();
		linhaExtrato.setLocalOriginal(localStr);
		linhaExtrato.setLinha(linha);
		
		Lancamento lancamento = new Lancamento();
		lancamento.setData(data);
		lancamento.setLocal(local);
		lancamento.setCategoria(categoria);
		lancamento.setConta(conta);
		lancamento.setValor(valor);
		lancamento.setMotivo(linhaExtrato);
		return lancamento;
	}

	public List<Lancamento> parseLinhas(List<String> linhas) {
		return linhas.stream()
			.filter(linha -> !linha.toLowerCase().contains("s a l d o"))
			.filter(linha -> !linha.toLowerCase().contains("saldo"))
			.filter(linha -> !linha.toLowerCase().contains("data do balancete"))
			.map(linha -> parseLinha(linha))
			.collect(Collectors.toList());
	}

}
