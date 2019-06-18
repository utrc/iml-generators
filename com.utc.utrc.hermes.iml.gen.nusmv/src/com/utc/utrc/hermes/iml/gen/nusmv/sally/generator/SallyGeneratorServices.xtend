package com.utc.utrc.hermes.iml.gen.nusmv.sally.generator

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.gen.nusmv.sally.model.SallyModel
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.Assertion
import com.utc.utrc.hermes.iml.iml.AtomicExpression
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
import com.utc.utrc.hermes.iml.iml.TermExpression
import com.utc.utrc.hermes.iml.iml.TermMemberSelection
import com.utc.utrc.hermes.iml.iml.Trait
import com.utc.utrc.hermes.iml.iml.TruthValue
import com.utc.utrc.hermes.iml.lib.ImlStdLib
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import com.utc.utrc.hermes.iml.typing.TypingEnvironment
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.sexpr.SExpr
import com.utc.utrc.hermes.sexpr.SExpr.Seq
import com.utc.utrc.hermes.sexpr.SExpr.Token
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import org.eclipse.xtext.naming.IQualifiedNameProvider
import com.utc.utrc.hermes.iml.iml.CaseTermExpression

class SallyGeneratorServices {

	@Inject
	private ImlTypeProvider typeProvider;

	@Inject
	private IQualifiedNameProvider qnp;

	@Inject
	private ImlStdLib stdLibs;

	static Token<String> IMPLIES = new Token<String>("implies") ;
	static Token<String> AND = new Token<String>("and") ;
	static Token<String> OR = new Token<String>("or") ;
	static Token<String> NOT = new Token<String>("not") ;
	static Token<String> EQ = new Token<String>("=") ;
	static Token<String> LEQ = new Token<String>("<=") ;
	static Token<String> GEQ = new Token<String>(">=") ;
	static Token<String> LT = new Token<String>("<") ;
	static Token<String> GT = new Token<String>(">") ;
	static Token<String> PLUS = new Token<String>("+") ;
	static Token<String> MINUS = new Token<String>("-") ;
	static Token<String> MULT = new Token<String>("*") ;
	static Token<String> DIV = new Token<String>("/") ;
	static Token<String> ITE = new Token<String>("ite") ;
	
	static Token<String> TRUE = new Token<String>("true") ;
	static Token<String> FALSE = new Token<String>("false") ;

	static Token<String> DEFINE_CONSTANT = new Token<String>("define-constant") ;
	static Token<String> DEFINE_STATE_TYPE = new Token<String>("define-state-type") ;
	static Token<String> DEFINE_STATES = new Token<String>("define-states") ;
	static Token<String> DEFINE_TRANSITION = new Token<String>("define-transition") ;
	static Token<String> DEFINE_TRANSITION_SYSTEM = new Token<String>("define-transition-system") ;
	

	def getBinaryOp(String s) {
		switch(s){
			case "=>" : IMPLIES
			case "&&" : AND
			case "||" : OR
			case "=" : EQ 
			case "<=" : LEQ
			case ">=" : GEQ
			case "<" : LT
			case ">" : GT
			case "+" : PLUS
			case "-" : MINUS
			case "*": MULT
			case "/" : DIV
		}
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
		return rec + "." + mem;
	}

	def String getNameFor(SymbolReferenceTerm s) {
		return s.getSymbol().getName();
	}

	def String serialize(SallyModel m) {
		//constants
		var expr = new Seq 
		for(l : m.state.literals.keySet) {
			var toadd = new Seq
			toadd.add(DEFINE_CONSTANT,new Token<String>(l),new Token<Integer>(m.state.literals.get(l)))
			expr.add(toadd)
		}
		var state = m.state;
		var toadd = new Seq ;
		toadd.add(DEFINE_STATE_TYPE,new Token<String>("state_type"));
		var listofstates = new Seq;
		toadd.add(listofstates)
		for(v : state.variables.keySet) {
			var thisstate = new Seq ;
			thisstate.add(new Token<String>(v),new Token<String>(state.variables.get(v).type.toString));
			listofstates.add(thisstate)
		}
		expr.add(toadd)
		
		var init = new Seq
		init.add(DEFINE_STATES, new Token<String>("initial_states"),new Token<String>("state_type"));
		var inite = new Seq;
		inite.add(AND,TRUE);
		for(e : m.init.values){
			inite.add(e);
		}
		init.add(inite)
		expr.add(init)
		
		var transition = new Seq
		transition.add(DEFINE_TRANSITION, new Token<String>("transition"),new Token<String>("state_type"));
		var transitione = new Seq;
		transitione.add(AND,TRUE);
		for(e : m.transition.values){
			transitione.add(e);
		}
		transition.add(transitione)
		expr.add(transition)
		
		var top = new Seq
		top.add(DEFINE_TRANSITION_SYSTEM, new Token<String>("top"),new Token<String>("state_type"),new Token<String>("initial_states"), new Token<String>("transition"))
		expr.add(top)
		return expr.toString ;
	}

	
	def SExpr serialize(FolFormula e, SimpleTypeReference ctx) {
		serialize(e, ctx, new HashMap<Symbol, String>());
	}

	def SExpr serialize(FolFormula e, SimpleTypeReference ctx, Map<Symbol, String> map) {
		var retval = new Seq;
		if (e.op !== null) {
			if (e.op == "<=>") {
				var left = new Seq
				var right = new Seq
				left.add(IMPLIES,serialize(e.left,ctx,map),serialize(e.right,ctx,map))
				right.add(IMPLIES, serialize(e.right,ctx,map),serialize(e.left,ctx,map))
				retval.add(AND,left,right);
			} else {
				retval.add(getBinaryOp(e.op),serialize(e.left,ctx,map),serialize(e.right,ctx,map)) 
			}
		} else if (e instanceof AtomicExpression) {

			if (e.rel === RelationKind.EQ) {
				var suff = getSuffix(e.left, ctx);
				if (suff.empty) {
					retval.add(getBinaryOp(e.rel.toString),serialize(e.left,ctx,map),serialize(e.right,ctx,map))
				} else {
					retval.add(AND,TRUE)
					for(suffix : suff) {
						var toadd = new Seq
						toadd.add(getBinaryOp(e.rel.toString),serialize(e.left,ctx,map),serialize(e.right,ctx,map))
						retval.add(toadd)
					}
				}
			} else {
				retval.add(getBinaryOp(e.rel.toString), serialize(e.left,ctx,map),serialize(e.right,ctx,map));
			}
		} else if (e instanceof Addition) {
			retval.add(getBinaryOp(e.sign),serialize(e.left,ctx,map),serialize(e.right,ctx,map))
		} else if (e instanceof Multiplication) {
			retval.add(getBinaryOp(e.sign),serialize(e.left,ctx,map),serialize(e.right,ctx,map))
		} else if (e instanceof TermMemberSelection || e instanceof SymbolReferenceTerm) {
			return (new Token<String>(toString(e,map)))
		}  else if (e instanceof ParenthesizedTerm) {
			return serialize(e.sub,ctx,map)
		} else if (e instanceof IteTermExpression) {
			retval.add( serialize(e.condition,ctx,map) ) 
			if (e.right === null) {
				retval.add(IMPLIES,serialize(e.condition,ctx,map),serialize(e.left,ctx,map))
			} else {
				retval.add(ITE,serialize(e.condition,ctx,map),serialize(e.left,ctx,map),serialize(e.right,ctx,map))
			}
		} else if (e instanceof CaseTermExpression) {

			retval.add(ITE);
			var current = retval;
			for(index : 0..e.cases.size -2) {
				if (index === e.cases.size -2) {
					current.add(serialize(e.cases.get(index),ctx,map),serialize(e.expressions.get(index),ctx,map),serialize(e.expressions.get(index+1),ctx,map));
					
				} else {
					current.add(serialize(e.cases.get(index),ctx,map),serialize(e.expressions.get(index),ctx,map));
					var next = new Seq
					next.add(ITE)
					current.add(next);
					current = next ;
				}
			}			
		}  else if (e instanceof SequenceTerm) {
			retval.add(serialize(e.^return,ctx,map))
		} else if (e instanceof SignedAtomicFormula) {
			if (e.neg) {
				retval.add(NOT, serialize(e.left, ctx, map))
			}
			return serialize(e.left, ctx, map);
		} else if (e instanceof NumberLiteral) {
			if (e.isNeg) {
				retval.add(MINUS,new Token<Integer>(e.value));
			}
			return new Token<Integer>(e.value)
		} else if (e instanceof TruthValue) {
			if(e.TRUE) return TRUE else return FALSE;
		}
		return retval;
	}
	
	def toString(FolFormula e, Map<Symbol, String> map) {
		var retval = "" ;
		 if (e instanceof TermMemberSelection) {
			// TODO this is a quick hack
			if (e.receiver instanceof SymbolReferenceTerm &&
				(e.receiver as SymbolReferenceTerm).symbol instanceof NamedType) {
				var typename = ((e.receiver as SymbolReferenceTerm).symbol as NamedType).name;
				var literalname = toString(e.member,map);
				retval = '''«typename».«literalname»'''
			} else {
				// get the data field of the data port
				if (isCarrierAccess(e.member)) {
					retval = '''«toString(e.receiver,map)»'''
				}else {
					retval = '''«toString(e.receiver,map)».«toString(e.member,map)»'''
				}
				
			}
		} else if (e instanceof SymbolReferenceTerm) {
			if (map.containsKey(e.symbol)) {
				retval = map.get(e.symbol);
			} else {
				retval = e.symbol.name;
			}
			
		}
		return retval
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
