package com.techmania.hibernate;

import com.techmania.entity.Airport;
import com.techmania.hibernateutils.AbstractDatabaseManager;

public class SampleTXManager extends AbstractDatabaseManager {
	public static void main(String[] args) {
		SampleTXManager mgr = new SampleTXManager();
		Airport airport = new Airport();
		airport.setAirportCode(4);
		airport.setAirportDescription("INMMA");
		airport.setActive(1);
		mgr.save(airport);
	}
}
