package com.utc.utrc.hermes.iml.gen.lustre.df;

import java.util.HashMap;
import java.util.Map;

import com.utc.utrc.hermes.iml.gen.systems.ComponentType;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;

public class Node {
	
	private SimpleTypeReference nodeType ;
	private Map<String, FolFormula> lets ;
	boolean component ;
	private ComponentType ctype ;
	
	public Node() {
		lets = new HashMap<String, FolFormula>() ;
	}

	public SimpleTypeReference getNodeType() {
		return nodeType;
	}

	public void setNodeType(SimpleTypeReference nodeType) {
		this.nodeType = nodeType;
	}


	public Map<String, FolFormula> getLets() {
		return lets;
	}

	
	public boolean isComponent() {
		return component;
	}

	public void setComponent(boolean component) {
		this.component = component;
	}
	
	public ComponentType getComponentType() {
		return ctype ;
	}
	public void setComponentType(ComponentType ct) {
		ctype = ct ;
	}
	
	
}
