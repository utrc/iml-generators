package com.sri.iml.gen.mcmt.model;

import java.util.ArrayList;
import java.util.List;

import com.utc.utrc.hermes.iml.iml.FolFormula;

public class SallySymbol {
	private SallyElementType elementType ;
	private String name ;
	private SallyTypeInstance type ;
	private String definition;
	private List<SallyVariable> parameters ;
	
	
	private static int id = 0;
	
	private SallySm container ;
	
	public SallySymbol(String name) {
		this.name = name;
		container = null ;
		parameters = new ArrayList<SallyVariable>() ;
		elementType = SallyElementType.VAR ;
	}
	
	public SallySymbol(SallySymbol other) {
		elementType = other.elementType;
		name = other.name;
		type = new SallyTypeInstance(other.type);
		definition = other.definition;
		parameters = new ArrayList<SallyVariable>() ;
		for(SallyVariable v : parameters) {
			parameters.add(new SallyVariable(v));
		}
		
	}
	
	public SallyElementType getElementType() {
		return elementType;
	}

	public void setElementType(SallyElementType elementType) {
		this.elementType = elementType;
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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public SallySm getContainer() {
		return container;
	}

	public void setContainer(SallySm container) {
		this.container = container;
	}
	
	public List<SallyVariable> getParameters(){
		return parameters;
	}
	public void addParameter(SallyVariable v) {
		parameters.add(v);
	}
	
	public int indexOf(String pname) {
		for(int index = 0 ; index < parameters.size() ; index++) {
			if (parameters.get(index).getName().equals(pname)) {
				return index ;
			}
		}
		return -1;
	}
	
	
	
	
}
