package com.gps.selenium.enums;

public enum TipoAtuacao {
	
	DESENVOLVIMENTO("Desenvolvimento"),
	FUNCIONAL("Funcional"),
	AMBOS("Ambos"),
	OUTRAS_ESQUIPES("Outras Equipes");

	private String valor;

	private TipoAtuacao(final String valor) {

		this.valor = valor;
	}

	public String getValor() {

		return this.valor;
	}

}

