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
	
	private class NuSmvConnection {
		public String target_machine;
		public String target_input;
		public String source;
	}

/*	public MCMT getMainModel(SallyModel m , SimpleTypeReference spec, SimpleTypeReference impl) {
		MCMT main = new MCMT("main");
		SallySymbol inst = new SallySymbol("inst");
		MCMT insttype = m.getType(generatorServices.getNameFor(impl)) ;
		SallyTypeInstance instti = new SallyTypeInstance(insttype);
		inst.setType(instti);
		inst.setElementType(SallyElementType.VAR);
		main.addSymbol(inst);
		
		for(Symbol s : spec.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				if (isInput((SymbolDeclaration)s)) {
					SallySymbol target = new SallySymbol(s.getName());
					SallyTypeInstance ti = new SallyTypeInstance(m.getType(generatorServices.getNameFor( ((SymbolDeclaration)s).getType())));
					target.setType(ti);
					target.setElementType(SallyElementType.VAR);
					main.addSymbol(target);
					instti.setParam( insttype.paramIndex(s.getName())  , new SallyVariable(s.getName()));
				}
			}
		}
		return main ;
	}
	*/

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
		// Check annotation [Sm] first?
		generateSystem(result, tr);
		return result;
	}

	// Translating transition systems
	private void generateSystem(MCMT target, SimpleTypeReference tr) throws GeneratorException {
		String name = generatorServices.getNameFor(tr);
		NamedStateType statetype = generateStateType(target,"_state", tr);
		Sexp<FormulaAtom<StateFormulaVariable>> init = generateInit(tr);
		NamedTransitionSystem result = new NamedTransitionSystem(name, statetype, init); 
		Sexp<FormulaAtom<StateTransFormulaVariable>> transition = generateTrans(tr);
		result.add_transition(transition);
		target.addTransitionSystem(result);
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
					
					System.out.println(sd.getTypeParameter().get(0));
					NamedType tmp = ((SimpleTypeReference) sd.getType()).getType(); //.getTypeParameter().get(0);
					ImlType imlType = typeProvider.bind(sd.getType(), tr);
					// replace `sd.getType()` by `((SimpleTypeReference) sd.getType()).getTypeBinding().get(0)` ?
					// ImlType parameter = imlType.getTypeParameter();
					System.out.println(sdname + " " + imlType + " " + sd.getType());
					BaseType basetype = generateBaseType(tmp.getName());
					statevars.add_field(new StateVariable(sdname,basetype));
				}				
				if (isInput(sd)) {
					ImlType imlType = typeProvider.bind(sd.getType(), tr);
					// replace `sd.getType()` by `((SimpleTypeReference) sd.getType()).getTypeBinding().get(0)` ? 
					BaseType basetype = generateBaseType(generatorServices.getNameFor(imlType));
					inputs.add_field(new Input(sdname,basetype));
				}
			}
		}
		return new NamedStateType(name, statevars, inputs);
	}

	// Translating the initial formula
	private Sexp<FormulaAtom<StateFormulaVariable>> generateInit(SimpleTypeReference tr) throws GeneratorException {
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				String sdname = sd.getName();
				ImlType imlType = typeProvider.bind(sd.getType(), tr);
				// replace `sd.getType()` by `((SimpleTypeReference) sd.getType()).getTypeBinding().get(0)` ? 
				BaseType basetype = generateBaseType(generatorServices.getNameFor(imlType));
				if (isInit(sd)) {
					return NamedStateFormula.symbol("true"); // TO CHANGE
				}
			}
		}
		return NamedStateFormula.symbol("true");
	}
	
	// Translating the transition formula
	private Sexp<FormulaAtom<StateTransFormulaVariable>> generateTrans(SimpleTypeReference tr) throws GeneratorException {
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				String sdname = sd.getName();
				ImlType imlType = typeProvider.bind(sd.getType(), tr);
				// replace `sd.getType()` by `((SimpleTypeReference) sd.getType()).getTypeBinding().get(0)` ? 
				BaseType basetype = generateBaseType(generatorServices.getNameFor(imlType));
				if (isInit(sd)) {
					return NamedStateTransition.symbol("true"); // TO CHANGE
				}
			}
		}
		return NamedStateTransition.symbol("true");
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
		
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration && ! isConnector((SymbolDeclaration) s)) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				generateSymbolDeclaration(target, sd, tr);
			}
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

		// Generate the type
		// Decide on the element type
		if (isInput(sd)) {
			MCMT nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.PARAMETER);
			m.addSymbol(target);

		} else if (isOutput(sd)) {
      throw new GeneratorException("OUTPUTS NOT PART OF MCMT "+tr.toString());

		} else if (isInit(sd)) {
			MCMT nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.INIT);
			target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
			m.addSymbol(target);

		} else if (isTransition(sd)) {
			MCMT nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.TRANSITION);
			target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
			m.addSymbol(target);

		} else if (isConnector(sd)) {
      throw new GeneratorException("CONNECTORS NOT PART OF MCMT "+tr.toString());

		} else if (isState(sd)) {
			MCMT ti = generateType(m.getContainer(),
					typeProvider.bind(((SimpleTypeReference) sd.getType()).getTypeBinding().get(0), ctx));
			target.setType(new SallyTypeInstance(ti));
			target.setElementType(SallyElementType.VAR);
			m.addSymbol(target);

		} else if (sd instanceof Assertion) {
			MCMT nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.INVAR);
			target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
			m.addSymbol(target);

		} else {
			MCMT nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.VAR);
			m.addSymbol(target);
		}
	}
*/	

	public boolean isInput(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.connectivity", "Input"));
	}

	public boolean isOutput(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.connectivity", "Output"));
	}

	public boolean isComponent(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.connectivity", "Component"));
	}

	public boolean isInit(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.fsm", "Init"));
	}

	public boolean isTransition(SymbolDeclaration s) {
		return MCMTranslationProvider.isA(s, libs.getNamedType("iml.fasm", "Transition"));
	}

	public boolean isInvariant(SymbolDeclaration s) {
		return (s instanceof Assertion);
	}

	private boolean isState(SymbolDeclaration sd) {
		return MCMTranslationProvider.hasType(sd, libs.getNamedType("iml.fsm", "PrimedVar"));
	}

	public boolean isConnector(SymbolDeclaration s) {
		return MCMTranslationProvider.hasType(s, libs.getNamedType("iml.connectivity", "Connector"));
	}
	
	public boolean isDelay(SymbolDeclaration s) {
		return MCMTranslationProvider.hasType(s, libs.getNamedType("iml.ports", "delay"));
	}
	
	public boolean isDelay(SimpleTypeReference st) {
		return ( st.getType() == libs.getNamedType("iml.ports", ".delay"));
	}

	public boolean isSimpleTypeReference(ImlType imlType) {
		return (imlType instanceof SimpleTypeReference);
	}

}
