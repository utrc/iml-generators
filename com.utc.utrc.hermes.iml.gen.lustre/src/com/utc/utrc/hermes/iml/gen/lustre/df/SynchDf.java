package com.utc.utrc.hermes.iml.gen.lustre.df;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.common.systems.Systems;
import com.utc.utrc.hermes.iml.iml.Annotation;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.Trait;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.util.ImlUtil;

public class SynchDf {

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	final Logger logger = LoggerFactory.getLogger(SynchDf.class);

	// TODO need to handle polymorphic nodes
	private Map<String, Node> nodes;
	private Map<String, ImlType> otherTypes ;
	

	private Systems sys ;
	
	public void setSystems(Systems sys) {
		this.sys = sys ;
	}
	
	public SynchDf() {
		this.sys = null ;
		nodes = new HashMap<>();
		otherTypes = new HashMap<>();
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
				if (ImlUtil.exhibits(nt, (Trait) stdLibs.getNamedType("iml.synchdf.ontological", "Synchronous"))) {
					Node node = processNode(ImlCustomFactory.INST.createSimpleTypeReference(nt));
					nodes.put(ImlUtil.getTypeName(ImlCustomFactory.INST.createSimpleTypeReference(nt), qnp), node);
				}
			}
		}
	}

	public Node processNode(SimpleTypeReference tr) {
		if (nodes.containsKey(ImlUtil.getTypeName(tr,qnp))){
			return nodes.get(ImlUtil.getTypeName(tr,qnp)) ;
		}
		Node retval = new Node();
	
		retval.setNodeType(tr);
		
		for(Symbol s : tr.getType().getSymbols()) {
			if ( s instanceof SymbolDeclaration) {
				if ( isLet((SymbolDeclaration) s)) {
					retval.getLets().put(s.getName(), ((SymbolDeclaration) s).getDefinition()) ;
				}
			}
		}

		if (ImlUtil.exhibits(tr, (Trait) stdLibs.getNamedType("iml.systems", "Component"))) {
			retval.setComponent(true);
			retval.setComponentType(sys.getComponent(tr));
		}

		return retval;
	}
	
	
	public Node getNode(ImlType t) {
		if (hasNode(t)) {
			return nodes.get(ImlUtil.getTypeName(t, qnp));
		}
		return null;
	}
	
	public boolean hasNode(ImlType t) {
		return (nodes.containsKey(ImlUtil.getTypeName(t, qnp)));
	}


	@Override
	public String toString() {
		String retval = "";
		for (String k : nodes.keySet()) {
			retval += k + "\n" + nodes.get(k).toString().replaceAll("(?m)^", "\t");
			retval += "\n";
		}
		return retval;
	}

	
	public boolean isLet(SymbolDeclaration s ) {
		return (ImlUtil.hasAnnotation((SymbolDeclaration) s, (Annotation) stdLibs.getNamedType("iml.synchdf.ontological", "Let"))) ;
	}

	public void reset() {
		nodes.clear();
		otherTypes.clear();
	}
	
	
}
