package com.sri.iml.gen.mcmt

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.Assertion
import com.utc.utrc.hermes.iml.iml.AtomicExpression
import com.utc.utrc.hermes.iml.iml.CaseTermExpression
import com.utc.utrc.hermes.iml.iml.FloatNumberLiteral
import com.utc.utrc.hermes.iml.iml.FolFormula
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.IteTermExpression
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.Multiplication
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.NumberLiteral
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm
import com.utc.utrc.hermes.iml.iml.RelationKind
import com.utc.utrc.hermes.iml.iml.SequenceTerm
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm
import com.utc.utrc.hermes.iml.iml.TermExpression
import com.utc.utrc.hermes.iml.iml.TermMemberSelection
import com.utc.utrc.hermes.iml.iml.TruthValue
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import java.util.ArrayList
import java.util.List

class MCMTGeneratorServices {
	
	@Inject
	ImlTypeProvider typeProvider;

	def String getNameFor(SimpleTypeReference tr) {
		return qualifiedName(tr.getType()) +
			'''«FOR b : tr.typeBinding BEFORE '<' SEPARATOR ',' AFTER '>'» «b.nameFor» «ENDFOR»'''
	}

	def String getNameFor(ImlType imlType) {
		if (imlType instanceof SimpleTypeReference) {
			return (imlType as SimpleTypeReference).nameFor
		}
		return "__NOT__SUPPORTED"
	}

	def String qualifiedName(NamedType t) {
		var typename = t.getName();
		return ( ( t.eContainer() as Model ).getName() + "." + typename);
	}

	def String getNameFor(FolFormula f) {
		if (f instanceof TermMemberSelection) {
			return getNameFor(f as TermMemberSelection);
		}
		if (f instanceof SymbolReferenceTerm) {
			return getNameFor(f as SymbolReferenceTerm);
		}
		return "NO_NAME_FOR_FOLFORMULA";

	}

	def String getNameFor(TermMemberSelection ts) {
		var String rec;
		var String mem;
		if (ts.getReceiver() instanceof SymbolReferenceTerm) {
			rec = getNameFor(ts.getReceiver() as SymbolReferenceTerm);
		} else {
			rec = getNameFor(ts.getReceiver() as TermMemberSelection);
		}
		if (ts.getMember() instanceof SymbolReferenceTerm) {
			mem = getNameFor(ts.getMember() as SymbolReferenceTerm);
		} else {
			mem = getNameFor(ts.getMember() as TermMemberSelection);
		}
		return rec + "." + mem;
	}

	def String getNameFor(SymbolReferenceTerm s) {
		return s.getSymbol().getName();
	}


	def String convertOp(String op) {
		switch (op) {
			case "&&": "&"
			case "||": "|"
			case "=>": "->"
			case "<=>": "<->"
		}
	}

	def List<String> getSuffix(FolFormula e,SimpleTypeReference ctx) {
		if (e instanceof TermExpression){
			return getSuffix(e as TermExpression,ctx) ;
		} else {
			var retval = new ArrayList<String>();
			retval.add("");
			return retval;
		}
	}

	def List<String> getSuffix(TermExpression e, SimpleTypeReference ctx) {
		var retval = new ArrayList<String>();
		var t = typeProvider.termExpressionType(e,ctx);
		if (qualifiedName((t as SimpleTypeReference).type).equals("iml.fsm.PrimedVar")){
				retval.add("") ;
				return retval;
		} 
		retval.addAll(getSuffix("",t, ctx))
		return retval;
	}

	def List<String> getSuffix(String sofar, ImlType t, SimpleTypeReference ctx) {
		var retval = new ArrayList<String>();
		if (t instanceof SimpleTypeReference) {
			
			for (s : t.type.symbols) {
				if (! (s instanceof Assertion)) {
					var boundtype = typeProvider.bind(s.type,t) ;
					if ( qualifiedName( (boundtype as SimpleTypeReference).type).equals("iml.lang.Bool") || 
						qualifiedName((boundtype as SimpleTypeReference).type).equals("iml.lang.Int") )
							
					{
						retval.add(sofar + "." + s.name) ;
					} else {
						retval.addAll(getSuffix(sofar + "." + s.name,boundtype,ctx)) ;
					}
					
				}
			}

		}
		return retval;
	}

}
