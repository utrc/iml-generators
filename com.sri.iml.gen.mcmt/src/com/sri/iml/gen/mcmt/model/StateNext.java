package com.sri.iml.gen.mcmt.model;

// State transitions are implemented as formulae. These can talk about a state variable in the current state or in the next state.
// This enum class implements that flag.

public enum StateNext { 
	
    State, Next;
	
	public String toString() {
		switch(this) {
		case State: return "state.";
		case Next : return "next.";
		default   : return null;
		}
	}
} 