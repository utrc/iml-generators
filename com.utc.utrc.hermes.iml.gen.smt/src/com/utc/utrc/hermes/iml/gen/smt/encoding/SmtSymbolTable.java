/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.iml.NamedType;
/**
 * This class responsible for storing SMT model and provide access to the symbols or elements inside it
 *
 * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
 * @author Gerald Wang (wangg@utrc.utc.com)
 *
 * @param <SortT>
 * @param <FunDeclT>
 */
public class SmtSymbolTable<SortT, FunDeclT, FormulaT> {

	@Inject EncodedIdFactory encodedIdFactory;
	
	private Map<EncodedId, SortT> sorts;
	private Map<EncodedId, FunDeclT> funDecls;
	private Map<EncodedId, FormulaT> assertions; 
	
	public SmtSymbolTable() {
		sorts = new HashMap<>();
		funDecls = new LinkedHashMap<>();
		assertions = new HashMap<>();
	}
	
	public void addSort(EObject type, SortT sort) {
		EncodedId id = encodedIdFactory.createEncodedId(type, null);
		if (sorts.containsKey(id)) return;
		sorts.put(id, sort);
	}
	
	public boolean containsSort(EObject type) {
		EncodedId encoded = encodedIdFactory.createEncodedId(type, null);
		return sorts.containsKey(encoded);
	}

	public String getUniqueId(EObject type, EObject container) {
		return encodedIdFactory.createEncodedId(type, container).stringId();
	}
	
	public SortT getSort(EObject type) {
		EncodedId id = encodedIdFactory.createEncodedId(type, null);
		return sorts.get(id);
	}

	public List<SortT> getSorts() {
		return new ArrayList<SortT>(sorts.values());
	}
	
	public void addFunDecl(EObject container, EObject symbol, FunDeclT funDecl) {
		EncodedId symbolId = encodedIdFactory.createEncodedId(symbol, container);
		
		if (funDecls.containsKey(symbolId)) return;
		
		funDecls.put(symbolId, funDecl);
	}

	public List<EncodedId> getEncodedIds() {
		return new ArrayList<>(sorts.keySet());
		
	}

	public SortT getPrimitiveSort(String typeName) {
		for (Map.Entry<EncodedId, SortT> sort : sorts.entrySet()) {
			EObject imlObject = sort.getKey().getImlObject();
			if (imlObject instanceof NamedType && ((NamedType) imlObject).getName().equals(typeName)) {
				return sort.getValue();
			}
		}
		return null;
	}

	public List<FunDeclT> getFunDecls() {
		return new ArrayList<FunDeclT>(funDecls.values());
	}

	public FunDeclT getFunDecl(EObject container, EObject imlObject) {
		EncodedId id = encodedIdFactory.createEncodedId(imlObject, container);
		return funDecls.get(id);
	}
	
	public Map<EncodedId, FunDeclT> getFunDeclsMap() {
		return funDecls;
	}

	public void addFormula(EObject container, EObject symbol, FormulaT assertion) {
		EncodedId symbolId = encodedIdFactory.createEncodedId(symbol, container);
		if (assertions.containsKey(symbolId)) return;
		
		assertions.put(symbolId, assertion);
	}
	
	public List<FormulaT> getAllFormulas() {
		return new ArrayList<FormulaT>(assertions.values());
	}
	
	public Map<EncodedId, FormulaT> getAssertions() {
		return assertions;
	}

	public void setAssertions(Map<EncodedId, FormulaT> assertions) {
		this.assertions = assertions;
	}

	public void clear() {
		encodedIdFactory.reset();
		assertions.clear();
		funDecls.clear();
		sorts.clear();
	}

}
