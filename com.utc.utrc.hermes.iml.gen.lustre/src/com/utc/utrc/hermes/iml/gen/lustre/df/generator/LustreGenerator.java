/*******************************************************************************
 * Copyright (c) 2021 Raytheon Technologies. All rights reserved.
 * See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.lustre.df.generator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.gen.common.systems.ComponentInstance;
import com.utc.utrc.hermes.iml.gen.common.systems.ComponentType;
import com.utc.utrc.hermes.iml.gen.common.systems.Connection;
import com.utc.utrc.hermes.iml.gen.common.systems.Direction;
import com.utc.utrc.hermes.iml.gen.common.systems.Port;
import com.utc.utrc.hermes.iml.gen.lustre.df.Node;
import com.utc.utrc.hermes.iml.gen.lustre.df.SynchDf;
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreElementType;
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreModel;
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreNode;
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreSymbol;
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreTypeInstance;
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreVariable;
import com.utc.utrc.hermes.iml.iml.Annotation;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.AtomicExpression;
import com.utc.utrc.hermes.iml.iml.EnumRestriction;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.InstanceConstructor;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm;
import com.utc.utrc.hermes.iml.iml.Refinement;
import com.utc.utrc.hermes.iml.iml.Relation;
import com.utc.utrc.hermes.iml.iml.RelationKind;
import com.utc.utrc.hermes.iml.iml.SequenceTerm;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.TermMemberSelection;
import com.utc.utrc.hermes.iml.iml.TraitExhibition;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;
import com.utc.utrc.hermes.iml.lib.ContractsServices;
import com.utc.utrc.hermes.iml.lib.LangServices;
import com.utc.utrc.hermes.iml.lib.SmsServices;
import com.utc.utrc.hermes.iml.lib.SystemsServices;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;

public class LustreGenerator {

	@Inject
	ImlTypeProvider typeProvider;

	@Inject
	LustreGeneratorServices generatorServices;

	@Inject
	private ContractsServices contractServices;
	
	@Inject 
	private LangServices langServices;
	
	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	private Configuration conf;

	private SynchDf sdf;
	
	private Map<String, String> lustre2Iml;
	private ArrayList<String> nodeCallOrder;

	public LustreGenerator() {
		lustre2Iml = new HashMap<>();
		nodeCallOrder = new ArrayList<>();
	}

	public LustreGenerator(Configuration conf, SynchDf sdf) {
		this.conf = conf;
		this.sdf = sdf;
		lustre2Iml = new HashMap<>();
		nodeCallOrder = new ArrayList<>();
	}

	public void setSdf(SynchDf sdf) {
		this.sdf = sdf;
	}

	public LustreNode generateLustreNode(LustreModel m, Node node) {		
		generatorServices.setAuxiliaryDS(lustre2Iml, nodeCallOrder);
		return generateLustreNodeR(m, node);
	}	

	public LustreNode generateLustreNodeR(LustreModel m, Node node) {		

		String type_name = ImlUtil.getTypeName(node.getNodeType(), qnp);
		
		if (!nodeCallOrder.contains(type_name)) {
			nodeCallOrder.add(type_name);
		} else {
			nodeCallOrder.remove(type_name);
			nodeCallOrder.add(type_name);	// forget the old
		}
				
		if (m.hasType(type_name)) {
			return m.getType(type_name);
		}

		LustreNode target = new LustreNode(type_name);
		m.addNode(target);

		SimpleTypeReference tr = node.getNodeType();
		if (node.isComponent()) {
			ComponentType ct = node.getComponentType();
			for (Port p : ct.getPorts(Direction.IN)) {
				generateInput(target, p);
			}
			for (Port p : ct.getPorts(Direction.OUT)) {
				generateOutput(target, p, tr);
			}
			for (ComponentInstance sub : ct.getSubs().values()) {
				LustreNode gensm = generateLustreNodeR(target.getContainer(),
						sdf.getNode(sub.getComponentType().getType()));
				addSymbol(target, sub.getName(), gensm, LustreElementType.COMPONENT);
			}
			// add all connections
			// first process connections to higher level
			Map<String, String> outputPort2OutputPort = new HashMap<>();
			for (Connection conn : ct.getConnections().values()) {
				if (conn.getTargetComponent() == ComponentInstance.self) {
					generateConnectionToHigherLevel(target, conn, tr, outputPort2OutputPort);
				}
			}
			// then process connections at this level or from higher level
			for (Connection conn : ct.getConnections().values()) {
				if (conn.getTargetComponent() != ComponentInstance.self) {
					generateConnectionSameLevelOrFromHigherLevel(target, conn, tr, outputPort2OutputPort);
				}
			}

			// add all other symbols
			TypingEnvironment typing = new TypingEnvironment(tr);
			for (SymbolDeclaration sd : ct.getOtherSymbols()) {
				if (! sdf.isLet(sd)) {	// never false
					
					if(ImlUtil.hasAnnotation(sd, contractServices.getAssumeAnnotation())) {
						processAG(target, tr, typing, sd, 1);
						continue;
					}
					if(ImlUtil.hasAnnotation(sd, (Annotation) contractServices.getGuaranteeAnnotation())) {
						processAG(target, tr, typing, sd, 2);
						continue;
					} 
					
					if (sd.getType() instanceof SimpleTypeReference) {
						generateType(m, (SimpleTypeReference) typing.bind(sd.getType()));
						addSymbol(m, target, sd, tr);
					}
					if ((sd instanceof Assertion) && !isConnectionAssertion(sd)) {
						addSymbol(m, target, sd, tr);						
					}
				}
				
				// Here to get more symbols from relations ????
			}
		} else {
			for (Symbol sd : node.getNodeType().getType().getSymbols()) {
				if (sd instanceof SymbolDeclaration) {
					if (! sdf.isLet( (SymbolDeclaration) sd)) {
						ImlType t = ((SymbolDeclaration)sd).getType();
						if ( t instanceof SimpleTypeReference) {
							addSymbol(m, target, (SymbolDeclaration) sd, tr);
						}
					}
				}
			}
			
			// Here to get more symbols from relations ????
		}
		
		for(String s : node.getLets().keySet()) {
			LustreSymbol symbol = addSymbol(target, s, LustreModel.Bool, LustreElementType.LET);
			symbol.setDefinition(generatorServices.serialize(node.getLets().get(s), tr, "."));
		}
		return target;
	}

	/**
	 * @param target
	 * @param tr
	 * @param typing
	 * @param sd
	 */
	private void processAG(LustreNode target, SimpleTypeReference tr, TypingEnvironment typing, SymbolDeclaration sd, int ag) {
		ImlType bound = typing.bind(sd.getType());
		if (bound instanceof SimpleTypeReference) {
			LustreNode nbound = generateType(target.getContainer(), (SimpleTypeReference) bound);
			LustreSymbol ls = addSymbol(target, sd.getName(), nbound, LustreElementType.FIELD, ag);
			if (sd.getDefinition() != null) {
				ls.setDefinition(generatorServices.serialize(sd.getDefinition(), tr, ".") );
			}
		}
	}

	private String SerializeIfAssertionWithStructFieldAsLValue(SymbolDeclaration sd, SimpleTypeReference ctx) {
		String retStr = "";
		FolFormula def = sd.getDefinition();
		def = stripParenthesis(def);
		if (def instanceof SequenceTerm) {
			FolFormula ret = ((SequenceTerm) def).getReturn();
			ret = stripParenthesis(ret);
			if (ret instanceof SignedAtomicFormula) {
				FolFormula l = ((SignedAtomicFormula) ret).getLeft();
				l = stripParenthesis(l);
				if (l instanceof AtomicExpression) {
					AtomicExpression ae = (AtomicExpression) l;
					if (ae.getRel() != RelationKind.EQ) {
						return retStr;
					}
					FolFormula ll = ae.getLeft();
					ll = stripParenthesis(ll);
					if (ll instanceof TermMemberSelection) {
						TermExpression rcvr = ((TermMemberSelection) ll).getReceiver();
						FolFormula rcvrl = stripParenthesis(rcvr);
						if (rcvrl instanceof SymbolReferenceTerm) {
							SymbolReferenceTerm srtl = (SymbolReferenceTerm) rcvrl;
							if (srtl.getSymbol() instanceof SymbolDeclaration) {
								SymbolDeclaration sdVar = (SymbolDeclaration) srtl.getSymbol();
								if (sdVar.getType() instanceof SimpleTypeReference) {
									SimpleTypeReference str = (SimpleTypeReference) sdVar.getType();
									if (str.getType() instanceof NamedType) {
										TypingEnvironment tEnv = new TypingEnvironment(ctx);
										SimpleTypeReference leftType = (SimpleTypeReference) tEnv.bind(str);
										TermExpression tel = ((TermMemberSelection) ll).getMember();
										FolFormula ml = stripParenthesis(tel);
										if (ml instanceof SymbolReferenceTerm ) {
											SymbolReferenceTerm srtlm = (SymbolReferenceTerm) ml;
											String cnstrctName = generatorServices.toLustreName(leftType);
											retStr += sdVar.getName() + " = " + cnstrctName +
													"{" + srtlm.getSymbol().getName() + " = " + 
													generatorServices.serialize(ae.getRight(), ctx, ".");
											
											tEnv.addContext(leftType);
											
											String restFields = generateRestFields(leftType.getType(), srtlm.getSymbol(), tEnv);
											
											retStr += (restFields + "}"); 
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return retStr;
	}
	
	private String generateRestFields(NamedType type, Symbol ignoreSymbol, TypingEnvironment tEnv) {
		String result = "";
		for (Symbol symbol: type.getSymbols()) {
			if (symbol == ignoreSymbol) continue;
			if (symbol instanceof SymbolDeclaration && !(symbol instanceof Assertion)) {
				result += "; " + symbol.getName() + " = " + getValueForType(tEnv.bind(((SymbolDeclaration) symbol).getType())); 
			}
		}
		for (SimpleTypeReference relatedType : ImlUtil.getRelatedTypes(type)) {
			result += generateRestFields(relatedType.getType(), ignoreSymbol, tEnv.clone().addContext(relatedType));
		}
		
		return result;
	}

	private String getValueForType(ImlType type) {
		if (type instanceof SimpleTypeReference) {
			SimpleTypeReference str = (SimpleTypeReference) type;
			if (langServices.isBool(str.getType())) {
				return "true";
			}
			if (langServices.isInt(str.getType())) {
				return "0";
			}
			if (langServices.isReal(str.getType())) {
				return "0.0";
			}
			if (ImlUtil.isEnum(str.getType())) {
				SymbolDeclaration enumLiteral = ((EnumRestriction) str.getType().getRestriction()).getLiterals().get(0);
				String imlName = ImlUtil.getTypeName(str, qnp) + "." + enumLiteral.getName();
				return generatorServices.toLustreName(imlName);
			}
		}
		return "";
	}

	
	private boolean isConnectionAssertion(SymbolDeclaration sd) {
		FolFormula def = sd.getDefinition();
		def = stripParenthesis(def);
		if (def instanceof SequenceTerm) {
			FolFormula ret = ((SequenceTerm) def).getReturn();
			ret = stripParenthesis(ret);
			if (ret instanceof SignedAtomicFormula) {
				FolFormula l = ((SignedAtomicFormula) ret).getLeft();
				l = stripParenthesis(l);
				if (l instanceof AtomicExpression) {
					AtomicExpression ae = (AtomicExpression) l;					
					FolFormula ll = ae.getLeft();
					ll = stripParenthesis(ll);
					FolFormula lr = ae.getRight();
					lr = stripParenthesis(lr);
					if ((ll instanceof TermMemberSelection) && (lr instanceof TermMemberSelection) && (ae.getRel() == RelationKind.EQ)) {
						TermExpression tel = ((TermMemberSelection) ll).getMember();
						FolFormula ml = stripParenthesis(tel);
						TermExpression ter = ((TermMemberSelection) lr).getMember();
						FolFormula mr = stripParenthesis(ter);
						if (ml instanceof SymbolReferenceTerm && mr instanceof SymbolReferenceTerm) {
							SymbolReferenceTerm srtl = (SymbolReferenceTerm) ml;
							SymbolReferenceTerm srtr = (SymbolReferenceTerm) mr;
							if (srtl.getSymbol().getName().equals("data") && srtr.getSymbol().getName().equals("data")) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private FolFormula stripParenthesis(FolFormula f) {
		while (f instanceof ParenthesizedTerm) {
			f = ((ParenthesizedTerm) f).getSub();
		}
		return f;
	}
	
	public LustreNode generateType(LustreModel m, SimpleTypeReference tr) {
		// start from the definition
		String type_name = ImlUtil.getTypeName(tr, qnp);
		if (m.hasType(type_name))
			return m.getType(type_name);

		if (ImlUtil.isEnum(tr.getType())) {
			return generateEnumType(m, tr.getType());
		} else {
			for (ImlType b : tr.getTypeBinding()) {
				if (b instanceof SimpleTypeReference) {
					generateType(m, (SimpleTypeReference) b);
				}
			}
			LustreNode target = new LustreNode(type_name);
			m.addNode(target);
			target.setStruct(true);
			
			for (Symbol s : tr.getType().getSymbols()) {
				if (s instanceof SymbolDeclaration) {
					SymbolDeclaration sd = (SymbolDeclaration) s;
					addSymbol(m, target, sd, tr);
				}
			}
			
			// process relations
			gatherFromRelation(m, tr, target);

			return target;
		}
	}

	
	private void gatherFromRelation(LustreModel m, SimpleTypeReference str, LustreNode target) {
		for (Symbol s : str.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				addSymbol(m, target, sd, str);
			}
		}	
		TypingEnvironment tEnv = new TypingEnvironment(str);
		for (Relation rl : str.getType().getRelations()) {
			if (rl instanceof TraitExhibition) {
				TraitExhibition tr = (TraitExhibition) rl;
				for (TypeWithProperties twp : tr.getExhibitions()) {
					if (twp.getType() instanceof SimpleTypeReference) {
						ImlType boundType = tEnv.bind(twp.getType());
						gatherFromRelation(m, (SimpleTypeReference) boundType, target);
					}
				}
			}
			else if (rl instanceof Refinement) {
				Refinement tr = (Refinement) rl;
				for (TypeWithProperties twp : tr.getRefinements()) {
					if (twp.getType() instanceof SimpleTypeReference) {
						ImlType boundType = tEnv.bind(twp.getType());
						gatherFromRelation(m, (SimpleTypeReference) boundType, target);
					}
				}				
			}
		}		
	}

	public LustreSymbol addSymbol(LustreModel m, LustreNode target, SymbolDeclaration sd, SimpleTypeReference ctx) {
		TypingEnvironment typing = new TypingEnvironment(ctx);
		ImlType bound = null;
		String name = null;
		if (sd instanceof Assertion) {
			if (sd.getName() != null) {
				name = sd.getName();
			} else {
				name = target.getContainer().newSymbolName();
			}

			String def = "";
			FolFormula aDef = stripParenthesis(sd.getDefinition());
			def = SerializeIfAssertionWithStructFieldAsLValue(sd, ctx);
			if (def.equals("")) {
				def = generatorServices.serialize(aDef, ctx, ".");
			}			
			return addSymbol(target, name, target.getContainer().getType("iml.lang.Bool"), LustreElementType.ASSERTION,
					def);
		} else {
			name = sd.getName();
			bound = typing.bind(sd.getType());
			if (bound instanceof SimpleTypeReference) {
				LustreNode nbound = generateType(target.getContainer(), (SimpleTypeReference) bound);
				LustreSymbol toadd; 
				// relax the test to be in relation list
				
				
				if (sd.eContainer() == ctx.getType()) {
					toadd = addSymbol(target, name, nbound, LustreElementType.VAR); 
				} else {
					if(ImlUtil.hasAnnotation(sd, (Annotation) contractServices.getAssumeAnnotation())) {
						toadd = addSymbol(target, name, nbound, LustreElementType.FIELD, 1); 											
					} else if(ImlUtil.hasAnnotation(sd, (Annotation) contractServices.getGuaranteeAnnotation())) {
						toadd = addSymbol(target, name, nbound, LustreElementType.FIELD, 2); 						
					} else {
						toadd = addSymbol(target, name, nbound, LustreElementType.FIELD);
					}
				}
				if (sd.getDefinition() != null) {
					toadd.setDefinition(generatorServices.serialize(sd.getDefinition(), ctx, ".") );
				}
				return toadd ;
			}
		}
		return (new LustreSymbol("__ERROR__"));
	}


	private LustreNode generateEnumType(LustreModel m, NamedType type) {
		LustreNode target = new LustreNode(qnp.getFullyQualifiedName(type).toString());
		m.addNode(target);
		target.setEnum(true);
		target.getLiterals().addAll(ImlUtil.getLiterals(type));
		return target;
	}

	private LustreSymbol generateInput(LustreNode m, Port p) {

		if (p.getType() instanceof SimpleTypeReference) {
			LustreSymbol target = new LustreSymbol(p.getName());
			LustreNode nbound = generateType(m.getContainer(), (SimpleTypeReference) p.getType());
			LustreTypeInstance ti = new LustreTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(LustreElementType.PARAMETER);
			m.addSymbol(target);
			return target;
		}

		return new LustreSymbol("__UNSUPPORTED__");
	}
	
	private LustreSymbol generateOutput(LustreNode m, Port p, SimpleTypeReference ctx) {

		if (p.getType() instanceof SimpleTypeReference) {
			LustreSymbol target = new LustreSymbol(p.getName());
			LustreNode nbound = generateType(m.getContainer(), (SimpleTypeReference) p.getType());
			LustreTypeInstance ti = new LustreTypeInstance(nbound);
			target.setType(ti);
			if (p.getDefinition() != null) {
				// TODO need to generalize this
				if (p.getDefinition() instanceof SignedAtomicFormula) {
					if (p.getDefinition().getLeft() instanceof InstanceConstructor) {
						InstanceConstructor cons = (InstanceConstructor) p.getDefinition().getLeft();
						SymbolDeclaration var = cons.getRef();
						Map<Symbol, String> remap = new HashMap<>();
						remap.put(var, p.getName());
						String expr = generatorServices.serialize(cons.getDefinition(), ctx, remap, ".") ;
						target.setDefinition(expr.replace(p.getName() + "=", ""));
					}
				}
			}
			target.setElementType(LustreElementType.RETURN);
			
			m.addSymbol(target);
			return target;
		}

		return new LustreSymbol("__UNSUPPORTED__");
	}

	private void generateConnectionToHigherLevel(LustreNode m, Connection conn, SimpleTypeReference tr, Map<String, String> map) {
		// Need to take the output symbol
		LustreSymbol out = m.getVariables().get(conn.getTargetPort().getName());
		if (out == null) {
//			LustreSymbol toadd = new LustreSymbol("");
//			FolFormula def = Phi.eq(
//					EcoreUtil.copy(
//							(TermExpression) ((TupleConstructor) ((TailedExpression) conn.getSymbolDeclaration()
//									.getDefinition().getLeft()).getTail()).getElements().get(0).getLeft()),
//					EcoreUtil.copy(
//							(TermExpression) ((TupleConstructor) ((TailedExpression) conn.getSymbolDeclaration()
//									.getDefinition().getLeft()).getTail()).getElements().get(1).getLeft()));
//			toadd.setName(m.getContainer().newSymbolName());
//			toadd.setElementType(LustreElementType.LET);
//			toadd.setDefinition(generatorServices.serialize(def, tr, "_"));
//			m.addSymbol(toadd);
				
			LustreSymbol machine = m.getComponents().get(conn.getSourceComponent().getName());
			if (machine != null) {
				String sourcePortName = conn.getSourcePort().getName();
				int index = machine.getType().getType().returnIndex(sourcePortName);
				if (index != -1) {
					String targetPortName = conn.getTargetPort().getName();
					LustreVariable param = new LustreVariable(targetPortName);
					machine.getType().setOutParam(index, param);
					String key = conn.getSourceComponent().getName() + "_" + sourcePortName;
					map.put(key, targetPortName);
				}
			}
		}
	}

	private void generateConnectionSameLevelOrFromHigherLevel(LustreNode m, Connection conn, SimpleTypeReference tr, Map<String, String> map) {
		LustreSymbol machine = m.getComponents().get(conn.getTargetComponent().getName());
		if (machine != null) {
			int index = machine.getType().getType().paramIndex(conn.getTargetPort().getName());
			if (index != -1) {
				String portName = "";
				if (conn.getSourceComponent() != ComponentInstance.self) {
					portName += conn.getSourceComponent().getName() + "_";
				}
				portName += conn.getSourcePort().getName();
				LustreVariable param;				
				if (map.containsKey(portName)) {
					param = new LustreVariable(map.get(portName));
				} else {
					param = new LustreVariable(portName);					
				}
				machine.getType().setParam(index, param);
			}
		}
			
		if (conn.getSourceComponent() != ComponentInstance.self) {
			machine = m.getComponents().get(conn.getSourceComponent().getName());
			if (machine != null) {
				int index = machine.getType().getType().returnIndex(conn.getSourcePort().getName());
				if (index != -1) {
					String portName = conn.getSourceComponent().getName() + "_";
					portName += conn.getSourcePort().getName();
					LustreVariable param;				
					if (map.containsKey(portName)) {
						param = new LustreVariable(map.get(portName));
					} else {
						param = new LustreVariable(portName);					
					}
					machine.getType().setOutParam(index, param);
				}
			}
		}
	}
		
	public boolean isSimpleTypeReference(ImlType imlType) {
		return (imlType instanceof SimpleTypeReference);
	}

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public LustreSymbol addSymbol(LustreNode container, String name, LustreNode type, LustreElementType et,
			String definition) {
		LustreSymbol target = addSymbol(container, name, type, et);
		target.setDefinition(definition);
		return target;
	}

	public LustreSymbol addSymbol(LustreNode container, String name, LustreNode type, LustreElementType et) {
		LustreSymbol target = new LustreSymbol(name);
		LustreTypeInstance ti = new LustreTypeInstance(type);
		target.setType(ti);
		target.setElementType(et);
		container.addSymbol(target);
		return target;
	}

	public LustreSymbol addSymbol(LustreNode container, String name, LustreNode type, LustreElementType et, int ag) {
		LustreSymbol target = new LustreSymbol(name);
		LustreTypeInstance ti = new LustreTypeInstance(type);
		target.setType(ti);
		target.setElementType(et);
		if (ag == 1) {
			target.setAssume(true);
		}
		if (ag == 2) {
			target.setGuarantee(true);
		}
		container.addSymbol(target);
		return target;
	}
	
	public String serialize(LustreModel m) {
		return generatorServices.serialize(m);
	}

	public Map<String, String> getMapLustre2Iml() {
		return lustre2Iml;
	}
	
	public void displayMapLustre2Iml() {
		for (String s : lustre2Iml.keySet()) {
			System.out.println(s + " : " + lustre2Iml.get(s));
		}
	}
}
