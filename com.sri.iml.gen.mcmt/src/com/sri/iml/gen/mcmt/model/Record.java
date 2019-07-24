package com.sri.iml.gen.mcmt.model;

import java.util.HashMap;
import java.util.HashSet;

public class Record<V extends Variable> implements Sexpable {

	private HashSet<V> set;
	private HashMap<String,V> map; 
	
	public Record(){
		this.set = new HashSet<V>();
		this.map = new HashMap<String,V>();
	}
	
	public void add_field(V field) {
		this.set.add(field);
		this.map.put(field.toString(),field);		
	}
	
	public V get(String name) {
		return map.get(name); 
	}
		
	public Sexp<String> toSexp() {
		Sexp_list<String> l = new Sexp_list<String>();
		for(V v : set){
			l.addRight(v.toSexp());
		}
		return l;
	}
	
}
