package com.utc.utrc.hermes.iml.gen.smt.encoding;

import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
/**
 * This is an interface to abstract the encoding of any IML objects
 *
 * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
 * @author Gerald Wang (wangg@utrc.utc.com) 
 */
public interface ImlEncoder {
	public void encode(NamedType type) throws SMTEncodingException;
	
	public void encode(ImlType imlType) throws SMTEncodingException;
	
	public void encode(Symbol symbol) throws SMTEncodingException;
	
	public void encode(SymbolDeclaration symbol) throws SMTEncodingException;
	
	public void encode(Model model) throws SMTEncodingException;
	
}
