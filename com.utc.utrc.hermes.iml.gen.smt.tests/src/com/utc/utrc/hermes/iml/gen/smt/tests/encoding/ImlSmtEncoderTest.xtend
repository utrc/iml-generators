/*******************************************************************************
*Copyright (c) 2021 Raytheon Technologies. All rights reserved.
*See License.txt in the project root directory for license information.
 ******************************************************************************/
package com.utc.utrc.hermes.iml.gen.smt.tests.encoding

import org.eclipse.xtext.testing.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.testing.InjectWith
import com.utc.utrc.hermes.iml.iml.Model
import com.google.inject.Inject
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import com.utc.utrc.hermes.iml.tests.TestHelper
import org.junit.Test
import com.utc.utrc.hermes.iml.gen.smt.encoding.ImlSmtEncoder
import static extension org.junit.Assert.*
import java.util.List
import org.eclipse.emf.ecore.EObject
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration
import java.util.stream.Collectors
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider
import com.utc.utrc.hermes.iml.iml.ArrayType
import com.utc.utrc.hermes.iml.typing.TypingServices
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference
import com.utc.utrc.hermes.iml.iml.Alias
import com.utc.utrc.hermes.iml.gen.smt.encoding.SMTEncodingException
import com.utc.utrc.hermes.iml.gen.smt.tests.SmtTestInjectorProvider
import com.utc.utrc.hermes.iml.ImlParseHelper
import com.utc.utrc.hermes.iml.iml.Inclusion
import com.utc.utrc.hermes.iml.typing.TypingEnvironment
import com.utc.utrc.hermes.iml.gen.smt.encoding.AtomicRelation
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSort
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleFunDeclaration
import com.utc.utrc.hermes.iml.gen.smt.model.simplesmt.SimpleSmtFormula

@RunWith(XtextRunner)
@InjectWith(SmtTestInjectorProvider)
class ImlSmtEncoderTest {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject ImlSmtEncoder<SimpleSort, SimpleFunDeclaration, SimpleSmtFormula> encoder
	
	@Inject TypingServices typingServices;
	
	@Inject ImlTypeProvider imlTypeProvider;
	
	@Test
	def void testSimpleNamedTypeEncoder() {
		val model = 
		encode('''
			package p1;
			type T1;
		''', "T1")
		
		assertSorts(model.findSymbol("T1"))
	}
	
	@Test
	def void testNamedTypeWithElementsEncoder() {
		val model = 
		encode('''
			package p1;
			type T1 {
				var1 : Int;
				var2 : Int;
			}
		''', "T1")
		val t1Sort = assertAndGetSort(model.findSymbol("T1"))
		val intSort = assertAndGetSort(model.eResource.resourceSet.findSymbol("Int"))
		
		val var1Fun = assertAndGetFuncDecl(
			(model.findSymbol("T1") as NamedType).findSymbol("var1"), #[t1Sort], intSort
		)
		
		val var2Fun = assertAndGetFuncDecl(
			(model.findSymbol("T1") as NamedType).findSymbol("var2"), #[t1Sort], intSort
		)
	}
	
	@Test
	def void testNamedTypeWithExtensionEncoder() {
		val model = 
		encode('''
			package p1;
			type T1 includes (T2);
			type T2;
		''', "T1")
		
		val t1Sort = assertAndGetSort(model.findSymbol("T1"))
		val t2Sort =  assertAndGetSort(model.findSymbol("T2"))
		val inclusionRelation = (model.findSymbol("T1") as NamedType).relations.get(0) as Inclusion;
		val extensionFun = assertAndGetFuncDecl(
			new AtomicRelation(inclusionRelation, inclusionRelation.inclusions.get(0).type),
			#[t1Sort], t2Sort
		)
	}
	
	@Test
	def void testNamedTypeWithAliasEncoder() {
		val model = 
		encode('''
			package p1;
			type T1 is T2;
			type T2;
		''', "T1")
		
		val t1Sort = assertAndGetSort(model.findSymbol("T1"))
		val t2Sort =  assertAndGetSort(model.findSymbol("T2"))
		val aliasRelation = (model.findSymbol("T1") as NamedType).relations.get(0) as Alias
		val extensionFun = assertAndGetFuncDecl(
			new AtomicRelation(aliasRelation, aliasRelation.type.type),
			#[t1Sort], t2Sort 
		)
	}
	
	
	@Test
	def void testArrayTypeEncoder() {
		val model = 
		encode('''
			package p1;
			type T1 {
				var1: Int[10][];
			}
		''', "T1")
		
		val t1Sort = assertAndGetSort(model.findSymbol("T1"))
		val intSort =  assertAndGetSort(model.eResource.resourceSet.findSymbol("Int"))
		val var1 = (model.findSymbol("T1") as NamedType).findSymbol("var1") as SymbolDeclaration;
		val int2Sort = assertAndGetSort(var1.type)
		val int1Sort = assertAndGetSort(typingServices.accessArray(var1.type as ArrayType, 1))
		
		val int2Access = assertAndGetFuncDecl(
			var1.type, #[int2Sort, intSort], int1Sort
		)
		
		val int1Access = assertAndGetFuncDecl(
			typingServices.accessArray(var1.type as ArrayType, 1),
			#[int1Sort, intSort], intSort
		)
		
		print(encoder.toString)
	}

	@Test
	def void testTupleTypeEncoder() {
		val model = encode('''
			package p1;
			var1 : (Int, Real);
		''', "var1")
		val intSort = assertAndGetSort(model.eResource.resourceSet.findSymbol("Int"))
		val realSort = assertAndGetSort(model.eResource.resourceSet.findSymbol("Real"))
		val tupleSort = assertAndGetSort((model.findSymbol("var1") as SymbolDeclaration).type)
		
		assertEquals(intSort, tupleSort.tupleElements.get(0))
		assertEquals(realSort, tupleSort.tupleElements.get(1))
						
		val var1 = assertAndGetFuncDecl(model.findSymbol("var1"), #[], tupleSort)
	}
	
	@Test
	def void testTemplateTypeEncoder() {
		val model = encode('''
			package p1;
			type T1<T, P> {
				vart: T -> P;
			}
			
			type T2 {
				var1 : T1<Int, Real>;
				var2 : T1<Int, Int>;
				var3 : T1<Int, Real>;
			}
			
		''', "T2")
		val intSort = assertAndGetSort(model.eResource.resourceSet.findSymbol("Int"))
		val realSort = assertAndGetSort(model.eResource.resourceSet.findSymbol("Real"))
		assertNull(encoder.getOrCreateSort(model.findSymbol("T1"), new TypingEnvironment())) // T<type T, type P> should not be encoded
		val T2Sort = assertAndGetSort(model.findSymbol("T2"))
		
		// Test new types for binding
		val t1IntReal = assertAndGetSort(model.getSymbolType("T2", "var1"))
		val t1IntInt = assertAndGetSort(model.getSymbolType("T2", "var2"))
		val t1IntReal2 = assertAndGetSort(model.getSymbolType("T2", "var3"))		
		
		assertNotSame(t1IntReal, t1IntInt) // Different sorts for different binding
		assertSame(t1IntReal, t1IntReal2)  // Same sort for same binding
		
		// Make sure we create concurrent sorts for T -> P
		val intToRealType = imlTypeProvider.getSymbolType(
			(model.findSymbol("T1") as NamedType).findSymbol("vart") as SymbolDeclaration,
			 new TypingEnvironment(model.getSymbolType("T2", "var1") as SimpleTypeReference)
		)
		
		val intToIntType = imlTypeProvider.getSymbolType(
			(model.findSymbol("T1") as NamedType).findSymbol("vart") as SymbolDeclaration,
			new TypingEnvironment(model.getSymbolType("T2", "var2") as SimpleTypeReference)
		)
	}
	
	
	def ImlType getSymbolType(Model model, String ctName, String symbolName) {
		if (ctName === null || ctName.empty) {
			return (model.findSymbol(symbolName) as SymbolDeclaration).type
		} else {
			return ((model.findSymbol(ctName) as NamedType).findSymbol(symbolName) as SymbolDeclaration).type
		}
	}
	
	def assertAndGetFuncDecl(EObject imlObject, List<SimpleSort> inputSorts, SimpleSort outputSort) {
		return assertAndGetFuncDecl(null, imlObject, inputSorts, outputSort)
	}
	
	def assertAndGetFuncDecl(EObject container, EObject imlObject, List<SimpleSort> inputSorts, SimpleSort outputSort) {
		var SimpleFunDeclaration funcDecl;
		funcDecl = encoder.getFuncDeclaration(container, imlObject)
		assertNotNull(funcDecl)
		assertEquals(outputSort, funcDecl.outputSort)
		assertContainTheSameElements(inputSorts, funcDecl.inputSorts);
		return funcDecl;
	}
	
	def assertAndGetSort(EObject type) {
		val sort = encoder.getOrCreateSort(type, new TypingEnvironment())
		assertNotNull(sort)
		assertNotNull(sort.name)
		return sort;
	}
	
	def assertSorts(EObject ... types) {
		assertContainTheSameElements(types.map[encoder.getOrCreateSort(it, new TypingEnvironment())], encoder.allSorts)
	}
	
	def assertContainTheSameElements(List list1, List list2) {
		if (list1 === null && list2 === null) return;
		if (list1 === null || list2 === null) 
		
		if (list1 === null) {
			if (list2 === null || list2.isEmpty) return;
			assertTrue("One of the lists is null, but the other has elements", false)
		}
		if (list2 === null) {
			if (list1.isEmpty) return;
			assertTrue("One of the lists is null, but the other has elements", false)
		}
		
		assertEquals(list1.length, list2.size)
		for (Object funcName : list1) {
			assertTrue("Couldn't find " + funcName, list2.contains(funcName))
		}
	}
	
	def encode(String modelString, String ctName) {
		val model = modelString.parse;
		model.assertNoErrors
		encoder.encode(model.findSymbol(ctName))
		// TODO make sure names are unique
		val distinctSorts = encoder.allSorts.map[it.name].stream.distinct.collect(Collectors.toList)
		assertEquals(distinctSorts.size, encoder.allSorts.size)
		val distinctFuncDecls = encoder.allFuncDeclarations.map[it.name].stream.distinct.collect(Collectors.toList)
		assertEquals(distinctFuncDecls.size, encoder.allFuncDeclarations.size)
		return model
	}
	
	@Test
	def void testTypeWithSymbolsEncoder() {
		val model = 
		'''
			package p;
			
			type T1 {
				a : Int;
				b : T2;
			}
			
			type T2;
		'''.parse
		model.assertNoErrors
		
		encoder.encode(model.findSymbol("T1"))
		println(encoder.toString)
	}
	
	@Test
	def void testComplexTypesEncoder() {
		val model = 
		'''
			package p;
			
			type T1 includes (T2) {
				a : Int;
				b : Int -> T2;
				c : (Int, Real);
				d : Int[10][];
			}
			
			type T2 {
				x: Int;	
				y: Float;
			};
			
			type Float is Real;
		'''.parse
		model.assertNoErrors
		
		encoder.encode(model.findSymbol("T1"))
		println(encoder.toString)
	}
	
	@Test
	def void testArrayOfParentheizedExpr() {
		val model = '''
			package p;
			type T1 {
				a : (Int)[];
			}
		'''.parse
		
		encoder.encode(model.findSymbol("T1"))
		println(encoder.toString)
	}
	
	@Test
	def void testExtendsEncoding() {
		val model = 
		'''
			package p;
			type T1 includes (T2);
			type T2;
		'''.parse
		model.assertNoErrors
		
		encoder.encode(model.findSymbol("T1"))
		println(encoder.toString)
	}
	
	@Test
	def void testAliasEncoding() {
		val model = 
		'''
			package p;
			type T1 is T2;
			type T2;
		'''.parse
		model.assertNoErrors
		
		encoder.encode(model.findSymbol("T1"))
		println(encoder.toString)
	}
	
	@Test
	def void testTypeWithTemplates() {
		val model = 
		'''
			package p;
			
			type T1<T> {
				a : T;
			}
			
			type T2 {
				b : T1<Int>;
			}
		'''.parse
		model.assertNoErrors
		
		encoder.encode(model.findSymbol("T2"))
		println(encoder.toString)
	}
	
	@Test
	def void testFormulaEncoding() {
		val model =
		encode('''
			package p;
			type T1 {
				var2: Int;
				var1 : Int := var2 * 5;
			}
			varT : T1;
		''', "T1");
		
		
		val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
		val definition = ((model.findSymbol("T1") as NamedType).findSymbol("var1") as SymbolDeclaration).definition
		
		val formulaEncoding = encoder.encodeFormula(definition, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		
		print(formulaEncoding);
	}
	
	@Test
	def void testFormulaEncodingTermMemberSelection() {
		val model =
		encode('''
			package p;
			type T1 {
				var1 : Int;
			}
			type T2 {
				var2: T1;
				var3: Int := var2.var1;
			}
			varT : T2;
		''', "T2");
		
		
		val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
		val definition = ((model.findSymbol("T2") as NamedType).findSymbol("var3") as SymbolDeclaration).definition
		
		val formulaEncoding = encoder.encodeFormula(definition, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		
		print(formulaEncoding);
	}
	
	@Test
	def void testFormulaEncodingSymbolRefWithExtension() {
		val model =
		encode('''
			package p;
			type T1 {
				var1 : Int;
			}
			type T2 includes (T1) {
				var2: Int := var1;
			}
			varT : T2;
		''', "T2");
		
		
		val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
		val definition = ((model.findSymbol("T2") as NamedType).findSymbol("var2") as SymbolDeclaration).definition
		
		val formulaEncoding = encoder.encodeFormula(definition, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		
		print(formulaEncoding);
	}
	
	@Test
	def void testFormulaEncodingIte() {
		val model =
		encode('''
			package p;
			type T1 {
				assert var1 {if (true && false) {5}};
			}
			varT : T1;
		''', "T1");
		
		
		val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
		val definition = ((model.findSymbol("T1") as NamedType).findSymbol("var1") as SymbolDeclaration).definition
		
		val formulaEncoding = encoder.encodeFormula(definition, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		
		print(formulaEncoding);
	}
	
	@Test
	def void testFormulaEncodingQuantifiers() {
		val model =
		encode('''
			package p;
			type T1 {
				var1 : Bool := forall (a : Int) {a > 5};
				var2 : Bool := exists (a : Int) {a = 0};
			}
			varT : T1;
		''', "T1");
		
		
		val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
		val definitionForAll = ((model.findSymbol("T1") as NamedType).findSymbol("var1") as SymbolDeclaration).definition
		val definitionExists = ((model.findSymbol("T1") as NamedType).findSymbol("var2") as SymbolDeclaration).definition
		
		
		val formulaEncoding = encoder.encodeFormula(definitionForAll, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		val formulaEncoding2 = encoder.encodeFormula(definitionExists, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		
		println(formulaEncoding);
		println(formulaEncoding2);
	}
		
	@Test
	def void testFormulaEncodingFunctionCall() {
		val model =
		encode('''
			package p;
			type T1 {
				var1 : Int -> Int;
				var2 : Int := var1(5);
			}
			varT : T1;
		''', "T1");
		
		
		val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
		val definition = ((model.findSymbol("T1") as NamedType).findSymbol("var2") as SymbolDeclaration).definition
		
		val formulaEncoding = encoder.encodeFormula(definition, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
		
		println(encoder.toString)
		println(formulaEncoding);
	}
	
	@Test
	def void testFormulaEncodingFunctionUsedAsAVariable() {
		try {
			val model = encode('''
				package p;
				type T1 {
					var1 : Int -> Int;
					var2 : Int -> Int := var1;
				}
				varT : T1;
			''', "T1");
			val varT = (model.findSymbol("varT") as SymbolDeclaration).type as SimpleTypeReference
			
			val definition = ((model.findSymbol("T1") as NamedType).findSymbol("var2") as SymbolDeclaration).definition
			val formulaEncoding = encoder.encodeFormula(definition, new TypingEnvironment(varT), new SimpleSmtFormula("inst"), null);
			
			println(encoder.toString)
			println(formulaEncoding);
//			assertTrue(false);
		} catch (SMTEncodingException e) {
		}
	}
	
	@Test
	def void testEncodingWithAssertions() {
		val model = encode('''
			package p;
			type T1 {
				var1 : Int;
				assert {var1 > 0};
			}
		''', "T1");
			
			println(encoder.toString)
	}
	
	@Test
	def void testEncodingWithDefinition() {
		val model = encode('''
			package p;
			type T1 {
				var1 : Int := 5;
			}
		''', "T1");
			
			println(encoder.toString)
	}
	
	@Test
	def void testEncodingWithFunction() {
		val model = encode('''
			package p;
			type T1 {
				var1 : Int -> Int := fun(p:Int) { p * 5};
			}
		''', "T1");
			
			println(encoder.toString)
	}
	
	@Test
	def void testEncodingWithFunctionHavingVars() {
		val model = encode('''
			package p;
			type T1 {
				var1 : Int -> Int := fun(p:Int) { var x : Int := 5; var y : Int := x * 2; x + y + p * 5};
			}
		''', "T1");
			
			println(encoder.toString)
	}
	
	
	@Test
	def void testEncodingWithFunctionMultipleParams() {
		val model = encode('''
			package p;
			type T1 {
				var1 : (Int, Real) -> Int := fun(p:Int, p2:Real) { p * 5};
			}
		''', "T1");
			
			println(encoder.toString)
	}
	
	@Test
	def void testEncodingWithInstanceConstructor() {
		val model = encode('''
			package p;
			type T1 {
				x : Int;
			}
			
			type T2 {
				a : T1 := some(t: T1) { t.x = 5 } ;
			}
			
		''', "T2");
			
			println(encoder.toString)
	}
	
	@Test
	def void testEncodingWithInstanceConstructorWithScope() {
		val model = encode('''
			package p;
			type T1 {
				x : Int;
			}
			
			type T2 {
				a : Int -> T1 := fun(p1 : Int) { some(t: T1) { t.x = p1 } };
			}
			
		''', "T2");
			
			println(encoder.toString)
	}
	
	@Test
	def void testPublicSymbolEncoding() {
		val model = encode('''
			package p;
			
			a : T1;
			f : Int -> Real; 
			
			
			type T1 {
				x : Real := f(5);
				y : Real := f(10);
			}
		''', "T1")
		
		println(encoder.toString)
	}
	
	@Test
	def void testSymbolEncodingWithDifferentTypeBinding() {
		val model = encode('''
			package p;
						
			type T1<T> {
				x : T;
			}
			
			type T2 {
				a : T1<Int>;
				b : T1<Real>;
			}
		''', "T2")
		
		println(encoder.toString)
	}
	
	@Test
	def void testRelationWithTemplates() {
		val model = encode('''
			package p;
			
			type T1<T> {
				a : T;	
			}
			
			type T3 {
				
			}
			
			type T2 includes(T1<Int>, T3) {
				b : Real;
			}
			
		''', "T2")
		
		println(encoder.toString)
	}
	
	@Test
	def void testRelationWithTemplates2() {
		val model = encode('''
			package p;
			
			type T1<T> {
				a : T;	
			}
			
			type T3 {
				
			}
			
			type T2<T> includes(T1<T>, T3) {
				b : Real;
			}
			
			type T4 {
				x : T2<Int>;
			}
			
		''', "T4")
		
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncoding() {
		val model = encode('''
			package p;
			
			trait tr {
				a : Int;
			}
			
			type T1 exhibits(tr) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncoding2() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T;
			}
						
			type T1 exhibits(tr<Int>) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncodingExhibitsSameTraitMultipleTimes() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T;
			}
						
			type T1 exhibits(tr<Int>, tr<Bool>) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncodingExhibitsMultipleTraitsWithShadowing() {
		val model = encode('''
			package p;
			
			trait tr {
				a : Int;
			}
			
			trait tr2 {
				a : Bool;
				c : Bool;
			}
			
						
			type T1 exhibits(tr, tr2) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	
	@Test
	def void testTraitEncodingExhibitsMultipleTraits() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T;
			}
			
			trait tr2<T> {
				c : T;
			}
						
			type T1 exhibits(tr<Int>, tr2<Bool>) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncodingExhibitsWithShadowing() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T;
			}
						
			type T1 exhibits(tr<Int>) {
				a : Bool;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncodingWithSelf() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T -> Self;
			}
						
			type T1 exhibits(tr<Int>) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncoding4() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T;
			}
			
			type T2<T> {
				c : T;
			}
			
			type T1 exhibits(tr<Int>, tr<T2<Bool>>) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testTraitEncodingWithDef() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T ;
				b : T := a;
			}
			
			type T1 exhibits(tr<Int>) {
				c : Real;
				d : Int := b;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	
	
	@Test
	def void testTraitEncodingWithInheritance() {
		val model = encode('''
			package p;
			
			trait tr<T> {
				a : T;
			}
			
			trait tr2<T> refines (tr<T>) {
				c : T;
			}
			
			type T1 exhibits(tr2<Int>) {
				b : Real;
			}
		''', "T1")
		println(encoder.toString)
	}
	
	@Test
	def void testIncludesWithShadowing() {
		val model = encode('''
			package p;
			
			type T1 {
				a : Int := 5;
			}
			
			type T2 includes(T1) {
				a : Real := 10;
				b : Real := a;
			}
		''', "T2")
		println(encoder.toString)
	}
	
	@Test
	def void testNotEqualSymoblEncoding() {
		val model = encode('''
			package p;
			
			type T1 {
				a : Bool;
				b : Bool := a != true;
			}
		''', "T1")
		println(encoder.toString)
		
	}
	
}
