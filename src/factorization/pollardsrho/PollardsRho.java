package factorization.pollardsrho;

import java.util.ArrayList;

import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;
import misc.CF;
import primes.core.Primality;

public class PollardsRho implements IPrimeFactorizer {

	public static void main(String[] args) throws PrimeFactoringException {
		PollardsRho derp = new PollardsRho();
		derp.setNumber(1024);
		derp.setRandFunc(new MyRandFunc());
		derp.factorize();
		for (long i : derp.getFactors()) {
			System.out.println(i);
		}
	}

	private long currentNumber;
	private ArrayList<Long> factors;
	private IPseudoRandomFunc randFunc;

	@Override
	public void setNumber(long number) {
		if (randFunc == null)
			randFunc = new MyRandFunc();
		this.currentNumber = number;
		factors = new ArrayList<>();
	}

	private long findFactor() {
		long a, b, p;
		a = 2;
		b = 2;

		if (currentNumber % 2 == 0)
			return 2;

		p = 1;

		while (p == 1) {
			a = randFunc.f(a) % currentNumber;
			b = randFunc.f(randFunc.f(b) % currentNumber) % currentNumber;
			if (a == b) {
				a = (long) Math.random() * currentNumber;
				b = a;
			}
			p = CF.binaryGCD(Math.abs(a - b), currentNumber);
		}

		return p;
	}

	@Override
	public void factorize() throws PrimeFactoringException {
		long factor;
		while (currentNumber != 1) {
			if (Primality.isPrimeTrial(currentNumber)) {
				factors.add(currentNumber);
				return;
			}

			factor = findFactor();

			factors.add(factor);
			currentNumber = currentNumber / factor;
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

	public void setRandFunc(IPseudoRandomFunc func) {
		this.randFunc = func;
	}

	private static class MyRandFunc implements IPseudoRandomFunc {

		private static final long C = 1;

		@Override
		public long f(long x) {
			return x * x + C;
		}

	}

	public String toString() {
		return "Pollard's Rho";
	}
}
