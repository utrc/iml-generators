package com.sri.iml.gen.mcmt.model;

import java.util.HashMap;
import java.util.Map;

public class SallyModel {
	private Map<String,SallySm> modules;
	private static int last_id = 0 ;
	public static SallySm Bool = new SallySm("iml.lang.Bool");
	public static SallySm Int = new SallySm("iml.lang.Int");
	public static SallySm Real = new SallySm("iml.lang.Real");
	
	public SallyModel() {
		modules = new HashMap<>();
		modules.put(Bool.getName(), Bool);
		modules.put(Int.getName(), Int);
		modules.put(Real.getName(), Real);
	}
	public Map<String, SallySm> getModules() {
		return modules;
	}
	public void setModules(Map<String, SallySm> modules) {
		this.modules = modules;
	}
	
	public boolean hasType(String s) {
		return modules.containsKey(s);
	}
	public SallySm getType(String s) {
		return modules.get(s);
	}

	public void addModule(SallySm m) {
		modules.put(m.getName(), m);
		m.setContainer(this);
	}
	
	public String newSymbolName() {
		last_id ++ ;
		return "symbol___" + last_id ;
	}

	
}
