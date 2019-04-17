package com.sri.iml.gen.mcmt.model;

import com.utc.utrc.hermes.iml.iml.FolFormula;

public class SallyVariable extends SallyElement{
	
	private String name ;
	private SallyTypeInstance type ;
	private FolFormula definition;
	
	private static int id = 0;

	public SallyVariable() {
		this.name = "unnamed___" + id;
		id ++ ;
	}
	public SallyVariable(SallyVariable other) {
		this.type = other.type;
		this.definition = other.definition;
	}

	public SallyVariable(String name) {
		this.name = name;
	}
	
	
	public SallyVariable(String name, SallyTypeInstance ti) {
		this.name = name;
		this.type = ti;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SallyTypeInstance getType() {
		return type;
	}

	public void setType(SallyTypeInstance type) {
		this.type = type;
	}

	public void setDefinition(FolFormula f) {
		definition = f;
	}
	public FolFormula getDefinition() {
		return definition;
	}
	
	
	
}
