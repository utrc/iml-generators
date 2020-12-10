package com.utc.utrc.hermes.iml.gen.nusmv.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.common.systems.ComponentInstance;
import com.utc.utrc.hermes.iml.gen.common.systems.ComponentType;
import com.utc.utrc.hermes.iml.gen.common.systems.Connection;
import com.utc.utrc.hermes.iml.gen.common.systems.Direction;
import com.utc.utrc.hermes.iml.gen.common.systems.Port;
import com.utc.utrc.hermes.iml.gen.common.systems.Systems;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvElementType;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModule;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvSymbol;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvTypeInstance;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvVariable;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.Sms;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.State;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.StateMachine;
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
import com.utc.utrc.hermes.iml.lib.SmsServices;
import com.utc.utrc.hermes.iml.lib.SystemsServices;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlModelPrinter;
import com.utc.utrc.hermes.iml.util.ImlUtil;
import com.utc.utrc.hermes.iml.util.Phi;

public class NuSmvGenerator {

	@Inject
	ImlTypeProvider typeProvider;

	@Inject
	NuSmvGeneratorServices generatorServices;


	@Inject
	private SmsServices sms_srv;
	
//	@Inject
	SystemsServices sysServ;

	Systems sys;

	@Inject
	Sms sms;

//	@Inject
	private IQualifiedNameProvider qnp;

	private static final Logger logger = Logger.getLogger(NuSmvGenerator.class);
	
	private Configuration conf;
	
	private Trait valid_transition ;
	private Trait state_machine ;
	
	@Inject
	public NuSmvGenerator(SystemsServices sysServ, IQualifiedNameProvider qnp) {
		// TODO Auto-generated constructor stub
		this.sysServ = sysServ;
		this.qnp = qnp;
		this.sys = new Systems(sysServ, qnp);
	}

	public void init(NamedType t) {
		valid_transition = sms_srv.getValidTransitionTrait() ;
		state_machine = sms_srv.getStateMachineTrait(); 
		sys.process(t);
		sms.setSystems(sys);
		//sms.process(iml_model);
		sms.process(t);
	}
	
	public void init(ImlType t) throws NuSmvGeneratorException {
		if (t instanceof SimpleTypeReference) {
			valid_transition = sms_srv.getValidTransitionTrait() ;
			state_machine = sms_srv.getStateMachineTrait(); 
			sys.process(((SimpleTypeReference) t).getType());
			sms.setSystems(sys);
			//sms.process(iml_model);
			sms.process(t);
		} else {
			throw new NuSmvGeneratorException(t, "The top level system must be a simple type reference but it is " + ImlModelPrinter.print(t));
		}
		
	}
	
	public NuSmvModel generateNuSmvModel(Configuration conf, NamedType t)  {
		init(t);
		NuSmvModel m = new NuSmvModel();
		try {
			generateStateMachine(m, ImlCustomFactory.INST.createSimpleTypeReference(t));
		} catch (NuSmvGeneratorException e) {
			logger.info(e.getMessage());
		}
		return m;
	}


	public String serialize(NuSmvModel m) {
		return generatorServices.serialize(m);
	}

	public String generateNuSmvModelString(Configuration conf, NamedType t) throws NuSmvGeneratorException {
		return serialize(generateNuSmvModel(conf, t));
	}

	public NuSmvModule generateMainModule(NuSmvModel m, SymbolDeclaration query) throws NuSmvGeneratorException {
		
		if (generatorServices.isRefinement(query)) {
			return generateMainModule(m,generatorServices.getSpec(query),generatorServices.getImpl(query));
		} else if (generatorServices.isLTL(query)) {
			//TBD after deciding the interface
			return generateMainModule(m, generatorServices.getSmsFromLtlQuery(query), generatorServices.getLtlFormula(query)) ;
		}
		throw new NuSmvGeneratorException(query, "Main modules can be generated only for refinement or ltl queries.") ;
	}
	
	public NuSmvModule generateMainModule(NuSmvModel m, SimpleTypeReference spec, SimpleTypeReference impl)
			throws NuSmvGeneratorException {
		init(spec); init(impl);
		NuSmvModule main = new NuSmvModule("main");
		NuSmvModule impltype = null;
		NuSmvModule spectype = null;
		if (m.hasType(ImlUtil.getTypeName(impl, qnp))) {
			impltype = m.getType(ImlUtil.getTypeName(impl, qnp));
		} else {
			impltype = generateStateMachine(m, impl);
		}
		if (m.hasType(ImlUtil.getTypeName(spec, qnp))) {
			spectype = m.getType(ImlUtil.getTypeName(spec, qnp));
		} else {
			spectype = generateStateMachine(m, spec);
		}

		NuSmvSymbol spec_inst = new NuSmvSymbol("spec_inst");
		NuSmvTypeInstance spec_instti = new NuSmvTypeInstance(spectype);
		spec_inst.setType(spec_instti);
		spec_inst.setElementType(NuSmvElementType.VAR);
		main.addSymbol(spec_inst);
		NuSmvSymbol impl_inst = new NuSmvSymbol("impl_inst");
		NuSmvTypeInstance impl_instti = new NuSmvTypeInstance(impltype);
		impl_inst.setType(impl_instti);
		impl_inst.setElementType(NuSmvElementType.VAR);
		main.addSymbol(impl_inst);

		//Assumption is that the implementation has more ports than the specification
		for (NuSmvSymbol in : impltype.getParameters()) {
			NuSmvSymbol target = new NuSmvSymbol(in.getName());
			NuSmvTypeInstance ti = new NuSmvTypeInstance(in.getType().getType());
			target.setType(ti);
			target.setElementType(NuSmvElementType.VAR);
			main.addSymbol(target);
			if (spectype.paramIndex(in.getName()) != -1) {
				spec_instti.setParam(spectype.paramIndex(in.getName()), new NuSmvVariable(in.getName()));
			}
			impl_instti.setParam(impltype.paramIndex(in.getName()), new NuSmvVariable(in.getName()));
		}
		

		m.addModule(main);
		
		NuSmvTypeInstance ti = new NuSmvTypeInstance(m.getType("iml.lang.Bool"));
		List<NuSmvSymbol> specs = generatorServices.generateRefSpec("spec_inst", "impl_inst", sys.getComponent(spec).getSubs().keySet(), ti) ;
		for(NuSmvSymbol s : specs) {
			main.addSymbol(s);
		}
		
		return main;
	}
	
	public NuSmvModule generateMainModule(NuSmvModel m, SimpleTypeReference spec, FolFormula ltl)
			throws NuSmvGeneratorException {
		init(spec);
		NuSmvModule main = new NuSmvModule("main");
		
		NuSmvModule spectype = null;
		if (m.hasType(ImlUtil.getTypeName(spec, qnp))) {
			spectype = m.getType(ImlUtil.getTypeName(spec, qnp));
		} else {
			spectype = generateStateMachine(m, spec);
		}
	
		NuSmvSymbol inst = new NuSmvSymbol("inst");
		NuSmvTypeInstance instti = new NuSmvTypeInstance(spectype);
		inst.setType(instti);
		inst.setElementType(NuSmvElementType.VAR);
		main.addSymbol(inst);

		for (NuSmvSymbol in : spectype.getParameters()) {
			NuSmvSymbol target = new NuSmvSymbol(in.getName());
			NuSmvTypeInstance ti = new NuSmvTypeInstance(in.getType().getType());
			target.setType(ti);
			target.setElementType(NuSmvElementType.VAR);
			main.addSymbol(target);
			instti.setParam(spectype.paramIndex(in.getName()), new NuSmvVariable(in.getName()));
		}
		m.addModule(main);
		
		//We now need to add the ltl spec formula to the main function
		NuSmvSymbol ltlspec = new NuSmvSymbol("ltlf") ;
		NuSmvTypeInstance ti = new NuSmvTypeInstance(m.getType("iml.lang.Bool"));
		ltlspec.setType(ti);
		ltlspec.setElementType(NuSmvElementType.LTLSPEC);
		ltlspec.setDefinition(generatorServices.ltlserialize(ltl,spec,new HashMap<>()));
		main.addSymbol(ltlspec);
		return main;
	}
	
	

	/**
	 * If the ImlType t is a has a state machine trait, this function generates a
	 * corresponding SMV module in the context of the model m. The model m is needed
	 * to search and store definitions that are needed in the process generation
	 * such as user-defined types. If the type t is not a state machines, an
	 * exception is thrown.
	 * 
	 * @throws NuSmvGeneratorException
	 */

	public NuSmvModule generateStateMachine(NuSmvModel m, ImlType t) throws NuSmvGeneratorException {
		
		if (sms_srv.isStateMachine(t) && t instanceof SimpleTypeReference) {
			return generateStateMachine(m, sms.getStateMachine(t));
		}
		throw new NuSmvGeneratorException(t,
				ImlUtil.getTypeNameManually(t, qnp) + " is not a state machine or not a SimpleTypeReference") ;
	}

	public NuSmvModule generateStateMachine(NuSmvModel m, StateMachine sm) throws NuSmvGeneratorException {

		String type_name = ImlUtil.getTypeName(sm.getSmType(), qnp);
		if (m.hasType(type_name))
			return m.getType(type_name);
		logger.info("Generating " + type_name);
		NuSmvModule target = new NuSmvModule(type_name);
		m.addModule(target);

		SimpleTypeReference tr = sm.getSmType();
		generateState(target, sm.getStateType());
		if (sm.isComponent()) {
			ComponentType ct = sm.getComponentType();
			for (Port p : ct.getPorts(Direction.IN)) {
				generateInput(target, p);
			}
			for (Port p : ct.getPorts(Direction.OUT)) {
				generateOutput(target, p, tr);
			}
			for (ComponentInstance sub : ct.getSubs().values()) {
				NuSmvModule gensm = generateStateMachine(target.getContainer(),
						sms.getStateMachine(sub.getComponentType().getType()));
				addSymbol(target, sub.getName(), gensm, NuSmvElementType.VAR);
			}
			// add all connections
			for (Connection conn : ct.getConnections().values()) {
				generateConnection(target, conn, tr);
			}

			// add all other symbols
			TypingEnvironment typing = new TypingEnvironment(tr);
			for (SymbolDeclaration sd : ct.getOtherSymbols()) {
				//Does it need to be generated? 
				//if (! dontGenerate(sd)) {
				//	generateType(m, (SimpleTypeReference) typing.bind(sd.getType()));
					addSymbol(target, sd, tr);
				//}
			}
		} else {
			for (Symbol sd : sm.getSmType().getType().getSymbols()) {
				if (sd instanceof SymbolDeclaration) {
					//if (! dontGenerate( (SymbolDeclaration) sd)) {
						addSymbol(target, (SymbolDeclaration) sd, tr);
					//}
				}
			}
		}

		if (sm.getInit() != null)
			generateInit(target, sm.getInit(), tr);
		if (sm.getInvariant() != null) {
			generateInvariant(target, sm.getInvariant(), tr);
		}
		if (sm.getTransition() != null) {
			generateTransition(target, sm.getTransition(), tr);
		}

		return target;

	}

	public NuSmvModule generateType(NuSmvModel m, SimpleTypeReference tr) throws NuSmvGeneratorException {
		// start from the definition
		String type_name = ImlUtil.getTypeName(tr, qnp);
		if (m.hasType(type_name))
			return m.getType(type_name);

		// generate the state first

		if (ImlUtil.isEnum(tr.getType())) {
			return generateEnumType(m, tr.getType());
		} else {

			for (ImlType b : tr.getTypeBinding()) {
				if (b instanceof SimpleTypeReference) {
					generateType(m, (SimpleTypeReference) b);
				}
			}
			NuSmvModule target = new NuSmvModule(type_name);
			m.addModule(target);

			for (Symbol s : tr.getType().getSymbols()) {
				if (s instanceof SymbolDeclaration) {
					SymbolDeclaration sd = (SymbolDeclaration) s;
					addSymbol(target, sd, tr);
				}
			}

			return target;
		}
	}

	public void addSymbol(NuSmvModule target, SymbolDeclaration sd, SimpleTypeReference ctx) throws NuSmvGeneratorException {
		if (dontGenerate(sd)) return ;
		TypingEnvironment typing = new TypingEnvironment(ctx);
		ImlType bound = null;
		String name = null;
		if (sd instanceof Assertion) {
			if (sd.getName() != null) {
				name = sd.getName();
			} else {
				name = target.getContainer().newSymbolName();
			}
			addSymbol(target, name, target.getContainer().getType("iml.lang.Bool"), NuSmvElementType.INVAR,
					generatorServices.serialize(sd.getDefinition(), ctx));
		} else {
			
			name = sd.getName();
			bound = typing.bind(sd.getType());
			if (bound instanceof SimpleTypeReference) {
				NuSmvModule nbound = null ;
				if (sms_srv.isStateMachine(bound)) {
					nbound = generateStateMachine(target.getContainer(), bound);
				} else {
					nbound = generateType(target.getContainer(), (SimpleTypeReference) bound);
				}
				addSymbol(target, name, nbound, NuSmvElementType.VAR);
				if(sd.getDefinition() != null) {
					addSymbol(target, name, nbound, NuSmvElementType.INVAR,
							name + " = (" + generatorServices.serialize(sd.getDefinition(), ctx) + ")");
				}
			}
			

		}
	}

	private NuSmvModule generateEnumType(NuSmvModel m, NamedType type) {
		NuSmvModule target = new NuSmvModule(qnp.getFullyQualifiedName(type).toString());
		m.addModule(target);
		target.setEnum(true);
		target.getLiterals().addAll(ImlUtil.getLiterals(type));
		return target;
	}

	private NuSmvSymbol generateInput(NuSmvModule m, Port p) throws NuSmvGeneratorException {

		if (p.getDataType() instanceof SimpleTypeReference) {
			NuSmvSymbol target = new NuSmvSymbol(p.getName());
			NuSmvModule nbound = generateType(m.getContainer(), (SimpleTypeReference) p.getDataType());
			NuSmvTypeInstance ti = new NuSmvTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(NuSmvElementType.PARAMETER);
			m.addSymbol(target);
			return target;
		}

		return new NuSmvSymbol("__UNSUPPORTED__");
	}

	private NuSmvSymbol generateOutput(NuSmvModule m, Port p, SimpleTypeReference ctx) throws NuSmvGeneratorException {

		if (p.getDataType() instanceof SimpleTypeReference) {
			NuSmvSymbol target = new NuSmvSymbol(p.getName());
			NuSmvModule nbound = generateType(m.getContainer(), (SimpleTypeReference) p.getDataType());
			NuSmvTypeInstance ti = new NuSmvTypeInstance(nbound);
			target.setType(ti);
			if (p.getDefinition() != null) {
				// TODO need to generalize this
				if (p.getDefinition() instanceof SignedAtomicFormula) {
					if (p.getDefinition().getLeft() instanceof InstanceConstructor) {
						InstanceConstructor cons = (InstanceConstructor) p.getDefinition().getLeft();
						SymbolDeclaration var = cons.getRef();
						Map<Symbol, String> remap = new HashMap<>();
						remap.put(var, p.getName());
						String expr = generatorServices.serialize(cons.getDefinition(), ctx, remap);
						target.setDefinition(expr.replace(p.getName() + "=", ""));
					}
				}
				target.setElementType(NuSmvElementType.DEFINE);
			} else {
				target.setElementType(NuSmvElementType.VAR);
			}
			m.addSymbol(target);
			return target;
		}

		return new NuSmvSymbol("__UNSUPPORTED__");
	}
	
	
	private NuSmvSymbol generateOutput(NuSmvModule m, SymbolDeclaration sd, SimpleTypeReference ctx) throws NuSmvGeneratorException {

		if (sd.getType() instanceof SimpleTypeReference) {
			NuSmvSymbol target = new NuSmvSymbol(sd.getName());
			NuSmvModule nbound = generateType(m.getContainer(), (SimpleTypeReference) sd.getType());
			NuSmvTypeInstance ti = new NuSmvTypeInstance(nbound);
			target.setType(ti);
			if (sd.getDefinition() != null) {
				Map<Symbol, String> remap = new HashMap<>();
				String expr = generatorServices.serialize(sd.getDefinition(), ctx, remap);
				target.setDefinition(expr);
				target.setElementType(NuSmvElementType.DEFINE);
			} else {
				target.setElementType(NuSmvElementType.VAR);
			}
			m.addSymbol(target);
			return target;
		}
		return new NuSmvSymbol("__UNSUPPORTED__");
	}

	private NuSmvSymbol generateInit(NuSmvModule m, FolFormula f, SimpleTypeReference tr) {
		NuSmvSymbol target = new NuSmvSymbol("init_");
		NuSmvTypeInstance ti = new NuSmvTypeInstance(m.getContainer().getType("iml.lang.Bool"));
		target.setType(ti);
		target.setElementType(NuSmvElementType.INIT);
		if (f instanceof SignedAtomicFormula && f.getLeft() instanceof LambdaExpression) {
			LambdaExpression le = (LambdaExpression) f.getLeft();
			Map<Symbol, String> remap = new HashMap<>();
			remap.put(le.getParameters().get(0), "state");
			target.setDefinition(generatorServices.serialize(le.getDefinition(), tr, remap));
		}
		m.addSymbol(target);
		return target;
	}

	private NuSmvSymbol generateTransition(NuSmvModule m, FolFormula f, SimpleTypeReference tr) {
		NuSmvSymbol target = new NuSmvSymbol("transition");
		NuSmvTypeInstance ti = new NuSmvTypeInstance(m.getContainer().getType("iml.lang.Bool"));
		target.setType(ti);
		target.setElementType(NuSmvElementType.TRANSITION);
		if (f instanceof SignedAtomicFormula && f.getLeft() instanceof LambdaExpression) {
			LambdaExpression le = (LambdaExpression) f.getLeft();
			Map<Symbol, String> remap = new HashMap<>();
			remap.put(le.getParameters().get(0), "state");
			remap.put(le.getParameters().get(1), "next(state)");
			target.setDefinition(generatorServices.serialize(le.getDefinition(), tr, remap));
		}
		m.addSymbol(target);
		return target;
	}

	private NuSmvSymbol generateInvariant(NuSmvModule m, FolFormula f, SimpleTypeReference tr) {
		NuSmvSymbol target = new NuSmvSymbol("invariant");
		NuSmvTypeInstance ti = new NuSmvTypeInstance(m.getContainer().getType("iml.lang.Bool"));
		target.setType(ti);
		target.setElementType(NuSmvElementType.INVAR);
		if (f instanceof SignedAtomicFormula && f.getLeft() instanceof LambdaExpression) {
			LambdaExpression le = (LambdaExpression) f.getLeft();
			Map<Symbol, String> remap = new HashMap<>();
			remap.put(le.getParameters().get(0), "state");
			target.setDefinition(generatorServices.serialize(le.getDefinition(), tr, remap));
		}
		m.addSymbol(target);
		return target;
	}

	private NuSmvSymbol generateState(NuSmvModule m, State state) throws NuSmvGeneratorException {

		if (state == State.stateless) {
			return null;
		}
		
		NuSmvSymbol target = new NuSmvSymbol("state");
		NuSmvModule ti = generateType(m.getContainer(), (SimpleTypeReference) state.getType());
		target.setType(new NuSmvTypeInstance(ti));
		target.setElementType(NuSmvElementType.VAR);
		m.addSymbol(target);
		logger.info("Added state to " + m.getName() + " with type " + ti.getName() + " (derived from " + ImlUtil.getTypeNameManually((SimpleTypeReference) state.getType(),qnp));
		return target;

//		ImlType type = state.getType();
//		if (type instanceof SimpleTypeReference) {
//			SimpleTypeReference typetr = (SimpleTypeReference) type;
//			TypingEnvironment typing = new TypingEnvironment(typetr);
//			for (Symbol s : typetr.getType().getSymbols()) {
//				if (s instanceof SymbolDeclaration) {
//					NuSmvSymbol target = new NuSmvSymbol(s.getName());
//					NuSmvModule ti = generateType(m.getContainer(), (SimpleTypeReference) typing
//							.bind(((SimpleTypeReference) ((SymbolDeclaration) s).getType())));
//					target.setType(new NuSmvTypeInstance(ti));
//					target.setElementType(NuSmvElementType.VAR);
//					m.addSymbol(target);
//				}
//			}
//		}
//		return null;
	}

	private void generateConnection(NuSmvModule m, Connection conn, SimpleTypeReference tr) {
		// If this is a connection to an output of the current machine
		// simply add a define
		if (conn.getTargetComponent() == ComponentInstance.self) {
			// Need to take the output symbol
			NuSmvSymbol out = m.getVariables().get(conn.getTargetPort().getName());
			if (out != null) {
				NuSmvSymbol toadd = new NuSmvSymbol("");
				FolFormula def = Phi.eq(
						EcoreUtil.copy(
								(TermExpression) ((TupleConstructor) ((TailedExpression) conn.getSymbolDeclaration()
										.getDefinition().getLeft()).getTail()).getElements().get(0).getLeft()),
						EcoreUtil.copy(
								(TermExpression) ((TupleConstructor) ((TailedExpression) conn.getSymbolDeclaration()
										.getDefinition().getLeft()).getTail()).getElements().get(1).getLeft()));
				toadd.setName(m.getContainer().newSymbolName());
				toadd.setElementType(NuSmvElementType.INVAR);
				toadd.setDefinition(generatorServices.serialize(def, tr));
				m.addSymbol(toadd);
			}
		} else {
			// otherwise
			NuSmvSymbol machine = m.getVariables().get(conn.getTargetComponent().getName());
			if (machine != null) {
				int index = machine.getType().getType().paramIndex(conn.getTargetPort().getName());
				if (index != -1) {

					String portname = "";
					if (conn.getSourceComponent() != ComponentInstance.self) {
						portname += conn.getSourceComponent().getName() + ".";
					}
					portname += conn.getSourcePort().getName();

					NuSmvVariable param = new NuSmvVariable(portname);
					machine.getType().setParam(index, param);
					;
				}
			}
		}

	}

	public void defineOutput(NuSmvModule target, Port p, SimpleTypeReference ctx) {

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

	public NuSmvSymbol addSymbol(NuSmvModule container, String name, NuSmvModule type, NuSmvElementType et,
			String definition) {
		NuSmvSymbol target = addSymbol(container, name, type, et);
		target.setDefinition(definition);
		return target;
	}

	public NuSmvSymbol addSymbol(NuSmvModule container, String name, NuSmvModule type, NuSmvElementType et) {
		NuSmvSymbol target = new NuSmvSymbol(name);
		NuSmvTypeInstance ti = new NuSmvTypeInstance(type);
		target.setType(ti);
		target.setElementType(et);
		container.addSymbol(target);
		return target;
	}
	
	public boolean dontGenerate(SymbolDeclaration sd) {
		//Needs to be a simple type reference
		if (valid_transition.getSymbols().contains(sd)) return true;
		if (state_machine.getSymbols().contains(sd)) return true;
		if ( sd.getType() != null && ! ( sd.getType() instanceof SimpleTypeReference) ) return true;
		return false;
	}
	
}
