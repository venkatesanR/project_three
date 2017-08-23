package com.techmania.entity;

import java.io.Serializable;

public class Airport implements Serializable {
	private Integer airportCode;
	private String airportDescription;
	private Integer active;

	public Integer getAirportCode() {
		return airportCode;
	}

	public void setAirportCode(Integer airportCode) {
		this.airportCode = airportCode;
	}

	public String getAirportDescription() {
		return airportDescription;
	}

	public void setAirportDescription(String airportDescription) {
		this.airportDescription = airportDescription;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

}
