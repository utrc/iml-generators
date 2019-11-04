package com.utc.utrc.hermes.iml.gen.nusmv.generator

import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModule
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvSymbol
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvTypeInstance
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.AtomicExpression
import com.utc.utrc.hermes.iml.iml.CaseTermExpression
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.FolFormula
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.IteTermExpression
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.Multiplication
import com.utc.utrc.hermes.iml.iml.NumberLiteral
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm
import com.utc.utrc.hermes.iml.iml.SequenceTerm
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm
import com.utc.utrc.hermes.iml.iml.TermMemberSelection
import com.utc.utrc.hermes.iml.iml.TruthValue
import java.util.List
import java.util.ArrayList
import com.utc.utrc.hermes.iml.iml.RelationKind
import com.utc.utrc.hermes.iml.iml.TermExpression
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import com.utc.utrc.hermes.iml.iml.Assertion
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.typing.TypingEnvironment
import com.utc.utrc.hermes.iml.util.ImlUtil
import org.eclipse.xtext.naming.IQualifiedNameProvider
import com.utc.utrc.hermes.iml.iml.InstanceConstructor
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration
import java.util.Map
import com.utc.utrc.hermes.iml.iml.LambdaExpression
import com.utc.utrc.hermes.iml.iml.Symbol
import java.util.HashMap
import com.utc.utrc.hermes.iml.lib.ImlStdLib
import com.utc.utrc.hermes.iml.iml.Trait

class NuSmvGeneratorServices {

	@Inject
	private ImlTypeProvider typeProvider;

	@Inject
	private IQualifiedNameProvider qnp;

	@Inject
	private ImlStdLib stdLibs;

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
		if(m.name.equals("iml.lang.Bool") || m.name.equals("iml.lang.Int") || m.name.equals("iml.lang.Real")) return "";
		if(m.isEnum()) return "";
		'''
			MODULE «toNuSmvName(m)» «FOR p : m.parameters BEFORE '(' SEPARATOR ',' AFTER ')'» «p.name» «ENDFOR»
			«FOR v : m.variables.values AFTER '\n'»«serialize(v)»«ENDFOR» 
			«FOR v : m.inits.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.trans.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.definitions.values AFTER '\n'»«serialize(v)»«ENDFOR»
			«FOR v : m.invar.values AFTER '\n'»«serialize(v)»«ENDFOR»
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
				}else {
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
			if (e.isNeg) {
				retval += "-";
			}
			retval = e.value.toString;
		} else if (e instanceof TruthValue) {
			if(e.TRUE) retval = "TRUE" else retval = "FALSE";
		}
		return retval;
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
		if (m.name.equals("main")){
			return "main" ;
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

}
