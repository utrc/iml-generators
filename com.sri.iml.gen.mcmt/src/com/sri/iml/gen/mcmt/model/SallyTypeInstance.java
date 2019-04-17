package com.sri.iml.gen.mcmt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SallyTypeInstance  {
	private SallySm type ;
	private List<SallyVariable> params ;
	
	public SallyTypeInstance() {
		params = new ArrayList<SallyVariable>();
	}
	public SallyTypeInstance(SallyTypeInstance other) {
		this.type = other.type;
		params = new ArrayList<SallyVariable>();
		for(SallyVariable v : other.params) {
			params.add(new SallyVariable(v));
		}
		
	}
	
	
	public SallyTypeInstance(SallySm t) {
		params = new ArrayList<SallyVariable>();
		type = t;
	}
	public SallySm getType() {
		return type;
	}
	public void setType(SallySm type) {
		this.type = type;
	}
	public List< SallyVariable> getParams() {
		return params;
	}
	
	public void setParam(int i , SallyVariable v) {
		int current_size = params.size();
		if (current_size <= i) {
			for(int index = 0 ; index <= i - current_size ; index++) {
				params.add(new SallyVariable("__NOT__SET__")) ;
			}
		}
		params.set(i,v);
	}
	
	
	
	
	
}
