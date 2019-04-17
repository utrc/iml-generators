package com.sri.iml.gen.mcmt;

import static com.sri.iml.gen.mcmt.MCMTranslationProvider.getLiterals;
import static com.sri.iml.gen.mcmt.MCMTranslationProvider.isEnum;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.inject.Inject;
import com.sri.iml.gen.mcmt.model.SallyElementType;
import com.sri.iml.gen.mcmt.model.SallyModel;
import com.sri.iml.gen.mcmt.model.SallySm;
import com.sri.iml.gen.mcmt.model.SallySymbol;
import com.sri.iml.gen.mcmt.model.SallyTypeInstance;
import com.sri.iml.gen.mcmt.model.SallyVariable;
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

	public SallySm getMainModel(SallyModel m , SimpleTypeReference spec, SimpleTypeReference impl) {
		SallySm main = new SallySm("main");
		SallySymbol inst = new SallySymbol("inst");
		SallySm insttype = m.getType(generatorServices.getNameFor(impl)) ;
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
	
	public SallySm generateType(SallyModel m, ImlType tr) {
		if (tr instanceof SimpleTypeReference) {
			return generateType(m, (SimpleTypeReference) tr);
		}
		return (new SallySm(m, "HIGHER_HORDER_TYPE_NOT_SUPPORTED"));
	}

	public SallySm generateType(SallyModel m, SimpleTypeReference tr) {
		String type_name = generatorServices.getNameFor(tr);
		if (m.hasType(type_name))
			return m.getType(type_name);
		if (isEnum(tr.getType())) {
			return generateEnumType(m, tr.getType());
		} else { // Check annotation [Sm] first
			for (ImlType b : tr.getTypeBinding()) {
				generateType(m, b);
			}
			SallySm target = new SallySm(type_name);
			m.addModule(target);
			addSymbols(target, tr);
			return target;
		}
	}
	
	private void addSymbols(SallySm target, SimpleTypeReference tr) {
		if (tr.getType().getRelations() != null) {
			for(Relation r : tr.getType().getRelations()) {
				if (r instanceof Extension) {
					for(TypeWithProperties p : ((Extension) r).getExtensions()) {
						//generate the type
						SallySm added = generateType(target.getContainer(), typeProvider.bind(p.getType(), tr)) ;
						for(SallySymbol s : added.getParameters()) {
							target.addSymbol(s);
						}
						for(SallySymbol s : added.getVariables().values()) {
							target.addSymbol(s);
						}
						for(SallySymbol s : added.getDefinitions().values()) {
							target.addSymbol(s);
						}
						for(SallySymbol s : added.getInits().values()) {
							target.addSymbol(s);
						}
						for(SallySymbol s : added.getTrans().values()) {
							target.addSymbol(s);
						}
						for(SallySymbol s : added.getInvar().values()) {
							target.addSymbol(s);
						}
					}
				}
			}
		}

		
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration && ! isConnector((SymbolDeclaration) s)) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				generateSymbolDeclaration(target, sd, tr);
			}
		}
		
		
		for (Symbol s : tr.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration && isConnector((SymbolDeclaration) s)) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				generateSymbolDeclaration(target, sd, tr);
			}
		}
		
		
	}

	private SallySm generateEnumType(SallyModel m, NamedType type) {
		SallySm target = new SallySm(generatorServices.qualifiedName(type));
		m.addModule(target);
		target.setEnum(true);
		target.getLiterals().addAll(getLiterals(type));
		return target;
	}

	private SallySymbol generateSymbolDeclaration(SallySm m, SymbolDeclaration sd, SimpleTypeReference ctx) {
			
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

		SallySymbol target = new SallySymbol(name);

		// Generate the type
		// Decide on the element type
		if (isInput(sd)) {
			SallySm nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.PARAMETER);
			m.addSymbol(target);
		} else if (isOutput(sd)) {
			SallySm nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			if (sd.getDefinition() != null) {
				target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
				target.setElementType(SallyElementType.DEFINE);
			} else {
				target.setElementType(SallyElementType.VAR);
			}
			m.addSymbol(target);
		} else if (isInit(sd)) {
			SallySm nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.INIT);
			target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
			m.addSymbol(target);
		} else if (isTransition(sd)) {
			SallySm nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.TRANSITION);
			target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
			m.addSymbol(target);
		} else if (isConnector(sd)) {
			NuSmvConnection conn = getConnection(m, sd,ctx);
			// If this is a connection to an output of the current machine
			// simply add a define
			if (conn.target_machine == null) {
				//Need to take the output symbol
				SallySymbol out = m.getVariables().get(conn.target_input) ;
				if (out != null) {
					SallySymbol toadd = new SallySymbol("") ;
					FolFormula def = 
							Phi.eq(
									EcoreUtil.copy((TermExpression) ((TupleConstructor) ((TailedExpression)sd.getDefinition().getLeft()).getTail()).getElements().get(0).getLeft()), 
									EcoreUtil.copy((TermExpression) ((TupleConstructor) ((TailedExpression)sd.getDefinition().getLeft()).getTail()).getElements().get(1).getLeft())
									) ;
					toadd.setName(m.getContainer().newSymbolName());
					toadd.setElementType(SallyElementType.INVAR);
					toadd.setDefinition(generatorServices.serialize(def,ctx));
					m.addSymbol(toadd);
				}
			} else {
				// otherwise
				SallySymbol machine = m.getVariables().get(conn.target_machine);
				if (machine != null) {
					int index = machine.getType().getType().paramIndex(conn.target_input);
					if (index != -1) {
						SallyVariable param = new SallyVariable(conn.source);
						machine.getType().setParam(index, param);
						;
					}
				}
			}

			return null;
		} else if (isState(sd)) {
			SallySm ti = generateType(m.getContainer(),
					typeProvider.bind(((SimpleTypeReference) sd.getType()).getTypeBinding().get(0), ctx));
			target.setType(new SallyTypeInstance(ti));
			target.setElementType(SallyElementType.VAR);
			m.addSymbol(target);
		} else if (sd instanceof Assertion) {
			SallySm nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.INVAR);
			target.setDefinition(generatorServices.serialize(sd.getDefinition(),ctx));
			m.addSymbol(target);
		} else {
			SallySm nbound = generateType(m.getContainer(), bound);
			SallyTypeInstance ti = new SallyTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(SallyElementType.VAR);
			m.addSymbol(target);
		}
		return target;
	}

	private NuSmvConnection getConnection(SallySm m, SymbolDeclaration sd, SimpleTypeReference ctx) {
		// At this point we know that this is a connector
		// Get the definition which is expected to be a function
		NuSmvConnection retval = new NuSmvConnection();
		retval.source = "UNNAMED_SOURCE";
		FolFormula f = sd.getDefinition();
		if (f instanceof SignedAtomicFormula) {
			FolFormula f1 = f.getLeft();
			if (f1 instanceof TailedExpression) {
				TailedExpression connect = (TailedExpression) f1;
				// get the source and destination
				if (connect.getTail() != null) {
					FolFormula sourcef = ((TupleConstructor) connect.getTail()).getElements().get(0).getLeft();
					FolFormula destf = ((TupleConstructor) connect.getTail()).getElements().get(1).getLeft();
					retval.source = generatorServices.serialize(sourcef,ctx);
					String dest_tmp = generatorServices.serialize(destf,ctx);
					int lastindex = dest_tmp.lastIndexOf('.');
					if (lastindex == -1) {
						retval.target_input = dest_tmp;
						retval.target_machine = null;

					} else {
						retval.target_machine = dest_tmp.substring(0, lastindex);
						retval.target_input = dest_tmp.substring(lastindex + 1);
					}
				}
			}
		}

		return retval;
	}
	

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
