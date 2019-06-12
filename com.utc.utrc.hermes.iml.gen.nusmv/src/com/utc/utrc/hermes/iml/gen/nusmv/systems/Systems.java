package com.utc.utrc.hermes.iml.gen.nusmv.systems;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModule;
import com.utc.utrc.hermes.iml.gen.nusmv.sms.StateMachine;
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

public class Systems {

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	final Logger logger = LoggerFactory.getLogger(Systems.class);

	private Map<String, Component> components;

	public Systems() {
		components = new HashMap<String, Component>();
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
			if (s instanceof NamedType) {
				NamedType nt = (NamedType) s;
				if (ImlUtil.exhibits(nt, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
					Component c = process(nt);
					components.put(ImlUtil.getTypeName(ImlCustomFactory.INST.createSimpleTypeReference(nt), qnp), c);
				}
			}
		}
	}

	public Component process(NamedType t) {
		return process("unnamed", ImlCustomFactory.INST.createSimpleTypeReference(t), null);
	}

	public Component process(String name, ImlType t, Component container) {

		// Create a new component
		Component c = new Component(name, t, container);
		if (container != null) {
			container.addSub(c);
		}
		// add ports and sub-components
		// TODO we only process simple type references at the moment
		// TODO we should also process arrays that are very convenient
		if (t instanceof SimpleTypeReference) {

			SimpleTypeReference str_t = (SimpleTypeReference) t;
			List<SymbolDeclaration> connectors = new ArrayList<>();
			for (Symbol s : str_t.getType().getSymbols()) {
				if (s instanceof SymbolDeclaration) {
					SymbolDeclaration sd_s = (SymbolDeclaration) s;
					ImlType s_type = sd_s.getType();
					if (ImlUtil.exhibits(s_type, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
						// TODO handle recursion (if name and type are the same, then point to itself
						// and do not recurse)
						process(sd_s.getName(), s_type, c);
					} else if (ImlUtil.exhibits(s_type, (Trait) stdLibs.getNamedType("iml.systems", "Port"))) {
						processPort(sd_s.getName(), s_type, c);
					} else if (ImlUtil.hasType(s_type, stdLibs.getNamedType("iml.systems", "Connector"))) {
						connectors.add(sd_s);
					} else {
						logger.info("Component {} also includes symbol { } which this class does not process.", name,
								sd_s.getName());
					}

				}
			}

			// process connectors
			for (SymbolDeclaration s : connectors) {
				Pair<Port, Port> ports = processConnector(s, c);
				c.connect(ports.getKey(), ports.getValue());

			}

		} else {
			logger.warn("We only support simple type references at the moment (the iml type was {} )",
					ImlUtil.getTypeName(t, qnp));
		}

		return c;
	}

	public void processPort(String name, ImlType t, Component container) {

		Port p = new Port();
		p.setName(name);
		p.setType(t);
		p.setContainer(container);
		if (container != null) {
			container.addPort(p);
		} else {
			logger.error("Port {} should have a container ", name);
		}

		if (ImlUtil.exhibits(t, (Trait) stdLibs.getNamedType("iml.systems", "In"))) {
			p.setDirection(Direction.IN);
		} else if (ImlUtil.exhibits(t, (Trait) stdLibs.getNamedType("iml.systems", "Out"))) {
			p.setDirection(Direction.OUT);
		} else {
			p.setDirection(Direction.INOUT);
		}

		if (ImlUtil.exhibits(t, (Trait) stdLibs.getNamedType("iml.systems", "EventCarrier"))) {
			p.setEvent(true);
			p.setEventType(getCarrierType(t, "Event"));
		} else {
			p.setEvent(false);
		}

		if (ImlUtil.exhibits(t, (Trait) stdLibs.getNamedType("iml.systems", "DataCarrier"))) {
			p.setData(true);
			p.setEventType(getCarrierType(t, "Data"));
		} else {
			p.setData(false);
		}

	}

	public Pair<Port, Port> processConnector(SymbolDeclaration sd, Component container) {

		Port sourcep = Port.nil;
		Port destp = Port.nil;
		FolFormula f = sd.getDefinition();
		if (f instanceof SignedAtomicFormula) {
			FolFormula f1 = f.getLeft();
			if (f1 instanceof TailedExpression) {
				TailedExpression connect = (TailedExpression) f1;
				// get the source and destination
				if (connect.getTail() != null) {
					FolFormula sourcef = ((TupleConstructor) connect.getTail()).getElements().get(0).getLeft();
					FolFormula destf = ((TupleConstructor) connect.getTail()).getElements().get(1).getLeft();

					sourcep = getPort(sourcef, container);
					destp = getPort(destf, container);
				}
			}
		}

		return (new Pair<Port, Port>(sourcep, destp));

	}

	public Port getPort(FolFormula f, Component container) {
		// We expect the formula to be <<component.port>>, or <<port>>
		if (f instanceof SymbolReferenceTerm) {
			String portname = ((SymbolReferenceTerm) f).getSymbol().getName();
			return container.getPort(portname);
		} else if (f instanceof TermMemberSelection) {
			TermExpression porte = ((TermMemberSelection) f).getMember();
			TermExpression componente = ((TermMemberSelection) f).getReceiver();
			if (componente instanceof SymbolReferenceTerm) {
				String componentname = ((SymbolReferenceTerm) componente).getSymbol().getName();
				Component subc = container.getComponent(componentname);
				if (porte instanceof SymbolReferenceTerm) {
					String portname = ((SymbolReferenceTerm) porte).getSymbol().getName();
					return subc.getPort(portname);
				} else {
					logger.error("Port expression in component {} is not a symbol reference term)",
							container.getName());
				}
			} else {
				logger.error("Component expression in component {} is not a symbol reference term)",
						container.getName());
			}

		} else {
			logger.error(
					"Port cannot be found in component {} (term denoting a port is not a symbol reference or a term selection)",
					container.getName());
		}
		return Port.nil;
	}

	@Override
	public String toString() {
		String retval = "";
		for (String k : components.keySet()) {
			retval += k + "\n" + components.get(k).toString().replaceAll("(?m)^", "\t");
			retval += "\n";
		}
		return retval;
	}

	public ImlType getCarrierType(ImlType portType, String carrier) {
		// Retrieve the type
		if (portType instanceof SimpleTypeReference) {
			SimpleTypeReference tr = (SimpleTypeReference) portType;
			TypingEnvironment env = new TypingEnvironment(tr);
			List<TypeWithProperties> traits = ImlUtil.getRelationTypes(tr.getType(), TraitExhibition.class);
			for (TypeWithProperties twp : traits) {
				if (twp.getType() instanceof SimpleTypeReference && ((SimpleTypeReference) twp.getType())
						.getType() == stdLibs.getNamedType("iml.systems", carrier + "Carrier")) {
					ImlType bound = env.bind(twp.getType());
					if (bound instanceof SimpleTypeReference) {
						return ((SimpleTypeReference) bound).getTypeBinding().get(0);
					}
				}
			}
		}
		return null;
	}
	
	public Component getComponent(ImlType t) {
		if (hasComponent(t)) {
			return components.get(t);
		}
		return null;
	}
	
	public boolean hasComponent(ImlType t) {
		return (components.containsKey(ImlUtil.getTypeName(t, qnp)));
	}
	
}
