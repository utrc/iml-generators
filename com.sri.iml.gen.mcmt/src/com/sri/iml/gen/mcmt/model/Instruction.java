package com.sri.iml.gen.mcmt.model;

import java.util.Collection;
import java.util.Map;

/* This empty interface is simply there to group together those classes that
can be used as top-level instructions in MCMT (they must all be Sexpable.
	Classes implementing that interface:
	- NamedStateType
	- NamedStateFormula
	- NamedStateTransition
	- NamedTransitionSystem
	- Query */

public interface Instruction extends Sexpable {

}
