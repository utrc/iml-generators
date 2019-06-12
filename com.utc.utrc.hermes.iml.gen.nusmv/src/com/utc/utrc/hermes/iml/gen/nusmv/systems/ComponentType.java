package com.utc.utrc.hermes.iml.gen.nusmv.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.xbase.lib.Pair;

import com.utc.utrc.hermes.iml.iml.ImlType;

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
