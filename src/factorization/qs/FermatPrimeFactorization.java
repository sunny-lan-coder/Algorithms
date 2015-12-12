package factorization.qs;

import java.util.ArrayList;

import factorization.core.IPrimeFactorizer;

public class FermatPrimeFactorization implements IPrimeFactorizer { // TODO make
																	// non-recursive
																	// function
	long current_number_being_factored;
	long a;
	long b;
	ArrayList<Long> factors;

	@Override
	public void setNumber(long number) {
		current_number_being_factored = number;
		a = (long) Math.floor(Math.sqrt(number));
		factors = new ArrayList<Long>();
	}

	@Override
	public void factorize() {
		while (true) {
			b = (long) Math.pow(a, 2) - current_number_being_factored;
			if (Squares.isPerfectSquare(b)) {
				// found square
				factors.add(a - b);
				factors.add(a + b);
				break;
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
	}

	@Override
	public String toString() {
		return "Fermat's method - incomplete";
	}

}
