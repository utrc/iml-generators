package com.utc.utrc.hermes.iml.gen.smt.model.simplesmt;

import java.util.List;

public class SimpleFunDeclaration {
	
	String name;
	List<SimpleSort> inputSorts;
	SimpleSort outputSort;
	SimpleSmtFormula def;
	List<SimpleSmtFormula> inputParams;
	
	public SimpleFunDeclaration() {
	}

	public SimpleFunDeclaration(String name, List<SimpleSort> inputSorts, SimpleSort outputSort) {
		this.name = name;
		this.inputSorts = inputSorts;
		this.outputSort = outputSort;
	}
	
	public SimpleFunDeclaration(String funName, List<SimpleSmtFormula> inputParams, SimpleSort outputSort,
			SimpleSmtFormula funcDef) {
		this.name = funName;
		this.inputParams = inputParams;
		this.outputSort = outputSort;
		this.def = funcDef;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SimpleSort> getInputSorts() {
		return inputSorts;
	}
	public void setInputSorts(List<SimpleSort> inputSorts) {
		this.inputSorts = inputSorts;
	}
	public SimpleSort getOutputSort() {
		return outputSort;
	}
	public void setOutputSort(SimpleSort outputSort) {
		this.outputSort = outputSort;
	}
	
	@Override
	public String toString() {
		if (def != null) {
			return String.format("(define-fun %s (%s) %s %s)", getQuotedName(), 
					inputParams.stream().map(SimpleSmtFormula::toString).reduce((acc, curr) -> acc + " " + curr + "").orElse(""), 
					outputSort.getQuotedName(), def.toString());
		} else if (inputSorts == null || inputSorts.isEmpty()) {
			return String.format("(declare-const %s  %s)", getQuotedName(), outputSort.getQuotedName());
		} else {
			return String.format("(declare-fun %s (%s) %s)", getQuotedName(), 
					inputSorts.stream().map(it -> it.getQuotedName()).reduce((acc, curr) -> acc + " " + curr).orElse("")
					, outputSort.getQuotedName());
		}
	}
	
	public boolean isFuncDef() {
		return def != null;
	}
	
	public String getQuotedName() {
		return SimpleSmtUtil.getQuotedName(name);
	}

}
