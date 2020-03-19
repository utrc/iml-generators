package com.utc.utrc.hermes.iml.gen.smt.encoding;

import static com.utc.utrc.hermes.iml.util.ImlUtil.getRelatedTypesWithProp;
import static com.utc.utrc.hermes.iml.util.ImlUtil.getTypeName;
import static com.utc.utrc.hermes.iml.util.ImlUtil.isGlobalSymbol;
import static com.utc.utrc.hermes.iml.util.ImlUtil.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.smt.model.AbstractSort;
import com.utc.utrc.hermes.iml.gen.smt.model.SmtModelProvider;
import com.utc.utrc.hermes.iml.iml.Addition;
import com.utc.utrc.hermes.iml.iml.Alias;
import com.utc.utrc.hermes.iml.iml.ArrayAccess;
import com.utc.utrc.hermes.iml.iml.ArrayType;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.AtomicExpression;
import com.utc.utrc.hermes.iml.iml.ExpressionTail;
import com.utc.utrc.hermes.iml.iml.FloatNumberLiteral;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.FunctionType;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Inclusion;
import com.utc.utrc.hermes.iml.iml.InstanceConstructor;
import com.utc.utrc.hermes.iml.iml.IteTermExpression;
import com.utc.utrc.hermes.iml.iml.LambdaExpression;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.Multiplication;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.NumberLiteral;
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm;
import com.utc.utrc.hermes.iml.iml.QuantifiedFormula;
import com.utc.utrc.hermes.iml.iml.Refinement;
import com.utc.utrc.hermes.iml.iml.Relation;
import com.utc.utrc.hermes.iml.iml.SelfTerm;
import com.utc.utrc.hermes.iml.iml.SequenceTerm;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.iml.TailedExpression;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.TermMemberSelection;
import com.utc.utrc.hermes.iml.iml.Trait;
import com.utc.utrc.hermes.iml.iml.TraitExhibition;
import com.utc.utrc.hermes.iml.iml.TruthValue;
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.iml.TupleType;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.typing.TypingServices;
import com.utc.utrc.hermes.iml.util.ImlUtil;
/**
 * SMT implementation for {@link ImlEncoder}. The encoder is build for SMT v2.5.
 * This encoder abstracts the underlying SMT model by using {@link SmtModelProvider}
  *
  * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
  * @author Gerald Wang (wangg@utrc.utc.com)
 *
 * @param <SortT> the model class for SMT sort declaration
 * @param <FuncDeclT> the model class for SMT function declaration
 */
// 
public class ImlSmtEncoder<SortT extends AbstractSort, FuncDeclT, FormulaT> implements ImlEncoder {

	/*
	 * TODO add to_real when using Int as Real
	 * TODO encode sqrt to pow .5
	 */
	@Inject SmtSymbolTable<SortT, FuncDeclT, FormulaT> symbolTable;
	@Inject SmtModelProvider<SortT, FuncDeclT, FormulaT> smtModelProvider;
	@Inject IQualifiedNameProvider qnp ;
	@Inject ImlTypeProvider typeProvider;	
	@Inject TypingServices typingServices;
	
	EncodeConfig config = EncodeConfig.Builder().build();
	
	Map<NamedType, ImlType> aliases = new HashMap<>();
	
	public static final String ARRAY_SELECT_FUNC_NAME = ".__array_select";
	public static final String ALIAS_FUNC_NAME = ".__alias_value";
	public static final String EXTENSION_BASE_FUNC_NAME = ".__base_";
	public static final String INST_NAME = "__inst__";
	
	public void setConfig(EncodeConfig config) {
		this.config = config;
	}

	@Override
	public void encode(Model model) {
		for (Symbol symbol : model.getSymbols()) {
			encode(symbol);
		}
	}

	@Override
	public void encode(Symbol symbol) {
		if (symbol instanceof NamedType) {
			encode((NamedType) symbol);
		} else {
			encode((SymbolDeclaration) symbol);
		}
	}
	
	@Override
	public void encode(SymbolDeclaration symbol) {
		if (symbol.getTypeParameter().isEmpty()) {
			encode(ImlCustomFactory.INST.createSymbolReferenceTerm(symbol), new TypingEnvironment()); // Only for public symbol now
		}
	}
	
	public void encode(SymbolReferenceTerm symbolRef, TypingEnvironment env) {
		declareSymbolFunc(symbolRef, env);
		
		try { // TODO don't handle it here
			defineSymbolAssertion(symbolRef, env);
		} catch (SMTEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(NamedType type) {
		encodeType(type);
	}
	
	@Override
	public void encode(ImlType imlType) {
		encodeType(imlType);
	}
	
	private void encodeType(EObject type) {
		if (symbolTable.containsSort(type)) return;
		
		// Stage 1: define sorts
		if (type instanceof NamedType) {
			defineTypes((NamedType) type);
		} else if (type instanceof ImlType) {
			defineTypes((ImlType) type);
		} else {
			throw new IllegalArgumentException("Type should be either NamedType or ImlType only");
		}
		
		// Stage 2: declare functions
		try {
			declareFuncs();
		} catch (SMTEncodingException e) {
			e.printStackTrace();
		}
		
//		// Stage 3: define formulas
//		try { // TODO shouldn't be handled here
//			defineAssertions();
//		} catch (SMTEncodingException e) {
//			e.printStackTrace();
//		}
	}
	
	private void defineTypes(NamedType type) {
		defineTypes(type, new TypingEnvironment());
	}
	
	/**
	 * Define all sorts related to the given {@link NamedType}, this only cares about creating the required sorts
	 * Given a context means that there are bindings for this template type
	 * @param type
	 * @param context
	 */
	private void defineTypes(NamedType type, TypingEnvironment env) {
		if (env.getSelfContext() == null) {
			if (symbolTable.containsSort(type)) return;
			
			if (type.isTemplate()) return; // We don't encode template types without bindings

			addTypeSort(type);

			for (TypeWithProperties relationType : getRelatedTypesWithProp(type)) {
				defineTypes(env.bind(relationType.getType()));
			}
		} 
	}
	
	private void defineTypes(ImlType type) {
		if (symbolTable.containsSort(type)) return;
		// Check if acceptable type
		if (!ImlUtil.isFirstOrderFunction(type)) {
			throw new IllegalArgumentException("the type '" + getTypeName(type, qnp) + "' is not supported for SMT encoding.");
		}
		
		if (type instanceof FunctionType) { 
			defineTypes(((FunctionType) type).getDomain());
			defineTypes(((FunctionType) type).getRange());
		} else if (type instanceof ArrayType) {
			ArrayType arrType = (ArrayType) type;
			// Create new type for each dimension beside the main type
			for (int dim = 0; dim < arrType.getDimensions().size() ; dim++) {
				ImlType currentDim = typingServices.accessArray(arrType, dim);
				addTypeSort(currentDim);
			}
			defineTypes(arrType.getType());
		} else if (type instanceof TupleType) {
			// TODO handle empty tuple!
			TupleType tupleType = (TupleType) type;
			for (ImlType element : tupleType.getTypes()) {
				defineTypes(element);
			}
			addTypeSort(tupleType);
		} else if (type instanceof SimpleTypeReference) {
			SimpleTypeReference simpleRef = (SimpleTypeReference) type;
			defineTypes(simpleRef.getType());
			if (!simpleRef.getTypeBinding().isEmpty()) { // Type is a Template
				for (ImlType binding : simpleRef.getTypeBinding()) {
					defineTypes(binding);
				}
				// Encode the new type content, this is necessary in case that type contains symbols with 
				// new Higher Order Types that need to be created
				defineTypes(simpleRef.getType(), new TypingEnvironment(simpleRef));
				
				addTypeSort(simpleRef);
			}
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type.getClass().getName());
		}
	}

	/**
	 * This method is responsible for creating and adding a new sort for the given IML type
	 * @param type it can be NamedType or ImlType
	 * @return the created sort
	 */
	private void addTypeSort(EObject type) {
		if (type instanceof  Trait || (type instanceof SimpleTypeReference && ((SimpleTypeReference) type).getType() instanceof Trait)) {
			return;
		}
		String sortName = getUniqueName(type, null);
		SortT sort = null;
		if (type instanceof TupleType){
			sort = smtModelProvider.createTupleSort(sortName, getTupleSorts((TupleType) type));
		} else if (isEnum(type)) { // TODO Should check for other Restricitions
			sort = smtModelProvider.createEnum(sortName, ImlUtil.getLiterals((NamedType) type));
		} else {
			sort = smtModelProvider.createSort(sortName);
		}
		if (sort != null) {
			symbolTable.addSort(type, sort);
		}
	}

	
	private boolean isEnum(EObject type) {
		if (type instanceof NamedType) {
			return ImlUtil.isEnum((NamedType) type);
		} else if (type instanceof SimpleTypeReference) {
			return ImlUtil.isEnum(((SimpleTypeReference) type).getType());
		} else {
			return false;
		}
	}

	public SortT getOrCreateSort(EObject type, TypingEnvironment env) {
		if (type instanceof NamedType || env == null) {
			System.err.println("NamedType!!!!!");
			return symbolTable.getSort(type);
		} else if (type instanceof ImlType) {
			ImlType bindedType = env.bind((ImlType) type);
			SortT result = symbolTable.getSort(bindedType);
			if (result == null) {
				encode(bindedType);
				result = symbolTable.getSort(bindedType);
			}
			return result;
		} else {
			throw new IllegalArgumentException("Type can be only NamedType or ImlType. Found: " + type.getClass());
		}
	}
	
//	private void defineAssertions() throws SMTEncodingException {
//		for (Entry<EncodedId, FuncDeclT> entry: new HashSet<>(symbolTable.getFunDeclsMap().entrySet())) {
//			EObject container = entry.getKey().getImlContainer();
//			TypingEnvironment env = new TypingEnvironment();
//			if (container instanceof SimpleTypeReference) {
//				env.addContext((SimpleTypeReference) container);
//			} else if(container instanceof NamedType) {
//				System.err.println("Found NamedType in the table!");
//				env.addContext((NamedType) container);
//			}
//
//			EObject symbol = entry.getKey().getImlObject();
//			
//			if (symbol instanceof SymbolDeclaration) {
//				if (((SymbolDeclaration) symbol).getDefinition() != null) {
//					defineSymbolAssertion(ImlCustomFactory.INST.createSymbolReferenceTerm((SymbolDeclaration) symbol), env);
//				}
//			} else if (symbol instanceof SymbolReferenceTerm) {
//				if (((SymbolDeclaration)((SymbolReferenceTerm) symbol).getSymbol()).getDefinition() != null) {
//					defineSymbolAssertion((SymbolReferenceTerm) symbol, env);
//				}
//			}
//		}
//	}

	private void defineSymbolAssertion(SymbolReferenceTerm symbolRef, TypingEnvironment env) throws SMTEncodingException {
		SymbolDeclaration symbol = (SymbolDeclaration) symbolRef.getSymbol();
		if (!(symbol instanceof Assertion) && config.allowFunDef()) {
			return; // No need to add the assertion
		}
		if (symbol.getDefinition() == null) return;
		env.addContext(symbolRef); // TODO is it needed?
		
		FormulaT inst = isGlobalSymbol(symbol)? null : smtModelProvider.createFormula(INST_NAME);
		FormulaT definitionEncoding = encodeFormula(symbol.getDefinition(), env, inst, new ArrayList<>());
		EObject container = null;
		if (!isGlobalSymbol(symbol)) {
			container = env.getSelfContext();
		}
					
		if (!(symbol instanceof Assertion))  {
			List<FormulaT> functionParams = getFunctionParameterList(symbol, env, false);
			FuncDeclT symbolFuncDecl = null;
			symbolFuncDecl = getOrCreateSymbolDeclFun(symbolRef, env);
			
			FormulaT symbolAccess = smtModelProvider.createFormula(symbolFuncDecl, functionParams);
			
			definitionEncoding = smtModelProvider.createFormula(OperatorType.EQ, Arrays.asList(symbolAccess, definitionEncoding));	
		} 
		FormulaT assertBody;
		if (symbol instanceof Assertion && isGlobalSymbol(symbol)) {
			assertBody = definitionEncoding;
		} else {
			List<FormulaT> forallScope = getFunctionParameterList(symbol, env, true);
			FormulaT forall = smtModelProvider.createFormula(OperatorType.FOR_ALL, 
					Arrays.asList(smtModelProvider.createFormula(forallScope), definitionEncoding));
			assertBody = forall;
		}
		FormulaT assertion = smtModelProvider.createFormula(OperatorType.ASSERT, Arrays.asList(assertBody));
		symbolTable.addFormula(container, symbolRef, assertion);
	}

	private List<FormulaT> getFunctionParameterList(SymbolDeclaration symbol, TypingEnvironment env, boolean withTypes) {
		FolFormula definition = symbol.getDefinition();
		if (definition instanceof SignedAtomicFormula) {
			definition = definition.getLeft();
		}
		FormulaT inst = isGlobalSymbol(symbol)? null : smtModelProvider.createFormula(INST_NAME);
		List<FormulaT> result = new ArrayList<>();
		if (!isGlobalSymbol(symbol)) {
			if (withTypes) {
				SimpleTypeReference container = env.getSelfContext();
				result.add(smtModelProvider.createFormula(INST_NAME, getOrCreateSort(container, env)));
			} else {
				result.add(inst);
			}
		}
		if (definition instanceof LambdaExpression) {
			for (SymbolDeclaration param : ((LambdaExpression) definition).getParameters()) {
				if (withTypes) {
					result.add(smtModelProvider.createFormula(param.getName(), getOrCreateSort(param.getType(), env)));
				} else {
					result.add(smtModelProvider.createFormula(param.getName()));
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param symbol the symbol which might be using template e.g T~>P
	 * @param context null or the type reference with the actual binding e.g Pair<Int,Real>
	 * @return the same symbol type if context is null or the binding of the type with the context
	 */
	private ImlType getActualType(SymbolDeclaration symbol, TypingEnvironment env) {
		return typeProvider.getSymbolType(symbol, env);
	}

	/**
	 * This method creates all necessary function declarations for all sorts already defined
	 * @throws SMTEncodingException 
	 */
	private void declareFuncs() throws SMTEncodingException {
		// Go over all sorts that already defined
		List<EncodedId> types = symbolTable.getEncodedIds();
		for (EncodedId container : types) {
			EObject type = container.getImlObject();
			if (type instanceof NamedType) {
				declareFuncs((NamedType) type);
			} else if (type instanceof ArrayType) {
				declareFuncs((ArrayType) type);
			} else if (type instanceof SimpleTypeReference) {
				declareFuncs((SimpleTypeReference) type);
			}
		}
		
	}

	/***
	 * Having {@link SimpleTypeReference} as a type in symbol table means it includes bindings 
	 * which means we need to take in consideration the bindings
	 * @param type
	 * @throws SMTEncodingException 
	 */
	private void declareFuncs(SimpleTypeReference type) throws SMTEncodingException {
		if (!type.getTypeBinding().isEmpty()) {
			declareFuncs(type.getType(), new TypingEnvironment(type));
		}
	}

	/**
	 * Declare all required functions for the given {@link NamedType}
	 * @param container
	 * @throws SMTEncodingException 
	 */
	private void declareFuncs(NamedType container) throws SMTEncodingException {
		declareFuncs(container, new TypingEnvironment(container));
	}
	
	/**
	 * Create all required functions for the given type. If context is provided then it will be used for any required template binding inside the type
	 * @param container with templates e.g {@code type Pair<T,P>} or without templates e.g {@code type System}
	 * @param context null or actual binding e.g {@code var1 : Pair<Int, Real>}
	 * @throws SMTEncodingException 
	 */
	private void declareFuncs(NamedType container, TypingEnvironment env) throws SMTEncodingException {
		SimpleTypeReference encodingContainer = env.getSelfContext();
		// encode type symbols first to enable shadowing
		for (SymbolDeclaration symbol : container.getSymbols()) {
			if (!symbol.getTypeParameter().isEmpty()) continue; // We don't encode polymorphic symbols now, only when we find symbolref with binding
			
			if (! (symbol instanceof Assertion)) {
				declareSymbolFunc(ImlCustomFactory.INST.createSymbolReferenceTerm(symbol), env);
			} 
			defineSymbolAssertion(ImlCustomFactory.INST.createSymbolReferenceTerm(symbol), env);
		}
		
		List<AtomicRelation> relations = relationsToAtomicRelations(container.getRelations());
		for (AtomicRelation relation : relations) {
			String funName = getUniqueName(relation, encodingContainer);
			if (relation.getRelation() instanceof Alias) {
				ImlType aliasType = ((Alias) relation.getRelation()).getType().getType();
				FuncDeclT aliasFunc = smtModelProvider.createFuncDecl(funName, Arrays.asList(getOrCreateSort(encodingContainer, env)), getOrCreateSort(aliasType, env));
				symbolTable.addFunDecl(encodingContainer, relation, aliasFunc);
			} else if (relation.getRelation() instanceof Inclusion) {
				FuncDeclT extensionFunc = smtModelProvider.createFuncDecl(funName, Arrays.asList(getOrCreateSort(encodingContainer, env)), getOrCreateSort(relation.getRelatedType(), env));
				symbolTable.addFunDecl(encodingContainer, relation, extensionFunc);
			} else if (relation.getRelation() instanceof TraitExhibition) {
				declareFuncs(((SimpleTypeReference) relation.getRelatedType()).getType(), env.clone().addContext((SimpleTypeReference) relation.getRelatedType()));
			}
		}
	}

	private void declareSymbolFunc(SymbolReferenceTerm symbolRef, TypingEnvironment env) {
		if (symbolRef.getSymbol() instanceof Assertion) {
			return;
		}
		
		SimpleTypeReference encodingContainer = env.getSelfContext();
		String funName;
		SymbolDeclaration symbol = (SymbolDeclaration) symbolRef.getSymbol();
		if (isGlobalSymbol(symbol)) {
			funName = getUniqueName(symbolRef, null);
		} else {
			funName = getUniqueName(symbolRef, encodingContainer);
		}
		
		ImlType symbolType = typeProvider.getSymbolType(symbol, env); // e.g if it was T~>P then maybe Int~>Real
		
		
		SortT funOutoutSort = null;
		List<SortT> funInputSorts = new ArrayList<>();
		
		if (!isGlobalSymbol(symbol)) {
			funInputSorts.add(getOrCreateSort(encodingContainer, env));
		}
		
		if (symbolType instanceof FunctionType) { // Encode it as a function
			funInputSorts.addAll(getDomainSorts(((FunctionType) symbolType).getDomain(), env));
			funOutoutSort = getOrCreateSort(((FunctionType) symbolType).getRange(), env);
		} else { // Symbol is not a function
			funOutoutSort = getOrCreateSort(symbolType, env);
		}

		FuncDeclT symbolFunDecl;
		if (symbol.getDefinition() != null && config.allowFunDef()) {
			// use define-fun
			FormulaT inst = isGlobalSymbol(symbol)? null : smtModelProvider.createFormula(INST_NAME);
			FormulaT funcDef;
			try {
				funcDef = encodeFormula(symbol.getDefinition(), env, inst, new ArrayList<>());
			} catch (SMTEncodingException e) {
				e.printStackTrace();
				return; // TODO handle this better
			}
			List<FormulaT> inputParams = getFunctionParameterList(symbol, env, true);
			symbolFunDecl = smtModelProvider.createFuncDef(funName, inputParams, funOutoutSort, funcDef);
		} else {
			// use declare-fun
			symbolFunDecl = smtModelProvider.createFuncDecl(funName, funInputSorts, funOutoutSort);
		}
		if (!isGlobalSymbol(symbol)) {
			symbolTable.addFunDecl(encodingContainer, symbolRef, symbolFunDecl); 
		} else { // Public symbol container is the model
			symbolTable.addFunDecl(symbol.eContainer(), symbolRef, symbolFunDecl);
		}
	}

	private Collection<? extends SortT> getDomainSorts(ImlType domain, TypingEnvironment env) {
		List<SortT> sorts = new ArrayList<>();
		if (domain instanceof TupleType) {
			for (ImlType tType : ((TupleType) domain).getTypes()) {
				sorts.add(getOrCreateSort(tType, env));
			}
		} else {
			sorts.add(getOrCreateSort(domain, env));
		}
		return sorts;
	}

	/**
	 * Create an array access function declaration
	 * @param type 
	 */
	private void declareFuncs(ArrayType type) {
		String funName = getUniqueName(type, null) + ARRAY_SELECT_FUNC_NAME;
		FuncDeclT arraySelectFunc = smtModelProvider.createFuncDecl(
				funName, Arrays.asList(getOrCreateSort(type, null), symbolTable.getPrimitiveSort("Int")), 
				getOrCreateSort(typingServices.accessArray(type, 1), null));
		symbolTable.addFunDecl(null, type, arraySelectFunc);
	}

	private List<SortT> getTupleSorts(TupleType type) {
		List<SortT> sorts = new ArrayList<>();
		for (ImlType  element : type.getTypes()) {
			sorts.add(getOrCreateSort(element, null));
		}
		return sorts;
	}
	
	public FormulaT encodeFormula(FolFormula formula, TypingEnvironment env, FormulaT inst, List<SymbolDeclaration> scope) throws SMTEncodingException {
		if (scope == null) {
			scope = new ArrayList<>();
		}
		// TODO need to refactor createFormula functions, we shouldn't assume the internal structure of it
		FormulaT leftFormula = null;
		FormulaT rightFormula = null; 
		if (formula.getLeft() != null) {
			leftFormula = encodeFormula(formula.getLeft(), env.clone(), inst, scope);
		}
		if (formula.getRight() != null) {
			rightFormula = encodeFormula(formula.getRight(), env.clone(), inst, scope);
		}
		
		if (formula instanceof ParenthesizedTerm) {
			return encodeFormula(((ParenthesizedTerm) formula).getSub(), env, inst, scope);
		} else if (formula.getOp() != null && !formula.getOp().isEmpty()) {
			OperatorType op = OperatorType.parseOp(formula.getOp());
			
			if (op == OperatorType.IMPL) { 
				return smtModelProvider.createFormula(op, Arrays.asList(leftFormula, rightFormula));
			} else if (op == OperatorType.EQUIV) {
				FormulaT l2r = smtModelProvider.createFormula(op, Arrays.asList(leftFormula, rightFormula));
				FormulaT r2l = smtModelProvider.createFormula(op, Arrays.asList(rightFormula, leftFormula));
				return smtModelProvider.createFormula(OperatorType.AND, Arrays.asList(l2r, r2l));
			} else if (op == OperatorType.FOR_ALL || op == OperatorType.EXISTS) {
				QuantifiedFormula quantFormula = (QuantifiedFormula) formula;
				List<FormulaT> scopeFormulas = quantFormula.getScope().stream().map(symbol -> {
					String typeName = getUniqueName(symbol.getType(), null);
					return smtModelProvider.createFormula(Arrays.asList(smtModelProvider.createFormula(symbol.getName()),
								smtModelProvider.createFormula(typeName)));
				}).collect(Collectors.toList());
				
				scope.addAll(quantFormula.getScope());
				leftFormula = encodeFormula(formula.getLeft(), env, inst, scope);
				
				return smtModelProvider.createFormula(op, Arrays.asList(smtModelProvider.createFormula(scopeFormulas), leftFormula));
			} else if (op == OperatorType.AND || op == OperatorType.OR) {
				return smtModelProvider.createFormula(op, Arrays.asList(leftFormula, rightFormula));
			}
		} else if (formula instanceof SignedAtomicFormula) {
			if (((SignedAtomicFormula) formula).isNeg()) {
				return smtModelProvider.createFormula(OperatorType.NOT, Arrays.asList(leftFormula));
			} else {
				return leftFormula;
			}
		} else if (formula instanceof AtomicExpression) {
			OperatorType op = OperatorType.parseOp(((AtomicExpression) formula).getRel().getLiteral());
			// TODO why AtmoicExpression has * instead of ?
			// Special case for !=
			if (op == OperatorType.NEQ) {
				return smtModelProvider.createFormula(OperatorType.NOT, 
						Arrays.asList(smtModelProvider.createFormula(OperatorType.EQ, Arrays.asList(leftFormula, rightFormula))));
			} else {
				return smtModelProvider.createFormula(op, Arrays.asList(leftFormula, rightFormula));
			}
		} else if (formula instanceof Addition) {
			return smtModelProvider.createFormula(OperatorType.parseOp(((Addition) formula).getSign()), Arrays.asList(leftFormula, rightFormula));
		} else if (formula instanceof Multiplication) {
			return smtModelProvider.createFormula(OperatorType.parseOp(((Multiplication) formula).getSign()), Arrays.asList(leftFormula, rightFormula));
		} else if (formula instanceof TermMemberSelection) {
			TermExpression receiver = ((TermMemberSelection) formula).getReceiver();
			TermExpression member = ((TermMemberSelection) formula).getMember();
			if (isEnumSelection((TermMemberSelection) formula)) {
				NamedType enumType = (NamedType) ((SymbolReferenceTerm)receiver).getSymbol();
				encode(enumType);
				return smtModelProvider.createFormula(((SymbolReferenceTerm)member).getSymbol().getName());
			}
			
			FormulaT receiverFormula = encodeFormula(receiver, env.clone(), inst, scope);
			ImlType receiverType = typeProvider.termExpressionType(receiver, env.clone());
			if (receiverType instanceof SimpleTypeReference) {
				return encodeFormula(member, env.clone().addContext((SimpleTypeReference) receiverType), receiverFormula, scope);
			}
		} else if (formula instanceof NumberLiteral) {
			if (((NumberLiteral) formula).isNeg()) {
				FormulaT valueFormula = smtModelProvider.createFormula(((NumberLiteral) formula).getValue());
				return smtModelProvider.createFormula(OperatorType.NEGATIVE, Arrays.asList(valueFormula));
			} else {
				return smtModelProvider.createFormula(((NumberLiteral) formula).getValue());
			}
		} else if (formula instanceof FloatNumberLiteral) {
			if (((FloatNumberLiteral) formula).isNeg()) {
				FormulaT valueFormula = smtModelProvider.createFormula(((FloatNumberLiteral) formula).getValue());
				return smtModelProvider.createFormula(OperatorType.NEGATIVE, Arrays.asList(valueFormula));
			} else {
				return smtModelProvider.createFormula(((FloatNumberLiteral) formula).getValue());
			}
		} else if (formula instanceof TupleConstructor) {
			// TODO
			ImlType tupleType = typeProvider.termExpressionType(formula, env.clone());
			if (tupleType instanceof TupleType) {
				SortT sort = getOrCreateSort(tupleType, env.clone());
				List<FormulaT> tupleFormulas = encodeTupleElements((TupleConstructor) formula, env, inst, scope);
				
				return smtModelProvider.createFormula(tupleFormulas);
			}
			
		} else if (formula instanceof TailedExpression) {
			TailedExpression tailedExpr = (TailedExpression) formula;
			ExpressionTail tail = tailedExpr.getTail();
	
			if (tail instanceof ArrayAccess) {
				ImlType leftType = typeProvider.termExpressionType(formula.getLeft(), env);
				FuncDeclT arrayAccessFun = getFuncDeclaration(leftType, null);
				return smtModelProvider.createFormula(arrayAccessFun, Arrays.asList(leftFormula));
			} else if (tail instanceof TupleConstructor) {
				List<FormulaT> paramFormulas = new ArrayList<>();
				paramFormulas.add(leftFormula);
				for (FolFormula element : ((TupleConstructor) tail).getElements()) {
					paramFormulas.add(encodeFormula(element, env, inst, scope));
				}
				return smtModelProvider.createFormula((OperatorType) null, paramFormulas);
			}
		
		} else if (formula instanceof SymbolReferenceTerm) {
			SymbolReferenceTerm symbolRef = (SymbolReferenceTerm) formula;
			if (symbolRef.getSymbol() instanceof SymbolDeclaration 
					&& ((SymbolDeclaration) symbolRef.getSymbol()).getType() instanceof FunctionType
					&& !(symbolRef.eContainer() instanceof TailedExpression)) { // Using function type without being inside tailed expression
//				throw new SMTEncodingException(((SymbolDeclaration) symbolRef.getSymbol()).getName() + " function can't be used as a variable");
			}
			FormulaT symbolRefFormula = getSymbolAccessFormula(symbolRef, env.clone(), inst, scope);
			return symbolRefFormula;
		} else if (formula instanceof InstanceConstructor) {
			// We create a separate symbol declaration for this instance
			InstanceConstructor instanceConstructor = (InstanceConstructor) formula;
			SymbolDeclaration instanceRef = instanceConstructor.getRef();
			ImlType instanceType = env.bind(instanceRef.getType());
			SortT contextSort = EcoreUtil2.getContainerOfType(formula, NamedType.class) != null? getOrCreateSort(env.getSelfContext(), env.clone()) : null;
			SortT outputSort = getOrCreateSort(instanceRef.getType(), env.clone());
			
			// Create a function for the instance constructor inside the same context
			List<SortT> inputSorts = new ArrayList<>();
			if (contextSort != null) {
				inputSorts.add(contextSort);
			}
			inputSorts.addAll(scope.stream().map(symbol -> getOrCreateSort(symbol.getType(), env.clone())).collect(Collectors.toList()));
			String funName = getUniqueName(instanceConstructor, instanceType);
			FuncDeclT instanceConstructorFun = smtModelProvider.createFuncDecl(funName, inputSorts, outputSort);
			symbolTable.addFunDecl(instanceType, instanceConstructor, instanceConstructorFun);
			
			// Add assertion for the created function with the encoded definition if InstanceConstructor
			List<FormulaT> forallScope = new ArrayList<>();
			if (contextSort != null) {
				forallScope.add(smtModelProvider.createFormula(INST_NAME, contextSort));
			}
			forallScope.addAll(scope.stream().map(symbol -> smtModelProvider.createFormula(
					symbol.getName(), getOrCreateSort(symbol.getType(), env.clone()))).collect(Collectors.toList()));
			
			String newInst = ((inst != null)? inst + " " : "") + scope.stream().map(symbol -> symbol.getName()).reduce((acc, curr) -> acc + " " + curr).orElse(""); // TODO really bad hack, need better solution
			
			FormulaT instanceConsDef = encodeFormula(instanceConstructor.getDefinition(), env, smtModelProvider.createFormula(newInst), scope);
			instanceConsDef = smtModelProvider.createFormula(OperatorType.ASSERT, 
								Arrays.asList(smtModelProvider.createFormula(OperatorType.FOR_ALL, Arrays.asList(
								smtModelProvider.createFormula(forallScope)
								, instanceConsDef))));
			
			symbolTable.addFormula(instanceType, instanceConstructor, instanceConsDef);
			
			// Return the created instance function declaration
			List<FormulaT> params = new ArrayList<>();
			if (inst != null) {
				params.add(inst);
			}
			params.addAll(scope.stream().map(symbol -> smtModelProvider.createFormula(symbol.getName())).collect(Collectors.toList()));
			return smtModelProvider.createFormula(instanceConstructorFun, params);
		} else if (formula instanceof IteTermExpression) {
			FolFormula condition = ((IteTermExpression) formula).getCondition();
			FormulaT conditionFormua = encodeFormula(condition, env, inst, scope);
			if (formula.getRight() != null) {
				return smtModelProvider.createFormula(OperatorType.ITE, Arrays.asList(conditionFormua, leftFormula, rightFormula));
			} else {
				if (isAssertion(formula)) {
					return smtModelProvider.createFormula(OperatorType.IMPL, Arrays.asList(conditionFormua, leftFormula));
				} else {
					throw new SMTEncodingException("If-then-else must include else clause if it is used in an assignment");
				}
			}
			
		} else if (formula instanceof SelfTerm) {
			// Not used for now
		} else if (formula instanceof TruthValue) {
			return smtModelProvider.createFormula(((TruthValue) formula).isTRUE());
		} else if (formula instanceof SequenceTerm) {
			scope.addAll(((SequenceTerm) formula).getDefs());
			FormulaT returnFormula = encodeFormula(((SequenceTerm) formula).getReturn(), env.clone(), inst, scope);
			
			if (!isNullOrEmpty(((SequenceTerm) formula).getDefs())) {
				for (int i=((SequenceTerm) formula).getDefs().size()-1 ; i >= 0 ; i--) {
					SymbolDeclaration currentSymbol = ((SequenceTerm) formula).getDefs().get(i);
					FormulaT binderFormula = smtModelProvider.createFormula(Arrays.asList(
							smtModelProvider.createFormula(Arrays.asList(
									smtModelProvider.createFormula(currentSymbol.getName()), encodeFormula(currentSymbol.getDefinition(), env, inst, scope)))));
					returnFormula = smtModelProvider.createFormula(OperatorType.LET, Arrays.asList(binderFormula, returnFormula));
			
				}
			}
			return returnFormula;
		} else if (formula instanceof LambdaExpression) {
			scope.addAll(((LambdaExpression) formula).getParameters());
			return encodeFormula(((LambdaExpression) formula).getDefinition(), env, inst, scope);
		} else {
			throw new SMTEncodingException("Unsupported formula: " + formula);
		}
		throw new SMTEncodingException("Couldn't encode the formula to SMT!" + formula);
	}

	private boolean isEnumSelection(TermMemberSelection formula) {
		if (formula.getReceiver() instanceof SymbolReferenceTerm && ((SymbolReferenceTerm) formula.getReceiver()).getSymbol() instanceof NamedType) {
			NamedType reciever = (NamedType) ((SymbolReferenceTerm) formula.getReceiver()).getSymbol();
			List<String> literals = ImlUtil.getLiterals(reciever);
			return formula.getMember() instanceof SymbolReferenceTerm && literals.contains(((SymbolReferenceTerm) formula.getMember()).getSymbol().getName());
		}
		return false;
	}

	private boolean isAssertion(FolFormula formula) {
		return EcoreUtil2.getContainerOfType(formula, Assertion.class) != null;
	}

	private List<FormulaT> encodeTupleElements(TupleConstructor tail, TypingEnvironment env, FormulaT inst, List<SymbolDeclaration> scope) throws SMTEncodingException {
		List<FormulaT> encodedFormulas = new ArrayList<>();
		for (FolFormula formula : tail.getElements()) {
			encodedFormulas.add(encodeFormula(formula, env, inst, scope));
		}
		return encodedFormulas;
	}

	private FormulaT getSymbolAccessFormula(SymbolReferenceTerm symbolRef, TypingEnvironment env, FormulaT inst, List<SymbolDeclaration> scope) {
		// Check the scope first
		if ((scope != null && scope.contains(symbolRef.getSymbol())) /*||
			isGlobalSymbol(symbolRef.getSymbol())*/) {
			return smtModelProvider.createFormula(getUniqueName(symbolRef.getSymbol(), null));
		}
		
		if (symbolRef.getSymbol() instanceof NamedType) { // Check for NamedType symbol
			// TODO We only support enum now
			encode((NamedType) symbolRef.getSymbol());
		}
		
		FuncDeclT symbolAccess = getOrCreateSymbolDeclFun(symbolRef, env);
		// Handle super types
		if (symbolAccess == null) {
			for (AtomicRelation relation : relationsToAtomicRelations(env.getTypeContext().getType().getRelations())) {
				if (relation.getRelatedType() instanceof SimpleTypeReference) {
					SimpleTypeReference parent = (SimpleTypeReference) relation.getRelatedType();
					FormulaT newInst = inst;
					if (!(relation.getRelation() instanceof TraitExhibition)) {
						FuncDeclT relationFunction = getFuncDeclaration(env.getSelfContext(), relation); // Get the function declaration for current relation
						newInst = smtModelProvider.createFormula(relationFunction, Arrays.asList(inst));
					}
					FormulaT parentSymbolAccess = getSymbolAccessFormula(symbolRef, env.clone().addContext(parent), newInst, scope);
					if (parentSymbolAccess != null) {
						return parentSymbolAccess;
					}
				}
			}
			return null; 
		} else {
			if (isGlobalSymbol(symbolRef.getSymbol())) {
				return smtModelProvider.createFormula(symbolAccess, null);
			} else {
				return smtModelProvider.createFormula(symbolAccess, Arrays.asList(inst));
			}
		}
	}

	private List<AtomicRelation> relationsToAtomicRelations(EList<Relation> relations) {
		List<AtomicRelation> atomicRelations = new ArrayList<>();
		for (Relation relation : relations) {
			if (relation instanceof Alias) {
				atomicRelations.add(new AtomicRelation(relation, ((Alias) relation).getType().getType()));
			} else {
				List<TypeWithProperties> types = new ArrayList<>();
				if (relation instanceof Inclusion) {
					types = ((Inclusion) relation).getInclusions();
				} else if (relation instanceof TraitExhibition){
					types = ((TraitExhibition) relation).getExhibitions();
				} else if (relation instanceof Refinement) {
					types = ((Refinement) relation).getRefinements();
				}
				for (TypeWithProperties type : types) {
					atomicRelations.add(new AtomicRelation(relation, type.getType()));
				}
			}
		}
		return atomicRelations;
	}

	/**
	 * This method tries to get function declaration that access the given symbol. It will also create that function
	 * in case the symbol was a global variable and wasn't encoded
	 * @param symbolDecl
	 * @return
	 */
	private FuncDeclT getOrCreateSymbolDeclFun(SymbolReferenceTerm symbolRef, TypingEnvironment env) {
		Symbol symbol = symbolRef.getSymbol();
		// Handle instance constructor case
		if (symbol.eContainer() instanceof InstanceConstructor) {
			return symbolTable.getFunDecl(env.bind(((SymbolDeclaration)symbol).getType()), symbol.eContainer());
		}
		
		EObject container = null;
		if (isGlobalSymbol(symbol)) {
			container = symbol.eContainer();
		} else {
			container = env.getSelfContext();
		}
		
		FuncDeclT funDecl = symbolTable.getFunDecl(container, symbolRef);
//		if (funDecl == null) { // Check for shadowing
//			funDecl = symbolTable.getFunDecl(container, symbolRef);
//		}
		if (funDecl == null && symbol instanceof SymbolDeclaration && 
				(isSymbolInsideContainer(symbol, container) || isSymbolInsideContainer(symbol, env.getTypeContext()))) {
			encode(symbolRef, env.clone().addContext(symbolRef));
			funDecl = getFuncDeclaration(container, symbolRef);
		}
		return funDecl;
	}

	private boolean isSymbolInsideContainer(Symbol symbol, EObject container) {
		if (container instanceof Model) {
			return ((Model) container).getSymbols().contains(symbol);
		} else if (container instanceof NamedType) {
			return ((NamedType) container).getSymbols().contains(symbol);
		} else if (container instanceof SimpleTypeReference) {
			return isSymbolInsideContainer(symbol, ((SimpleTypeReference) container).getType());
		}
		return false;
	}

	private String getUniqueName(EObject type, EObject container) {
		return symbolTable.getUniqueId(type, container);
	}


	public List<SortT> getAllSorts() {
		return symbolTable.getSorts();
	}

	public List<FuncDeclT> getAllFuncDeclarations() {
		return symbolTable.getFunDecls();
	}

	public FuncDeclT getFuncDeclaration(EObject container, EObject imlObject) {
		return symbolTable.getFunDecl(container,  imlObject);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (SortT sort : symbolTable.getSorts()) {
			if (!SmtStandardLib.isNative(sort.getName())) {
				sb.append(sort + "\n");
			}
		}
		
		for (FuncDeclT funDecl : symbolTable.getFunDecls()) {
			sb.append(funDecl + "\n");
		}
		
		for (FormulaT formula : symbolTable.getAllFormulas()) {
			sb.append(formula + "\n");
		}
		
		return sb.toString();
	}

	public FormulaT encodeFormula(FolFormula formula, SymbolDeclaration symbol) throws SMTEncodingException {
		if (symbol == null) {
			return encodeFormula(formula, new TypingEnvironment(), null, new ArrayList<>());
		} else {
			return encodeFormula(formula, new TypingEnvironment((SimpleTypeReference) symbol.getType()),  smtModelProvider.createFormula(symbol.getName()), new ArrayList<>());
		}
	}
	
	/**
	 * Reset the encoder to allow encoding new models
	 */
	public void reset() {
		symbolTable.clear();
		aliases.clear();
	}
	
}
 