package com.claro.rbmservice.callback.messages.bean;


/*
 * Input Principal que se utiliza en el metodo para cargar una campaña RBM
 * */

public class CampaignRbmInputBean {
	
	private Integer pais=1;

	private String agente="Claro";
	
	private String bill_number="0";

	private BodyRBMBean json_rbm;
	

	
	public CampaignRbmInputBean() {
		super();
	}	
	
	public Integer getPais() {
		return pais;
	}

	public void setPais(Integer pais) {
		this.pais = pais;
	}

	public String getAgente() {
		return agente;
	}

	public void setAgente(String agente) {
		this.agente = agente;
	}

	public String getBill_number() {
		return bill_number;
	}

	public void setBill_number(String bill_number) {
		this.bill_number = bill_number;
	}

	public BodyRBMBean getJson_rbm() {
		return json_rbm;
	}

	public void setJson_rbm(BodyRBMBean json_rbm) {
		this.json_rbm = json_rbm;
	}

	
	
}
