package com.claro.rbmservice.callback.messages.bean;


/*
 * Bean que presenta un objeto de accion sugerida
 * */

public class AccionSugeridaBean {
	
	private int tipo;
	
	private String descripcion;

	private String dato;

	public int getTipo() {
		return tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getDato() {
		return dato;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setDato(String dato) {
		this.dato = dato;
	}

	

}
