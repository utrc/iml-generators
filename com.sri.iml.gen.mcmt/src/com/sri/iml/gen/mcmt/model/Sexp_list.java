package com.sri.iml.gen.mcmt.model;

import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;

public class Sexp_list<Atom> extends Sexp<Atom> {

	private LinkedList<Sexp<Atom>> list;
	
	public Sexp_list(){
		this.list = new LinkedList<Sexp<Atom>>();
	}

	public Sexp_list(Collection<Sexp<Atom>> l){
		this.list = new LinkedList<Sexp<Atom>>();
		this.list.addAll(l);
	}

	public void addLeft(Sexp<Atom> s) {
		list.addFirst(s);
	}

	public void addRight(Sexp<Atom> s) {
		list.addLast(s);
	}

	public void add(Sexp<Atom> s) {
		list.addLast(s);
	}

	public String toString() {
		StringBuilder tmp = new StringBuilder();
		tmp.append('(');
		Iterator<Sexp<Atom>> i = list.listIterator();
		Sexp<Atom> current;		
		while (i.hasNext()) {
			current = i.next();
			tmp.append(current.toString());
			if (i.hasNext()) tmp.append(' ');
		}
		tmp.append(')');
		return tmp.toString();
	}
	
	public Sexp<String> toSexp(){
		Sexp_list<String> result = new Sexp_list<String>();
		Iterator<Sexp<Atom>> i = list.listIterator();
		Sexp<Atom> current;		
		while (i.hasNext()) {
			current = i.next();
			result.addRight(current.toSexp());
		}
		return result;
	}

	LinkedList<Sexp<Atom>> getList(){
		return list;
	}

}
