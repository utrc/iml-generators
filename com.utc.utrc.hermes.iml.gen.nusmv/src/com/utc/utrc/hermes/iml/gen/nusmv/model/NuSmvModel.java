package com.utc.utrc.hermes.iml.gen.nusmv.model;

import java.util.HashMap;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGeneratorServices;

public class NuSmvModel {
	private Map<String,NuSmvModule> modules;
	private static int last_id = 0 ;
	public static NuSmvModule Bool = new NuSmvModule("iml.lang.Bool");
	public static NuSmvModule Int = new NuSmvModule("iml.lang.Int");
	public static NuSmvModule Real = new NuSmvModule("iml.lang.Real");
	public static NuSmvModule Empty = new NuSmvModule("___EMPTY___") ;
	
	public NuSmvModel() {
		modules = new HashMap<>();
		modules.put(Bool.getName(), Bool);
		modules.put(Int.getName(), Int);
		modules.put(Real.getName(), Real);
		modules.put(Empty.getName(), Empty);
	}
	public Map<String, NuSmvModule> getModules() {
		return modules;
	}
	public void setModules(Map<String, NuSmvModule> modules) {
		this.modules = modules;
	}
	
	public boolean hasType(String s) {
		return modules.containsKey(s);
	}
	public NuSmvModule getType(String s) {
		return modules.get(s);
	}

	public void addModule(NuSmvModule m) {
		modules.put(m.getName(), m);
		m.setContainer(this);
	}
	
	public String newSymbolName() {
		last_id ++ ;
		return "symbol___" + last_id ;
	}
	
}
