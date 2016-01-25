/**
 * 
 */
package tutorial.symreg;

import net.sf.jclec.exprtree.fun.AbstractPrimitive;
import net.sf.jclec.exprtree.fun.ExprTreeFunction;

/**
 * @author Rafael Barbudo Lunar
 *
 */

public class Constant extends AbstractPrimitive 
{
	private static final long serialVersionUID = -4349262554064087860L;

	protected Constant() 
	{
		super(new Class<?> [] {Double.class, Double.class}, Double.class);
	}


	@Override
	protected void evaluate(ExprTreeFunction context) 
	{
		// TODO esto es lo mas guarro que existe preguntar a Sebastian
		int max = 10;
		int min = -10;
		
		int rand = (int)(Math.random() * (max - min) + min);
		push(context, rand);
		
	}

}
