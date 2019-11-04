package com.utc.utrc.hermes.iml.gen.nusmv.sally.generator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.nusmv.generator.Configuration;
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGeneratorServices;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvElementType;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModule;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvSymbol;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvTypeInstance;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvVariable;
import com.utc.utrc.hermes.iml.gen.nusmv.sally.model.SallyModel;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.Sms;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.State;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.StateMachine;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.ComponentInstance;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.ComponentType;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Connection;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Direction;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Port;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.InstanceConstructor;
import com.utc.utrc.hermes.iml.iml.LambdaExpression;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.TailedExpression;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.Trait;
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;
import com.utc.utrc.hermes.iml.util.Phi;

public class SallyGenerator {

	@Inject
	ImlTypeProvider typeProvider;

	@Inject
	SallyGeneratorServices generatorServices;

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	private Configuration conf;
	
	private SallyModel m ;
	
	private Sms sms ;

	public SallyGenerator() {
		sms = new Sms() ;
	}

	
	public void setSms(Sms sms) {
		this.sms = sms ;
		m = new SallyModel(stdLibs) ;
	}
	
	public SallyModel getModel() {
		return m;
	}

	public void generateStateMachine(ImlType t) {
		
		if (t instanceof SimpleTypeReference) {
			generateStateMachine("top", sms.getStateMachine(t));
		}
	}

	public void generateStateMachine(String prefix, StateMachine sm) {

		SimpleTypeReference statetr = (SimpleTypeReference) sm.getStateType().getType() ;
		SymbolDeclaration states = ImlCustomFactory.INST.createSymbolDeclaration("state", statetr);
		m.addState(prefix, states);
		
		SimpleTypeReference tr = sm.getSmType();
		if (sm.isComponent()) {
			ComponentType ct = sm.getComponentType();
			//TODO Input, Output
			for (ComponentInstance sub : ct.getSubs().values()) {
				generateStateMachine(prefix + "." + sub.getName(), sms.getStateMachine(sub.getComponentType().getType()));
			}
			// TODO Connections
			// TODO add all other symbols

		} else {
			// TODO add all other symbols
		}

		if (sm.getInit() != null) {
			generateInit(prefix, sm.getInit(), tr);
		}

		if (sm.getTransition() != null) {
			generateTransition(prefix, sm.getTransition(), tr);
		}
	}

	private void generateEnumType( NamedType type) {
	
	}

	private void generateInput(Port p) {

	
	}

	

	private void generateInit(String name, FolFormula f, SimpleTypeReference tr) {
		if (f instanceof SignedAtomicFormula && f.getLeft() instanceof LambdaExpression) {
			LambdaExpression le = (LambdaExpression) f.getLeft();
			Map<Symbol, String> remap = new HashMap<>();
			remap.put(le.getParameters().get(0), name + "." + "state");
			m.addInit(name + "." + "init", generatorServices.serialize(le.getDefinition(), tr,remap));
		}
	}

	private void generateTransition(String name, FolFormula f, SimpleTypeReference tr) {
		if (f instanceof SignedAtomicFormula && f.getLeft() instanceof LambdaExpression) {
			LambdaExpression le = (LambdaExpression) f.getLeft();
			Map<Symbol, String> remap = new HashMap<>();
			remap.put(le.getParameters().get(0), "state" + "." + name + "." + "state");
			remap.put(le.getParameters().get(1), "next" + "." + name + "." + "state");
			m.addTransition(name + "." + "transition", generatorServices.serialize(le.getDefinition(), tr,remap));
		}
	}

	private void generateInvariant( FolFormula f, SimpleTypeReference tr) {

	}



	private void generateConnection(Connection conn, SimpleTypeReference tr) {

	}

	
	public boolean isDelay(SymbolDeclaration s) {
		return ImlUtil.hasType(s.getType(), stdLibs.getNamedType("iml.sms", "delay"));
	}

	public boolean isInput(SymbolDeclaration sd) {

			if ( ImlUtil.exhibits(sd.getType(), (Trait) stdLibs.getNamedType("iml.systems", "In")   ) ) {
				return true ;
			}
		
		return false ;
	}
	
	public boolean isDelay(ImlType st) {
		return (st == stdLibs.getNamedType("iml.sms", "delay"));
	}

	public boolean isSimpleTypeReference(ImlType imlType) {
		return (imlType instanceof SimpleTypeReference);
	}

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}
	
	public String serializeModel() {
		return generatorServices.serialize(m);
	}
	
	
	
}
