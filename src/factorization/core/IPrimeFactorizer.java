package factorization.core;

import java.util.ArrayList;

public interface IPrimeFactorizer {
	public void setNumber(long number);

	public void factorize() throws PrimeFactoringException;

	public ArrayList<Long> getFactors();

	public void gc();
}
