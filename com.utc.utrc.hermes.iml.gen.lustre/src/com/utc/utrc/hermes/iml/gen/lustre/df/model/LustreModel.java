package com.utc.utrc.hermes.iml.gen.lustre.df.model;

import java.util.HashMap;
import java.util.Map;

public class LustreModel {
	private Map<String,LustreNode> nodes;
	private static int last_id = 0 ;
	public static LustreNode Bool = new LustreNode("iml.lang.Bool");
	public static LustreNode Int = new LustreNode("iml.lang.Int");
	public static LustreNode Real = new LustreNode("iml.lang.Real");
	
	public LustreModel() {
		nodes = new HashMap<>();
		nodes.put(Bool.getName(), Bool);
		nodes.put(Int.getName(), Int);
		nodes.put(Real.getName(), Real);
	}
	public Map<String, LustreNode> getNodes() {
		return nodes;
	}
	public void setNodes(Map<String, LustreNode> nodes) {
		this.nodes = nodes;
	}
	
	public boolean hasType(String s) {
		return nodes.containsKey(s);
	}
	public LustreNode getType(String s) {
		return nodes.get(s);
	}

	public void addNode(LustreNode m) {
		nodes.put(m.getName(), m);
		m.setContainer(this);
	}
	
	public String newSymbolName() {
		last_id ++ ;
		return "symbol___" + last_id ;
	}
	
	
}
