package com.sri.iml.gen.mcmt;

import static com.sri.iml.gen.mcmt.MCMTranslationProvider.getLiterals;
import static com.sri.iml.gen.mcmt.MCMTranslationProvider.isEnum;

import java.util.Collection;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.inject.Inject;
import com.sri.iml.gen.mcmt.model.*;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.Extension;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Relation;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.iml.TailedExpression;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.util.Phi;

public class MCMTGenerator {
	
	@Inject
	ImlTypeProvider typeProvider;
	
	@Inject
	MCMTGeneratorServices generatorServices;

	@Inject
	private ImlStdLib libs;

	StateVarBuilder stateVarBuilder;
	TransVarBuilder transVarBuilder;
	FormulaGenerator<StateFormulaVariable> stateFormGenerator;
	FormulaGenerator<StateTransFormulaVariable> transFormGenerator;

	// This is the top-level function: it converts an ImlType into an MCMT problem
	public MCMT generate(ImlType imltype) throws GeneratorException {
		if (!(imltype instanceof SimpleTypeReference)) {
			throw new GeneratorException("HIGHER_ORDER_TYPE_NOT_PART_OF_MCMT "+imltype.toString());
		}
		SimpleTypeReference tr = (SimpleTypeReference) imltype;
		/* if (m.hasType(type_name))
			return m.getType(type_name); */
		if (isEnum(tr.getType())) {
			throw new GeneratorException("ENUM_TYPES_NOT_PART_OF_MCMT "+tr.toString());
		}
		for (ImlType b : tr.getTypeBinding()) {
	        throw new GeneratorException("TYPE BINDINGS NOT PART OF MCMT "+tr.toString());
			// generateType(m, b);
		}
		MCMT result = new MCMT();
		stateVarBuilder = new StateVarBuilder(result);
		transVarBuilder = new TransVarBuilder(result);
		stateFormGenerator = new FormulaGenerator<StateFormulaVariable>(stateVarBuilder);
		transFormGenerator = new FormulaGenerator<StateTransFormulaVariable>(transVarBuilder);
		if (!isStateMachine(tr)) {
	        throw new GeneratorException("No SM (State Machine) annotation for "+tr.toString());
		}
		NamedTransitionSystem system = generateSystem(result, tr);
		generateQueries(result, system, tr);
		return result;
	}

	// Translating transition systems
	private NamedTransitionSystem generateSystem(MCMT target, SimpleTypeReference tr) throws GeneratorException {
		String name = generatorServices.getNameFor(tr);
		NamedStateType statetype = generateStateType(target,"_state", tr);
		Sexp<FormulaAtom<StateFormulaVariable>> init = stateVarBuilder.symbol("true");
		Sexp<FormulaAtom<StateTransFormulaVariable>> transition = transVarBuilder.symbol("true");
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				try {
					ImlType imlType = typeProvider.bind(sd.getType(), tr);
					BaseType basetype = generateBaseType(generatorServices.getNameFor(imlType));
					if (basetype.equals(BaseType.Bool) && (sd.getDefinition() != null)) {
						// Trying to parse it as state formula
						try {
							Sexp<FormulaAtom<StateFormulaVariable>> state_tmp
								= stateFormGenerator.generate(sd.getDefinition(),statetype,target);
							NamedStateFormula state_namedForm = new NamedStateFormula(sd.getName(),statetype,state_tmp); 
							target.addStateFormula(state_namedForm);
							// target.addStateTransition(namedForm.convert(StateNext.State));
							target.addStateTransition(state_namedForm.convert(StateNext.Next));
							if (isInit(sd)) { init = stateVarBuilder.variable(state_namedForm.toString()); }
						}
						catch(GeneratorException e) {}
						// Trying to parse it as transition formula
						try {
							Sexp<FormulaAtom<StateTransFormulaVariable>> trans_tmp
								= transFormGenerator.generate(sd.getDefinition(),statetype,target);
							NamedStateTransition trans_namedForm = new NamedStateTransition(sd.getName(),statetype,trans_tmp); 
							target.addStateTransition(trans_namedForm);
							if (isTransition(sd)) { transition = transVarBuilder.variable(trans_namedForm.toString()); }
						}
						catch(GeneratorException e) {}
					}
				} catch(GeneratorException e) { /* e.printStackTrace();*/ }
			}
		}
		NamedTransitionSystem result = new NamedTransitionSystem(name, statetype, init); 
		result.add_transition(transition);
		target.addTransitionSystem(result);
		return result;
	}
	
	// Translating state types
	private NamedStateType generateStateType(MCMT target, String suffix,SimpleTypeReference tr) throws GeneratorException {
		String name = generatorServices.getNameFor(tr) + suffix;
		Record<Input> inputs = new Record<Input>();
		Record<StateVariable> statevars = new Record<StateVariable>();
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				String sdname = sd.getName();
				if (isState(sd)) {
					ImlType tmp = typeProvider.bind(((SimpleTypeReference) sd.getType()).getTypeBinding().get(0), tr);
					BaseType basetype = generateBaseType(generatorServices.getNameFor((SimpleTypeReference) tmp));
					statevars.add_field(new StateVariable(sdname,basetype));
				}
				if (isInput(sd)) {
					ImlType imlType = typeProvider.bind(sd.getType(), tr);
					BaseType basetype = generateBaseType(generatorServices.getNameFor(imlType));
					inputs.add_field(new Input(sdname,basetype));
				}
			}
		}
		NamedStateType result = new NamedStateType(name, statevars, inputs); 
		target.addStateType(result);
		return result;
	}

	// Translating the initial formula
	private void generateQueries(MCMT target, NamedTransitionSystem system, SimpleTypeReference tr) throws GeneratorException {
		NamedStateType statetype = system.getStateType();
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				if (isInvariantQuery(sd)) {
					ImlType imlType = typeProvider.bind(sd.getType(), tr);
					BaseType basetype = generateBaseType(generatorServices.getNameFor(imlType));
					assert(basetype.equals(BaseType.Bool));
					Query query = new Query(system,stateFormGenerator.generate(sd.getDefinition(),statetype,target));
					target.addQuery(query);
				}
			}
		}
	}

	// Translating base types
	private BaseType generateBaseType(String type_name) throws GeneratorException {
		if (type_name.equals("iml.lang.Bool")) return BaseType.Bool;
		if (type_name.equals("iml.lang.Int")) return BaseType.Int;
		if (type_name.equals("iml.lang.Real")) return BaseType.Real;
		throw new GeneratorException("This is not an MCMT base type: "+type_name);
	}


	/*
	private void addSymbols(MCMT target, SimpleTypeReference tr) {
		if (tr.getType().getRelations() != null) {
      if (tr.getType().getRelations().isEmpty())
        throw new GeneratorException("TYPE RELATIONS NOT PART OF MCMT "+tr.toString());
		}
	}

	
	private void generateSymbolDeclaration(MCMT m, SymbolDeclaration sd, SimpleTypeReference ctx) {
			
		ImlType bound = null;
		String name = null;
		if (sd instanceof Assertion) {
			if (sd.getName() != null) {
				name = sd.getName();
			} else {
				name = m.getContainer().newSymbolName();
			}
			bound = ImlCustomFactory.INST.createSimpleTypeReference(libs.getBoolType());
		} else {
			name = sd.getName();
			bound = typeProvider.bind(sd.getType(), ctx);
		}
		[...]


		if (sd instanceof Assertion) {
			MCMT nbound = generateType(m.getContainer(), bound);
			}
	}
*/	

	public boolean isStateMachine(SimpleTypeReference tr) {
		return MCMTranslationProvider.isA(tr.getType(), libs.getNamedType("iml.fsm", "Sm"));
	}

	public boolean isInput(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.connectivity", "Input"));
	}

	public boolean isComponent(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.connectivity", "Component"));
	}

	public boolean isInit(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.fsm", "Init"));
	}

	public boolean isTransition(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.fsm", "Transition"));
	}

	public boolean isInvariantQuery(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.fsm", "Invariant"))
				&& MCMTranslationProvider.isA(s, libs.getNamedType("iml.fsm", "Query"));
	//	return (s instanceof Assertion);
	}

	private boolean isState(SymbolDeclaration sd) {
		return MCMTranslationProvider.hasType(sd, libs.getNamedType("iml.fsm", "PrimedVar"));
	}

	public boolean isConnector(SymbolDeclaration s) {
		return MCMTranslationProvider.hasType(s, libs.getNamedType("iml.connectivity", "Connector"));
	}
	
	public boolean isSimpleTypeReference(ImlType imlType) {
		return (imlType instanceof SimpleTypeReference);
	}

}
