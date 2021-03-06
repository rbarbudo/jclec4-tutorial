package tutorial.symreg;

import net.sf.jclec.exprtree.fun.Argument;

/**
 * Represents one the constant of the constant pull
 * 
 * @author Rafael Barbudo Lunar
 */

public class Const2 extends Argument<Double> 
{
	//////////////////////////////////////////////////////////////////////
	// -------------------------------------------- Serialization constant
	//////////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */
	
	private static final long serialVersionUID = -3563591858156220628L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor
	 */
	
	public Const2() {
		super(Double.class, 2);
	}	

	// java.lang.Object methods	
	
	public boolean equals(int argindex)
	{
		return this.argindex == argindex;
	}	
	
	public String toString()
	{
		return "C2";
	}
	
}
