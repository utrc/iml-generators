package com.utc.utrc.hermes.iml.gen.nusmv.systems;

import com.utc.utrc.hermes.iml.iml.ImlType;

public class Port {
	private String name;
	private ImlType type;
	private Direction direction;
	private boolean data;
	private ImlType dataType;
	private boolean event;
	private ImlType eventType;
	private ComponentType container;

	public static Port nil = new Port();

	public Port() {
		data = false;
		event = false;
		container = null;
		type = null;
		name = null;

	}

	public ImlType getType() {
		return type;
	}

	public void setType(ImlType type) {
		this.type = type;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ComponentType getContainer() {
		return container;
	}

	public void setContainer(ComponentType container) {
		this.container = container;
	}

	public boolean isData() {
		return data;
	}

	public void setData(boolean data) {
		this.data = data;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public ImlType getDataType() {
		return dataType;
	}

	public void setDataType(ImlType dataType) {
		this.dataType = dataType;
	}

	public ImlType getEventType() {
		return eventType;
	}

	public void setEventType(ImlType eventType) {
		this.eventType = eventType;
	}

	@Override
	public String toString() {
		String retval = "";
		switch (direction) {
		case IN:
			retval += "in ";
			break;
		case OUT:
			retval += "out ";
			break;
		case INOUT:
			retval += "inout ";
			break;
		}
		if (isEvent()) retval += "event " ;
		if (isData()) retval += "data " ;
		retval += getName() ;
		return retval ;
	}

}
