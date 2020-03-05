package com.claro.rbmservice.callback.messages.bean;

import javax.validation.constraints.Size;

import com.claro.rbmservice.callback.messages.validation.MatchRegexp;

/*
 * Input Principal que se utiliza en los metodos de cargar y modificar campaña
 * */

public class TarjetaRBMBean {
	

	@MatchRegexp(regexp="[a-zA-Z]",message="error.status.accepted")
	@Size(min=1,max=140,message="error.status.accepted")		
	private String url_imagen;	
	
	private String dimension;
	
	@MatchRegexp(regexp="[a-zA-Z]",message="error.status.accepted")
	@Size(min=1,max=140,message="error.status.accepted")		
	private String titulo;
	
	
	@MatchRegexp(regexp="[a-zA-Z]",message="error.status.accepted")
	@Size(min=1,max=240,message="error.status.accepted")		
	private String descripcion;
	
	private AccionSugeridaBean[] acciones_sugeridas =null;
	
	private String dimensionImagen;

	public String getUrl_imagen() {
		return url_imagen;
	}

	public void setUrl_imagen(String url_imagen) {
		this.url_imagen = url_imagen;
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDimensionImagen() {
		return dimensionImagen;
	}

	public void setDimensionImagen(String dimensionImagen) {
		this.dimensionImagen = dimensionImagen;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public AccionSugeridaBean[] getAcciones_sugeridas() {
		return acciones_sugeridas;
	}

	public void setAcciones_sugeridas(AccionSugeridaBean[] acciones_sugeridas) {
		this.acciones_sugeridas = acciones_sugeridas;
	}

	
}
