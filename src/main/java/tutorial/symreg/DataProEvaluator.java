package tutorial.symreg;

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
import net.sf.jclec.syntaxtree.SyntaxTreeIndividual;
import es.uco.kdis.datapro.dataset.InstanceIterator;
import es.uco.kdis.datapro.dataset.source.ExcelDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

public class DataProEvaluator extends AbstractEvaluator implements IConfigure
{
	//////////////////////////////////////////////////////////////////////
	// -------------------------------------------- Serialization constant
	//////////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */
	
	private static final long serialVersionUID = 6564636094132321667L;

	//////////////////////////////////////////////////////////////////////
	// ------------------------------------------------ Internal variables
	//////////////////////////////////////////////////////////////////////

	private static Comparator<IFitness> COMPARATOR;
	
	/** Maximize of minimize functions? */
	
	protected boolean maximize;
	
	private static ArrayList <Double> xvalues = new ArrayList<Double>();
	
	private static ArrayList <Double> yvalues = new ArrayList<Double>();
	
	/** Auxiliary function */
	
	ExprTreeFunction function = new ExprTreeFunction();
	
	//////////////////////////////////////////////////////////////////////
	// ------------------------------------------------------ Constructors
	//////////////////////////////////////////////////////////////////////
	
	public DataProEvaluator()
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ------------------------------- Setting and getting properties
	/////////////////////////////////////////////////////////////////
	
	/** Is this evaluator being used to maximize a function?
	* @return true if evaluator is used to maximize function, false
	otherwise. */
	public boolean isMaximize()
	{
		return maximize;
	}
	
	/** Set the maximize flag.
	* @param maximize Actual maximize flag. */
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
			// TODO Problemas al leer las formulas (la segunda columna son las funciones)
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
	}

	@Override
	protected void evaluate(IIndividual ind) 
	{
		// Individual phenotype
		ExprTree ind_expr = ((SyntaxTreeIndividual) ind).getGenotype().getExprTree();
		
		// Set function code
		function.setCode(ind_expr);
		// Estimated values 
		double y; 
		// Pass all
		double rms = 0.0;
		// xvalues.length == yvalues.length
		for (int i=0; i<xvalues.size(); i++) {
			y = function.<Double>execute(xvalues.get(i));
			double diff = y - yvalues.get(i);
			rms += diff * diff;
		}
		rms = Math.sqrt(rms);
		// Set rms as fitness for ind
		ind.setFitness(new SimpleValueFitness(rms));	
	}
	
	public Comparator<IFitness> getComparator()
	{
		// Set fitness comparator (if necessary)
		if (COMPARATOR == null)
			COMPARATOR = new ValueFitnessComparator(!maximize);
	
		// Return comparator
		return COMPARATOR;
	}
	
}