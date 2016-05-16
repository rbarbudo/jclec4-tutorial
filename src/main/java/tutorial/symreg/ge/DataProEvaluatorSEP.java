package tutorial.symreg.ge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractEvaluator;
import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.exprtree.fun.ExprTreeFunction;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.ge.GEIndividual;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.range.IRange;
import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.source.ExcelDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class DataProEvaluatorSEP extends AbstractEvaluator implements IConfigure
{
	//////////////////////////////////////////////////////////////////////
	// -------------------------------------------- Serialization constant
	//////////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */
	
	private static final long serialVersionUID = 6564636094132321667L;

	//////////////////////////////////////////////////////////////////////
	// ------------------------------------------------ Internal variables
	//////////////////////////////////////////////////////////////////////
	
	/** Maximize of minimize functions? */
	
	protected boolean maximize;
	
	private static Comparator<IFitness> COMPARATOR;
	
	private static ArrayList <Double> xvalues = new ArrayList<Double>();
	
	private static ArrayList <Double> yvalues = new ArrayList<Double>();
			
	/** Random generator used in evaluation */
	
	protected IRandGen randgen;
	
	/** Range used in constants **/
	
	protected IRange[] range;
	
	//////////////////////////////////////////////////////////////////////
	// ------------------------------------------------------ Constructors
	//////////////////////////////////////////////////////////////////////
	
	public DataProEvaluatorSEP()
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ------------------------------- Setting and getting properties
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Is this evaluator being used to maximize a function?
	 * 
	 * @return true if evaluator is used to maximize function, false otherwise
	 */
	
	public boolean isMaximize()
	{
		return maximize;
	}
	
	/**
	 * Set the maximize flag
	 * 
	 * @param maximize Actual maximize flag
	 */
	
	public void setMaximize(boolean maximize)
	{
		this.maximize = maximize;
	}
	
	//////////////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Public methods
	//////////////////////////////////////////////////////////////////////

	@Override
	public void configure(Configuration settings) 
	{
		String dataFile = settings.getString("data-file");
		ExcelDataset dataset = new ExcelDataset(dataFile);
		dataset.setName("'My ExcelDataset'");
		try {
			dataset.readDataset("n%v", "%f%f");

			//Show the dataset
			InstanceIterator it = new InstanceIterator(dataset);
			do {
				xvalues.add((double) it.currentInstance().get(0));
				yvalues.add((double) it.currentInstance().get(1));
				it.next();
			} while (!it.isDone());			
			
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NotAddedValueException e) {
			e.printStackTrace();
		} catch (IllegalFormatSpecificationException e) {
			e.printStackTrace();
		}
		
		setMaximize(false);
	}

	@Override
	protected void evaluate(IIndividual ind) 
	{			
		// Firstly, we must check if the individual is valid
		if(((GEIndividual)ind).isFeasible())
		{
			ExprTree ind_expr = (((GEIndividual)ind).getPhenotype().getExprTree());	
			//System.out.println(ind_expr);
			ExprTreeFunction function = new ExprTreeFunction(ind_expr);
			
			// Set function code
			function.setCode(ind_expr);
			// Estimated values 
			double y; 
			// Pass all
			double rms = 0.0;
			
			for (int i=0; i<xvalues.size(); i++) {
				y = function.<Double>execute(xvalues.get(i));			
				double diff = y - yvalues.get(i);
				rms += diff * diff;
			}
			rms = Math.sqrt(rms);
			
			double average = 0;
			
			for(int i=0; i<yvalues.size(); i++)
				average += yvalues.get(i);
			average = average/yvalues.size();
			
			//rms = (rms /(average));
			
			// Set rms as fitness for ind		
			if(Double.isNaN(rms) || rms > Double.MAX_VALUE || Double.isInfinite(rms))
				ind.setFitness(new SimpleValueFitness(Double.MAX_VALUE));
			else
				ind.setFitness(new SimpleValueFitness(rms));
			
		}
		// If the individual is not valid, we assign a bad fitness
		else
			ind.setFitness(new SimpleValueFitness(Double.MAX_VALUE));

	}
	
	@Override
	public Comparator<IFitness> getComparator()
	{
		// Set fitness comparator (if necessary)
		if (COMPARATOR == null)
			COMPARATOR = new ValueFitnessComparator(!maximize);
	
		// Return comparator
		return COMPARATOR;
	}
}