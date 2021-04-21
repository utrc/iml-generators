/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.lustre.df.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LustreType {
	
	private String name ;
	protected boolean is_enum ;
	private List<String> literals ;
	private boolean is_struct ;
	private Map<String,LustreSymbol> fields ;
	protected LustreModel container ;

	public static LustreType Bool = new LustreType("boolean") ;
	public static LustreType Int = new LustreType("integer") ;
	
	public LustreType(String name) {
		this.name = name ;
		literals = new ArrayList<String>();
		is_enum = false;
		fields = new HashMap<>() ;
		is_struct = false;
	}
	
	public boolean isEnum() {
		return is_enum;
	}

	public void setEnum(boolean is_enum) {
		this.is_enum = is_enum;
	}

	public boolean isStruct() {
		return is_struct;
	}

	public void setStruct(boolean is_struct) {
		this.is_struct = is_struct;
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

	public Map<String,LustreSymbol> getFields() {
		return fields;
	}

	public void setFields(Map<String,LustreSymbol> fields) {
		this.fields = fields;
	}

	public void addField(String name, LustreSymbol symbol) {
		fields.put(name, symbol);
	}
	
	
	
}
