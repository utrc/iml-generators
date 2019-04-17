package com.sri.iml.gen.mcmt.model;

import java.util.ArrayList;
import java.util.List;

public class SallyType {
	
	private String name ;
	private boolean is_enum ;
	private List<String> literals ;

	public static SallyType Bool = new SallyType("boolean") ;
	public static SallyType Int = new SallyType("integer") ;
	
	public SallyType(String name) {
		this.name = name ;
		literals = new ArrayList<String>();
		is_enum = false;
	}
	
	public boolean isEnum() {
		return is_enum;
	}

	public void setEnum(boolean is_enum) {
		this.is_enum = is_enum;
	}

	public List<String> getLiterals() {
		return literals;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StringBuffer serialize() {
		return new StringBuffer("MODULE " + name) ;
	}
	
	
	
}
