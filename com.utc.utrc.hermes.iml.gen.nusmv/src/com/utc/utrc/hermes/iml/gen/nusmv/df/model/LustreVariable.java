package com.utc.utrc.hermes.iml.gen.nusmv.df.model;

import com.utc.utrc.hermes.iml.iml.FolFormula;

public class LustreVariable {
	
	private String name ;
	private LustreTypeInstance type ;
	private FolFormula definition;
	
	private static int id = 0;

	public LustreVariable() {
		this.name = "unnamed___" + id;
		id ++ ;
	}
	public LustreVariable(LustreVariable other) {
		this.type = other.type;
		this.definition = other.definition;
	}

	public LustreVariable(String name) {
		this.name = name;
	}
	
	
	public LustreVariable(String name, LustreTypeInstance ti) {
		this.name = name;
		this.type = ti;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LustreTypeInstance getType() {
		return type;
	}

	public void setType(LustreTypeInstance type) {
		this.type = type;
	}

	public void setDefinition(FolFormula f) {
		definition = f;
	}
	public FolFormula getDefinition() {
		return definition;
	}
	
	
	
}
