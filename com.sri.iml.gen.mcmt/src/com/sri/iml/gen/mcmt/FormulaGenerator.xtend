package com.sri.iml.gen.mcmt

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.iml.Addition
import com.utc.utrc.hermes.iml.iml.Assertion
import com.utc.utrc.hermes.iml.iml.AtomicExpression
import com.utc.utrc.hermes.iml.iml.CaseTermExpression
import com.utc.utrc.hermes.iml.iml.FloatNumberLiteral
import com.utc.utrc.hermes.iml.iml.FolFormula
import com.utc.utrc.hermes.iml.iml.ImlType
import com.utc.utrc.hermes.iml.iml.IteTermExpression
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.Multiplication
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.iml.NumberLiteral
import com.utc.utrc.hermes.iml.iml.ParenthesizedTerm
import com.utc.utrc.hermes.iml.iml.RelationKind
import com.utc.utrc.hermes.iml.iml.SequenceTerm
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm
import com.utc.utrc.hermes.iml.iml.TermExpression
import com.utc.utrc.hermes.iml.iml.TermMemberSelection
import com.utc.utrc.hermes.iml.iml.TruthValue

import com.sri.iml.gen.mcmt.model.FormulaAtom
import com.sri.iml.gen.mcmt.model.Sexp
import com.sri.iml.gen.mcmt.model.NamedStateType
import com.sri.iml.gen.mcmt.model.StateNext
import com.sri.iml.gen.mcmt.model.MCMT
import com.sri.iml.gen.mcmt.model.StateFormulaVariable
import com.utc.utrc.hermes.iml.iml.TailedExpression
import com.utc.utrc.hermes.iml.iml.TupleConstructor
import com.sri.iml.gen.mcmt.model.StateTransVariable
import com.sri.iml.gen.mcmt.model.StateTransFormulaVariable
import com.sri.iml.gen.mcmt.model.FormulaVar
import com.sri.iml.gen.mcmt.model.Sexp_atom

class FormulaGenerator<V> {
	
	@Inject
	MCMTGeneratorServices gen;

	AtomBuilder<V> atomBuilder;
	
	new(AtomBuilder<V> atomBuilder){
		this.atomBuilder = atomBuilder
		this.gen = new MCMTGeneratorServices()
	}

	def Sexp<FormulaAtom<V>> generate(FolFormula e, NamedStateType ctx, MCMT mcmt) throws GeneratorException {
		var Sexp<FormulaAtom<V>> retval = null;
		
		if (e.getOp() !== null &&
			(e.getOp().equals("=>") || e.getOp().equals("<=>") || e.getOp().equals("&&") || e.getOp().equals("||"))) {
			retval = (new AppBuilder(gen.convertOp(e.op))).app(generate(e.left,ctx,mcmt)).app(generate(e.right,ctx,mcmt));
			
		} else if (e instanceof AtomicExpression) {

			retval = (new AppBuilder(e.rel.toString)).app(generate(e.left,ctx,mcmt)).app(generate(e.right,ctx,mcmt));

		} else if (e instanceof Addition) {

			retval = (new AppBuilder(e.sign)).app(generate(e.left,ctx,mcmt)).app(generate(e.right,ctx,mcmt));

		} else if (e instanceof Multiplication) {

			retval = (new AppBuilder(e.sign)).app(generate(e.left,ctx,mcmt)).app(generate(e.right,ctx,mcmt));

		} else if (e instanceof TermMemberSelection) {
			// TODO this is a quick hack
			if (e.receiver instanceof SymbolReferenceTerm &&
				(e.receiver as SymbolReferenceTerm).symbol instanceof NamedType) {
				var typename = gen.qualifiedName((e.receiver as SymbolReferenceTerm).symbol as NamedType)
				var literalname = '''"«typename».«e.member»"'''
				retval = atomBuilder.variable(ctx.getInput(literalname))
			} else {
				var rec = (e.receiver as SymbolReferenceTerm).symbol.name
				var mem = (e.member as SymbolReferenceTerm).symbol.name
				if (mem.equals("current")) {
					retval = atomBuilder.variable(StateNext.State, ctx.getVar(rec))
				} else if (mem.equals("next")) {
					retval = atomBuilder.variable(StateNext.Next, ctx.getVar(rec))
				} else {
					var literalname = '''"«e.receiver».«e.member»"'''
					retval = atomBuilder.variable(ctx.getInput(literalname))
				}
			}
		} else if (e instanceof SymbolReferenceTerm) {
			
			var input = ctx.getInput(e.symbol.name)
			if (input !== null) retval = atomBuilder.variable(input)
			else retval = atomBuilder.variable(e.symbol.name)
			
		} else if (e instanceof ParenthesizedTerm) {

			retval = generate(e.sub,ctx,mcmt)

		} else if (e instanceof IteTermExpression) {

			retval = (new AppBuilder("ite")).app(generate(e.condition,ctx,mcmt)).app(generate(e.left,ctx,mcmt)).app(generate(e.right,ctx,mcmt));

		} else if (e instanceof SequenceTerm) {

			retval = generate(e.^return,ctx,mcmt)

		} else if (e instanceof SignedAtomicFormula) {
			if (e.neg) {
				retval = (new AppBuilder("not")).app(generate(e.left,ctx,mcmt))
			} else {
				retval = generate(e.left,ctx,mcmt);
			}
		} else if (e instanceof NumberLiteral) {
			if (e.isNeg) {
				retval = atomBuilder.symbol("-" + e.value.toString())
			} else {
				retval = atomBuilder.symbol(e.value.toString())
			}
		} else if (e instanceof FloatNumberLiteral) {
			if (e.isNeg) {
				retval = atomBuilder.symbol("-" + e.value.toString())
			} else {
				retval = atomBuilder.symbol(e.value.toString())
			}
		} else if (e instanceof TruthValue) {
			if (e.isTRUE) {
				retval = atomBuilder.symbol("true")
			} else {
				retval = atomBuilder.symbol("false")
			}
		} else if (e instanceof TailedExpression) {
			var e2 = (e.getTail() as TupleConstructor).getElements().get(0).left;
			if (!(e.left instanceof SymbolReferenceTerm && (e.left as SymbolReferenceTerm).symbol.name.equals("prime"))) {
				throw new GeneratorException("Function is not \"prime\" -- Some kind of higher-order that does not exist in MCMT");
			}
			if (!(e2 instanceof SymbolReferenceTerm)) {
				throw new GeneratorException("Argument not a SymbolReferenceTerm");
			}
			var form = (e2 as SymbolReferenceTerm).symbol.name;
			retval = atomBuilder.variable(StateNext.Next, form);
		}
		return retval;
	}

}
