package com.utc.utrc.hermes.iml.gen.zinc.model;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.siemens.ct.jmz.elements.Variable;
import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.integer.IntegerExpression;
import at.siemens.ct.jmz.expressions.integer.IntegerVariable;

public class IntegerFunction extends IntegerVariable {

	List<Object> params ;
	List<Object> actuals ;
	IntegerExpression definition ;
	

	public IntegerFunction(String name) {
		super(name);
		params = new ArrayList<>();
		definition = null ;
	}
	
	public void addParameter(Object o)  {
		if (o instanceof IntegerVariable || o instanceof FloatVariable) {
			params.add(o) ;
		} 
	}
	
	public void addActual(Object o)  {
		if (o instanceof IntegerVariable || o instanceof FloatVariable) {
			actuals.add(o) ;
		} 
	}
	
	public void setDefinition(IntegerExpression e) {
		definition = e ;
	}
	
	public IntegerExpression getDefinition() {
		return definition ;
	}
	
	public List<Object> getParameters() {
		return params;
	}
	public List<Object> getActuals() {
		return actuals;
	}
	
}
