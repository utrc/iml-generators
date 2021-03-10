package com.utc.utrc.hermes.iml.gen.zinc.generator

import com.utc.utrc.hermes.iml.iml.util.ImlSwitch

import com.utc.utrc.hermes.iml.iml.Addition;
import com.utc.utrc.hermes.iml.iml.Alias;
import com.utc.utrc.hermes.iml.iml.AndExpression;
import com.utc.utrc.hermes.iml.iml.Annotation;
import com.utc.utrc.hermes.iml.iml.ArrayAccess;
import com.utc.utrc.hermes.iml.iml.ArrayType;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.AtomicExpression;
import com.utc.utrc.hermes.iml.iml.CardinalityRestriction;
import com.utc.utrc.hermes.iml.iml.CaseTermExpression;
import com.utc.utrc.hermes.iml.iml.CharLiteral;
import com.utc.utrc.hermes.iml.iml.Datatype;
import com.utc.utrc.hermes.iml.iml.DatatypeConstructor;
import com.utc.utrc.hermes.iml.iml.Doc;
import com.utc.utrc.hermes.iml.iml.EnumRestriction;
import com.utc.utrc.hermes.iml.iml.ExpressionTail;
import com.utc.utrc.hermes.iml.iml.FloatNumberLiteral;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.FunctionType;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Import;
import com.utc.utrc.hermes.iml.iml.Inclusion;
import com.utc.utrc.hermes.iml.iml.InstanceConstructor;
import com.utc.utrc.hermes.iml.iml.IteTermExpression;
import com.utc.utrc.hermes.iml.iml.LambdaExpression;
import com.utc.utrc.hermes.iml.iml.MatchExpression;
import com.utc.utrc.hermes.iml.iml.MatchStatement;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.Multiplication;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.NumberLiteral;
import com.utc.utrc.hermes.iml.iml.OptionalTermExpr;
import com.utc.utrc.hermes.iml.iml.OrExpression;
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm;
import com.utc.utrc.hermes.iml.iml.Property;
import com.utc.utrc.hermes.iml.iml.PropertyList;
import com.utc.utrc.hermes.iml.iml.QuantifiedFormula;
import com.utc.utrc.hermes.iml.iml.RecordConstructor;
import com.utc.utrc.hermes.iml.iml.RecordConstructorElement;
import com.utc.utrc.hermes.iml.iml.RecordType;
import com.utc.utrc.hermes.iml.iml.Refinement;
import com.utc.utrc.hermes.iml.iml.Relation;
import com.utc.utrc.hermes.iml.iml.SelfTerm;
import com.utc.utrc.hermes.iml.iml.SelfType;
import com.utc.utrc.hermes.iml.iml.SequenceTerm;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.StringLiteral;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.iml.TailedExpression;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.TermMemberSelection;
import com.utc.utrc.hermes.iml.iml.Trait;
import com.utc.utrc.hermes.iml.iml.TraitExhibition;
import com.utc.utrc.hermes.iml.iml.TruthValue;
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.iml.TupleType;
import com.utc.utrc.hermes.iml.iml.TypeRestriction;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;
import org.eclipse.emf.common.util.EList
import java.util.List
import org.eclipse.emf.ecore.EObject
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.iml.gen.zinc.model.MiniZincModel
import com.utc.utrc.hermes.iml.lib.ImlStdLib
import at.siemens.ct.jmz.writer.ModelWriter
import com.google.inject.Inject
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.naming.IQualifiedNameProvider

import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import java.util.Map
import java.util.HashMap
import java.io.File
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import org.eclipse.xtext.EcoreUtil2
import at.siemens.ct.jmz.ModelBuilder
import at.siemens.ct.jmz.expressions.integer.IntegerConstant
import at.siemens.ct.jmz.expressions.integer.IntegerVariable
import at.siemens.ct.jmz.expressions.bool.BooleanConstant
import at.siemens.ct.jmz.expressions.bool.BooleanVariable
import com.utc.utrc.hermes.iml.gen.zinc.model.FloatConstant
import com.utc.utrc.hermes.iml.gen.zinc.model.FloatVariable
import com.utc.utrc.hermes.iml.iml.impl.FunctionTypeImpl
import at.siemens.ct.jmz.expressions.bool.RelationalOperation
import at.siemens.ct.jmz.expressions.bool.RelationalOperator
import at.siemens.ct.jmz.elements.constraints.Constraint
import at.siemens.ct.jmz.expressions.bool.BooleanExpression
import java.util.stream.Collectors
import at.siemens.ct.jmz.elements.Element
import at.siemens.ct.jmz.expressions.integer.BasicInteger
import at.siemens.ct.jmz.elements.Variable
import at.siemens.ct.jmz.elements.solving.Optimize
import at.siemens.ct.jmz.elements.solving.OptimizationType
import at.siemens.ct.jmz.expressions.integer.IntegerExpression
import org.eclipse.emf.ecore.resource.ResourceSet
import com.utc.utrc.hermes.iml.ImlParseHelper
import com.utc.utrc.hermes.iml.util.FileUtil
import java.io.BufferedReader
import java.io.InputStreamReader

public class MiniZincGeneratorBasedImlSwitch extends ImlSwitch<String> {
	
	
	@Inject private MiniZincGeneratorServices mzGeneratorSevices;
	@Inject private ImlStdLib stdLibs;
	@Inject private IQualifiedNameProvider qnp;
	@Inject private ImlTypeProvider typeProvider;
	@Inject private ImlParseHelper parser;
	private String LAMBDA_EXPRESSION_SUFFIX = ".lambdaExpression"
	
	private String pathIML;
	private ResourceSet allResources;
	
	private Map<String, MiniZincModel> subModelBuilders = new HashMap<String, MiniZincModel>();

	def public void parseIMLModel(String pathIML, String modelName) {
		this.pathIML = pathIML
		allResources = parser.parseDir(pathIML, true);
		parser.assertNoErrors(allResources);
		val model = ImlUtil.findModel(allResources, modelName);
		caseModel(model as Model);
	}
		
	def public void ExportModel(String queryName){
		val mzModel = getModel(queryName);
		val writer = new ModelWriter(mzModel as MiniZincModel)
		val fileName = queryName.replace(".", "_");
	
		val directoryName = pathIML + "\\mzn" 
		val directory = new File(directoryName);
		if (! directory.exists())
        	directory.mkdir();
        
        val mznFileName = directory+ "\\" + fileName +".mzn"	
		FileUtil.writeFileContent(mznFileName, writer.toString());
		
		//minizinc -c optimization_happiness_happiness.mzn
		val cmd = "minizinc -c "+mznFileName;
		val p = Runtime.getRuntime().exec(cmd);
		
		val stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		val stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	}
	
	def public Map<String, MiniZincModel>  getSubModelbuilders(){
		subModelBuilders
	}
	
	def public List<String> getSubModelList(){	
		subModelBuilders.keySet().toList();
	}	
	
	private def String getLambdaExpressionQName(LambdaExpression le){
		qnp.getFullyQualifiedName(le.eContainer().eContainer()).toString()+LAMBDA_EXPRESSION_SUFFIX
	}
		
	private def MiniZincModel getCurrentModelBuilder(EObject object){
		val container = object.eContainer()
		var qcname =  ""
		if (qnp.getFullyQualifiedName(object) != null 
			&& subModelBuilders.containsKey(qnp.getFullyQualifiedName(object).toString())
		){
			qcname = qnp.getFullyQualifiedName(object).toString()
		}else{
			if(container!=null 
				&& qnp.getFullyQualifiedName(container)!=null
			){  
				qcname = qnp.getFullyQualifiedName(container).toString()
			}else if(container instanceof LambdaExpression){
				val le = container
				qcname = getLambdaExpressionQName(le)
			} 
		}
		subModelBuilders.get(qcname)		
	}
	
	private def MiniZincModel getRootModelBuilder(SimpleTypeReference object){
		val tqName = qnp.getFullyQualifiedName(object.type); 
		subModelBuilders.get(tqName.toString());		
	}
	
	def public String printSubModel(String subModelName){
		val mb = subModelBuilders.get(subModelName)
		var modeltxt ="null Model"
		if (mb!=null){
			val mw = new ModelWriter(mb)
			//if ((mw!=null) && (subModelName!="optimization.happiness.Test.Y"))
				try
					modeltxt = mw.toString()
				catch (Exception e)
					System.out.println(e)
//			else
//				System.out.println(subModelName)
		}
		modeltxt
	}
	
	def public MiniZincModel getModel(String subModelName){
		return subModelBuilders.get(subModelName)
	}

	
	def public String cloneSubModel(String subModelName){
		val mb = subModelBuilders.get(subModelName)
		var modeltxt ="null Model"
		if (mb!=null){
			val cmb = mb.clone("cloned.model")
			val mw = new ModelWriter(cmb)
			modeltxt = mw.toString()
		}
		modeltxt
	}
	
	private def MiniZincModel createNewSubModel(EObject object){	
		val qName = qnp.getFullyQualifiedName(object)
		createNewSubModel(qName.toString())
	}
	
	private def MiniZincModel createNewSubModel(String qName){
		var modelBuilder = new MiniZincModel(qName);	
		subModelBuilders.put(qName.toString(), modelBuilder)
		modelBuilder
	}
	
	def String getCPConventionalName(EObject object){
		//val container = object.eContainer();	
		val qName = qnp.getFullyQualifiedName(object).toString()
		getCPConventionalName(qName);
	}
		
	def String getCPConventionalName(String qName){
		qName.replace(".", "___");
	}
	
	def addConstant(MiniZincModel builder, SymbolDeclaration object) {
		val cpName = getCPConventionalName(object);
		if (stdLibs.isBool(object.getType())) {
			val c = new BooleanConstant(mzGeneratorSevices.getBooleanConstant(object)); // BooleanConstant
			builder.add(c.toNamedConstant(cpName));
		} else if (stdLibs.isInt(object.getType())) {
			val c = new IntegerConstant(mzGeneratorSevices.getIntConstant(object));
			builder.add(c.toNamedConstant(cpName));
		} else if (stdLibs.isReal(object.getType())) {
			val c = new FloatConstant(mzGeneratorSevices.getRealConstant(object)); //FloatConstant
			builder.add(c.toNamedConstant(cpName));
		}
	}
	
		
	def addPrimitiveVariable(MiniZincModel builder, SymbolDeclaration object) {
		val cpName = getCPConventionalName(object);
		if (stdLibs.isBool(object.getType())) {
			val v = new BooleanVariable(cpName); // BooleanConstant
			builder.add(v);
		} else if (stdLibs.isInt(object.getType())) {
			val v = new IntegerVariable(cpName);
			builder.add(v);
		} else if (stdLibs.isReal(object.getType())) {
			val v = new FloatVariable(cpName); //FloatConstant
			builder.add(v);
		}
	}
			
	def addPrimitiveVariable(MiniZincModel builder, String cpName, ImlType type) {
		if (stdLibs.isBool(type)) {
			val v = new BooleanVariable(cpName); // BooleanConstant
			builder.add(v);
		} else if (stdLibs.isInt(type)) {
			val v = new IntegerVariable(cpName);
			builder.add(v);
		} else if (stdLibs.isReal(type)) {
			val v = new FloatVariable(cpName); //FloatConstant
			builder.add(v);
		}
	}
	
	def addVariable(String qName, ImlType type , MiniZincModel mb){
		val cpName = getCPConventionalName(qName)
		val variable = mb.getElementByName(cpName)
		if (variable != null){
			// variable already created
			return null
		}		
		if (stdLibs.isPrimitive(type)) {	
			addPrimitiveVariable(mb, cpName, type)
		}else{ 
				if(type instanceof SimpleTypeReference) {
					// Clone the model of the type of SimpleTypeReference
					val tmb = getRootModelBuilder(type as SimpleTypeReference)
					//val qtName = getCPConventionalName((type as SimpleTypeReference).type)
					//val newmb = 
					tmb.clone(cpName, mb) 
				}
				
				if(type instanceof FunctionType) {
					addVariable(qName+".output", (type as FunctionType).range as ImlType, mb)
					addVariable(qName+".input", (type as FunctionType).domain as ImlType, mb)
				}
				
				if(type instanceof TupleType) {
					var index = -1
					for (t: type.types)
					{
						index += 1
						addVariable(qName+"."+index, t, mb)
					}
				}
				
			}		
	}
	
	def addVariables(SymbolDeclaration object, MiniZincModel mb){
		val cpName = getCPConventionalName(object)
		val variable = mb.getElementByName(cpName)
		if (variable != null){
			// variable already created
			return null
		}
		if (stdLibs.isPrimitive(object.getType())) {
			if (mzGeneratorSevices.isConstant(object)) {
				addConstant(mb, object);
			} else {
				addPrimitiveVariable(mb, object);
			}
		}else{	
//			System.out.println("object: " + object)	
			addVariable(qnp.getFullyQualifiedName(object).toString(), object.getType() , mb)
		}

	}	
		
	def addObjectEqualityConstraints(String left_prefix, String left_name, 
		EObject right, String right_prefix, String right_name, 
		ImlType type , MiniZincModel mb
	){
		
		if (stdLibs.isPrimitive(type)) {
			if (right instanceof SymbolReferenceTerm || (left_name=="output" && right_name=="output")){
				val v1Name = getCPConventionalName(left_prefix+"."+left_name)	
				val v2Name = getCPConventionalName(right_prefix+"."+right_name)	
				addPrimitiveEqualityConstraints(v1Name, v2Name, mb)
			}else{ //SignedAtomicFormula		
				val qName= QualifiedName.create(right_prefix+"."+right_name);
				val cpExpression = mzGeneratorSevices.processIntegerExpression(qName, mb, type as SimpleTypeReference, (right as SignedAtomicFormula).left as FolFormula, new HashMap());
				val v1Name = getCPConventionalName(left_prefix+"."+left_name)
				val x1 = mb.getElementByName(v1Name)
				if (x1 != null && cpExpression != null){
					val constraintEQ = new RelationalOperation(x1, RelationalOperator.EQ, cpExpression)
					mb.add(new Constraint(constraintEQ))
				}
			}
		}
		
		if (type instanceof SimpleTypeReference){
			val v1Name = getCPConventionalName(left_prefix+"."+left_name)	
			val v2Name = getCPConventionalName(right_prefix+"."+right_name)	
			addSubmodelEqualityConstraints(v1Name, v2Name, mb)
		}
		
		if (type instanceof FunctionType){
			if (right instanceof LambdaExpression){
				//inputs are equal
				val numParms = right.parameters.size()
				if(numParms == 1){
					val p = right.parameters.get(0)
					val lPrefix = left_prefix +"."+left_name
					val rPrefix = right_prefix +"."+right_name +".input"
					addObjectEqualityConstraints(lPrefix, "input", 
							p, rPrefix, p.name, 
							p.type , mb
						)
				}else{
					val lPrefix = left_prefix +"."+left_name+".input"
					val rPrefix = right_prefix +"."+right_name+".input"
					var i = -1
					for (p: right.parameters){
						i += 1
						val lname = ""+i
						addObjectEqualityConstraints(lPrefix, lname, 
													p, rPrefix, p.name, 
													p.type , mb
												)
					}
				}
				
				//outputs are equal
				val loPrefix = left_prefix +"."+left_name
				val roPrefix = right_prefix +"."+right_name
				addObjectEqualityConstraints(loPrefix, "output", 
							(right.getDefinition() as SequenceTerm).getReturn(), roPrefix,  "output", type.range , mb)
			}
		}
		
		if(type instanceof TupleType) {
			val lPrefix = left_prefix +"."+left_name
			val rPrefix = right_prefix +"."+right_name
			var i = -1
			for (t: type.types)
			{
				i += 1
				val name = ""+i
				addObjectEqualityConstraints(lPrefix, name, 
											((right as SignedAtomicFormula).left as TupleConstructor).elements.get(i), rPrefix, name, 
											t , mb
										)
			}
		}		
	}
	
	def addSubmodelEqualityConstraints(String model1_perfix, String model2_perfix, MiniZincModel mb){
		val allElements = mb.elements().collect(Collectors.toList());
	    // parse all variables
	    for (Element element : allElements) {  
	    	if (element instanceof Variable){
//	    		System.out.println(element.name);
	    		if (element.name.startsWith(model1_perfix)){
	    			val sufix = element.name.replace(model1_perfix, "")
	    			val v1Name = model1_perfix  + sufix
	    			val v2Name = model2_perfix  + sufix
	    			addPrimitiveEqualityConstraints(v1Name, v2Name, mb)
		    	}
		    }
	    }
	}
	
	def addPrimitiveEqualityConstraints(String v1Name, String v2Name, MiniZincModel mb){
		val x1 = mb.getElementByName(v1Name)
		val x2 = mb.getElementByName(v2Name)
		if (x1 != null && x2 != null){
			val constraintEQ = new RelationalOperation(x1, RelationalOperator.EQ, x2)
			mb.add(new Constraint(constraintEQ))
		}
	}
	
	def addEqualityConstraints(EObject left, EObject right, ImlType type, MiniZincModel mb){
		val x1_name = getCPConventionalName(left)			
		if (stdLibs.isPrimitive(type)) {
			if (right instanceof SymbolReferenceTerm){
				val x2_name = getCPConventionalName((right as SymbolReferenceTerm).symbol)			
				addPrimitiveEqualityConstraints(x1_name, x2_name, mb)
			}else{
				
				System.out.println("right isPrimitive: "+right)
				//TODO: handle expressions 
			}
		}else  if (type instanceof SimpleTypeReference){
		 	if (right instanceof SymbolReferenceTerm){		
				val listOfSymbols = ImlUtil.getAllSymbols((type as SimpleTypeReference).type, false)
				for (sym: listOfSymbols){
					if (sym instanceof SymbolDeclaration && !(sym instanceof Assertion)){
						if(stdLibs.isPrimitive(sym.getType())){
							if (!mzGeneratorSevices.isConstant(sym)){
								val sqName = qnp.getFullyQualifiedName(sym).toString()
								val scqName = qnp.getFullyQualifiedName(sym.eContainer()).toString()
								val lvName = getCPConventionalName(sqName.replace(scqName, qnp.getFullyQualifiedName(left).toString()))
								val rvName = getCPConventionalName(sqName.replace(scqName, qnp.getFullyQualifiedName((right as SymbolReferenceTerm).symbol).toString()))
								addPrimitiveEqualityConstraints(lvName, rvName, mb)
							}
						}
					}
				} 
			}else {//if (right instanceof NamedType){
				//TODO: handle expressions 
				System.out.println("right isNotPrimitive: "+right)
			}
		}else  if (type instanceof FunctionType){
			//TODO: inputs are == and outputs are equal left == right
//			System.out.println("type.range: "+  type.range)
			val left_model_input_name =  getCPConventionalName(qnp.getFullyQualifiedName(left).toString()+".input")
			val left_model_output_name =  getCPConventionalName(qnp.getFullyQualifiedName(left).toString()+".output")
			if (right instanceof LambdaExpression){
				//for (p: right.parameters){				
				val right_model_input_name =  getCPConventionalName(getLambdaExpressionQName(right)+".input") 
				addSubmodelEqualityConstraints(left_model_input_name, right_model_input_name, mb)
				
				val right_model_output_name =  getCPConventionalName(getLambdaExpressionQName(right)+".output")
				addSubmodelEqualityConstraints(left_model_output_name, right_model_output_name, mb)
				//}
			}else if (right instanceof NamedType){
				//TODO: handle expressions 
				System.out.println("type isNotPrimitive: "+right)
			}
		}
	}

	def addDefinitionModel(SymbolDeclaration object){
		if (object.definition != null 
			&& !(object.definition.left instanceof NumberLiteral)
		){
			var mb = getCurrentModelBuilder(object)
			if (object.definition.left instanceof LambdaExpression){
				val le = object.definition.left as LambdaExpression
				val qcname = getLambdaExpressionQName(le)
				val dmb = subModelBuilders.get(qcname)
				val cpNewName = getCPConventionalName(object) + getCPConventionalName(qcname)
				dmb.copyInto(mb);
			}
			
			val lpn = getPrefixName(object)
			val lPrefix = lpn.get(0)
			val lName = lpn.get(1)
			val rpn = getPrefixName(object.definition.left)
			val rPrefix = rpn.get(0)
			val rName = rpn.get(1)
						
			addObjectEqualityConstraints(lPrefix, lName, 
							object.definition.left, rPrefix, rName, 
							object.getType() , mb
						)
					
//			addEqualityConstraints(object, object.definition.left, object.getType(), mb)
		}

	}
	
	private def getPrefixName(EObject object){
		var prefix = ""
		var name = ""
		if (object instanceof SymbolDeclaration){
			prefix = qnp.getFullyQualifiedName(object.eContainer).toString()
			name = object.name
		}
		if (object instanceof SymbolReferenceTerm){
			val sym = (object as SymbolReferenceTerm).symbol
			prefix = qnp.getFullyQualifiedName(sym.eContainer).toString()
			name = sym.name
		}
		if (object instanceof LambdaExpression){
			prefix = qnp.getFullyQualifiedName(object.eContainer.eContainer).toString()
			name = "lambdaExpression"
		}
		
		val myList = newArrayList(prefix, name)
		myList
	}
		
	private def add2CPModel(SymbolDeclaration object){	
		var mzModelBuilder = getCurrentModelBuilder(object)	
		if(mzModelBuilder == null){
			val qName = qnp.getFullyQualifiedName(object).toString()
			createNewSubModel(qName)
			mzModelBuilder = subModelBuilders.get(qName) // getCurrentModelBuilder(qName)
		}
		addVariables(object, mzModelBuilder)		
	}
	
//	def static String print(EObject object) {
//		new MiniZincGeneratorBasedImlSwitch().doSwitch(object)
//	}

	override String caseModel(Model object) {
//		modelWriter = new ModelWriter(mzModelBuilder);
		
		'''
		package «object.name»;
		
		«FOR importt : object.imports»
		«importt.doSwitch»
		«ENDFOR»
		
		«FOR symbol : object.symbols»
		«symbol.doSwitch»
		«ENDFOR»
		'''
		
	}

	
	override String caseImport(Import object) {
		"import " + object.importedNamespace + ";"
	}

	
	override String caseSymbol(Symbol object) {
		return super.caseSymbol(object);
	}

	
	override String caseDoc(Doc object) {
		'''«FOR prop : object.properties BEFORE '@' SEPARATOR ' '»«prop.name» "«object.descriptions.get(object.properties.indexOf(prop))»"«ENDFOR»'''
	}

	
	override String caseProperty(Property object) {
		object.ref.doSwitch + " " + object.definition.doSwitch
	}

	
	override String casePropertyList(PropertyList object) {
		'''«FOR property : object.properties BEFORE '[' SEPARATOR ', ' AFTER ']'»«property.doSwitch»«ENDFOR»'''
	}

	
	override String caseTypeWithProperties(TypeWithProperties object) {
		doSwitch(object.properties) + doSwitch(object.type) 
	}

	
	override String caseRelation(Relation object) {
		return super.caseRelation(object);
	}
	
	override String caseNamedType(NamedType object) {
		// create a new minizinc sub-model
		createNewSubModel(object)
		if (ImlUtil.isActualNamedType(object)) {		
			return "type " + namedTypeContent(object);
		} else {
			return super.caseNamedType(object)
		}
		
	}
	
	def namedTypeContent(NamedType type) {	
		val sb = new StringBuilder
		if (type.propertylist !== null)
			sb.append(doSwitch(type.propertylist) + " ");
		sb.append(type.name)
		if (type.template) 
			sb.append(" " + typeTemplatesString(type.typeParameter))
		if (type.constructors !== null && type.constructors.size > 0)
			sb.append(" " + typeConstructorsString(type.constructors))
		if (type.relations !== null && type.relations.size > 0)
			sb.append(''' «FOR rel : type.relations SEPARATOR ' '»«doSwitch(rel)»«ENDFOR»''')
		if (type.restriction !== null)
			sb.append(" " + doSwitch(type.restriction))
		sb.append(
		'''{
	«FOR symbol : type.symbols SEPARATOR '\n'»«doSwitch(symbol)»;«ENDFOR»
}''')
		sb.toString
	}
	
	def typeConstructorsString(EList<Symbol> list) {
		'''«FOR param : list BEFORE '(' SEPARATOR ', ' AFTER ')'»«doSwitch(param)»«ENDFOR»'''
	}
	
	def scopeParamsString(EList<SymbolDeclaration> list) {
		'''«FOR param : list BEFORE '(' SEPARATOR ', ' AFTER ')'»«doSwitch(param)»«ENDFOR»'''
	}
	
	def typeTemplatesString(EList<NamedType> list) {
		'''«FOR param : list BEFORE '<' SEPARATOR ', ' AFTER '>'»«param.name»«ENDFOR»'''
	}

	
	override String caseTypeRestriction(TypeRestriction object) {
		return super.caseTypeRestriction(object);
	}
	
	override String caseSymbolDeclaration(SymbolDeclaration object) {	
//		System.out.println(object)
//		System.out.println(object.definition)
//		if (object.definition instanceof SymbolReferenceTerm)
//			//(type as SimpleTypeReference).type
//			System.out.println((object.definition as SimpleTypeReference).type)
			
		add2CPModel(object)	

		//'''fun «scopeParamsString(object.parameters)»«IF object.returnType !== null»:«object.returnType.doSwitch»«ENDIF»«object.definition.doSwitch»'''
		
		val returnVal = '''«doSwitch(object.propertylist)» «object.name»«typeTemplatesString(object.typeParameter)»''' + 
		'''«IF object.type !== null»:«doSwitch(object.type)»«ENDIF»''' +
		'''«IF object.definition !== null»:=«doSwitch(object.definition)»«ENDIF»'''
		
		addDefinitionModel(object)
		returnVal
	}

	
	override String caseImlType(ImlType object) {
		return super.caseImlType(object);
	}

	
	override String caseOptionalTermExpr(OptionalTermExpr object) {
		'''«IF object.term !== null»«doSwitch(object.term)»«ENDIF»'''
	}

	
	override String caseFolFormula(FolFormula object) {
		if (object.op !== null) {
			return doSwitch(object.left) + object.op + doSwitch(object.right)
		} else {
			return super.caseFolFormula(object);
		}
	}

	
	override String caseTermExpression(TermExpression object) {
		return super.caseTermExpression(object);
	}

	
	override String caseExpressionTail(ExpressionTail object) {
		return super.caseExpressionTail(object);
	}

	
	override String caseRecordConstructorElement(RecordConstructorElement object) {
//		System.out.println("Assignment statement")
		object.name + " := " + doSwitch(object.definition)
	}

	
	override String caseMatchExpression(MatchExpression object) {
		'''match («doSwitch(object.datatypeExpr)») { «FOR stmt:object.matchStatements SEPARATOR ' '»«doSwitch(stmt)»«ENDFOR» }'''
	}

	
	override String caseMatchStatement(MatchStatement object) {
		'''
		«object.constructor.name»«FOR param:object.paramSymbols BEFORE '(' SEPARATOR ', ' AFTER ')'»«doSwitch(param)»«ENDFOR»
		''' + ":" + doSwitch(object.^return)
	}

	
	override String caseInclusion(Inclusion object) {
		"includes " + listOfParams(object.inclusions)
	}
	
	def listOfParams(EList<TypeWithProperties> list) {
		'''«FOR param:list BEFORE '(' SEPARATOR ', ' AFTER ')'»«doSwitch(param)»«ENDFOR»'''
	}

	
	override String caseRefinement(Refinement object) {
		"refines " + listOfParams(object.refinements)
	}

	
	override String caseAlias(Alias object) {
		"is " + doSwitch(object.type)
	}

	
	override String caseTraitExhibition(TraitExhibition object) {
		"exhibits " + listOfParams(object.exhibitions)
	}

	
	override String caseAnnotation(Annotation object) {
		"annotation " + namedTypeContent(object);
	}

	
	override String caseTrait(Trait object) {
		"trait " + namedTypeContent(object);
	}

	
	override String caseDatatype(Datatype object) {
		"datatype " + namedTypeContent(object);
	}

	
	override String caseDatatypeConstructor(DatatypeConstructor object) {
		doSwitch(object.propertylist) + object.name + 
		'''«FOR param:object.parameters BEFORE '(' SEPARATOR ', ' AFTER ')'»«doSwitch(param)»«ENDFOR»'''
	}

	
	override String caseCardinalityRestriction(CardinalityRestriction object) {
		"finite " + object.cardinality
	}

	
	override String caseEnumRestriction(EnumRestriction object) {
		"enum " + '''«FOR param:object.literals BEFORE '{' SEPARATOR ', ' AFTER '}'»«doSwitch(param)»«ENDFOR»'''
	}

	
	override String caseAssertion(Assertion object) {
		val mzModelBuilder = getCurrentModelBuilder(object)
		val container = object.eContainer()
		
		mzGeneratorSevices.addConstraint(qnp.getFullyQualifiedName(container), 
			mzModelBuilder, 
			ImlCustomFactory.INST.createSimpleTypeReference(container as NamedType), object
		);		
			
		'''assert «doSwitch(object.propertylist)»«IF object.comment !== null»"«object.comment»" «ENDIF»«IF object.name !== null»«object.name» «ENDIF»«doSwitch(object.definition)»'''
	}

	
	override String caseFunctionType(FunctionType object) {
		doSwitch(object.domain) + "->" + doSwitch(object.range)
	}

	
	override String caseArrayType(ArrayType object) {
		doSwitch(object.type) + '''«FOR param:object.dimensions»[«doSwitch(param)»]«ENDFOR»'''
	}

	
	override String caseRecordType(RecordType object) {
		'''«FOR param:object.symbols BEFORE '{' SEPARATOR ', ' AFTER '}'»«doSwitch(param)»«ENDFOR»'''
	}

	
	override String caseTupleType(TupleType object) {
		'''«FOR param:object.types BEFORE '(' SEPARATOR ', ' AFTER ')'»«doSwitch(param)»«ENDFOR»'''
	}

	
	override String caseSelfType(SelfType object) {
		"Self"
	}

	
	override String caseSimpleTypeReference(SimpleTypeReference object) {
		object.type.name + '''«FOR param:object.typeBinding BEFORE '<' SEPARATOR ', ' AFTER '>'»«doSwitch(param)»«ENDFOR»'''
	}

	
	override String caseQuantifiedFormula(QuantifiedFormula object) {
		object.op+typeTemplatesString(object.typeParameter)+scopeParamsString(object.scope)+doSwitch(object.left)
	}

	
	override String caseOrExpression(OrExpression object) {
		doSwitch(object.left) + "||" + doSwitch(object.right)
	}

	
	override String caseAndExpression(AndExpression object) {
		doSwitch(object.left) + "&&" + doSwitch(object.right)
	}

	
	override String caseSignedAtomicFormula(SignedAtomicFormula object) {
//		System.out.println("SignedAtomicFormula:"+object)
		'''«IF object.neg»!«ENDIF»''' +
		'''«doSwitch(object.left)»'''
	}

	
	override String caseAtomicExpression(AtomicExpression object) {
		doSwitch(object.left) + object.rel.literal + doSwitch(object.right)
	}

	
	override String caseTruthValue(TruthValue object) {
		if (object.TRUE) return 'true' else return 'false'
	}

	
	override String caseAddition(Addition object) {
		doSwitch(object.left) + object.sign + doSwitch(object.right)
	}

	
	override String caseMultiplication(Multiplication object) {
		doSwitch(object.left) + object.sign + doSwitch(object.right)
	}

	
	override String caseTermMemberSelection(TermMemberSelection object) {
		doSwitch(object.receiver) + '.' + doSwitch(object.member)
	}

	
	override String caseTailedExpression(TailedExpression object) {
		if(object.left instanceof SymbolReferenceTerm) {
			val symbol = (object.left as SymbolReferenceTerm).symbol
			val tqName = qnp.getFullyQualifiedName(symbol).toString(); 		
				
			if (tqName == "iml.queries.max" || tqName == "iml.queries.min")
			{
				//create new model
				val mb = createNewSubModel(tqName)
				val sym = ((object.tail as TupleConstructor).elements.get(0).left as SymbolReferenceTerm).symbol
				val rqName = qnp.getFullyQualifiedName(sym).toString(); 
				val tmb = subModelBuilders.get(rqName)
				tmb.copyInto(mb) 
				
				val cost_var_name = getCPConventionalName(rqName+"."+"output")
				val cost_var = tmb.getElementByName(cost_var_name)
				if (cost_var instanceof IntegerVariable){
					if (tqName == "iml.queries.max"){
						val opt = new Optimize(OptimizationType.MAX, cost_var)
						tmb.add(opt)
					}else{
						val opt = new Optimize(OptimizationType.MIN, cost_var)
						tmb.add(opt)
					}
				}else{
					val objName = getCPConventionalName(tmb.getName+".objective")
					val objVar = new IntegerVariable(objName);
					tmb.add(objVar);
					addPrimitiveEqualityConstraints(objName, cost_var_name, tmb)
					
					if (tqName == "iml.queries.max"){
						val opt = new Optimize(OptimizationType.MAX, objVar)
						tmb.add(opt)
					}else{
						val opt = new Optimize(OptimizationType.MIN, objVar)
						tmb.add(opt)
					}
				}
			}
		}
		doSwitch(object.left) + doSwitch(object.tail)
	}

	
	override String caseArrayAccess(ArrayAccess object) {
		'[' + object.index.doSwitch + ']'
	}

	
	override String caseNumberLiteral(NumberLiteral object) {
		'''«object.value»'''
	}

	
	override String caseFloatNumberLiteral(FloatNumberLiteral object) {
		'''«object.value»'''
	}

	
	override String caseStringLiteral(StringLiteral object) {
		'''"«object.value»"'''
	}

	
	override String caseCharLiteral(CharLiteral object) {
		"'" + object.value + "'"
	}

	
	override String caseSelfTerm(SelfTerm object) {
		'self'
	}

	
	override String caseLambdaExpression(LambdaExpression object) {	
		val qcname = getLambdaExpressionQName(object)
		//Create a lambdaExpression model
		val mb = createNewSubModel(qcname)
		
		//Create input variables
		if (object.parameters != null){
			for (p: object.parameters)
				addVariable(qcname+".input"+"."+p.name, p.getType() as ImlType, subModelBuilders.get(qcname))
		}
				
		//Create output variables
		//var type = null
		if (object.returnType != null){
			val type = object.returnType as ImlType
			addVariable(qcname+".output", type, subModelBuilders.get(qcname))
			//return expression
			addObjectEqualityConstraints(qcname, "output", 
				(object.getDefinition() as SequenceTerm).getReturn(), qcname,  "input", 
				type, mb
				)			
		}else{
			val type = typeProvider.termExpressionType((object.getDefinition() as SequenceTerm).getReturn()) as ImlType
			addVariable(qcname+".output", type, subModelBuilders.get(qcname))
			//return expression
			addObjectEqualityConstraints(qcname, "output", 
				(object.getDefinition() as SequenceTerm).getReturn(), qcname,  "input", 
				type, mb
				)			
		}
		

		
		//IntegerExpression costfunction = server.processIntegerExpression(QualifiedName.EMPTY, builder, ctx, formula.getLeft(), new HashMap<>());
//		 '''fun «scopeParamsString(object.parameters)»«IF object.returnType !== null»:«object.returnType.doSwitch»«ENDIF»«object.definition.doSwitch»'''
		 '''fun «IF object.returnType !== null»:«object.returnType.doSwitch»«ENDIF»«object.definition.doSwitch»'''

	}

	
	override String caseInstanceConstructor(InstanceConstructor object) {
		'''some («object.ref.doSwitch»)«object.definition.doSwitch»'''
	}

	
	override String caseTupleConstructor(TupleConstructor object) {
		'''«FOR fol: object.elements BEFORE '(' SEPARATOR ', ' AFTER ')'»«fol.doSwitch»«ENDFOR»'''
	}

	
	override String caseParenthesizedTerm(ParenthesizedTerm object) {
		"(" + object.sub.doSwitch + ")"
	}

	
	override String caseRecordConstructor(RecordConstructor object) {
		'''«FOR rec: object.elements BEFORE '{' SEPARATOR ', ' AFTER '}'»«rec.doSwitch»«ENDFOR»'''
	}

	
	override String caseSymbolReferenceTerm(SymbolReferenceTerm object) {
		//System.out.println("SymbolReferenceTerm:" +object.symbol+", "+qnp.getFullyQualifiedName(object.symbol))
		object.symbol.name + '''«FOR param: object.typeBinding BEFORE '<' SEPARATOR ', ' AFTER '>'»«param.doSwitch»«ENDFOR»'''
	}

	
	override String caseIteTermExpression(IteTermExpression object) {
		'''if («object.condition.doSwitch») «object.left.doSwitch»«IF object.right!==null»else «object.right.doSwitch»«ENDIF»'''
	}

	
	override String caseCaseTermExpression(CaseTermExpression object) {
		'''case {«FOR cas:object.cases» «cas.doSwitch»:«object.expressions.get(object.cases.indexOf(cas)).doSwitch»;«ENDFOR»}'''
	}

	
	override String caseSequenceTerm(SequenceTerm object) {
//		System.out.println("SequenceTerm:" +object+", "+qnp.getFullyQualifiedName(object))
		'''{ 	«FOR va:object.defs»var «va.doSwitch»;«ENDFOR»
	«object.^return.doSwitch»;
}'''
	}
	
	override doSwitch(EObject eObject) {
		if (eObject === null) return ""
		else return super.doSwitch(eObject)
	}
	
	def String forMap(List<EObject> list, String before, String sep, String after) {
		'''«FOR param:list BEFORE before SEPARATOR sep AFTER after»«doSwitch(param)»«ENDFOR»'''
	}
	
}