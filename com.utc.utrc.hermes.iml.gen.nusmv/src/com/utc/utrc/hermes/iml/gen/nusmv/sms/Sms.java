package com.utc.utrc.hermes.iml.gen.nusmv.sms;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Pair;


import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModule;
import com.utc.utrc.hermes.iml.gen.common.systems.ComponentType;
import com.utc.utrc.hermes.iml.gen.common.systems.Systems;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.NamedType;
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
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;

public class Sms {

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	private static final Logger logger = Logger.getLogger(Sms.class);

	// TODO need to handle polymorphic states
	private Map<String, State> states;
	// TODO need to handle polymorphic state machines
	private Map<String, StateMachine> sms;
	private Map<String, ImlType> otherTypes ;
	

	private Systems sys ;  
	
	public void setSystems(Systems sys) {
		this.sys = sys ;
	}
	
	public Sms() {
		this.sys = null ;
		states = new HashMap<>();
		sms = new HashMap<>();
	}
	
	public void clear() {
		this.sys = null ;
		states.clear(); 
		sms.clear();
		otherTypes.clear();
	}

	public void process(ResourceSet rs) {
		for (Resource r : rs.getResources()) {
			process(r);
		}
		
	}

	public void process(Resource r) {
		for (EObject o : r.getContents()) {
			if (o instanceof Model) {
				process((Model) o);
			}
		}
	}

	public void process(Model m) {
		for (Symbol s : m.getSymbols()) {
			if (s instanceof NamedType && ! ImlUtil.isPolymorphic(s)) {
				NamedType nt = (NamedType) s;
				process(nt) ;
			}else {
				logger.info("Skipping symbol " + qnp.getFullyQualifiedName(s) + " which is not a type or is polymorphic");
			}
		}
	}

	public void process(NamedType nt) {
		if (ImlUtil.exhibits(nt, (Trait) stdLibs.getNamedType("iml.sms", "StateMachine"))) {
			logger.info("Processing type  " + qnp.getFullyQualifiedName(nt));
			StateMachine sm = processStateMachine(ImlCustomFactory.INST.createSimpleTypeReference(nt));
			
		} else {
			logger.info("Skipping type " + qnp.getFullyQualifiedName(nt) + " which is not a state machine");
		}
	}
	
	public void process(ImlType t) {
		if (t instanceof SimpleTypeReference && !ImlUtil.isPolymorphic(((SimpleTypeReference) t).getType())) {
			process(((SimpleTypeReference) t).getType());
		}
	}
	
	public StateMachine processStateMachine(SimpleTypeReference tr) {
		if (sms.containsKey(ImlUtil.getTypeName(tr,qnp))){
			return sms.get(ImlUtil.getTypeName(tr,qnp)) ;
		}
		StateMachine retval = new StateMachine();
		// get the type of the state
		ImlType stateType = getStateType(tr);
		if (stateType instanceof SimpleTypeReference) {
			State theState = processState((SimpleTypeReference) stateType);
			logger.info("Adding state with type " + ImlUtil.getTypeNameManually(theState.getType(),qnp) );
			retval.setStateType(theState);
		} else {
			logger.info("State with type " + ImlUtil.getTypeNameManually(stateType,qnp) + " was not added because it is not a simple type reference" );
		}
		retval.setSmType(tr);

		SymbolDeclaration invariant = ImlUtil.findSymbol(tr.getType(), "invariant");
		SymbolDeclaration transition = ImlUtil.findSymbol(tr.getType(), "transition");
		SymbolDeclaration init = ImlUtil.findSymbol(tr.getType(), "init");

		if (invariant != null && invariant.getDefinition() != null) {
			logger.info("Found invariant " + invariant.getName() );
			retval.setInvariant(invariant.getDefinition());
		}
		if (transition != null && transition.getDefinition() != null) {
			logger.info("Found transition function " + transition.getName() );
			retval.setTransition(transition.getDefinition());
		}
		if (init != null && init.getDefinition() != null) {
			logger.info("Found initial state definition " + init.getName() );
			retval.setInit(init.getDefinition());
		}
		if (ImlUtil.exhibits(tr, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
			logger.info("The state machine is aso a component. Adding a reference to component model " + sys.getComponent(tr).getName() );
			retval.setComponent(true);
			ComponentType c = sys.getComponent(tr);
			retval.setComponentType(c);
			for(String cname : c.getSubs().keySet()) {
				process(c.getSubs().get(cname).getComponentType().getType()) ;
			}
		}
		sms.put(ImlUtil.getTypeName(tr, qnp), retval);
		return retval;
	}
	
	public State processState(SimpleTypeReference type) {
		
		if (type.getType() == stdLibs.getNamedType("iml.sms","Stateless")) {
			logger.info("The sate machine is stateless.");
			return State.stateless;
		}
		
		State retval = new State(type);
		states.put(ImlUtil.getTypeName(type, qnp), retval);
		logger.info("State with type " + ImlUtil.getTypeNameManually(retval.getType(),qnp) + " defined.");
		return retval ;
	}
	

	public ImlType getStateType(ImlType smType) {
		// Retrieve the type
		if (smType instanceof SimpleTypeReference) {
			SimpleTypeReference tr = (SimpleTypeReference) smType;
			TypingEnvironment env = new TypingEnvironment(tr);
			List<TypeWithProperties> traits = ImlUtil.getRelationTypes(tr.getType(), TraitExhibition.class);
			for (TypeWithProperties twp : traits) {
				if (twp.getType() instanceof SimpleTypeReference && ((SimpleTypeReference) twp.getType())
						.getType() == stdLibs.getNamedType("iml.sms", "StateMachine")) {
					ImlType bound = env.bind(twp.getType());
					if (bound instanceof SimpleTypeReference) {
						return ((SimpleTypeReference) bound).getTypeBinding().get(0);
					}
				}
			}
		}
		return null;
	}

	
	public State getState(ImlType t) {
		if (hasState(t)) {
			return states.get(t);
		}
		return null ;
	}
	
	public StateMachine getStateMachine(ImlType t) {
		if (hasStateMachine(t)) {
			return sms.get(ImlUtil.getTypeName(t, qnp));
		}
		return null;
	}
	
	public boolean hasStateMachine(ImlType t) {
		return (sms.containsKey(ImlUtil.getTypeName(t, qnp)));
	}

	public boolean hasState(ImlType t) {
		return (states.containsKey(ImlUtil.getTypeName(t, qnp)));
	}

	@Override
	public String toString() {
		String retval = "";
		for (String k : sms.keySet()) {
			retval += k + "\n" + sms.get(k).toString().replaceAll("(?m)^", "\t");
			retval += "\n";
		}
		return retval;
	}

	
	
}
