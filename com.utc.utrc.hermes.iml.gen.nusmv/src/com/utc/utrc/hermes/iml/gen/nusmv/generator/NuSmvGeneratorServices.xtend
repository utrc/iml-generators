package com.utc.utrc.hermes.iml.gen.nusmv.generator

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvElementType
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModule
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvSymbol
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvTypeInstance
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.Assertion
import com.utc.utrc.hermes.iml.iml.AtomicExpression
import com.utc.utrc.hermes.iml.iml.CaseTermExpression
import com.utc.utrc.hermes.iml.iml.FolFormula
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.IteTermExpression
import com.utc.utrc.hermes.iml.iml.LambdaExpression
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
import com.utc.utrc.hermes.iml.lib.LtlServices
import com.utc.utrc.hermes.iml.lib.SystemsServices
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import com.utc.utrc.hermes.iml.typing.TypingEnvironment
import com.utc.utrc.hermes.iml.util.ImlUtil
import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.List
import java.util.Map
import org.eclipse.xtext.naming.IQualifiedNameProvider
import com.utc.utrc.hermes.iml.lib.ContractsServices
import com.utc.utrc.hermes.iml.lib.SmsServices

class NuSmvGeneratorServices {

	@Inject
	ImlTypeProvider typeProvider;

	@Inject
	IQualifiedNameProvider qnp;

	@Inject
	LtlServices ltl_srv ;
	@Inject
	SystemsServices sys_srv ;
	@Inject
	ContractsServices ctr_srv;
	@Inject
	SmsServices sms_srv ;

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
		return rec + "." + mem;
	}

	def String getNameFor(SymbolReferenceTerm s) {
		return s.getSymbol().getName();
	}

	def String serialize(NuSmvModel m) {
		'''«FOR mod : m.modules.values AFTER '\n'»«serialize(mod)»«ENDFOR»'''
	}

	def String serialize(NuSmvModule m) {
		if(m.name.equals("iml.lang.Bool") || m.name.equals("iml.lang.Int") || m.name.equals("iml.lang.Real") ||
			m.name.equals("___EMPTY___")) return "";
		if(m.isEnum()) return "";
		'''
			MODULE «toNuSmvName(m)» «FOR p : m.parameters BEFORE '(' SEPARATOR ',' AFTER ')'» «p.name» «ENDFOR»
			«FOR v : m.variables.values AFTER '\n'»«serialize(v)»«ENDFOR» 
			«FOR v : m.inits.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.trans.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.definitions.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.invar.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.specs.values AFTER '\n'»«serialize(v)»«ENDFOR»
		'''
	}

	def String serialize(NuSmvSymbol s) {
		switch (s.elementType) {
			case VAR: '''
				VAR «s.name» : «serialize(s.type)» «FOR p : s.type.params BEFORE '(' SEPARATOR ',' AFTER ')'»«p.name»«ENDFOR» ;
			'''
			case DEFINE: '''
				DEFINE «s.name» := «s.definition» ;
			'''
			case INIT: '''
				DEFINE «s.name» := «s.definition» ;
				INIT «s.name» ;
			'''
			case INVAR: '''
				INVAR «s.definition»  ;
			'''
			case TRANSITION: '''
				DEFINE «s.name» := «s.definition» ;
				TRANS «s.name» ;
			'''
			case LTLSPEC: '''
				LTLSPEC NAME «s.name» := «s.definition» ; 
			'''
		}
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

	def String serialize(NuSmvTypeInstance i) {
		if (! i.type.enum) {
			return toNuSmvName(i.type);
		}
		'''«FOR l : i.type.literals BEFORE '{' SEPARATOR ',' AFTER '}'»«toNuSmvName(i.type,l)»«ENDFOR»'''
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
				retval = '''"«typename».«literalname»"'''
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
				retval = retval + "!";
			}
			retval = retval + serialize(e.left, ctx, map);
		} else if (e instanceof NumberLiteral) {
			retval = e.value.toString;
		} else if (e instanceof TruthValue) {
			if(e.TRUE) retval = "TRUE" else retval = "FALSE";
		}
		return retval;
	}

	def String ltlserialize(FolFormula e, SimpleTypeReference ctx, Map<Symbol, String> map) {

		// We assume e is an ltl formula
		if (e instanceof SymbolReferenceTerm) {
			if (e.symbol === ltl_srv.ltlTrueSymbol) {
				return "TRUE";
			} else {
				val prop_symbol = e.symbol;
				if (prop_symbol instanceof SymbolDeclaration) {
					val prop_def = getLambdaFromDef(prop_symbol.definition);
					if (prop_def !== null) {
						// remap whatever parameter to inst.state
						val param = prop_def.parameters.get(0);
						map.put(param, "inst.state");
						return serialize(prop_def.definition, ctx, map);
					}
				}
			}
		} else if (e instanceof SignedAtomicFormula) {
			val s = ltlserialize(e.left, ctx, map)
			if (e.neg) {
				return " ! " + s
			} else {
				return s
			}
		} else if (e instanceof TailedExpression) {
			val head = e.left;
			val tail = (e.tail as TupleConstructor);
			if (head instanceof SymbolReferenceTerm) {
				if (head.symbol === ltl_srv.ltlAtomSymbol) {
					val prop = tail.elements.get(0);
					return ltlserialize(prop, ctx, map)
				} else if (head.symbol === ltl_srv.ltlNotSymbol) {
					return " ! " + ltlserialize(tail.elements.get(0), ctx, map)
				} else if (head.symbol === ltl_srv.ltlAndSymbol) {
					return "(" + ltlserialize(tail.elements.get(0), ctx, map) + " & " +
						ltlserialize(tail.elements.get(1), ctx, map) + ")"
				} else if (head.symbol === ltl_srv.ltlOrSymbol) {
					return "(" + ltlserialize(tail.elements.get(0), ctx, map) + " | " +
						ltlserialize(tail.elements.get(1), ctx, map) + ")"
				} else if (head.symbol === ltl_srv.ltlImpliesSymbol) {
					return "(" + ltlserialize(tail.elements.get(0), ctx, map) + " -> " +
						ltlserialize(tail.elements.get(1), ctx, map) + ")"
				} else if (head.symbol === ltl_srv.ltlXSymbol) {
					return "X" + ltlserialize(tail.elements.get(0), ctx, map)
				} else if (head.symbol === ltl_srv.ltlUSymbol) {
					return "(" + ltlserialize(tail.elements.get(0), ctx, map) + " U " +
						ltlserialize(tail.elements.get(1), ctx, map) + ")"
				} else if (head.symbol === ltl_srv.ltlFSymbol) {
					return "F" + ltlserialize(tail.elements.get(0), ctx, map)
				} else if (head.symbol === ltl_srv.ltlGSymbol) {
					return "G" + ltlserialize(tail.elements.get(0), ctx, map)
				}
			}
		}
		return "NOT_AN_LTL_SYMBOL";
	}

	def isPort(TermExpression e) {
		if (e instanceof SymbolReferenceTerm) {
			if (e.symbol instanceof SymbolDeclaration) {
				return sys_srv.isPort((e.symbol as SymbolDeclaration).type);
			}
		}
		return false;
	}

	def isCarrierAccess(TermExpression e) {
		if (e instanceof SymbolReferenceTerm) {
			if ((e as SymbolReferenceTerm).symbol instanceof SymbolDeclaration) {
				if (((e as SymbolReferenceTerm).symbol as SymbolDeclaration) == sys_srv.dataCarrierDataVar) {
					return true
				}
			}
		}
		return false;
	}

	def String convertOp(String op) {
		switch (op) {
			case "&&": "&"
			case "||": "|"
			case "=>": "->"
			case "<=>": "<->"
		}
	}

	def String toNuSmvName(NuSmvModule m) {
		if (m == NuSmvModel.Bool) {
			return "boolean"
		}
		if (m == NuSmvModel.Int) {
			return "0..256";
		}
		if (m.name.equals("main")) {
			return "main";
		}
		return '''"«m.name»"'''
	}

	def String toNuSmvName(NuSmvModule m, String literal) {
		'''"«m.name».«literal»"'''
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

	def boolean isRefinement(SymbolDeclaration query) {
		val qdef = query.getDefinition();
		if (qdef !== null) {
			if (qdef instanceof SignedAtomicFormula) {
				val atom = qdef.left;
				if (atom instanceof SymbolReferenceTerm) {
					if (atom.symbol === ctr_srv.isRefinementSymbol) {
						return true;
					}
				}
			}
		}
		return false;
	}

	def SimpleTypeReference getSpec(SymbolDeclaration query) {
		if (isRefinement(query)) {
			val spectype = ( query.definition.left as SymbolReferenceTerm).typeBinding.get(0);
			if (spectype instanceof SimpleTypeReference) {
				return spectype;
			}
			throw new NuSmvGeneratorException(query,
				"Could not retrieve the specification contract because it is not a simple type reference.");
		}
		throw new NuSmvGeneratorException(query,
			"Could not retrieve the specification contract because the query is not a refinement.");
	}

	def SimpleTypeReference getImpl(SymbolDeclaration query) {
		if (isRefinement(query)) {
			val impltype = ( query.definition.left as SymbolReferenceTerm).typeBinding.get(1);
			if (impltype instanceof SimpleTypeReference) {
				return impltype;
			}
			throw new NuSmvGeneratorException(query,
				"Could not retrieve the implementation contract because it is not a simple type reference.");
		}
		throw new NuSmvGeneratorException(query,
			"Could not retrieve the implementation contract because the query is not a refinement.");
	}

	def boolean isLTL(SymbolDeclaration query) {
		val qdef = query.getDefinition();
		if (qdef !== null) {
			if (qdef instanceof SignedAtomicFormula) {
				val atom = qdef.left;
				if (atom instanceof TailedExpression) {
					if (atom.left instanceof SymbolReferenceTerm) {
						if (( atom.left as SymbolReferenceTerm).symbol === sms_srv.smSatSymbol) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	def SimpleTypeReference getSmsFromLtlQuery(SymbolDeclaration query) {
		if (isLTL(query)) {
			val ref = (query.definition.left.left as SymbolReferenceTerm).typeBinding.get(1);
			if (ref instanceof SimpleTypeReference) {
				return ref;
			}
			throw new NuSmvGeneratorException(query,
				"The transition system does not seem to be a simple type reference");
		}
		throw new NuSmvGeneratorException(query, "Could not return the LTL formula because the query is not smSat.")
	}

	def FolFormula getLtlFormula(SymbolDeclaration query) {
		if (isLTL(query)) {
			return ((query.definition.left as TailedExpression).tail as TupleConstructor).elements.get(0);
		}
		throw new NuSmvGeneratorException(query, "Could not return the LTL formula because the query is not smSat.")
	}

	def LambdaExpression getLambdaFromDef(FolFormula f) {
		if (f instanceof SignedAtomicFormula) {
			return getLambdaFromDef(f.left);
		} else if (f instanceof LambdaExpression) {
			return f;
		}
		return null;
	}

	def List<NuSmvSymbol> generateRefSpec(String spec_name, String inst_name, Collection<String> sub_names,
		NuSmvTypeInstance binst) {
		var retval = new ArrayList<NuSmvSymbol>();

		if (sub_names.empty) {
			
			//check direct refinement
			var a_ltlspec = new NuSmvSymbol("_assumption_spec");
			var g_ltlspec = new NuSmvSymbol("_guarantee_spec");
			a_ltlspec.setType(binst);
			a_ltlspec.setElementType(NuSmvElementType.LTLSPEC);
			g_ltlspec.setType(binst);
			g_ltlspec.setElementType(NuSmvElementType.LTLSPEC);
			
			a_ltlspec.definition = '''G(«spec_name».assumption) -> G(«inst_name».assumption)'''
			g_ltlspec.definition = '''G(«inst_name».guarantee) -> G(«spec_name».guarantee)'''
			retval.add(a_ltlspec);
			retval.add(g_ltlspec);
		} else {

			for (sub : sub_names) {
				// generate an LTL spec
				val sub_names_n = sub_names;
				sub_names_n.remove(sub);
				var ltlspec = new NuSmvSymbol(sub + "_assumption");
				ltlspec.setType(binst);
				ltlspec.setElementType(NuSmvElementType.LTLSPEC);
				ltlspec.definition = '''
					«FOR sub1 : sub_names_n BEFORE "G(" + spec_name + ".assumption)" SEPARATOR "&" AFTER " => " + "G(" + inst_name + "." + sub + ".assumption) ;"»
						G(«inst_name».«sub1».assumption => «inst_name».«sub1».guarantee)
					«ENDFOR»
				''';
				retval.add(ltlspec);
			}

		}
		return retval;
	}

}
