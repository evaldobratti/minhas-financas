package org.bratti.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.repository.ContaRepository;
import org.bratti.service.ExtratoParserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ExtratoParserResource {

	ExtratoParserService extratoParserService;
	private ContaRepository contaRepository;
	
	public ExtratoParserResource(ContaRepository contaRepository, ExtratoParserService extratoParserService) {
		this.contaRepository = contaRepository;
		this.extratoParserService = extratoParserService;
	}
	
    @PostMapping("{contaId}/upload")
    public List<Lancamento> upload(@PathVariable Long contaId, @RequestParam("extrato") MultipartFile file) throws IOException {
    	Conta conta = contaRepository.findOne(contaId);
    	extratoParserService.setConta(conta);
    	
    	byte[] bytes = new byte[new Long(file.getSize()).intValue()];
    	
    	IOUtils.readFully(file.getInputStream(), bytes);
    	
    	int countUtf8 = countEspeciais(bytes, "UTF-8");
    	int countCp1252 = countEspeciais(bytes, "cp1252");
    	
    	String rightEncoding = countUtf8 > countCp1252 ? "UTF-8" : "cp1252"; 
    	
    	List<String> lines = IOUtils.readLines(new ByteArrayInputStream(bytes), rightEncoding);
    	return extratoParserService.parseLinhas(lines);
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
