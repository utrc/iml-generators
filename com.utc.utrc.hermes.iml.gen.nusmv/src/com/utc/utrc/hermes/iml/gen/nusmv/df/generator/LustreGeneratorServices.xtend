package com.utc.utrc.hermes.iml.gen.nusmv.df.generator

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreModel
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreNode
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvSymbol
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvTypeInstance
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.Assertion
import com.utc.utrc.hermes.iml.iml.AtomicExpression
import com.utc.utrc.hermes.iml.iml.CaseTermExpression
import com.utc.utrc.hermes.iml.iml.FolFormula
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.IteTermExpression
import com.utc.utrc.hermes.iml.iml.Multiplication
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.NumberLiteral
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm
import com.utc.utrc.hermes.iml.iml.RelationKind
import com.utc.utrc.hermes.iml.iml.SequenceTerm
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference
import com.utc.utrc.hermes.iml.iml.Symbol
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm
import com.utc.utrc.hermes.iml.iml.TailedExpression
import com.utc.utrc.hermes.iml.iml.TermExpression
import com.utc.utrc.hermes.iml.iml.TermMemberSelection
import com.utc.utrc.hermes.iml.iml.Trait
import com.utc.utrc.hermes.iml.iml.TruthValue
import com.utc.utrc.hermes.iml.iml.TupleConstructor
import com.utc.utrc.hermes.iml.lib.ImlStdLib
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import com.utc.utrc.hermes.iml.typing.TypingEnvironment
import com.utc.utrc.hermes.iml.util.ImlUtil
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import org.eclipse.xtext.naming.IQualifiedNameProvider
import com.utc.utrc.hermes.iml.iml.FunctionType
import com.utc.utrc.hermes.iml.iml.LambdaExpression
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.utc.utrc.hermes.iml.iml.TupleType

class LustreGeneratorServices {

	@Inject
	private ImlTypeProvider typeProvider;

	@Inject
	private IQualifiedNameProvider qnp;

	@Inject
	private ImlStdLib stdLibs;

	private Map<String, SymbolDeclaration> functional_nodes ;

	new() {
		functional_nodes = newHashMap
	}

	def String getNameFor(SimpleTypeReference tr) {
		return ImlUtil.getTypeName(tr, qnp);
	}

	def String getNameFor(ImlType imlType) {
		if (imlType instanceof SimpleTypeReference) {
			return (imlType as SimpleTypeReference).nameFor
		}
		return "__NOT__SUPPORTED"
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
		return rec + "_dot_" + mem;
	}

	def String getNameFor(SymbolReferenceTerm s) {
		return s.getSymbol().getName();
	}

	def String serialize(LustreModel m) {
		'''«FOR n : m.nodes.values AFTER '\n'»«serialize(n)»«ENDFOR»'''
	}

	def String serialize(LustreNode m) {
		if(m.name.equals("iml.lang.Bool") || m.name.equals("iml.lang.Int") || m.name.equals("iml.lang.Real")) return "";
		if(m.isEnum()) {
			return 
			'''
			type «toLustreName(m)» = enum {
				«FOR l : m.literals SEPARATOR ',\n'» «toLustreName(m) + "_dot_" +l» «ENDFOR»
			} ;
			''';
		}
		if(m.isStruct()) {
			return 
			'''
			type «toLustreName(m)» = struct { 
				«FOR f : m.fields.values SEPARATOR ';\n'» «f.name» : «toLustreName(f.type.type)» «ENDFOR»
			} ;
			'''
		} 
		var nodes = 
		'''
			node imported «toLustreName(m)» «FOR p : m.parameters BEFORE '(' SEPARATOR ';' AFTER ')'» «p.name» : «p.type.type.toLustreName» «ENDFOR»
			returns «FOR v : m.returns BEFORE '(' SEPARATOR ';' AFTER ')'» «v.name» : «v.type.type.toLustreName» «ENDFOR»
			«IF (m.variables.size > 0 || m.fields.size > 0 || m.components.size > 0)»
			(*@contract
«««				var
«««				«FOR v : m.variables.values SEPARATOR '; \n' AFTER ';'» var «v.name» : «v.type.type.toLustreName»«ENDFOR» 
				«FOR v : m.fields.values SEPARATOR '; \n' AFTER ';'» var «v.name» : «v.type.type.toLustreName» «IF v.definition !== null» = ( «v.definition» )«ENDIF» «ENDFOR» 
				«FOR v : m.components.values» 
				«FOR p : v.type.type.returns SEPARATOR ';\n' AFTER ';'» var «v.name»_«p.name» : «p.type.type.toLustreName» «ENDFOR»
				«ENDFOR»
			*) 
			«FOR v : m.variables.values» var «v.name» : «v.type.type.toLustreName» ; \n «ENDFOR» 
			«FOR v : m.fields.values» «IF v.definition === null » var «v.name» : «v.type.type.toLustreName» ; \n «ENDIF» «ENDFOR» 
			«ENDIF»
			«IF (m.components.size > 0)»
			let
«««			«IF (m.components.size > 0)»
			«FOR v : m.components.values » 
			(«FOR p : v.type.type.returns SEPARATOR ','» «v.name»_«p.name» «ENDFOR») = «v.type.type.toLustreName» («FOR param : v.type.params SEPARATOR ','» «param.name»«ENDFOR») ; «'\n '» 
			«ENDFOR» 
«««			«ENDIF»
«««			«IF (m.fields.size > 0)»
«««			«FOR v : m.fields.values» «IF v.definition !== null» «v.name» = «v.definition» ; «'\n '» «ENDIF»«ENDFOR» 
«««			«ENDIF»
«««			«FOR v : m.lets.values AFTER '; \n'»«v.definition»«ENDFOR»
			tel
			«ENDIF»
		'''
		var closed = new ArrayList<String>() ;
		while(functional_nodes.size > 0) {
			var fname = functional_nodes.keySet.get(0) ;
			if (! closed.contains(fname)) { 
				var togen = functional_nodes.get(fname);
				nodes = nodes + serializeFunctionalNode(togen);
			}
			functional_nodes.remove(fname) ;
			closed.add(fname);
		}
		
		return nodes ;
		
	}

	def List<String> suffix(NuSmvSymbol s) {
		var retval = new ArrayList<String>();
		for (field : s.type.type.variables.keySet) {
			if (s.type.type.variables.get(field).type.type.name.equals("iml.lang.Bool") ||
				s.type.type.variables.get(field).type.type.name.equals("iml.lang.Int") ||
				s.type.type.variables.get(field).type.type.name.equals("iml.lang.Real")) {
				retval.add(field);
			} else {
				var tmplist = suffix(s.type.type.variables.get(field))
				for (ssuffix : tmplist) {
					retval.add(field + ssuffix);
				}
			}
		}
		return retval;
	}

	def List<String> suffix(NuSmvTypeInstance s) {
		var retval = new ArrayList<String>();
		for (field : s.type.variables.keySet) {
			if (s.type.variables.get(field).type.type.name.equals("iml.lang.Bool") ||
				s.type.variables.get(field).type.type.name.equals("iml.lang.Int") ||
				s.type.variables.get(field).type.type.name.equals("iml.lang.Real")) {
				retval.add(field);
			} else {
				var tmplist = suffix(s.type.variables.get(field))
				for (ssuffix : tmplist) {
					retval.add(field + ssuffix);
				}
			}
		}
		return retval;
	}

	def String serialize(FolFormula e, SimpleTypeReference ctx) {
		serialize(e, ctx, new HashMap<Symbol, String>());
	}

	def String serialize(FolFormula e, SimpleTypeReference ctx, Map<Symbol, String> map) {
		var String retval = "";
		if (e.getOp() !== null &&
			(e.getOp().equals("=>") || e.getOp().equals("<=>") || e.getOp().equals("&&") || e.getOp().equals("||"))) {
			retval = '''«serialize(e.left,ctx,map)»  «convertOp(e.op)»  «serialize(e.right,ctx,map)» ''';
		} else if (e instanceof AtomicExpression) {
			if (e.rel === RelationKind.EQ) {
				var suff = getSuffix(e.left, ctx);
				if (suff.empty) {
					retval = '''«serialize(e.left,ctx,map)»«e.rel.toString»  «serialize(e.right,ctx,map)»'''
				} else {
					retval = '''«FOR suffix : suff SEPARATOR " & "» «serialize(e.left,ctx,map)»«suffix» «e.rel.toString»  «serialize(e.right,ctx,map)»«suffix» «ENDFOR»'''
				}
			} else {
				retval = ''' «serialize(e.left,ctx,map)» «e.rel.toString»  «serialize(e.right,ctx,map)» ''';
			}
		} else if (e instanceof Addition) {
			retval = ''' «serialize(e.left,ctx,map)» «e.sign» «serialize(e.right,ctx,map)»'''
		} else if (e instanceof Multiplication) {
			retval = ''' «serialize(e.left,ctx,map)» «e.sign» «serialize(e.right,ctx,map)»'''
		} else if (e instanceof TermMemberSelection) {
			// TODO this is a quick hack
			if (e.receiver instanceof SymbolReferenceTerm &&
				(e.receiver as SymbolReferenceTerm).symbol instanceof NamedType) {
				var typename = qnp.getFullyQualifiedName((e.receiver as SymbolReferenceTerm).symbol as NamedType).
					toString();
				
				var literalname = serialize(e.member, ctx, map);
				retval = '''«typename.replaceAll("\\.","_dot_")»_dot_«literalname»'''
			} else {

				// get the data field of the data port
				if (isCarrierAccess(e.member)) {
					retval = '''«serialize(e.receiver,ctx,map)»'''
				} else {
					retval = '''«serialize(e.receiver,ctx,map)».«serialize(e.member,ctx,map)»'''
				}

			}
		} else if (e instanceof SymbolReferenceTerm) {
			if (map.containsKey(e.symbol)) {
				retval = map.get(e.symbol);
			} else {
				retval = e.symbol.name;
			}
		} else if (e instanceof TailedExpression) {
			var prefix = serialize(e.left, ctx, map);
			var taile = e.tail;
			var String tails = "";
			if (taile instanceof TupleConstructor) {
				tails = '''( «FOR tailelem : taile.elements SEPARATOR ','» «serialize(tailelem,ctx,map)» «ENDFOR» )'''
				// add to queue of nodes to be generated
				if (e.left instanceof SymbolReferenceTerm &&
					(e.left as SymbolReferenceTerm).symbol instanceof SymbolDeclaration) {
					var symbol = (e.left as SymbolReferenceTerm).symbol as SymbolDeclaration;
					functional_nodes.put(symbol.name, symbol);
				}
			}
			retval = prefix + tails;
		} else if (e instanceof ParenthesizedTerm) {
			retval = '''( «serialize(e.sub,ctx,map)» )'''
		} else if (e instanceof IteTermExpression) {

			if (e.right === null) {
				retval = '''( «serialize(e.condition,ctx,map)» -> «serialize(e.left,ctx,map)» )'''
			} else {
				retval = '''( «serialize(e.condition,ctx,map)» ? «serialize(e.left,ctx,map)» : «serialize(e.right,ctx,map)»'''
			}
		} else if (e instanceof CaseTermExpression) {

			retval = '''
				case 
					«FOR index : 0..e.cases.size-1 SEPARATOR ";\n" AFTER ";\n"»«serialize(e.cases.get(index),ctx,map)» : «serialize(e.expressions.get(index),ctx,map)»«ENDFOR»
				esac
			'''
		} else if (e instanceof SequenceTerm) {
			retval = '''( «serialize(e.^return,ctx,map)» )'''
		} else if (e instanceof SignedAtomicFormula) {
			if (e.neg) {
				retval = retval + "not";
			}
			retval = retval + serialize(e.left, ctx, map);
		} else if (e instanceof NumberLiteral) {
			if (e.isNeg) {
				retval += "-";
			}
			retval = e.value.toString;
		} else if (e instanceof TruthValue) {
			if(e.TRUE) retval = "true" else retval = "false";
		}
		return retval;
	}

	def serializeFunctionalNode(SymbolDeclaration sd) {

		var type = sd.type;
		if (type instanceof FunctionType) {
			if (sd.definition !== null) {
				var lambda = (sd.definition.left as LambdaExpression)
				var expr = lambda.definition as SequenceTerm
				return 
				'''
					node «sd.name» ( «FOR v : lambda.parameters SEPARATOR ';'» «v.name» : «toLustreName(v.type)»«ENDFOR» )
					returns (_return : «toLustreName(lambda.returnType)» )
					«IF expr.defs.size >0»
						var
						«FOR s : expr.defs SEPARATOR '; \n' AFTER ';'» «s.name» : «toLustreName(s.type)» «ENDFOR»
					«ENDIF»
					let
					_return = «serialize(expr.^return, null)» ;
					tel
				'''
			} else {
				var domain = type.domain;
				var range = type.range;
				var parameters = new ArrayList<SymbolDeclaration>() ;
				
				if (domain instanceof SimpleTypeReference) {
					var p = ImlCustomFactory.INST.createSymbolDeclaration("_x0_", domain) ;
					parameters.add(p) ; 
				} else if (domain instanceof TupleType) {
					var index = 0 ;
					for(t : domain.types) {
						var p = ImlCustomFactory.INST.createSymbolDeclaration("_x" + index + "_", t) ;
						parameters.add(p) ; 
					}
				}
				return 
				'''
					node «toLustreName(sd)» ( «FOR v : parameters SEPARATOR ';'» «v.name» : «toLustreName(sd.type)»«ENDFOR» )
					returns (_return : «toLustreName(range)» )
					let
					tel
				'''

			}

		}
		
		return "" 

	}

	def isPort(TermExpression e) {
		if (e instanceof SymbolReferenceTerm) {
			if (e.symbol instanceof SymbolDeclaration) {
				if (ImlUtil.exhibits((e.symbol as SymbolDeclaration).type,
					( stdLibs.getNamedType("iml.systems", "Port") as Trait))) {
					return true;
				}
			}
		}
		return false;
	}

	def isCarrierAccess(TermExpression e) {
		var data = ImlUtil.findSymbol(stdLibs.getNamedType("iml.systems", "DataCarrier"), "data")
		if (e instanceof SymbolReferenceTerm) {
			if ((e as SymbolReferenceTerm).symbol instanceof SymbolDeclaration) {
				if (((e as SymbolReferenceTerm).symbol as SymbolDeclaration) == data) {
					return true
				}
			}
		}
		return false;
	}

	def String convertOp(String op) {
		switch (op) {
			case "&&": "and"
			case "||": "or"
			case "=>": "=>"
			case "<=>": "<=>"
		}
	}

	def String toLustreName(LustreNode m) {
		if (m == LustreModel.Bool) {
			return "bool"
		}
		if (m == LustreModel.Int) {
			return "int";
		}
		if (m == LustreModel.Real) {
			return "real";
		}
		return '''«m.name.replaceAll("\\.","_dot_")»'''
	}

	def String toLustreName(LustreNode m, String literal) {
		'''"«toLustreName(m)»_dot_«literal»"'''
	}

	def String toLustreName(SymbolDeclaration sd) {
		'''«qnp.getFullyQualifiedName(sd).toString().replaceAll("\\.","_dot_")»'''
	}

	def String toLustreName(ImlType t) {
		if (t instanceof SimpleTypeReference) {
			if (t.type === stdLibs.boolType) return "bool" ;
			if (t.type === stdLibs.intType) return "int";
			if (t.type === stdLibs.realType) return "real";
		}
		'''«ImlUtil.getTypeName(t,qnp).replaceAll("\\.","_dot_")»'''
	}

	def List<String> getSuffix(FolFormula e, SimpleTypeReference ctx) {
		if (e instanceof TermExpression) {
			return getSuffix(e as TermExpression, ctx);
		} else {
			var retval = new ArrayList<String>();
			retval.add("");
			return retval;
		}
	}

	def List<String> getSuffix(TermExpression e, SimpleTypeReference tr) {
		var retval = new ArrayList<String>();
		var ctx = new TypingEnvironment(tr);
		var t = typeProvider.termExpressionType(e, ctx);
		retval.addAll(getSuffix("", t, tr))
		return retval;
	}

	def List<String> getSuffix(String sofar, ImlType t, SimpleTypeReference tr) {
		var retval = new ArrayList<String>();
		var ctx = new TypingEnvironment(tr);
		if (t instanceof SimpleTypeReference) {
			ctx.addContext(t);
			for (s : t.type.symbols) {
				if (! (s instanceof Assertion)) {
					var boundtype = ctx.bind(s.type);
					if (qnp.getFullyQualifiedName((boundtype as SimpleTypeReference).type).toString().equals(
						"iml.lang.Bool") ||
						qnp.getFullyQualifiedName((boundtype as SimpleTypeReference).type).toString().equals(
							"iml.lang.Int")) {
						retval.add(sofar + "." + s.name);
					} else {
						retval.addAll(getSuffix(sofar + "." + s.name, boundtype, tr));
					}

				}
			}

		}
		return retval;
	}

}
