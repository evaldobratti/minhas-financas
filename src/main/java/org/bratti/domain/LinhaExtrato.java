package org.bratti.domain;

import org.apache.commons.codec.digest.DigestUtils;

public class LinhaExtrato extends LancamentoMotivo {

	private String localOriginal;
	private String linha;
	private String hash;

	public void setLocalOriginal(String localOriginal) {
		this.localOriginal = localOriginal;
	}
	
	public String getLocalOriginal() {
		return this.localOriginal;
	}

	public void setLinha(String linha) {
		this.linha = linha;
		this.hash = DigestUtils.sha256Hex(linha);
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getLinha() {
		return linha;
	}

}
