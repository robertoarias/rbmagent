package com.claro.rbmservice.callback.messages.bean;



/*
 * Bean para mapear la estructura del JSON con la campaña a enviar
 * */

public class BodyRBMBean {
	
	private TarjetaRBMBean tarjeta = null;
	
	private TarjetaRBMBean[] carrusel = null;

	public TarjetaRBMBean getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(TarjetaRBMBean tarjeta) {
		this.tarjeta = tarjeta;
	}

	public TarjetaRBMBean[] getCarrusel() {
		return carrusel;
	}

	public void setCarrusel(TarjetaRBMBean[] carrusel) {
		this.carrusel = carrusel;
	}

	
	
}
