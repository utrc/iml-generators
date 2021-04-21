/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.model;

import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.xtext.naming.QualifiedName;

import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.util.ImlUtil;

import at.siemens.ct.jmz.ModelBuilder;
import at.siemens.ct.jmz.elements.Element;
import at.siemens.ct.jmz.elements.TypeInst;
import at.siemens.ct.jmz.elements.Variable;
import at.siemens.ct.jmz.expressions.Constant;
import at.siemens.ct.jmz.expressions.Expression;
import at.siemens.ct.jmz.expressions.Operator;
import at.siemens.ct.jmz.expressions.bool.BooleanExpression;
import at.siemens.ct.jmz.expressions.bool.BooleanVariable;
import at.siemens.ct.jmz.expressions.bool.RelationalOperation;
import at.siemens.ct.jmz.expressions.bool.RelationalOperator;
import at.siemens.ct.jmz.expressions.integer.BasicInteger;
import at.siemens.ct.jmz.expressions.integer.IntegerOperation;
import at.siemens.ct.jmz.expressions.integer.IntegerVariable;
import at.siemens.ct.jmz.writer.ModelWriter;
import at.siemens.ct.jmz.elements.constraints.Constraint;

public class MiniZincModel extends ModelBuilder {
	
	Map<String,IntegerFunction> functions ;
	Map<String,EnumType> enums ;
	Map<String,EnumVar> enumvars;
	private String name;
	private Map<String,IntegerVariable> oldName2newVariable;
	
	public MiniZincModel() {
		super() ;
		functions = new HashMap<String, IntegerFunction>() ;
		enums = new HashMap<>();
		enumvars = new HashMap<>();
	}
	
	
	public MiniZincModel(String name) {
		this() ;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	public void add(IntegerFunction f) {
	
		functions.put(f.getName(),f);
	}
	
	public Set<String> getFunctionNames(){
		return functions.keySet();
	}
	
	public IntegerFunction getFunction(String name) {
		if (functions.containsKey(name)) {
			return functions.get(name);
		}
		return null;
	}
	
	public Map<String,EnumType> getEnums(){
		return enums;
	}
	
	public Map<String,EnumVar> getEnumVars(){
		return enumvars;
	}
	
	public MiniZincModel copyInto(MiniZincModel inModelBuilder){
		//MiniZincModel newModelBuilder = new MiniZincModel();
		List<Element> allElements = this.elements().collect(Collectors.toList());
    
    // Copy constants
    for (Element element : allElements) {  
    	if (BasicInteger.class.isAssignableFrom(element.getClass())
	    	&& (!inModelBuilder.elements().collect(Collectors.toList()).contains(element)))
	    		inModelBuilder.add(element);
    }
    
    // Copy variable
    for (Element element : allElements) {  
    	if (element instanceof IntegerVariable
	    	&& (!inModelBuilder.elements().collect(Collectors.toList()).contains(element)))
	    		inModelBuilder.add(element);
    }
    
    // Copy constraints
    for (Element element : allElements) {  
    	if (element instanceof Constraint
    			&& (!inModelBuilder.elements().collect(Collectors.toList()).contains(element)))
	    		inModelBuilder.add(element);
    }
    
    return inModelBuilder;
	}
	
	public MiniZincModel clone(String newCPModelName, MiniZincModel inModelBuilder){
		//MiniZincModel newModelBuilder = new MiniZincModel();
		String oldCPModelName = this.name;
		List<Element> allElements = this.elements().collect(Collectors.toList());
    
    // Copy constants
    for (Element element : allElements) {  
      if (BasicInteger.class.isAssignableFrom(element.getClass())
      		&& (!inModelBuilder.elements().collect(Collectors.toList()).contains(element)))
      	inModelBuilder.add(element);
	      
    }
    
		//Create new variables
		oldName2newVariable = new HashMap<String,IntegerVariable>();	
    for (Element element : allElements) {  
      if  (element instanceof IntegerVariable){
      	IntegerVariable variable=((IntegerVariable)element);
      	String newName = variable.getName();
      	newName = newName.replace(oldCPModelName.replace(".", "___"), newCPModelName.replace(".", "___"));
      	IntegerVariable replacement = new IntegerVariable(newName);
      	oldName2newVariable.put(variable.getName(), replacement);
      	inModelBuilder.add(replacement);
      }	      
    }
      
    //Create new constraints
    for (Element element : allElements) {
      if (element instanceof Constraint) {
        Constraint constraint = (Constraint) element;
        Expression<Boolean> newExpression=null;
				try {
					newExpression = (Expression<Boolean>) this.copy(constraint.getExpression());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
        Constraint replacement = new Constraint(newExpression);
        inModelBuilder.add(replacement);
      }
    }
    
    return inModelBuilder;
	}
	
	public MiniZincModel clone(String newCPModelName){
		MiniZincModel newModelBuilder = new MiniZincModel();
		clone(newCPModelName, newModelBuilder);
    return newModelBuilder;
	}	
	
	private <T> T copy(T entity) throws IllegalAccessException, InstantiationException {
		T newEntity = entity;
	  Class<?> clazz = entity.getClass();
  	if((Variable.class.isAssignableFrom(clazz)) 
  			||(Operator.class.isAssignableFrom(clazz)) 
  			||(Constant.class.isAssignableFrom(clazz))
  			||(BasicInteger.class.isAssignableFrom(clazz))){
  		if (Variable.class.isAssignableFrom(clazz)
  				//|| Constant.class.isAssignableFrom(clazz)
  				) {
  			String name = ((Variable) entity).getName();
  			if (oldName2newVariable.containsKey(name))
  				newEntity = (T) oldName2newVariable.get(name);
  		}
  	}else {   
//		  System.out.println(clazz);		  
		  if (entity instanceof RelationalOperation) {
		  	RelationalOperation<Boolean> ro = (RelationalOperation)entity;
		  	newEntity = (T) new RelationalOperation(ro.getLeft(),(RelationalOperator) ro.getOperator(),ro.getRight());
		  }
		  if (entity instanceof IntegerOperation) {
		  	IntegerOperation ro = (IntegerOperation)entity;
		  	newEntity = (T) new IntegerOperation(ro.getLeft(), ro.getOperator(),ro.getRight());
		  }	  
		  //IntegerOperation
		  		
		  while (clazz != null) {
//		  		System.out.println(" " + entity + ", " +newEntity +", " + clazz);
		      copyFields(entity, newEntity, clazz);
		      clazz = clazz.getSuperclass();
		  }
  	}
	
	  return newEntity;
	}

	private <T> T copyFields(T entity, T newEntity, Class<?> clazz) throws IllegalAccessException, InstantiationException {
	  for (Field field : clazz.getDeclaredFields()) {
	  	field.setAccessible(true);
	  	Object value = field.get(entity);
	  	if (value!=null)
	  		value = this.copy(field.get(entity));
      field.set(newEntity, value);
	  }
	  return newEntity;
	}
}
