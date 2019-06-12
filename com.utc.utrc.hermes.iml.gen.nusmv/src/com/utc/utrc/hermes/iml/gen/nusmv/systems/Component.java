package com.utc.utrc.hermes.iml.gen.nusmv.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.utc.utrc.hermes.iml.iml.ImlType;


public class Component {
	
	private String name ;
	private ImlType type ;
	private List<Port> ports ;
	private List<Component> subs ;
	private Map<Port,Port> connections ;
	private Component container ;
	
	public static Component nil = new Component("Nil", null);
	
	public Component(String name, ImlType type) {
		ports = new ArrayList<>();
		subs = new ArrayList<>();
		connections = new HashMap<Port, Port>();
		this.container = null;
		this.type = type ;
		this.name = name;
	}
	
	public Component(String name, ImlType type, Component container) {
		ports = new ArrayList<>();
		subs = new ArrayList<>();
		connections = new HashMap<Port, Port>();
		this.container = container;
		this.type = type ;
		this.name = name;
	}
	
	public boolean isTop() {
		return (container == null );
	}
	
	public ImlType getType() {
		return type;
	}

	public void setType(ImlType type) {
		this.type = type;
	}

	public List<Port> getPorts() {
		return ports;
	}
	
	
	public List<Port> getPorts(Direction d) {
		List<Port> retval = new ArrayList<Port>();
		for(Port p: ports) {
			if (p.getDirection() == d) {
				retval.add(p);
			}
		}
		return retval;
	}
	
	
	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}
	
	public void addPort(Port p) {
		ports.add(p);
	}
	
	public Port getPort(String name) {
		for(Port p : ports) {
			if (p.getName().equals(name)) {
				return p ;
			}
		}
		return Port.nil;
	}
	
	public List<Component> getSubs() {
		return subs;
	}

	public void setSubs(List<Component> subs) {
		this.subs = subs;
	}
	
	public void addSub(Component c) {
		subs.add(c);
	}
	
	public Component getComponent(String name) {
		for ( Component c : subs) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return Component.nil ;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public void connect(Port source, Port dest) {
		connections.put(source, dest) ;
	}
	
	public Map<Port,Port> getConnections() {
		return connections ;
	}
	
	public Port getDest(Port source) {
		if (connections.containsKey(source)) {
			return connections.get(source) ;
		} else {
			return Port.nil;
		}
	}
	
	public Port getSource(Port dest) {
		for(Port k : connections.keySet()) {
			if (connections.get(k) == dest) {
				return k ;
			}
		}
		return Port.nil;
	}

	public Component getContainer() {
		return container;
	}

	public void setContainer(Component container) {
		this.container = container;
	}

	@Override
	public String toString() {
		
		String retval = "" ;
		retval += "Component " + getName() + "\n";
		for(Port p : ports) {
			retval += "\t" + p.toString() + "\n" ;
		}
		for(Component c : subs) {
			retval += c.toString().replaceAll("(?m)^", "\t");
		}
		for(Port k : connections.keySet()) {
			String connexpr = "" ;
			Port v = connections.get(k);
			if ( k.getContainer() != this) {
				connexpr += k.getContainer().getName() + "." ; 
			}
			connexpr +=  k.getName() + " -> " ;
			if ( v.getContainer() != this) {
				connexpr += v.getContainer().getName() + "." ; 
			}
			connexpr +=  v.getName() ;
			retval += "\t" + connexpr + "\n" ;
		}
		
		return retval;
	}
	
	
	
}
