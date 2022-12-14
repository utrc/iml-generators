/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.model.simplesmt;

import java.util.List;

import com.utc.utrc.hermes.iml.gen.smt.model.AbstractSort;

public class SimpleSort extends AbstractSort {
	SimpleSort domain;
	SimpleSort range;
	
	List<SimpleSort> tupleElements;
	List<String> enumList;
	
	public SimpleSort() {
	}

	public SimpleSort(String name) {
		super();
		this.name = name;
	}
	
	public SimpleSort(String name, SimpleSort domain, SimpleSort range) {
		super();
		this.name = name;
		this.domain = domain;
		this.range = range;
	}
	
	public SimpleSort(String name, List<SimpleSort> tupleElements) {
		super();
		this.name = name;
		this.tupleElements = tupleElements;
	}
	
	public SimpleSort(String sortName, List<String> enumList, SortType type) {
		super();
		this.name = sortName;
		this.enumList = enumList;
	}

	public SimpleSort getDomain() {
		return domain;
	}

	public void setDomain(SimpleSort domain) {
		this.domain = domain;
	}

	public SimpleSort getRange() {
		return range;
	}

	public void setRange(SimpleSort range) {
		this.range = range;
	}
	
	public List<SimpleSort> getTupleElements() {
		return tupleElements;
	}

	public void setTupleElements(List<SimpleSort> tupleElements) {
		this.tupleElements = tupleElements;
	}

	@Override
	public String toString() {
		if (domain != null) {
			return "(define-sort " + getQuotedName() + " () (Array " + domain.getQuotedName() + " " + range.getQuotedName() + ")" ;
		} else if (tupleElements != null && !tupleElements.isEmpty()) {
			return String.format("(declare-datatypes () ((%s (|mk_%s| %s))))", getQuotedName(), getName(), getTupleListTypes());
		} else if (enumList != null && !enumList.isEmpty()) {
			return String.format("(declare-datatypes () ((%s %s)))", getQuotedName(), enumList.stream().reduce((acc, curr) ->  acc + " " + curr).orElse(""));  // Z3 style
//			return String.format("(declare-datatypes ((%s 0)) ((%s)))", getQuotedName(), enumList.stream().reduce("",(acc, curr) ->  acc + " (" + curr + ")")); // CVC4 style
		} else {
			return "(declare-sort " + getQuotedName() + " 0)";
		}
	}
	
	private String getTupleListTypes() {
		StringBuilder sb = new StringBuilder();
		for (int i=0 ; i < getTupleElements().size() ; i++) {
			sb.append("(__index_" + i + " " + getTupleElements().get(i).getQuotedName() + ") ");
		}
		return sb.toString();
	}

	public String getQuotedName() {
		return SimpleSmtUtil.getQuotedName(getName());
	}
}
