package com.utc.utrc.hermes.iml.gen.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.xbase.lib.Pair;

import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Inclusion;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.Refinement;
import com.utc.utrc.hermes.iml.iml.Relation;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.TraitExhibition;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;

public class ComponentType {

	private String name;
	private ImlType type;
	private Map<String, Port> ports;
	private Map<String, ComponentInstance> subs;
	private Map<String, Connection> connections;
	private ComponentType container;

	public static ComponentType nil = new ComponentType("Nil", null);

	public ComponentType(String name, ImlType type) {
		ports = new HashMap<>();
		subs = new HashMap<>();
		connections = new HashMap<>();
		this.container = null;
		this.type = type;
		this.name = name;
	}

	public ComponentType(String name, ImlType type, ComponentType container) {
		ports = new HashMap<>();
		subs = new HashMap<>();
		connections = new HashMap<>();
		this.container = container;
		this.type = type;
		this.name = name;
	}

	public boolean isTop() {
		return (container == null);
	}

	public ImlType getType() {
		return type;
	}

	public void setType(ImlType type) {
		this.type = type;
	}

	public Map<String, Port> getPorts() {
		return ports;
	}

	public List<Port> getPorts(Direction d) {
		List<Port> retval = new ArrayList<Port>();
		for (Port p : ports.values()) {
			if (p.getDirection() == d) {
				retval.add(p);
			}
		}
		return retval;
	}

	public void setPorts(Map<String, Port> ports) {
		this.ports = ports;
	}

	public void addPort(Port p) {
		ports.put(p.getName(), p);
	}

	public Port getPort(String name) {
		if (ports.containsKey(name))
			return ports.get(name);
		return Port.nil;
	}

	public Map<String,ComponentInstance> getSubs() {
		return subs;
	}

	public void setSubs(Map<String,ComponentInstance> subs) {
		this.subs = subs;
	}

	public void addSub(ComponentInstance c) {
		subs.put(c.getName(),c);
	}

	public ComponentInstance getComponent(String name) {
		if (subs.containsKey(name)) {
			return subs.get(name);
		}
		return ComponentInstance.nil;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addConnection(String name, Connection c) {
		connections.put(name, c);
	}

	public Map<String, Connection> getConnections() {
		return connections;
	}

	public Pair<ComponentInstance,Port> getDest(ComponentInstance s, Port sp) {
		for(Connection c : connections.values()) {
			if (c.getSourceComponent() == s && c.getSourcePort() == sp) {
				return new Pair<ComponentInstance, Port>(c.getTargetComponent(),c.getTargetPort()) ;
			}
		}
		return new Pair<ComponentInstance, Port>(ComponentInstance.nil,Port.nil) ;
	}

	public Pair<ComponentInstance,Port> getSource(ComponentInstance s, Port sp) {
		for(Connection c : connections.values()) {
			if (c.getTargetComponent() == s && c.getTargetPort() == sp) {
				return new Pair<ComponentInstance, Port>(c.getSourceComponent(),c.getSourcePort()) ;
			}
		}
		return new Pair<ComponentInstance, Port>(ComponentInstance.nil,Port.nil) ;
	}

	public ComponentType getContainer() {
		return container;
	}

	public void setContainer(ComponentType container) {
		this.container = container;
	}


	public List<SymbolDeclaration> getOtherSymbols() {
		List<SymbolDeclaration> sd = new ArrayList<>();
		if (type instanceof SimpleTypeReference) {
			for(Symbol s : ((SimpleTypeReference)type).getType().getSymbols()) {
				if ( (s instanceof SymbolDeclaration) && ! ( ports.containsKey(s.getName()) || subs.containsKey(s.getName()) || connections.containsKey(s.getName())) ) {
					sd.add((SymbolDeclaration)s);
				}
			}
			
			// also need to gather from exhibited traits
			// not dealing with shadowing
			for (Relation rl : ((SimpleTypeReference)type).getType().getRelations()) {
				if (rl instanceof TraitExhibition) {
					TraitExhibition tr = (TraitExhibition) rl;
					for (TypeWithProperties twp : tr.getExhibitions()) {
						if (twp.getType() instanceof SimpleTypeReference) {
							for(Symbol s : ((SimpleTypeReference)(twp.getType())).getType().getSymbols()) {
								if ( (s instanceof SymbolDeclaration) && ! ( ports.containsKey(s.getName()) || subs.containsKey(s.getName()) || connections.containsKey(s.getName())) ) {
									sd.add((SymbolDeclaration)s);
								}
							}
						}
					}
				}
			}
	
		}
		return sd ;
	}
	
	
	public List<SymbolDeclaration> getOtherSymbols2() {
		List<SymbolDeclaration> sd = new ArrayList<>();
		List<NamedType> l = new ArrayList<>();
		if (type instanceof SimpleTypeReference) {
			l.add(((SimpleTypeReference)type).getType());
		}
		for (int i = 0; i < l.size(); i++) {
			NamedType t = l.get(i);
			for(Symbol s : t.getSymbols()) {
				if ( (s instanceof SymbolDeclaration) && ! ( ports.containsKey(s.getName()) || subs.containsKey(s.getName()) || connections.containsKey(s.getName())) ) {
					sd.add((SymbolDeclaration)s);
				}
			}			
			// also need to gather from exhibited traits
			// not dealing with shadowing
			for (Relation rl : t.getRelations()) {
				if (rl instanceof TraitExhibition) {
					TraitExhibition tr = (TraitExhibition) rl;
					for (TypeWithProperties twp : tr.getExhibitions()) {
						if (twp.getType() instanceof SimpleTypeReference) {
							SimpleTypeReference str = (SimpleTypeReference)(twp.getType()); 
							if (!l.contains(str.getType())) {
								l.add(l.size(), str.getType());	// added to process its "super"
							}
						}
					}
				}
					
				// Refinement
				if (rl instanceof Refinement) {
					Refinement rft = (Refinement) rl;
					for (TypeWithProperties twp : rft.getRefinements()) {
						if (twp.getType() instanceof SimpleTypeReference) {
							SimpleTypeReference str = (SimpleTypeReference)(twp.getType());
							if (!l.contains(str.getType())) {
								l.add(l.size(), str.getType());	// added to process its "super"
							}
						}
					}
				}
				// Inclusion
				if (rl instanceof Inclusion) {
					Inclusion icln = (Inclusion) rl;
					for (TypeWithProperties twp : icln.getInclusions()) {
						if (twp.getType() instanceof SimpleTypeReference) {
							SimpleTypeReference str = (SimpleTypeReference)(twp.getType());
							if (!l.contains(str.getType())) {								
								l.add(l.size(), str.getType());	// added to process its "super"
							}
						}
					}
				}
			}
			
		}
		return sd ;
	}

	
	@Override
	public String toString() {

		String retval = "";
		retval += "Component " + getName() + "\n";
		for (Port p : ports.values()) {
			retval += "\t" + p.toString() + "\n";
		}
		for (ComponentInstance c : subs.values()) {
			retval += c.toString().replaceAll("(?m)^", "\t");
		}
		for (Connection k : connections.values()) {
			String connexpr = "";
			Port v = k.getSourcePort();
			if (k.getSourceComponent() != ComponentInstance.self) {
				connexpr += k.getSourceComponent().getName() + ".";
			}
			connexpr += v.getName() + " -> ";
			v = k.getTargetPort() ;
			if (k.getTargetComponent() != ComponentInstance.self) {
				connexpr += k.getTargetComponent().getName() + ".";
			}
			connexpr += v.getName();
			retval += "\t" + connexpr + "\n";
		}

		return retval;
	}

}
