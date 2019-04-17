package com.sri.iml.gen.mcmt.tests

import org.eclipse.xtext.testing.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import org.junit.Test
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import com.sri.iml.gen.mcmt.MCMTGenerator
import com.utc.utrc.hermes.iml.tests.TestHelper
import com.utc.utrc.hermes.iml.iml.NamedType
import com.sri.iml.gen.mcmt.model.SallyModel
import com.utc.utrc.hermes.iml.iml.ImlFactory
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class MCMTTests {
	
	@Inject extension ImlParseHelper
	@Inject extension ValidationTestHelper
	@Inject extension TestHelper
	
	@Inject MCMTGenerator generator
	
	@Test
	def void testSimpleSM() {
		val model = 
		'''
			package p;
			import iml.fsm.*;
	
			type [Fsm] mySM {
				
			}
			
		'''.parse
		
		model.assertNoErrors
		
		print(model.name)
	}
	
	@Test
	def void testSmModel0() {
		val model = '''
		package p;
		import iml.fsm.*;
		type [Sm] T {
		    
		    x : PrimedVar<Real> ;
		     
		    [Init] initial_states : Bool := {x.current = 0.0} ;
		
		    [Transition] transition : Bool := { x.next = x.current+ 1.0} ;
		   
		}
		
		[Query, Invariant] property1 : T -> Bool := fun(t:T):Bool { t.x.current >= 0.0} ;
		
		'''.parse
		
		model.assertNoErrors
		
		val stateModel = ImlCustomFactory.INST.createSimpleTypeReference(model.findSymbol("T") as NamedType)
		val m = new SallyModel
		
		val sallySm = generator.generateType(m, stateModel)
		
		
		
	}
	
}