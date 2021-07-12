package nl.uu.cs.ape.models.enums;

/**
 *  Defines the fragments of the logic supported by Z3 SMT solver.
 *  
 * @author Vedran Kasalica
 *
 */
public enum SMTLogicFragment {

	ABV, ALIA, AUFLIA, AUFLIRA, AUFNIA, AUFNIRA, BV, FP, LIA, LRA, NIA, NRA, QF_ABV, QF_ALIA, QF_ANIA, QF_AUFBV,
	QF_AUFLIA, QF_AUFNIA, QF_AX, QF_BV, QF_BVFP, QF_DT, QF_FP, QF_FPLRA, QF_IDL, QF_LIA, QF_LIRA, QF_LRA, QF_NIA,
	QF_NIRA, QF_NRA, QF_RDL, QF_UF, QF_UFBV, QF_UFIDL, QF_UFLIA, QF_UFLRA, QF_UFNIA, QF_UFNRA, UF, UFBV, UFIDL, UFLIA,
	UFLRA, UFNIA;
	
	
	public String toString() {
		return this.name();
	 }
}
