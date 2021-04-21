/*******************************************************************************
*Copyright (c) 2021 Raytheon Technologies. All rights reserved.
*See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.nusmv.tests

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import com.utc.utrc.hermes.iml.gen.nusmv.generator.NuSmvGenerator
import com.utc.utrc.hermes.iml.gen.nusmv.model.NuSmvModel
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.utc.utrc.hermes.iml.tests.TestHelper
import com.utc.utrc.hermes.iml.util.FileUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class NuSmvTranslatorTests {

	@Inject extension ImlParseHelper

	

	@Inject extension TestHelper

	@Inject
	NuSmvGenerator gen ;

	

	@Test
	def void testTranslation() {

		var Model m = parse(FileUtil.readFileContent("models/fromaadl/UxASRespondsEvents_pkg.iml"), true);
		var NamedType smtype = m.findSymbol("UxAS_responds_dot_i") as NamedType;
		var NamedType spectype = m.findSymbol("UxAS_responds") as NamedType;
		var NuSmvModel smv = gen.generateNuSmvModel(null, smtype);

		var output = gen.serialize(smv);
		System.out.println(output);

	// var main = gen.getMainModel(smv,ImlCustomFactory.INST.createSimpleTypeReference(spectype),ImlCustomFactory.INST.createSimpleTypeReference(smtype))
	// System.out.println(generatorServices.serialize(main));
	}

	@Test
	def void testDoorController() {
		var Model m = parse(FileUtil.readFileContent("models/examples/door/controller.iml"), true);
		var NamedType smtype = m.findSymbol("DoorController") as NamedType;
		var NuSmvModel smv = gen.generateNuSmvModel(null, smtype);
		var output = gen.serialize(smv);

		if (canRunNuSMV()) {
			var main_module = '''
				MODULE main
				IVAR in_ : {"com.rtx.hermes.examples.sm.Input.CLOSE", "com.rtx.hermes.examples.sm.Input.LOCK" , "com.rtx.hermes.examples.sm.Input.OPEN", "com.rtx.hermes.examples.sm.Input.UNLOCK"};
				VAR fsm : "com.rtx.hermes.examples.sm.DoorController" (in_);
			''';
			val reach = reachableStates(output + "\n" + main_module);
			printModelReach(output,main_module,reach);
		} else {
			System.out.println(output);
		}

	}

	@Test
	def void testDoorGenerator() {

		var Model m = parse(FileUtil.readFileContent("models/examples/door/generator.iml"), true);
		var NamedType smtype = m.findSymbol("DoorGenerator") as NamedType;
		var NuSmvModel smv = gen.generateNuSmvModel(null, smtype);
		var output = gen.serialize(smv);
		
		if (canRunNuSMV()) {
			var main_module = '''
				MODULE main
				VAR fsm : "com.rtx.hermes.examples.sm.DoorGenerator";
			''';
			val reach = reachableStates(output + "\n" + main_module);
			printModelReach(output,main_module,reach);
		} else {
			System.out.println(output);
		}
		
	
	}
	
	@Test
	def void testSystemLtl(){
		var Model m = parse(FileUtil.readFileContent("models/examples/door/system.iml"), true);
		var query_type = m.findSymbol("query") as SymbolDeclaration;
		var nm = new NuSmvModel();
		var main = gen.generateMainModule(nm,query_type);
		var output = gen.serialize(nm);
		System.out.println(output);
		if (canRunNuSMV()) {
			val res = ltlCheck(output);			
			System.out.println(res);
		} 
	}
	
	@Test
	def void test1to1Refinement(){
		var Model m = parse(FileUtil.readFileContent("models/examples/door/refinement.iml"), true);
		var query_type = m.findSymbol("query") as SymbolDeclaration;
		var nm = new NuSmvModel();
		var main = gen.generateMainModule(nm,query_type);
		var output = gen.serialize(nm);
		System.out.println(output);
		if (canRunNuSMV()) {
			val res = ltlCheck(output);			
			System.out.println(res);
		} 
	}
	

	@Test
	def void itRuns() {
		var t = canRun("NuSMV.exe -help");
		Assert.assertTrue(t);
	}

	@Test
	def void doesNotRun() {
		var t = canRun("fakeNuSMV.exe -help");
		Assert.assertFalse(t);
	}

	def canRun(String command) {

		try {
			val p = Runtime.getRuntime().exec(command);
			p.destroy();
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	def canRunNuSMV() {
		return canRun("NuSMV.exe -help");
	}

	def printModelReach(String model, String main, String reach) {
		val out = '''
			*********** MODEL ***********
			
			-- Generated NuSMV Model
			«model»
			«IF main !== null» 
			-- Main module (added by the test routine)
			«main»			
			«ENDIF»
			*****************************
			
			Reachable state computed by NuSMV:
			«reach»
		'''
		System.out.println(out);
	}

	def reachableStates(String model) {
		FileUtil.writeFileContent(".generated_model.smv",model);
		val proc = Runtime.getRuntime().exec("NuSMV.exe -flt -r " + ".generated_model.smv");

		val stdInput = new BufferedReader(new InputStreamReader(proc.inputStream));
		var String retval = "";
		var String line;
		// Read the output from the command
		while ((line = stdInput.readLine()) !== null) {
			retval = retval + "\n" + line;
		}

		return retval;

	}
	
	def ltlCheck(String model) {
		FileUtil.writeFileContent(".generated_model.smv",model);
		val proc = Runtime.getRuntime().exec("NuSMV.exe .generated_model.smv");

		val stdInput = new BufferedReader(new InputStreamReader(proc.inputStream));
		var String retval = "";
		var String line;
		// Read the output from the command
		while ((line = stdInput.readLine()) !== null) {
			retval = retval + "\n" + line;
		}

		return retval;

	}
	
	
	

}
