package com.sri.iml.gen.mcmt.model;

import java.util.HashSet;

public class Record<V extends Variable> implements Sexpable {

	private HashSet<V> set;
	
	public Record(){
		this.set = new HashSet<V>();
	}
	
	public void add_field(V field) {
		this.set.add(field);
	}
		
	public Sexp<String> toSexp() {
		Sexp_list<String> l = new Sexp_list<String>();
		for(V v : set){
			l.addRight(v.toSexp());
		}
		return l;
	}
	
}
