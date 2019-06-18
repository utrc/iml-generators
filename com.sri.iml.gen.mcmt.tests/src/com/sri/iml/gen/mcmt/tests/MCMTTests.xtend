package com.sri.iml.gen.mcmt.tests

import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import com.google.inject.Inject
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.utc.utrc.hermes.iml.ImlParseHelper
import com.utc.utrc.hermes.iml.tests.TestHelper
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.ImlFactory
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.sri.iml.gen.mcmt.MCMTGenerator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;



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
		import iml.connectivity.*;
		type [Sm] T {
		    
		    x : PrimedVar<Real> ;
		    y : PrimedVar<Real> ;

		    [Input] z : Int ;
		     
		    [Init] initial_states : Bool := {x.current = 0.0} ;
		
		    [Transition] transition : Bool := { (x.next = x.current + y.current)
		    									 && ((z < 0)  => (y.next = y.current))
		    									 && ((z >= 0) => (y.next = 0.0)) } ;

			[Query, Invariant] property1 : Bool := { x.current >= 0.0} ;
		   
		}
		
		
		'''.parse
		
//		       || next_initial_states
		
		
		var model2 = '''
package p;
import iml.fsm.*;
import iml.connectivity.*;

prime : Bool -> Bool;

type [Sm] T {
    
    // A definition of a state type called "my_state_type" with variables
    // x and y of type Real. 

   x : PrimedVar<Real> ;
   y : PrimedVar<Real> ;

   // Definition of a set of states "x_is_zero" capturing all states 
   // over the state type "my_state_type" where x is 0.

   x_is_zero : Bool := { x.current = 0.0 };
   
   // A definition of a set of states "initial_states" over 
   // "my_state_type" by a state formula. These are all states where 
   // both x and y are 0.
   [Init] initial_states : Bool := { x_is_zero && (y.current = 0.0) };
   
   // Definition of a transition where the next value of x is the 
   // current value + 1.
   inc_x : Bool := { x.next = x.current + 1.0 };

   // Definition of a transition that increases both x and y
   inc_x_and_y : Bool := { inc_x && (y.next = y.current + 1.0)};

   // Definition of a transition that increases x and y if not 
   // exceeding 100, or goes back to the state with x = y = 0
   [Transition] transition : Bool := {
       ((x.current < 100.0) && inc_x_and_y)
       || (prime(initial_states))
   } ;

   // Check whether x, y <= 20
   [Query, Invariant] query1 : Bool := { (x.current <= 20.0) && (y.current <= 20.0) };
		
   // Check whether x, y <= 19
   [Query, Invariant] query2 : Bool := { (x.current <= 19.0) && (y.current <= 19.0) };
}
'''.parse
		
		model2.assertNoErrors
		
		val stateModel = ImlCustomFactory.INST.createSimpleTypeReference(model2.findSymbol("T") as NamedType)
		
		val m = generator.generate(stateModel)
		
		print(m)
	}
}