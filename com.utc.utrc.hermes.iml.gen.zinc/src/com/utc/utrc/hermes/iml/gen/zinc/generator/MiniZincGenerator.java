/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.zinc.generator;

import java.util.HashMap;
import java.util.List;

import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.Extension;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory;
import com.utc.utrc.hermes.iml.gen.zinc.model.Constraint;
import com.utc.utrc.hermes.iml.gen.zinc.model.EnumType;
import com.utc.utrc.hermes.iml.gen.zinc.model.EnumVar;
import com.utc.utrc.hermes.iml.gen.zinc.model.MiniZincModel;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.LambdaExpression;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.SequenceTerm;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.util.ImlUtil;

import at.siemens.ct.jmz.IModelBuilder;
import at.siemens.ct.jmz.ModelBuilder;
import at.siemens.ct.jmz.elements.solving.OptimizationType;
import at.siemens.ct.jmz.elements.solving.Optimize;
import at.siemens.ct.jmz.elements.solving.SolveSatisfy;
import at.siemens.ct.jmz.elements.solving.SolvingStrategy;
import at.siemens.ct.jmz.expressions.bool.BooleanExpression;
import at.siemens.ct.jmz.expressions.bool.RelationalOperation;
import at.siemens.ct.jmz.expressions.bool.RelationalOperator;
import at.siemens.ct.jmz.expressions.integer.IntegerExpression;
import at.siemens.ct.jmz.expressions.integer.IntegerVariable;
import at.siemens.ct.jmz.writer.IModelWriter;
import at.siemens.ct.jmz.writer.ModelWriter;

public class MiniZincGenerator {

	MiniZincModel builder ;
	IModelWriter modelWriter ;
	
	@Inject
	ImlTypeProvider typeProvider;

	@Inject
	MiniZincGeneratorServices server;

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;
	
	String prefixname ;
	String typename;
	
	public MiniZincGenerator() {
		
	}
	
	public void generate(SymbolDeclaration query) throws MiniZincGeneratorException {
		//Retrieve the type
		builder = new MiniZincModel();
		NamedType type = server.getQueryType(query);
		LambdaExpression cost = server.getCostDefinition(query);
		QualifiedName prefix = server.getPrefixFromCostFunction(query);
		prefixname = prefix.toString();
		typename = type.getName();
		generateType(prefix, type);
		SolvingStrategy sol = generateSolvingStrategy(prefix, cost, server.getSolutionType(query));
		modelWriter = new ModelWriter(builder);
		modelWriter.setSolvingStrategy(sol);
		//System.out.println(modelWriter.toString());
		
		
	}
	
	public String getModelAsString() {
		StringBuilder retval = new StringBuilder();
		retval.append(modelWriter.toString()) ;
		//append both enum types and constraints
		for(EnumType t : builder.getEnums().values()) {
			retval.append(t.declare());
		}
		
		for(EnumVar v : builder.getEnumVars().values()) {
			retval.append(v.declare()) ;
		}
		
		return retval.toString();
	}
	
	public IModelWriter getModelWriter() {
		return modelWriter;
	}
	
	public MiniZincModel getBuilder() {
		return builder;
	}
	
	public String getPrefixAsString() {
		return prefixname;
	}
	
	public String getTypeName() {
		return typename;
	}
	
	public void generateType(QualifiedName prefix, NamedType type) throws MiniZincGeneratorException {
		//Get all symbol declarations
		//if this is an enum type, then generate the enum and return
		
		List<SymbolDeclaration> symbols = server.getSymbolDeclarations(type);
		for(SymbolDeclaration s : symbols) {
			if (stdLibs.isPrimitive(s.getType())) {
				server.addSymbol(prefix, builder,s);
			} else {
				//need to recursively generate the type
				ImlType stype = s.getType();
				if (stype instanceof SimpleTypeReference) {
					if (ImlUtil.isEnum(((SimpleTypeReference) stype).getType())) {
						server.addEnum(prefix,builder,((SimpleTypeReference) stype).getType());
						//add the enum variable
						server.addSymbol(prefix, builder,s);
					} else {
						generateType(prefix.append(s.getName()),((SimpleTypeReference)stype).getType());
					}
				}
			}
		}
				
		//we now need to get the assertions
		List<Assertion> asserts = server.getAssertions(type);
		for(Assertion a : asserts) {
			server.addConstraint(prefix,builder,ImlCustomFactory.INST.createSimpleTypeReference(type),a);
		}
	}
	
	public SolvingStrategy generateSolvingStrategy(QualifiedName prefix, LambdaExpression c, QueryType qtype) throws MiniZincGeneratorException {
		//get the context
		ImlType imlt = c.getParameters().get(0).getType() ;
		if (imlt instanceof SimpleTypeReference) {
			SimpleTypeReference ctx = (SimpleTypeReference) imlt;
			FolFormula formula = ((SequenceTerm) c.getDefinition()).getReturn() ;
			if ( formula instanceof SignedAtomicFormula  ) {
				SolvingStrategy opt = null ;
				if (qtype == QueryType.SAT) {
					BooleanExpression property = server.process(QualifiedName.EMPTY, builder, ctx, formula.getLeft(), new HashMap<>());
					builder.add(new Constraint(property));
					opt = new SolveSatisfy();
				} else {
					server.addVariable(QualifiedName.EMPTY, builder, ImlCustomFactory.INST.createSymbolDeclaration(stdLibs.getIntType(), "cost___"));
					IntegerExpression costfunction = server.processIntegerExpression(QualifiedName.EMPTY, builder, ctx, formula.getLeft(), new HashMap<>());
					BooleanExpression costeq = new RelationalOperation<Integer>((IntegerVariable) builder.getElementByName("cost___"), RelationalOperator.EQ, costfunction);
					builder.add(new Constraint(costeq));
					if (qtype == QueryType.MIN) {
						opt = new Optimize(OptimizationType.MIN,(IntegerVariable) builder.getElementByName("cost___")) ;
					} else {
						opt = new Optimize(OptimizationType.MAX,(IntegerVariable) builder.getElementByName("cost___")) ;
					}
				}
				return opt;
			} else {
				throw new MiniZincGeneratorException("The cost function must be an atomic expression") ;
  			}
		} else {
			throw new MiniZincGeneratorException("The type of the argument of the cost function myst be a simple named type");
		}
	}
	
	
}
