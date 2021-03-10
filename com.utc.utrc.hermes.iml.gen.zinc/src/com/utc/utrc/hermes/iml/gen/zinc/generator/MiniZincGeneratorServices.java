package com.utc.utrc.hermes.iml.gen.zinc.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Generated;
import javax.management.openmbean.SimpleType;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.zinc.model.Constraint;
import com.utc.utrc.hermes.iml.gen.zinc.model.EnumType;
import com.utc.utrc.hermes.iml.gen.zinc.model.EnumVar;
import com.utc.utrc.hermes.iml.gen.zinc.model.FloatConstant;
import com.utc.utrc.hermes.iml.gen.zinc.model.FloatExpression;
import com.utc.utrc.hermes.iml.gen.zinc.model.FloatVariable;
import com.utc.utrc.hermes.iml.gen.zinc.model.IntegerFunction;
import com.utc.utrc.hermes.iml.gen.zinc.model.MiniZincModel;
import com.utc.utrc.hermes.iml.iml.Addition;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.AtomicExpression;
import com.utc.utrc.hermes.iml.iml.CaseTermExpression;
import com.utc.utrc.hermes.iml.iml.CharLiteral;
import com.utc.utrc.hermes.iml.iml.EnumRestriction;
import com.utc.utrc.hermes.iml.iml.ExpressionTail;
import com.utc.utrc.hermes.iml.iml.FloatNumberLiteral;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.IteTermExpression;
import com.utc.utrc.hermes.iml.iml.LambdaExpression;
import com.utc.utrc.hermes.iml.iml.Multiplication;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.NumberLiteral;
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm;
import com.utc.utrc.hermes.iml.iml.RelationKind;
import com.utc.utrc.hermes.iml.iml.SequenceTerm;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.StringLiteral;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.iml.TailedExpression;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.TermMemberSelection;
import com.utc.utrc.hermes.iml.iml.TruthValue;
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.iml.TypeRestriction;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;
import com.utc.utrc.hermes.iml.util.Phi;

import at.siemens.ct.jmz.elements.Element;
import at.siemens.ct.jmz.elements.TypeInst;
import at.siemens.ct.jmz.expressions.Constant;
import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.bool.BooleanConstant;
import at.siemens.ct.jmz.expressions.bool.BooleanExpression;
import at.siemens.ct.jmz.expressions.bool.BooleanVariable;
import at.siemens.ct.jmz.expressions.bool.Conjunction;
import at.siemens.ct.jmz.expressions.bool.Disjunction;
import at.siemens.ct.jmz.expressions.bool.Equivalence;
import at.siemens.ct.jmz.expressions.bool.Implication;
import at.siemens.ct.jmz.expressions.bool.RelationalOperation;
import at.siemens.ct.jmz.expressions.bool.RelationalOperator;
import at.siemens.ct.jmz.expressions.conditional.BooleanConditionalExpression;
import at.siemens.ct.jmz.expressions.conditional.IntegerConditionalExpression;
import at.siemens.ct.jmz.expressions.integer.ArithmeticOperation;
import at.siemens.ct.jmz.expressions.integer.BasicInteger;
import at.siemens.ct.jmz.expressions.integer.IntegerConstant;
import at.siemens.ct.jmz.expressions.integer.IntegerExpression;
import at.siemens.ct.jmz.expressions.integer.IntegerOperation;
import at.siemens.ct.jmz.expressions.integer.IntegerVariable;

public class MiniZincGeneratorServices {

	@Inject
	private ImlTypeProvider typeProvider;

	@Inject
	private IQualifiedNameProvider qnp;

	@Inject
	private ImlStdLib stdLibs;

	public MiniZincGeneratorServices() {

	}

	public NamedType getQueryType(SymbolDeclaration query) throws MiniZincGeneratorException {
		ImlType t = query.getType();
		if (t instanceof SimpleTypeReference) {
			return ((SimpleTypeReference) t).getType();
		} else {
			throw new MiniZincGeneratorException("The current generator only support basic types");
		}
	}

	public SymbolReferenceTerm getCost(SymbolDeclaration query) throws MiniZincGeneratorException {
		FolFormula f = query.getDefinition();
		if (f instanceof SignedAtomicFormula) {
			if (f.getLeft() instanceof TailedExpression) {
				ExpressionTail tail = ((TailedExpression) f.getLeft()).getTail();
				if (tail instanceof TupleConstructor) {
					FolFormula arg = ((TupleConstructor) tail).getElements().get(0);
					if (arg instanceof SignedAtomicFormula) {
						if (arg.getLeft() instanceof SymbolReferenceTerm) {
							return (SymbolReferenceTerm) arg.getLeft();
						}
					}
				}
			}
		}
		return null;
	}

	public QualifiedName getPrefixFromCostFunction(SymbolDeclaration query) throws MiniZincGeneratorException {
		LambdaExpression le = getCostDefinition(query);
		List<SymbolDeclaration> params = le.getParameters();
		if (params.size() == 1) {
			return QualifiedName.create(params.get(0).getName());
		} else {
			throw new MiniZincGeneratorException("The number of parameters of the cost function must be exactly 1.");
		}
	}

	public SymbolDeclaration getCostSymbol(SymbolDeclaration query) throws MiniZincGeneratorException {
		SymbolReferenceTerm ref = getCost(query);
		Symbol s = ref.getSymbol();
		if (s instanceof SymbolDeclaration) {
			return (SymbolDeclaration) s;
		}
		throw new MiniZincGeneratorException("Cost symbol not found");
	}

	public LambdaExpression getCostDefinition(SymbolDeclaration query) throws MiniZincGeneratorException {
		SymbolDeclaration s = getCostSymbol(query);
		FolFormula def = (FolFormula) s.getDefinition();
		if (def != null) {
			if (def instanceof SignedAtomicFormula) {
				if (def.getLeft() instanceof LambdaExpression) {
					return (LambdaExpression) def.getLeft();
				}
			}
			throw new MiniZincGeneratorException("The cost definition must be a 'fun' expression");

		} else {
			throw new MiniZincGeneratorException("Cost symbol does not have a definition");
		}
	}
	
	public QueryType getSolutionType(SymbolDeclaration s) throws MiniZincGeneratorException {
		if (s.getDefinition() instanceof SignedAtomicFormula) {
			if (s.getDefinition().getLeft() instanceof TailedExpression) {
				TailedExpression te = (TailedExpression) s.getDefinition().getLeft();
				if (te.getLeft() instanceof SymbolReferenceTerm) {
					SymbolReferenceTerm sr = (SymbolReferenceTerm) te.getLeft();
					if (qnp.getFullyQualifiedName(sr.getSymbol()).toString().equals("iml.queries.min")) {
						return QueryType.MIN ;
					} else if (qnp.getFullyQualifiedName(sr.getSymbol()).toString().equals("iml.queries.max")) {
						return QueryType.MAX;
					} else if (qnp.getFullyQualifiedName(sr.getSymbol()).toString().equals("iml.queries.sat")) {
						return QueryType.SAT;
					} else {
						throw new MiniZincGeneratorException("The query should be a minimization or a maximization but it is none");
					}
				}
			}
		}
		throw new MiniZincGeneratorException("Malformed optimization query");
	}

	public List<SymbolDeclaration> getSymbolDeclarations(NamedType type) {
		List<SymbolDeclaration> retval = new ArrayList<>();
		List<SymbolDeclaration> allSymbols = ImlUtil.getAllSymbols(type, true) ;
		for (Symbol s : allSymbols) {
			if (s instanceof SymbolDeclaration && !(s instanceof Assertion)) {
				retval.add((SymbolDeclaration) s);
			}
		}
		return retval;
	}

	public List<Assertion> getAssertions(NamedType type) {
		List<Assertion> retval = new ArrayList<>();
		List<SymbolDeclaration> allSymbols = ImlUtil.getAllSymbols(type, true) ;
		for (Symbol s : allSymbols) {
			if (s instanceof Assertion) {
				retval.add((Assertion) s);
			}
		}
		return retval;
	}

	public void addSymbol(QualifiedName prefix, MiniZincModel builder, SymbolDeclaration s) {
		if (isConstant(s)) {
			addConstant(prefix, builder, s);
		} else {
			// TODO handle symbols with definition as constraint instead of variable
			addVariable(prefix, builder, s);
		}

	}

	public void addConstant(QualifiedName prefix, MiniZincModel builder, SymbolDeclaration s) {

		if (stdLibs.isBool(s.getType())) {
			BooleanConstant c = new BooleanConstant(getBooleanConstant(s));
			builder.add(c.toNamedConstant(getName(prefix, s)));
		} else if (stdLibs.isInt(s.getType())) {
			IntegerConstant c = new IntegerConstant(getIntConstant(s));
			builder.add(c.toNamedConstant(getName(prefix, s)));
		} else if (stdLibs.isReal(s.getType())) {
			FloatConstant c = new FloatConstant(getRealConstant(s));
			builder.add(c.toNamedConstant(getName(prefix, s)));
		}

	}

	public void addVariable(QualifiedName prefix, MiniZincModel builder, SymbolDeclaration s) {
		if (builder.getElementByName(getName(prefix, s)) != null) {
			return ;
		}
		if (stdLibs.isBool(s.getType())) {
			BooleanVariable c = new BooleanVariable(getName(prefix, s));
			builder.add(c);
		} else if (stdLibs.isInt(s.getType())) {
			IntegerVariable c = new IntegerVariable(getName(prefix, s));
			builder.add(c);
		} else if (stdLibs.isReal(s.getType())) {
			FloatVariable c = new FloatVariable(getName(prefix, s));
			builder.add(c);
		} else {
			if (s.getType() instanceof SimpleTypeReference) {
				 if (ImlUtil.isEnum(((SimpleTypeReference) s.getType()).getType())) {
					IntegerVariable c = new IntegerVariable(getName(prefix, s));
					builder.add(c);
					EnumType vartype = builder.getEnums().get(getName(prefix, ((SimpleTypeReference) s.getType()).getType())) ;
					EnumVar var = new EnumVar(c, vartype);				 
					builder.getEnumVars().put(c.getName(), var) ;
				 }
			}
		}

	}

	public void addConstraint(QualifiedName prefix, MiniZincModel builder, SimpleTypeReference ctx, Assertion a) throws MiniZincGeneratorException {

		SequenceTerm term = (SequenceTerm) a.getDefinition();
		BooleanExpression expr = process(prefix, builder, ctx, term);
		Constraint toadd = null ;
		if (a.getComment() != null ) {
			toadd = new Constraint(prefix.toString(), a.getComment(), expr);	
		} else {
			toadd = new Constraint(expr);
		}
		builder.add(toadd);
		
	}

	public BooleanExpression process(QualifiedName prefix, MiniZincModel builder, SimpleTypeReference ctx,
			FolFormula e) throws MiniZincGeneratorException {
		return process(prefix, builder, ctx, e, new HashMap<Symbol, String>());
	}

	public BooleanExpression process(QualifiedName prefix, MiniZincModel builder, SimpleTypeReference ctx, FolFormula e,
			Map<Symbol, String> map) throws MiniZincGeneratorException {
		if (e.getOp() != null && (e.getOp().equals("=>") || e.getOp().equals("<=>") || e.getOp().equals("&&")
				|| e.getOp().equals("||"))) {
			switch (e.getOp()) {
			case "=>": {
				BooleanExpression retval = new Implication(process(prefix, builder, ctx, e.getLeft(), map),
						process(prefix, builder, ctx, e.getRight(), map));
				return retval;
			}
			case "<=>": {
				BooleanExpression retval = new Equivalence(process(prefix, builder, ctx, e.getLeft(), map),
						process(prefix, builder, ctx, e.getRight(), map));
				return retval;
			}
			case "&&": {
				BooleanExpression retval = new Conjunction(process(prefix, builder, ctx, e.getLeft(), map),
						process(prefix, builder, ctx, e.getRight(), map));
				return retval;
			}
			case "||": {
				BooleanExpression retval = new Disjunction(process(prefix, builder, ctx, e.getLeft(), map),
						process(prefix, builder, ctx, e.getRight(), map));
				return retval;
			}
			default:
				return null;
			}
		} else if (e instanceof AtomicExpression) {
			RelationalOperator op = getRelationalOperator(((AtomicExpression) e).getRel());
			TypingEnvironment env = new TypingEnvironment(ctx);
			ImlType lefttype = typeProvider.termExpressionType(e.getLeft(),env);
			if (stdLibs.isBool(lefttype)) {
				return new RelationalOperation<Boolean>(process(prefix, builder, ctx, e.getLeft(), map), op,
						process(prefix, builder, ctx, e.getRight(), map));
			} else if (stdLibs.isInt(lefttype) || isEnum(lefttype)) {
				return new RelationalOperation<Integer>(
						processIntegerExpression(prefix, builder, ctx, e.getLeft(), map), op,
						processIntegerExpression(prefix, builder, ctx, e.getRight(), map));
			} else if (stdLibs.isReal(lefttype)) {
				return new RelationalOperation<Float>(processFloatExpression(prefix, builder, ctx, e.getLeft(), map),
						op, processFloatExpression(prefix, builder, ctx, e.getRight(), map));
			} else {
				//this is not a primitive type and therefore it must be an equality
				if (lefttype instanceof SimpleTypeReference) {
					NamedType nt = ((SimpleTypeReference) lefttype).getType();
					if (e.getLeft() instanceof TermExpression && e.getRight() instanceof TermExpression) {
						TermExpression lefte = (TermExpression) e.getLeft() ;
						TermExpression righte = (TermExpression) e.getRight();
						return expandEquality(prefix,builder,ctx,nt, lefte, righte);
					}
				}
			}
		} else if (e instanceof Addition) {
			throw new MiniZincGeneratorException("A boolean expression should not be an addition") ;
		} else if (e instanceof Multiplication) {
			throw new MiniZincGeneratorException("A boolean expression should not be a multiplication") ;
		} else if (e instanceof TermMemberSelection) {
			
			QualifiedName receiver_name = getQualifiedName(prefix, ((TermMemberSelection) e).getReceiver());
			if (((TermMemberSelection) e).getMember() instanceof SymbolReferenceTerm) {
				return (BooleanVariable) builder .getElementByName(getName(receiver_name, ((SymbolReferenceTerm) ((TermMemberSelection) e).getMember() ).getSymbol()));
			}
			
			
			
			
		} else if (e instanceof SymbolReferenceTerm) {
			return (BooleanVariable) builder.getElementByName(getName(prefix, ((SymbolReferenceTerm) e).getSymbol()));
			
		} else if (e instanceof ParenthesizedTerm) {
			return process(prefix, builder, ctx, ((ParenthesizedTerm) e).getSub(), map);
		} else if (e instanceof IteTermExpression) {
			
			BooleanExpression condition = process(prefix, builder, ctx, ((IteTermExpression) e).getCondition()) ;
			BooleanExpression thenbranch = process(prefix, builder, ctx, e.getLeft()) ;
			BooleanExpression elsebranch = process(prefix, builder, ctx, e.getRight()) ;			
			BooleanConditionalExpression exp = new BooleanConditionalExpression(condition,thenbranch,elsebranch) ;
			
			return exp;
			
		} else if (e instanceof CaseTermExpression) {

		} else if (e instanceof SequenceTerm) {
			return process(prefix, builder, ctx, ((SequenceTerm) e).getReturn(), map);
		} else if (e instanceof SignedAtomicFormula) {
			BooleanExpression retval = process(prefix, builder, ctx, e.getLeft(), map);
			if (((SignedAtomicFormula) e).isNeg()) {
				return retval.negate();
			}
			return retval;
		} else if (e instanceof TruthValue) {
			if (((TruthValue) e).isTRUE()) {
				return new BooleanConstant(true) ;
			} else {
				return new BooleanConstant(false);
			}
		}
		System.out.println("Returning null");
		return null;
	}

	public boolean isConstant(SymbolDeclaration s) {

		if (s.getDefinition() != null) {
			// make sure the definition is a number
			FolFormula def = s.getDefinition();
			if (def instanceof SignedAtomicFormula) {
				if (def.getLeft() instanceof NumberLiteral || def.getLeft() instanceof FloatNumberLiteral
						|| def.getLeft() instanceof StringLiteral || def.getLeft() instanceof CharLiteral
						|| def.getLeft() instanceof TruthValue) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean getBooleanConstant(SymbolDeclaration s) {
		TruthValue v = (TruthValue) s.getDefinition().getLeft();
		if (v.isTRUE()) {
			return true;
		}
		return false;
	}

	public int getIntConstant(SymbolDeclaration s) {
		NumberLiteral v = (NumberLiteral) s.getDefinition().getLeft();
		if (((NumberLiteral) s.getDefinition().getLeft()).getValue().intValue() < 0) {
			return (-v.getValue().intValue());
		}
		return (v.getValue().intValue());
	}

	public float getRealConstant(SymbolDeclaration s) {
		FloatNumberLiteral v = (FloatNumberLiteral) s.getDefinition().getLeft();
		if (((FloatNumberLiteral) s.getDefinition().getLeft()).getValue().floatValue() < 0) {
			return (-v.getValue().floatValue());
		}
		return (v.getValue().floatValue());
	}

	/*
	 * We will have a list of contexts
	 */
	public String getName(QualifiedName prefix, Symbol s) {
		if (s instanceof NamedType) {
			if (ImlUtil.isEnum((NamedType)s)) {
				return qnp.getFullyQualifiedName(s).toString().replaceAll("\\.", "___") ;
			}
		}
		return prefix.append(s.getName()).toString().replaceAll("\\.", "___") ;
	}
	
	
	

	public QualifiedName getQualifiedName(QualifiedName prefix, TermExpression e) {
		if (e instanceof SymbolReferenceTerm) {
			return prefix.append(((SymbolReferenceTerm) e).getSymbol().getName());
		} else if (e instanceof TermMemberSelection) {
			return getQualifiedName(getQualifiedName(prefix, ((TermMemberSelection) e).getReceiver()),
					((TermMemberSelection) e).getMember());
		}
		return prefix;
	}

	public RelationalOperator getRelationalOperator(RelationKind r) {
		RelationalOperator retval;
		switch (r) {
		case EQ:
			retval = RelationalOperator.EQ;
			break;
		case GEQ:
			retval = RelationalOperator.GE;
			break;
		case GREATER:
			retval = RelationalOperator.GT;
			break;
		case LEQ:
			retval = RelationalOperator.LE;
			break;
		case LESS:
			retval = RelationalOperator.LT;
			break;
		case NEQ:
			retval = RelationalOperator.NEQ;
			break;
		default:
			retval = RelationalOperator.EQ;
			break;
		}
		return retval;
	}

	public IntegerExpression processIntegerExpression(QualifiedName prefix, MiniZincModel builder,
			SimpleTypeReference ctx, FolFormula e, Map<Symbol, String> map) throws MiniZincGeneratorException {

		if (e instanceof Addition) {
			if (((Addition) e).getSign().equals("+")) {
				return new IntegerOperation(
						ArithmeticOperation.plus(processIntegerExpression(prefix, builder, ctx, e.getLeft(), map),
								processIntegerExpression(prefix, builder, ctx, e.getRight(), map)));
			} else {
				return new IntegerOperation(
						ArithmeticOperation.minus(processIntegerExpression(prefix, builder, ctx, e.getLeft(), map),
								processIntegerExpression(prefix, builder, ctx, e.getRight(), map)));
			}
		} else if (e instanceof Multiplication) {
			if (((Multiplication) e).getSign().equals("*")) {
				return new IntegerOperation(
						ArithmeticOperation.times(processIntegerExpression(prefix, builder, ctx, e.getLeft(), map),
								processIntegerExpression(prefix, builder, ctx, e.getRight(), map)));
			} else if (((Multiplication) e).getSign().equals("/")) {
				return new IntegerOperation(
						ArithmeticOperation.div(processIntegerExpression(prefix, builder, ctx, e.getLeft(), map),
								processIntegerExpression(prefix, builder, ctx, e.getRight(), map)));
			} else {
				return new IntegerOperation(
						ArithmeticOperation.modulo(processIntegerExpression(prefix, builder, ctx, e.getLeft(), map),
								processIntegerExpression(prefix, builder, ctx, e.getRight(), map)));
			}
		} else if (e instanceof TermMemberSelection) {
			// the model is flat, so we just need the name of the variable
			// TODO for the type, use the type provider (although it should be integer at
			// this point)
			
			if (((TermMemberSelection) e).getReceiver() instanceof SymbolReferenceTerm && 
					((SymbolReferenceTerm) ((TermMemberSelection) e).getReceiver()).getSymbol() instanceof NamedType
					) {
				SymbolReferenceTerm srt = (SymbolReferenceTerm) ((TermMemberSelection) e).getReceiver() ;
				if (srt.getSymbol() instanceof NamedType) {
					//this is an enum reference
					TermExpression lite = ((TermMemberSelection) e).getMember() ;
					if (lite instanceof SymbolReferenceTerm) {
						String literalname = ((SymbolReferenceTerm) lite).getSymbol().getName() ;
						String typename = getName(prefix, srt.getSymbol()) ;
						EnumType etype = builder.getEnums().get(typename);
						if (etype == null) {
							System.out.println("Null");
						}
						return new IntegerConstant(etype.getIntegerForLiteral(literalname)) ;
					}
				}
			} else {
			
				QualifiedName receiver_name = getQualifiedName(prefix, ((TermMemberSelection) e).getReceiver());
				if (((TermMemberSelection) e).getMember() instanceof SymbolReferenceTerm) {
//					
//					SymbolReferenceTerm t = (SymbolReferenceTerm) ((TermMemberSelection) e).getReceiver();
//					String qNmae =qnp.getFullyQualifiedName(t).toString();
					String vName = getName(receiver_name, ((SymbolReferenceTerm) ((TermMemberSelection) e).getMember() ).getSymbol());
					TypeInst<?, ?> typeinst = builder
							.getElementByName(getName(receiver_name, ((SymbolReferenceTerm) ((TermMemberSelection) e).getMember() ).getSymbol()));
					if (typeinst instanceof IntegerVariable) {
						return ((IntegerVariable) typeinst);
					} else {
						return ((BasicInteger) typeinst);
					}
				}
			}
		} else if (e instanceof SymbolReferenceTerm) {
			// need to retrieve it from the maps
			TypeInst<?, ?> typeinst = builder.getElementByName(getName(prefix, ((SymbolReferenceTerm) e).getSymbol()));
			if (typeinst instanceof IntegerVariable) {
				return ((IntegerVariable) typeinst);
			} else {
				return ((BasicInteger) typeinst);
			}
		} else if (e instanceof IteTermExpression) {
			
			BooleanExpression condition = process(prefix, builder, ctx, ((IteTermExpression) e).getCondition()) ;
			IntegerExpression thenbranch = processIntegerExpression(prefix, builder, ctx, e.getLeft(),map) ;
			IntegerExpression elsebranch = processIntegerExpression(prefix, builder, ctx, e.getRight(),map) ;			
			IntegerExpression exp = new IntegerConditionalExpression(condition,thenbranch,elsebranch) ;
			
			return exp;
			
		} else if (e instanceof ParenthesizedTerm) {
			return processIntegerExpression(prefix, builder, ctx, ((ParenthesizedTerm) e).getSub(), map);
		} else if (e instanceof NumberLiteral) {
			IntegerConstant c = new IntegerConstant(((NumberLiteral) e).getValue().intValue());
			if (((NumberLiteral) e).getValue().intValue() < 0) {
				return new IntegerConstant(-((NumberLiteral) e).getValue().intValue());
			}
			return new IntegerConstant(((NumberLiteral) e).getValue().intValue());
		} else if (e instanceof TailedExpression) {
			FolFormula left = e.getLeft() ;
			ExpressionTail tail = ((TailedExpression) e).getTail() ;
			//if function
			if (left instanceof SymbolReferenceTerm) {
				if (((SymbolReferenceTerm) left).getSymbol() instanceof SymbolDeclaration &&
							tail instanceof TupleConstructor) {
						SymbolDeclaration fdecl = (SymbolDeclaration) ((SymbolReferenceTerm) left).getSymbol() ;
						TupleConstructor actuals = (TupleConstructor) tail ;
						//TODO Replace the function call with an inline definition
						
					}
			}
			//else if array
		}
		System.out.println("Returning null");
		return null;
	}

	public FloatExpression processFloatExpression(QualifiedName prefix, MiniZincModel builder, SimpleTypeReference ctx,
			FolFormula e, Map<Symbol, String> map) {

		return null;
	}

	public void addEnum(QualifiedName prefix, MiniZincModel builder, NamedType type) {
		//name
		EnumType etype = new EnumType(getName(prefix, type));
		if (builder.getEnums().containsKey(etype.getName())) {
			return;
		}
		TypeRestriction r = type.getRestriction() ;
		if (r instanceof EnumRestriction) {
			EnumRestriction er = (EnumRestriction) r;
			int i = 0;
			for(SymbolDeclaration l : er.getLiterals()) {
				etype.getLiterals().put(i, l.getName()) ;
				i = i + 1;
			}
		}
		builder.getEnums().put(etype.getName(), etype);
	}

	public boolean isEnum(ImlType t) {
		if (t instanceof SimpleTypeReference) {
			return ImlUtil.isEnum(((SimpleTypeReference) t).getType());
		}
		return false;
	}
	
	public BooleanExpression expandEquality(QualifiedName prefix, MiniZincModel builder, SimpleTypeReference ctx, NamedType nt ,TermExpression left, TermExpression right) throws MiniZincGeneratorException {
		BooleanExpression retval = new BooleanConstant(true);
		for(SymbolDeclaration s : ImlUtil.getAllSymbols(nt, true).stream().filter(s -> s instanceof SymbolDeclaration).collect(Collectors.toList())) {
			TermMemberSelection lefttms = ImlCustomFactory.INST.createTermMemberSelection(EcoreUtil.copy(left), s);
			TermMemberSelection righttms = ImlCustomFactory.INST.createTermMemberSelection(EcoreUtil.copy(right), s);
			FolFormula eq = Phi.eq(lefttms, righttms);
			retval = retval.and(process(prefix, builder, ctx, eq));
		}
		return retval;
	}
	
	
}
