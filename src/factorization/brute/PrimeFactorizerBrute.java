package factorization.brute;

import java.util.ArrayList;
//import java.util.Hashtable;

//import srlab.api.IPlugin;
//import srlab.api.PluginParam;

import primes.CachingBrutePrimeFinder;
import primes.core.IPrimeFinder;
import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;

public class PrimeFactorizerBrute implements IPrimeFactorizer {

	// private static final PluginParam param;
	// static {
	// Hashtable<String, Object> params = new Hashtable<String, Object>();
	// params.put("Number", 0l);
	// param = new PluginParam(params);
	// }

	// factors
	private ArrayList<Long> factors; // results

	// primes
	private long currentFactorCandidate;// the current number to check for
	private long currentFactorIndex;
	// primeness
	private long currentFactorize;// the current number to factorize

	/**
	 * If a factor is greater than this, then will throw exception. Mostly used
	 * for the QS.
	 */
	public long max_factor = Long.MAX_VALUE;

	/**
	 * Prime number source. Default is {@link CachingBrutePrimeFinder}.
	 */
	public IPrimeFinder primeSource = null;

	public void setNumber(long number) {
		if (primeSource == null) {
			primeSource = new CachingBrutePrimeFinder();
		}

		factors = new ArrayList<Long>();
		currentFactorize = number;

		currentFactorIndex = 0;

		currentFactorCandidate = primeSource.getNthPrime(currentFactorIndex);
	}

	public void factorize() throws PrimeFactoringException {
		while (true) {
			if (currentFactorCandidate > max_factor)
				throw new PrimeFactoringException("Over max factor");
			// System.out.println("factor testing " + currentFactorCandidate);
			if (currentFactorize % currentFactorCandidate == 0l) {
				factors.add(currentFactorCandidate);
				// System.out.print(currentFactorize);
				currentFactorize = currentFactorize / currentFactorCandidate;
				// System.out.print("=" + currentFactorize
				// * currentFactorCandidate);
				if (currentFactorize == 1) {
					return;
				}
			} else {
				currentFactorIndex++;
				currentFactorCandidate = primeSource
						.getNthPrime(currentFactorIndex);
			}
		}

	}

	@Override
	public ArrayList<Long> getFactors() {
		return factors;
	}

	@Override
	public void gc() {
		factors = null;
		System.gc();
	}

	public String toString() {
		return "Brute force";
	}

	// @Override
	// public long getID() {
	// return 3214;
	// }
	//
	// @Override
	// public String getName() {
	// return "Prime factorizer - Brute force";
	// }
	//
	// @Override
	// public String getOperationName() {
	// return "Factorize";
	// }
	//
	// @Override
	// public PluginParam getParameterContainer() {
	// return param;
	// }
	//
	// @Override
	// public void init() {
	// primes = new ArrayList<Long>();
	// factors = new ArrayList<Long>();
	// primesFound = 0;
	// currentPrimeCandidate = 2;
	// }
	//
	// @Override
	// public Object performOperation(PluginParam arg0) {
	// try {
	// currentFactorize = Long.valueOf( arg0.getParameter("Number").toString());
	// factorize();
	// } catch (Exception e) {
	// throw new ClassCastException("Error:Input must be of type long");
	// }
	// return factors;
	// }
}
