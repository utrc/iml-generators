package com.utc.utrc.hermes.iml.gen.zinc.model;

import at.siemens.ct.jmz.expressions.Expression;

public class Constraint extends at.siemens.ct.jmz.elements.constraints.Constraint {
	
	private Expression<Boolean> expression;
	public Expression<Boolean>  getExpression() {
		return expression;
	}
	
	public Constraint(Expression<Boolean> expression) {
		super(expression);
		this.expression = expression;
	}
	
	public Constraint(String group, String name,Expression<Boolean> expression) {
		super(group,name,expression);
		this.expression = expression;
	}
	
	@Override
	public String declare() {
		if (getConstraintGroup()!=null && getConstraintName() != null) {
			return "constraint :: \"" + getConstraintGroup() + " - " + getConstraintName() + "\"" + getExpressionString() + ";";
		}
		return "constraint " + getExpressionString() + ";";
	}

	
}
