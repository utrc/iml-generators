package com.utc.utrc.hermes.iml.gen.nusmv.df.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LustreTypeInstance  {
	private LustreNode type ;
	private List<LustreVariable> params ;
	
	public LustreTypeInstance() {
		params = new ArrayList<LustreVariable>();
	}
	public LustreTypeInstance(LustreTypeInstance other) {
		this.type = other.type;
		params = new ArrayList<LustreVariable>();
		for(LustreVariable v : other.params) {
			params.add(new LustreVariable(v));
		}
		
	}
	
	
	public LustreTypeInstance(LustreNode t) {
		params = new ArrayList<LustreVariable>();
		type = t;
	}
	public LustreNode getType() {
		return type;
	}
	public void setType(LustreNode type) {
		this.type = type;
	}
	public List< LustreVariable> getParams() {
		return params;
	}
	
	public void setParam(int i , LustreVariable v) {
		int current_size = params.size();
		if (current_size <= i) {
			for(int index = 0 ; index <= i - current_size ; index++) {
				params.add(new LustreVariable("__NOT__SET__")) ;
			}
		}
		params.set(i,v);
	}
	
	
	
	
	
}
