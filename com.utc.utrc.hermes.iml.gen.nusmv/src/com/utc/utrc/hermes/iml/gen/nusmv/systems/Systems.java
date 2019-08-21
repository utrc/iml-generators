package com.utc.utrc.hermes.iml.gen.nusmv.systems;

import static com.utc.utrc.hermes.iml.util.ImlUtil.getRelatedTypesWithProp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.Relation;
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
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;

public class Systems {

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	final Logger logger = LoggerFactory.getLogger(Systems.class);

	private Map<String, ComponentType> components;

	public Systems() {
		components = new HashMap<String, ComponentType>();
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
			if (s instanceof NamedType && !ImlUtil.isPolymorphic(s)) {
				NamedType nt = (NamedType) s;
				if (ImlUtil.exhibits(nt, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
					SimpleTypeReference ref = ImlCustomFactory.INST.createSimpleTypeReference(nt);
					ComponentType c = processComponent(ref);
					components.put(ImlUtil.getTypeName(ref, qnp), c);
				}
			}
		}
	}

	

	public ComponentType processComponent(SimpleTypeReference t) {

		if (hasComponent(t)) {
			return getComponent(t);
		}
		List<SymbolDeclaration> connectors = new ArrayList<>();
		ComponentType c = new ComponentType(ImlUtil.getTypeName(t, qnp), t);
		for (Symbol s : t.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd_s = (SymbolDeclaration) s;
				ImlType s_type = sd_s.getType();
				if (ImlUtil.exhibits(s_type, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
					if (s_type instanceof SimpleTypeReference) {
						ComponentType ct = processComponent((SimpleTypeReference) s_type);
						ComponentInstance ci = new ComponentInstance(sd_s, ct);
						c.addSub(ci);
					}
				} else if (ImlUtil.exhibits(s_type, (Trait) stdLibs.getNamedType("iml.systems", "Port"))) {
					processPort(sd_s, s_type, c);
				} else if (ImlUtil.hasType(s_type, stdLibs.getNamedType("iml.systems", "Connector"))) {
					connectors.add(sd_s);
				} else {
					logger.info("Component {} also includes symbol { } which this class does not process.", c.getName(),
							sd_s.getName());
				}

			}
		}

		// add ports and sub-components
		// TODO we only process simple type references at the moment
		// TODO we should also process arrays that are very convenient
		
		for (Relation rl : t.getType().getRelations()) {
			if (rl instanceof TraitExhibition) {
				TraitExhibition tr = (TraitExhibition) rl;
				for (TypeWithProperties twp : tr.getExhibitions()) {
					if (twp.getType() instanceof SimpleTypeReference) {
						gatherFromExhibit((SimpleTypeReference) twp.getType(), c, connectors);
					}
				}
			}
		}
		
		// process connectors
		for (SymbolDeclaration s : connectors) {
			processConnector(s, c);
		}

		return c;
	}

	private void gatherFromExhibit(SimpleTypeReference str, ComponentType c, List<SymbolDeclaration> connectors) {
		for (Symbol s : str.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd_s = (SymbolDeclaration) s;
				ImlType s_type = sd_s.getType();
				if (ImlUtil.exhibits(s_type, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
					if (s_type instanceof SimpleTypeReference) {
						ComponentType ct = processComponent((SimpleTypeReference) s_type);
						ComponentInstance ci = new ComponentInstance(sd_s, ct);
						c.addSub(ci);
					}
				} else if (ImlUtil.exhibits(s_type, (Trait) stdLibs.getNamedType("iml.systems", "Port"))) {
					processPort(sd_s, s_type, c);
				} else if (ImlUtil.hasType(s_type, stdLibs.getNamedType("iml.systems", "Connector"))) {
					connectors.add(sd_s);
				} else {
					logger.info("Component {} also includes symbol { } which this class does not process.", c.getName(),
							sd_s.getName());
				}
			}
		}
		for (Relation rl : str.getType().getRelations()) {
			if (rl instanceof TraitExhibition) {
				TraitExhibition tr = (TraitExhibition) rl;
				for (TypeWithProperties twp : tr.getExhibitions()) {
					if (twp instanceof SimpleTypeReference) {
						gatherFromExhibit((SimpleTypeReference) twp, c, connectors);
					}
				}
			}
		}
	}

	public void processPort(SymbolDeclaration sd, ImlType t, ComponentType container) {

		Port p = new Port(sd);
		p.setContainer(container);
		if (container != null) {
			container.addPort(p);
		} else {
			logger.error("Port {} should have a container ", sd.getName());
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
			p.setDataType(getCarrierType(t, "Data"));
		} else {
			p.setData(false);
		}

	}

	public void processConnector(SymbolDeclaration sd, ComponentType container) {

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
					Pair<ComponentInstance, Port>  source = getComponentAndPort(sourcef, container);
					Pair<ComponentInstance, Port>  dest = getComponentAndPort(destf, container);
					Connection conn = new Connection(sd,source.getKey(), source.getValue(), dest.getKey(), dest.getValue());
					container.addConnection(sd.getName(), conn);
				}
			}
		}
	}

	public Pair<ComponentInstance, Port> getComponentAndPort(FolFormula f, ComponentType container) {
		// We expect the formula to be <<component.port>>, or <<port>>
		if (f instanceof SymbolReferenceTerm) {
			//This is a port which we shuld be able to get from the component itself
			String portname = ((SymbolReferenceTerm) f).getSymbol().getName();
			return (new Pair<ComponentInstance,Port>(ComponentInstance.self,container.getPort(portname)));
		} else if (f instanceof TermMemberSelection) {
			TermExpression porte = ((TermMemberSelection) f).getMember();
			TermExpression componente = ((TermMemberSelection) f).getReceiver();
			if (componente instanceof SymbolReferenceTerm) {
				String componentname = ((SymbolReferenceTerm) componente).getSymbol().getName();
				ComponentInstance subc = container.getComponent(componentname);
				if (porte instanceof SymbolReferenceTerm) {
					String portname = ((SymbolReferenceTerm) porte).getSymbol().getName();
					Port p = subc.getComponentType().getPort(portname);
					return (new Pair<ComponentInstance,Port>(subc,p)) ;
				} else {
					logger.error("Port expression in component {} is not a symbol reference term)",
							container.getName());
					return (new Pair<ComponentInstance,Port>(subc,Port.nil)) ;
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
		return (new Pair<ComponentInstance,Port>(ComponentInstance.nil,Port.nil)) ;
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

	public ComponentType getComponent(ImlType t) {
		if (hasComponent(t)) {
			return components.get(ImlUtil.getTypeName(t, qnp));
		}
		return null;
	}

	public boolean hasComponent(ImlType t) {
		return (components.containsKey(ImlUtil.getTypeName(t, qnp)));
	}

	public void reset() {
		components.clear();
	}

}
