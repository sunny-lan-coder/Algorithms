package factorization.pollardspminusone;

import java.util.ArrayList;

import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;
import factorization.pollardsrho.IPseudoRandomFunc;
import misc.CF;
//import factorization.pollardsrho.IPseudoRandomFunc;

public class PollardsPMinusOne implements IPrimeFactorizer {

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

	public void setRandFunc(IPseudoRandomFunc func) {
		this.randFunc = func;
	}

	private class MyRandFunc implements IPseudoRandomFunc {

		private static final long C = 1;

		@Override
		public long f(long x) {
			return x * x + C;
		}

	}

	private boolean isPrime(long N)

	{

		for (int i = 2; i <= Math.sqrt(N); i++)

			if (N % i == 0)

				return false;

		return true;

	}

	@Override
	public void factorize() throws PrimeFactoringException {
		long factor;
		while (currentNumber != 1) {
			if (isPrime(currentNumber)) {
				factors.add(currentNumber);
				return;
			}

			factor = findFactor();

			factors.add(factor);
			currentNumber = currentNumber / factor;
		}
	}

	private long findFactor() {
		long n = this.currentNumber;
		// System.out.println("factoring " + n);
		long a = 2;
		long r;
		long d;
		long aP;
		while (true) {
			a = randFunc.f(a);
			d = CF.euclidGCF(a, n);

			// System.out.println("a="+a+" gcd="+d);
			if (d > 1)
				return d;

			r = 2;
			aP = 1;
			while (true) {
				for (int i = 0; i < r; i++) {
					aP = (aP * a) % n;
				}
				// System.out.println("a^(r!)="+a+" gcd="+d);
				d = CF.euclidGCF(aP - 1, n);
				if (d == n)
					break;
				if (d != 1)
					return d;

				r++;
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
		return "Pollard's P - 1";
	}
}
