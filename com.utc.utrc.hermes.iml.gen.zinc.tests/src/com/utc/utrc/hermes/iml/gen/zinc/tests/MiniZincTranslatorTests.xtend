package com.utc.utrc.hermes.iml.gen.zinc.tests


import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.InjectWith
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import com.utc.utrc.hermes.iml.tests.TestHelper
import org.junit.Test
import com.utc.utrc.hermes.iml.util.FileUtil
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.util.ImlUtil
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGenerator
import com.utc.utrc.hermes.iml.gen.zinc.generator.MiniZincGeneratorServices
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration
import com.utc.utrc.hermes.iml.gen.zinc.utils.SolverRunner

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class MiniZincTranslatorTests  {
	
	@Inject
	ImlParseHelper parser;
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject
	MiniZincGenerator gen ;
	
	@Inject
	MiniZincGeneratorServices generatorServices;
	
	@Test
	def void testTranslation1() {
		val rs = parser.parseDir("models/happiness/", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.happiness.optimalWeek") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		print(model)
//		var output  = SolverRunner.run(model,gen.prefixAsString,gen.typeName);
//		System.out.println(output.toString);
			
	}
	
	@Test
	def void testTranslation2() {
		val rs = parser.parseDir("models/happiness/happiness2.iml", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.happiness2.optimalDoubleWeek1") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		var output  = SolverRunner.run(model,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
			
	}
	
	
	
	@Test
	def void testTranslation3() {
		val rs = parser.parseDir("models/happiness/happiness2.iml", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.happiness2.optimalDoubleWeek2") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		var output  = SolverRunner.run(model,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
		
		/* Functions can be serialized at the end. The following works
		 * 
		 * var int: x___week1___s;
var int: x___week1___p;
var int: x___week1___e;
constraint x___week1___s + x___week1___p + x___week1___e = 168;
constraint x___week1___e >= 56;
constraint x___week1___p + x___week1___e >= 70;
constraint x___week1___s >= 60;
constraint 2 * x___week1___s + x___week1___e - 3 * x___week1___p >= 150;
constraint x___week1___p >= 0;
var int: x___week2___s;
var int: x___week2___p;
var int: x___week2___e;
constraint x___week2___s + x___week2___p + x___week2___e = 168;
constraint x___week2___e >= 56;
constraint x___week2___p + x___week2___e >= 70;
constraint x___week2___s >= 60;
constraint 2 * x___week2___s + x___week2___e - 3 * x___week2___p >= 150;
constraint x___week2___p >= 0;
var int: cost___;
constraint cost___ = happiness(x___week1___s,x___week1___p,x___week1___e) + happiness(x___week2___s,x___week2___p,x___week2___e);
solve maximize cost___;

function var int: happiness(var int: x___s, var int: x___p, var int:x___e) =
2*x___p + x___e ;
		 * 
		 * 
		 */
			
	}
	
	@Test
	def void testTranslation4() {
		val rs = parser.parseDir("models/filtering/optfilter.iml", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.filtering.optimal_system") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		var output  = SolverRunner.run(model,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
			
	}
	
	@Test
	def void testTranslation5() {
		val rs = parser.parseDir("models/filtering/optfilter2.iml", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.filtering2.optimal_system") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		System.out.println(model);
		var output  = SolverRunner.run(gen,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
			
	}
	
	@Test
	def void testTranslation6() {
		val rs = parser.parseDir("models/filtering/optfilter3.iml", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.filtering3.optimal_system") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		System.out.println(model);
		var output  = SolverRunner.run(gen,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
			
	}
	
	@Test
	def void testTranslation7() {
		val rs = parser.parseDir("models/filtering/optfilter4.iml", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.filtering4.system1") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		System.out.println(model);
		var output  = SolverRunner.run(gen,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
			
	}
	@Test
	def void testTranslation8() {
		val rs = parser.parseDir("models/filtering5", true);
		parser.assertNoErrors(rs);
		
		var query = ImlUtil.findSymbol(rs, "optimization.filtering5.system1") 
		
		gen.generate(query as SymbolDeclaration);
		
		var model = gen.modelAsString;
		System.out.println(model);
		var output  = SolverRunner.run(gen,gen.prefixAsString,gen.typeName);
		System.out.println(output.toString);
			
	}
	
}

