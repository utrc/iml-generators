/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGenerator;

public class SolutionTable {

	Map<String,Object> solution ;
	Integer cost ;
	String prefix ;
	String typename;
	boolean unsat ;
	public SolutionTable(String s, String prefix, String typename) {
		solution = new HashMap<String, Object>();
		this.prefix = prefix ;
		this.typename = typename ;
		parse(s);
	}
	public SolutionTable(MiniZincGenerator gen, String s, String prefix, String typename) {
		solution = new HashMap<String, Object>();
		this.prefix = prefix ;
		this.typename = typename ;
		parse(gen.getBuilder(),s);
	}
	
	public void parse(String s) {
		
		String[] tokens = s.split(";") ;
		for(String token : tokens) {
			token = token.trim();
			if (token.contains("UNSATISFIABLE")) {
				unsat = true ;
				return ;
			} else {
				unsat = false;
			}
			if (! (token.startsWith("-") || token.startsWith("="))) {
				String[] elements = token.split("=") ;
				String varname = elements[0].trim();
				if (varname.equals("cost___")) {
					String value = elements[1].trim();
					cost = Integer.parseInt(value) ;
				} else {
					varname = varname.replaceFirst(prefix, typename);
					varname = varname.replaceAll("___", ".");
					String value = elements[1].trim();
					try {
						Integer ivalue = Integer.parseInt(value) ;
						solution.put(varname,ivalue);
					} catch (Exception e) {
						try {
							Float fvalue = Float.parseFloat(value);
							solution.put(varname,fvalue);
						} catch (Exception e2) {
							solution.put(varname,Boolean.parseBoolean(value));
						}						
					}
				}
			}
		}
	}
	
	/*
	 * 
	 */
	public void parse(MiniZincModel m, String s) {
			
			String[] tokens = s.split(";") ;
			for(String token : tokens) {
				token = token.trim();
				if (token.contains("UNSATISFIABLE")) {
					unsat = true ;
					return ;
				} else {
					unsat = false;
				}
				if (! (token.startsWith("-") || token.startsWith("="))) {
					String[] elements = token.split("=") ;
					String varname = elements[0].trim();
					if (varname.equals("cost___")) {
						String value = elements[1].trim();
						cost = Integer.parseInt(value) ;
					} else {
						EnumVar var = null ;
						if (m.getEnumVars().containsKey(varname)) {
							var = m.getEnumVars().get(varname);
						}
						varname = varname.replaceFirst(prefix, typename);
						varname = varname.replaceAll("___", ".");
						String value = elements[1].trim();
						try {
							Integer ivalue = Integer.parseInt(value) ;
							if (var != null ) {
								solution.put(varname, var.getType().getLiteralName(ivalue));
							} else {
								solution.put(varname,ivalue);
							}
						} catch (Exception e) {
							try {
								Float fvalue = Float.parseFloat(value);
								solution.put(varname,fvalue);
							} catch (Exception e2) {
								solution.put(varname,Boolean.parseBoolean(value));
							}						
						}
					}
				}
			}
		}
	
	public Set<String> getVars() {
		return solution.keySet();
	}
	
	/*
	 * 
	 */
	public Object getValue(String varname) {
		if (solution.containsKey(varname)) {
			return solution.get(varname) ;
		}
		return null;
	}
	
	/*
	 * 
	 */
	public Integer getCost() {
		return cost;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (unsat) {
			return "UNSATISFIABLE";
		}
		String retval = "" ;
		for(String varname : getVars()) {
			retval += varname + " = ";
			retval += getValue(varname).toString() + "\n" ;
		}
		if (getCost() != null) {
			retval += "cost = " + getCost().toString() + "\n" ;
		}
		return retval;
	}
	
	
	
}
