package com.sri.iml.gen.mcmt.model;

public abstract class Named {

	private String name;

	public Named(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

}
