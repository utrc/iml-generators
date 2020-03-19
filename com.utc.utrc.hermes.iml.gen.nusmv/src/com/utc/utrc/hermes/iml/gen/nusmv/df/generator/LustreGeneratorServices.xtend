package com.utc.utrc.hermes.iml.gen.nusmv.df.generator

import com.google.common.collect.Maps
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreModel
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreNode
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreSymbol
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreVariable
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvSymbol
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvTypeInstance
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.Assertion
import com.utc.utrc.hermes.iml.iml.AtomicExpression
import com.utc.utrc.hermes.iml.iml.CaseTermExpression
import com.utc.utrc.hermes.iml.iml.EnumRestriction
import com.utc.utrc.hermes.iml.iml.FloatNumberLiteral
import com.utc.utrc.hermes.iml.iml.FolFormula
import com.utc.utrc.hermes.iml.iml.FunctionType
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.IteTermExpression
import com.utc.utrc.hermes.iml.iml.LambdaExpression
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.Multiplication
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.NumberLiteral
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm
import com.utc.utrc.hermes.iml.iml.QuantifiedFormula
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
import com.utc.utrc.hermes.iml.iml.TupleType
import com.utc.utrc.hermes.iml.lib.ImlStdLib
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import com.utc.utrc.hermes.iml.typing.TypingEnvironment
import com.utc.utrc.hermes.iml.util.ImlUtil
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.naming.IQualifiedNameProvider

class LustreGeneratorServices {

	@Inject
	private ImlTypeProvider typeProvider;

	@Inject
	private IQualifiedNameProvider qnp;

	@Inject
	private ImlStdLib stdLibs;

	private Map<String, SymbolReferenceTerm> functional_nodes ;
	private Map<String, SymbolDeclaration> global_constants ;

	private Map<String, String> lustre2Iml;
	private List<String> nodeCallOrder;
	
	new() {
		functional_nodes = Maps.newHashMap()
		global_constants = Maps.newHashMap();
	}

	def String getNameFor(SimpleTypeReference tr) {
		var String name = ImlUtil.getTypeName(tr, qnp);
		name = typeNameWithReplacement(name);
		return name;
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


	def setAuxiliaryDS(Map<String, String> LustreMapIml, List<String> callOrder) {
		lustre2Iml = LustreMapIml;
		nodeCallOrder = callOrder;
	}


	def String serialize(LustreModel m) {
		var types = '''«FOR n : m.nodes.values AFTER '\n'»«serializeType(n)»«ENDFOR»''';
		var constants = serializeGlobalConstants();
		var functions = serializeFunctionalNodes();
		var nodes = serializeNodes(m);
		return types + constants + functions + nodes;
	}

	def String serializeEnum(LustreNode m, String l) {
		var String imlName = m.name + "." + l;
		var String name = toLustreName(m) + "_dot_" +l;
		if (!lustre2Iml.containsKey(name)) {
			lustre2Iml.put(name, imlName)
		}
		return name
	}

	def String serializeLustreSymbol(LustreSymbol f) {
		var String res = f.name;
		
		var String imlRes = f.container.name;
		var String lustreName = typeNameWithReplacement(imlRes);
		lustreName = lustreName.replaceAll("\\.","_dot_");
		imlRes = imlRes + "." + f.name;
		lustreName = lustreName + "." + f.name;
		
		if (!lustre2Iml.containsKey(lustreName)) {
			lustre2Iml.put(lustreName, imlRes);
		}
		return res;
	}

	def String serializeLustreVariable(LustreVariable f) {
		var String res = f.name;
//		if (!lustre2Iml.containsKey(res)) {
//			lustre2Iml.put(res, res);
//		}
		return res;
	}

	def String serializeLustreSymbol(LustreSymbol fType, LustreSymbol fReturn) {
		var String imlName = fType.container.name + "." + fType.name + "." + fReturn.name;
		var String lustreName = imlName.replaceAll("\\.","_dot_")
		var String res = fType.name + "_" + fReturn.name;
		if (!lustre2Iml.containsKey(lustreName)) {
			lustre2Iml.put(lustreName, imlName);
		}
		return res;
	}

	def String serializeType(LustreNode m) {
		if(m.name.equals("iml.lang.Bool") || m.name.equals("iml.lang.Int") || m.name.equals("iml.lang.Real")) return "";
		if(m.isEnum()) {
			return 
			'''
			type «toLustreName(m)» = enum {
			    «FOR l : m.literals SEPARATOR ',\n'»«serializeEnum(m, l)»«ENDFOR»
			};
			''';
		}
		if(m.isStruct()) {
			return 
			'''
			type «toLustreName(m)» = struct { 
			    «FOR f : m.variables.values SEPARATOR ';\n'»«serializeLustreSymbol(f)» : «toLustreName(f.type.type)»«ENDFOR»
			};
			
			'''
		} 
	}
	
	def String serializeGlobalConstants() {
		var nodes = "";
		var closed = new ArrayList<String>() ;
		while(global_constants.size > 0) {
			var fname = global_constants.keySet.get(0) ;
			if (! closed.contains(fname)) { 
				var togen = global_constants.get(fname);
				nodes = nodes + serializeGlobalConstant(togen);
			}
			global_constants.remove(fname) ;
			closed.add(fname);
		}
		return nodes + "\n" ;		
	}
	
	def String serializeFunctionalNodes() {
		var nodes = "";
		var closed = new ArrayList<String>() ;
		while(functional_nodes.size > 0) {
			var fname = functional_nodes.keySet.get(0) ;
			if (! closed.contains(fname)) { 
				var togen = functional_nodes.get(fname);
				nodes = nodes + serializeFunctionalNode(togen) + "\n";
			}
			functional_nodes.remove(fname) ;
			closed.add(fname);
		}
		return nodes;
	}

	def String serializeNodes(LustreModel m) {
		var nodes = "";
		for (var i = nodeCallOrder.size - 1; i >= 0; i--) {
			if (m.nodes.keySet.contains(nodeCallOrder.get(i))) {
				nodes = nodes + serializeNode(m.nodes.get(nodeCallOrder.get(i))) + "\n";
			}
		} 
		return nodes;
	}


	def String serializeNode(LustreNode m) {
		if(m.name.equals("iml.lang.Bool") || m.name.equals("iml.lang.Int") || m.name.equals("iml.lang.Real")) return "";
		if(m.isEnum() || m.isStruct()) return "";
		var nodes = 
		'''
			node «needImported(m)»«toLustreName(m)» «FOR p : m.parameters BEFORE '(' SEPARATOR '; ' AFTER ')'»«serializeLustreSymbol(p)» : «p.type.type.toLustreName»«ENDFOR»
			returns «FOR v : m.returns BEFORE '(' SEPARATOR '; ' AFTER ')'»«serializeLustreSymbol(v)» : «v.type.type.toLustreName»«ENDFOR»
			«IF (m.variables.size > 0 || m.fields.size > 0 || m.components.size > 0)»
			«IF (isContract(m))»
			(*@contract
				«FOR v : m.fields.values»
		    		«IF (!(serializeLustreSymbol(v).equals("assumption")) && !(serializeLustreSymbol(v).equals("guarantee")))»
			    		«IF (!(v.assume || v.guarantee))»    var «serializeLustreSymbol(v)» : «v.type.type.toLustreName»«IF v.definition !== null» = («v.definition»);«ENDIF»
			    		«ENDIF»
			    	«ENDIF»
				«ENDFOR»
				«FOR v : m.fields.values»
		    		«IF (!(serializeLustreSymbol(v).equals("assumption")) && !(serializeLustreSymbol(v).equals("guarantee")))»
			    		«IF (v.assume || v.guarantee)»
							«IF (v.assume)»    assume«ELSE»    guarantee«ENDIF» "«serializeLustreSymbol(v)»" «v.definition»;
						«ENDIF»
			    	«ENDIF»
				«ENDFOR»
			*) 
			«ENDIF»
			«FOR v : m.components.values» 
			«FOR p : v.type.type.returns» 
				«IF (v.type.outParams.size > v.type.type.returns.indexOf(p))»
					«IF (v.type.outParams.get(v.type.type.returns.indexOf(p)).name == (v.name + "_" + p.name))» 
						var «serializeLustreSymbol(v, p)» : «p.type.type.toLustreName»; 
					«ENDIF»
					«IF (v.type.outParams.get(v.type.type.returns.indexOf(p)).name.equals("__NOT__SET__"))» 
						var «v.name»_«p.name» : «p.type.type.toLustreName»; 
					«ENDIF»					
				«ELSE»
					var «serializeLustreSymbol(v, p)» : «p.type.type.toLustreName»;
				«ENDIF»				
			«ENDFOR»
			«ENDFOR»
			«FOR v : m.variables.values»
			var «v.name» : «v.type.type.toLustreName»;
			«ENDFOR»
			«ENDIF»
			«IF (m.components.size > 0) || m.lets.size > 0 || m.variables.size > 0»
			let
			«FOR v : m.variables.values»«IF (v.definition !== null)»    «v.name» = «v.definition»; «ENDIF»
			«ENDFOR»			
			«FOR l : m.lets.values»    «l.definition»;«'\n'»«ENDFOR»
			«FOR v : m.components.values AFTER '    --%MAIN; \n'»    («FOR p : v.type.outParams SEPARATOR ', '»«IF (p.name.equals("__NOT__SET__"))»«v.name»_«(v.type.type.returns.get(v.type.outParams.indexOf(p))).name»«ELSE»«serializeLustreVariable(p)»«ENDIF»«ENDFOR»«FOR p : v.type.type.returns»«IF (v.type.type.returns.indexOf(p) >= v.type.outParams.size)», «v.name»_«p.name»«ENDIF»«ENDFOR») = «v.type.type.toLustreName» («FOR param : v.type.params SEPARATOR ','» «serializeLustreVariable(param)»«ENDFOR»);«'\n '» 
			«ENDFOR»
			tel
			«ENDIF»
		'''
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


	def String serialize(FolFormula e, SimpleTypeReference ctx, String sp) {
		serialize(e, ctx, new HashMap<Symbol, String>(), sp);
	}


	def String serialize(FolFormula e, SimpleTypeReference ctx, Map<Symbol, String> map, String sp) {
		var String retval = "";
		if (e.getOp() !== null &&
			(e.getOp().equals("=>") || e.getOp().equals("<=>") || e.getOp().equals("&&") || e.getOp().equals("||"))) {
			retval = '''«serialize(e.left, ctx, map, sp)» «convertOp(e.op)» «serialize(e.right, ctx, map, sp)»''';
		} else if (e instanceof AtomicExpression) {
			if (e.rel === RelationKind.EQ) {
				var suff = getSuffix(e.left, ctx);
				if (suff.empty) {
					retval = '''«serialize(e.left, ctx, map, sp)» «e.rel.toString» «serialize(e.right, ctx, map, sp)»'''
				} else {
					retval = '''«FOR suffix : suff SEPARATOR " & "» «serialize(e.left, ctx, map, sp)»«suffix» «e.rel.toString» «serialize(e.right, ctx, map, sp)»«suffix»«ENDFOR»'''
				}
			} else {
				retval = '''«serialize(e.left, ctx, map, sp)» «IF e.rel.toString.equals("!=")» <> «ELSE» «e.rel.toString» «ENDIF»«serialize(e.right, ctx, map, sp)»''';
			}
		} else if (e instanceof Addition) {
			retval = '''«serialize(e.left, ctx, map, sp)» «e.sign» «serialize(e.right, ctx, map, sp)»'''
		} else if (e instanceof Multiplication) {
			retval = '''«serialize(e.left, ctx, map, sp)» «e.sign» «serialize(e.right, ctx, map, sp)»'''
		} else if (e instanceof TermMemberSelection) {
			var isEnum = false
			if (e.receiver instanceof SymbolReferenceTerm &&
				(e.receiver as SymbolReferenceTerm).symbol instanceof NamedType) {
				var rcvAsNT = (e.receiver as SymbolReferenceTerm).symbol as NamedType
				var typename = qnp.getFullyQualifiedName(rcvAsNT).toString();				
				var literalname = serialize(e.member, ctx, map, sp);
				if (rcvAsNT.restriction instanceof EnumRestriction) {
					isEnum = (rcvAsNT.restriction as EnumRestriction).enum
				}
				retval = toLustreName(typename, literalname, isEnum)
			} else {
				if (e.receiver instanceof SymbolReferenceTerm &&
				(e.receiver as SymbolReferenceTerm).symbol instanceof SymbolDeclaration) {
					var sdname = qnp.getFullyQualifiedName((e.receiver as SymbolReferenceTerm).symbol as SymbolDeclaration).toString();				
					var literalname = serialize(e.member, ctx, map, sp);
					toLustreName(sdname, literalname, isEnum);
				}				
				retval = '''«serialize(e.receiver, ctx, map, sp)»«sp»«serialize(e.member, ctx, map, sp)»'''
			}
		} else if (e instanceof SymbolReferenceTerm) {
			if (map.containsKey(e.symbol)) {
				retval = map.get(e.symbol);
			} else {	
				retval = e.symbol.name;
				var String imlRetval = qnp.getFullyQualifiedName(e.symbol).toString()
				var lustreRetval = imlRetval.replaceAll("\\.","_dot_");
				if (!lustre2Iml.containsKey(lustreRetval)) {
					lustre2Iml.put(lustreRetval, imlRetval);
				}
				map.put(e.symbol, retval);				
				if (e.symbol.eContainer instanceof Model) {	// Global
						global_constants.put(retval, e.symbol as SymbolDeclaration);
				}
			}
		} else if (e instanceof TailedExpression) {
			var String prefix;
			if (e.left instanceof SymbolReferenceTerm) {
				prefix = toLustreName(e.left as SymbolReferenceTerm);
			} else {
				prefix = serialize(e.left, ctx, map, sp);	
			}
			var taile = e.tail;
			var String tails = "";
			if (taile instanceof TupleConstructor) {
				var del = ",";
				var startSymbol = "(";
				if (e.left instanceof SymbolReferenceTerm &&
					(e.left as SymbolReferenceTerm).symbol instanceof SymbolDeclaration) {
					if (!isInit(e.left as SymbolReferenceTerm) && !isPre(e.left as SymbolReferenceTerm)) {			
						functional_nodes.put(prefix, (e.left as SymbolReferenceTerm));
								if (!nodeCallOrder.contains(prefix)) {
									nodeCallOrder.add(prefix);
								} else {
									nodeCallOrder.remove(prefix);
									nodeCallOrder.add(prefix);	// forget the old
								}						
					}
					if (isInit(e.left as SymbolReferenceTerm)) {			
						del = " -> ";
						prefix = "";
					}
					if (isPre(e.left as SymbolReferenceTerm)) {			
						startSymbol = "pre(";
						prefix = "";
					}
				}
				if (e.left instanceof TermMemberSelection &&
					(e.left as TermMemberSelection).member instanceof SymbolReferenceTerm) {
					functional_nodes.put(prefix, ((e.left as TermMemberSelection).member as SymbolReferenceTerm));
					if (!nodeCallOrder.contains(prefix)) {
						nodeCallOrder.add(prefix);
					} else {
						nodeCallOrder.remove(prefix);
						nodeCallOrder.add(prefix); // forget the old
					}						
					if (isAgreeAnnexNode(e.left as TermMemberSelection)) {
						prefix = agreeAnnexNodeName(e.left as TermMemberSelection);
					}
				}
				tails = '''«startSymbol»«FOR tailelem : taile.elements SEPARATOR del»«serialize(tailelem, ctx, map, sp)»«ENDFOR»)'''
				// add to queue of nodes to be generated
			}
			retval = prefix + tails;
		} else if (e instanceof ParenthesizedTerm) {
			retval = '''(«serialize(e.sub, ctx, map, sp)»)'''
		} else if (e instanceof IteTermExpression) {

			if (e.right === null) {
				retval = '''(«serialize(e.condition, ctx, map, sp)» => «serialize(e.left, ctx, map, sp)»)'''
			} else {
				retval = '''(if «serialize(e.condition, ctx, map, sp)» then «serialize(e.left, ctx, map, sp)» else «serialize(e.right, ctx, map, sp)»)'''
			}
		} else if (e instanceof CaseTermExpression) {

			retval = '''
				case 
					«FOR index : 0..e.cases.size-1 SEPARATOR ";\n" AFTER ";\n"»«serialize(e.cases.get(index), ctx, map, sp)» : «serialize(e.expressions.get(index), ctx, map, sp)»«ENDFOR»
				esac
			'''
		} else if (e instanceof SequenceTerm) {
			retval = '''(«serialize(e.^return, ctx, map, sp)»)'''
		} else if (e instanceof SignedAtomicFormula) {
			if (e.neg) {
				retval = retval + "not (";
			}
			retval = retval + serialize(e.left, ctx, map, sp)
			if (e.neg) {
				retval = retval + ")"
			}
		} else if (e instanceof NumberLiteral) {
			if (e.isNeg) {
				retval += "-";
			}
			retval += e.value.toString;
		} else if (e instanceof FloatNumberLiteral) {
			if (e.isNeg) {
				retval += "-";
			}
			retval += e.value.toString;
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
					node «sd.name»(«FOR v : lambda.parameters SEPARATOR '; '»«v.name» : «toLustreName(v.type)»«ENDFOR»)
					returns (_return : «toLustreName((type as FunctionType).range)»)
					«IF expr.defs.size >0»
						var
						«FOR s : expr.defs SEPARATOR '; \n' AFTER ';'»«s.name» : «toLustreName(s.type)»«ENDFOR»
					«ENDIF»
					let
					    _return = «serialize(expr.^return, null, ".")»;
					tel
				'''
			} else {
				if (isContainerAgreeAnnexNode(sd)) {
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
						node «containerAgreeAnnexNodeName(sd)»(«FOR v : parameters SEPARATOR '; '»«v.name» : «toLustreName(v.type)»«ENDFOR»)
						returns (_return : «toLustreName(range)»)
						let
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
						node «toLustreName(sd)»(«FOR v : parameters SEPARATOR '; '» «v.name» : «toLustreName(v.type)»«ENDFOR»)
						returns (_return : «toLustreName(range)»)
						let
						tel
					'''					
				}
			}

		}
		return "" 
	}

	def serializeGlobalConstant(SymbolDeclaration sd) {
		var type = sd.type;
		if (type instanceof SimpleTypeReference) {
			return 
			'''
			const «toLustreNameGlobal(sd)» : «toLustreName(type)» = «serialize(sd.definition, null, ".")»;
			'''
		}
		return "" 
	}

	def serializeFunctionalNode(SymbolReferenceTerm sr) {
		val TypingEnvironment te = new TypingEnvironment();
		te.addContext(sr);		
		var sd = sr.symbol as SymbolDeclaration;
		var type = sd.type;
		if (type instanceof FunctionType) {
			if (sd.definition !== null) {
				var lambda = (sd.definition.left as LambdaExpression)
				var expr = lambda.definition as SequenceTerm
				return 
				'''
					node «toLustreName(sd)»(«FOR v : lambda.parameters SEPARATOR '; '»«v.name» : «toLustreName(v.type)»«ENDFOR»)
					returns (_return : «toLustreName((type as FunctionType).range)»)
					«IF expr.defs.size >0»
						var
						«FOR s : expr.defs SEPARATOR '; \n' AFTER ';'»«s.name» : «toLustreName(s.type)»«ENDFOR»
					«ENDIF»
					let
					    _return = «serialize(expr.^return, null, ".")»;
					tel
				'''
			} else {
				if (isContainerAgreeAnnexNode(sd)) {
//					var domain = type.domain;
					var range = type.range;
					if (! sr.typeBinding.empty) {
						range = te.bind(range);
					}
					var Assertion a = null;
					for (SymbolDeclaration t : (sd.eContainer as NamedType).symbols) {
						if (t instanceof Assertion) {
							a = t;	// want to break, but not supported, only expect one assertion?
						}	
					}
					var scopes = containerAgreeAnnexNodeAssertionScope(a) ;
					var returns = containerAgreeAnnexNodeAssertionOutput(a) ;
					var letContent = containerAgreeAnnexNodeAssertionLet(a);					
					return 
					'''
						node «containerAgreeAnnexNodeName(sd)»(«FOR v : scopes SEPARATOR '; '»«v»«ENDFOR»)
						returns («FOR v : returns SEPARATOR '; '»«v»«ENDFOR»)
						let
						    «letContent»;
						tel
					'''					
				} else {				
					var domain = type.domain;
					var range = type.range;
					if (! sr.typeBinding.empty) {
						range = te.bind(range);
					}
					var parameters = new ArrayList<SymbolDeclaration>() ;
					if (domain instanceof SimpleTypeReference) {
						if (! sr.typeBinding.empty) {
							domain = te.bind(domain);
						}
						var p = ImlCustomFactory.INST.createSymbolDeclaration("_x0_", domain);
						parameters.add(p) ; 
					} else if (domain instanceof TupleType) {
						var index = 0 ;
						for(t : domain.types) {
							var ImlType tBound
							if (sr.typeBinding.empty) {
								tBound = t
							} else {
								tBound = te.bind(t);
							}
							var p = ImlCustomFactory.INST.createSymbolDeclaration("_x" + index + "_", EcoreUtil.copy(tBound)) ;
							parameters.add(p) ; 
							index = index + 1;
						}
					}
					return 
					'''
						node «toLustreName(sr)»(«FOR v : parameters SEPARATOR '; '»«v.name» : «toLustreName(v.type)»«ENDFOR»)
						returns (_return : «toLustreName(range)»)
						let
						tel
					'''					
				}
			}
		}
		return "" 
	}

	def  isContract(LustreNode m) {
		if ( m.fields.size > 0) {
			for (v : m.fields.values) {
				if (v.assume || v.guarantee ||(v.name.equals("assumption") || v.name.equals("guarantee")) && v.definition !== null) {
					return true
				}
			}
		}
		return false
	}
	
	def isInit(SymbolReferenceTerm srt) {
		var sd = srt.symbol as SymbolDeclaration;
		return (sd.name.equals("init") && sd.definition === null );
	}
	
	def isPre(SymbolReferenceTerm srt) {
		var sd = srt.symbol as SymbolDeclaration;
		return (sd.name.equals("pre") && sd.definition === null );
	}
	
	def isAgreeAnnexNode (TermMemberSelection tms) {
		var retval = false;
		var TermExpression rcv = tms.receiver;
		while (rcv instanceof TermMemberSelection) {
			rcv = rcv.receiver;
		}
		if (rcv instanceof SymbolReferenceTerm && 
			(rcv as SymbolReferenceTerm).symbol instanceof NamedType) {
			var NamedType nt = (rcv as SymbolReferenceTerm).symbol as NamedType;
			var Trait t = (stdLibs.getNamedType("iml.synchdf.ontological", "Synchronous") as Trait)
			var singleElementEnum = false;
			if (nt.restriction instanceof EnumRestriction) {
				var enumRestriction = (nt.restriction) as EnumRestriction;
				singleElementEnum = (enumRestriction.literals.size == 1);
			} 
			if (ImlUtil.exhibits(nt, t) && singleElementEnum) {
				retval = true;
			}
		}		
		return retval;
	}

	def agreeAnnexNodeName (TermMemberSelection tms) {
		var TermExpression rcv = tms.receiver;
		while (rcv instanceof TermMemberSelection) {
			rcv = rcv.receiver;
		}
		return (rcv as SymbolReferenceTerm).symbol.name;
	}

	def isContainerAgreeAnnexNode (SymbolDeclaration sd) {
		var retval = false;
		if (sd.eContainer instanceof NamedType) {
			var NamedType nt = sd.eContainer as NamedType;
			var Trait t = (stdLibs.getNamedType("iml.synchdf.ontological", "Synchronous") as Trait)
			var singleElementEnum = false;
			if (nt.restriction instanceof EnumRestriction) {
				var enumRestriction = (nt.restriction) as EnumRestriction;
				singleElementEnum = (enumRestriction.literals.size == 1);
			} 
			if (ImlUtil.exhibits(nt, t) && singleElementEnum) {
				retval = true;
			}
		}		
		return retval;
	}

	def containerAgreeAnnexNodeName (SymbolDeclaration sd) {
		var retval = "";
		if (sd.eContainer instanceof NamedType) {
			var NamedType nt = sd.eContainer as NamedType;
			return nt.name;
		}		
		return retval;
	}
	
				 
	def containerAgreeAnnexNodeAssertionScope(Assertion a) {
		var retval = new ArrayList<String>()
		if (a !== null) {
			if (a.definition instanceof SequenceTerm) {
				var SequenceTerm st = a.definition as SequenceTerm;
				if (st.^return instanceof SignedAtomicFormula) {
					var SignedAtomicFormula saf = st.^return as SignedAtomicFormula;
					if (saf.left instanceof QuantifiedFormula) {
						var QuantifiedFormula qf = saf.left as QuantifiedFormula;
						for (SymbolDeclaration sd : qf.scope) {
							var String v = sd.name + " : " + toLustreName(sd.type);
							retval.add(v);
						}
					}
				}
			}
		}
		return retval
	}
	
	def containerAgreeAnnexNodeAssertionOutput(Assertion a) {
		var retval = new ArrayList<String>()
		if (a !== null) {		
			if (a.definition instanceof SequenceTerm) {
				var SequenceTerm st = a.definition as SequenceTerm;
				if (st.^return instanceof SignedAtomicFormula) {
					var SignedAtomicFormula saf = st.^return as SignedAtomicFormula;
					if (saf.left instanceof QuantifiedFormula) {
						var QuantifiedFormula qf = saf.left as QuantifiedFormula;
						if (qf.left instanceof SequenceTerm) {
							var SequenceTerm st2 = qf.left as SequenceTerm;
							for (SymbolDeclaration sd : st2.defs) {
								var String v = sd.name + " : " + toLustreName(sd.type);
								retval.add(v);
							}
						}
					}
				}
			}
		}
		return retval		
	}

	def containerAgreeAnnexNodeAssertionLet(Assertion a) {
		var retval = "";
		if (a !== null) {
			if (a.definition instanceof SequenceTerm) {
				var SequenceTerm st = a.definition as SequenceTerm;
				if (st.^return instanceof SignedAtomicFormula) {
					var SignedAtomicFormula saf = st.^return as SignedAtomicFormula;
					if (saf.left instanceof QuantifiedFormula) {
						var QuantifiedFormula qf = saf.left as QuantifiedFormula;
						if (qf.left instanceof SequenceTerm) {
							var SequenceTerm st2 = qf.left as SequenceTerm;
							return serialize(st2.^return, ImlCustomFactory.INST.createSimpleTypeReference(a.eContainer as NamedType), new HashMap<Symbol, String>(), '.');
						}
					}
				}
			}
		}
		return retval		
	}
	
	def  hasAssumption(LustreNode m) {
		if ( m.fields.size > 0) {
			for (v : m.fields.values) {
				if (v.name.equals("assumption") && v.definition !== null) {
					return true
				}
			}
		}
		return false
	}
	
	def hasGuarantee(LustreNode m) {
		if ( m.fields.size > 0) {
			for (v : m.fields.values) {
				if (v.name.equals("guarantee") && v.definition !== null) {
					return true
				}
			}
		}
		return false
	}
	
	
	def needImported(LustreNode m) {
		if (m.components.size > 0 || m.lets.size > 0 || m.variables.size > 0) {
			return ""
		}
		return "imported "
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
		var String imlName = m.name;
		
		var String nm = typeNameWithReplacement(imlName);
		var String res = nm.replaceAll("\\.","_dot_")
		if (!lustre2Iml.containsKey(res)) {
			lustre2Iml.put(res, imlName);
		}		
		return res
	}


	def String toLustreName(SymbolDeclaration sd) {
		var String imlName = qnp.getFullyQualifiedName(sd).toString()
		return toLustreName(imlName)
	}

	def String toLustreNameGlobal(SymbolDeclaration sd) {
		var String name = sd.name;
		if (!lustre2Iml.containsKey(name)) {
			lustre2Iml.put(name, name);
		}
		return name;
	}
	
	def String toLustreName(SymbolReferenceTerm sr) {
		var String imlName = qnp.getFullyQualifiedName((sr.symbol as SymbolDeclaration).eContainer).toString();
		imlName = imlName + "." + (sr.symbol as SymbolDeclaration).name;
		var String bt = ""
		if (!sr.typeBinding.empty) {
			bt = "<";
			for (ImlType t : sr.typeBinding) {
				bt = bt + ImlUtil.getTypeName(t, qnp) + ",";
			}
			bt += ">";
			bt = bt.replaceAll(",\\>", "\\>")
		}		
		imlName = imlName + bt;
		var String lustreName = typeNameWithReplacement(imlName);
		lustreName = lustreName.replaceAll("\\.","_dot_");
		if (!lustre2Iml.containsKey(lustreName)) {
			lustre2Iml.put(lustreName, imlName);
		}
		return lustreName;
	}

	def String toLustreName(String name) {
		var String lustreName = name.replaceAll("\\.","_dot_");
		if (!lustre2Iml.containsKey(lustreName)) {
			lustre2Iml.put(lustreName, name);
		}
		return lustreName;		
	}


	def String toLustreName(String name, String literal, boolean isEnum) {
		var String imlName = name + "." + literal
		var String lustreName
		if (isEnum) {
			lustreName = name.replaceAll("\\.","_dot_") + "_dot_" + literal;
		} else {
			lustreName = name.replaceAll("\\.","_dot_") + "." + literal;
		}
		if (!lustre2Iml.containsKey(lustreName)) {
			lustre2Iml.put(lustreName, imlName);
		}
		return lustreName;
	}
	
	def String toLustreName(ImlType t) {
		if (t instanceof SimpleTypeReference) {
			if (t.type === stdLibs.boolType) return "bool" ;
			if (t.type === stdLibs.intType) return "int";
			if (t.type === stdLibs.realType) return "real";
		}
		var String imlName = ImlUtil.getTypeName(t,qnp);
		var String name = typeNameWithReplacement(imlName);
		name = name.replaceAll("\\.","_dot_");
		if (!lustre2Iml.containsKey(name)) {
			lustre2Iml.put(name, imlName);
		}
		return name;
	}
	
	def String typeNameWithReplacement(String name) {
		var String nm = name.replaceAll("<", "__");
		nm = nm.replaceAll("\\(", "__");
		nm = nm.replaceAll(", ", "__");
		nm = nm.replaceAll("->", "__")
		nm = nm.replaceAll(">", "__");
		nm = nm.replaceAll("\\)", "__");
		return nm;
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
