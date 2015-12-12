package factorization.pollardsrho;

import java.util.ArrayList;

import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;
import misc.CF;
import primes.core.Primality;

public class MontegomoryRho implements IPrimeFactorizer {

	private long N;
	private int c_max;
	private long[] C;
	private int c;
	private ArrayList<Long> factors;

	private class MyRandFunc implements IPseudoRandomFunc {

		private static final long C = 1;

		@Override
		public long f(long x) {
			return x * x + C;// * x + C;
		}

	}

	@Override
	public void setNumber(long number) {
		N = number;
		c_max = 100;
		C = new long[c_max];
		factors = new ArrayList<>();
	}

	private void MCF(IPseudoRandomFunc G, long x_0) throws PrimeFactoringException {
		System.out.println("MCF");
		long z = G.f(x_0 * x_0) % N;
		long y = z * z % N;
		c = 0;
		long e = 1;
		long f = 1;
		long i = 1;
		long t_1 = z;
		long t_2 = x_0;
		long x = G.f(y) % N;
		long a_1;
		long a_2;
		long a_3;
		long a_4;
		long tmp;
		long w;
		do {
			// calculate constants for polynomial
			a_1 = (t_1 + t_2) % N;
			a_2 = (a_1 + z) % N;
			a_3 = (a_1 * z + t_1 * t_2) % N;
			a_4 = a_1 * (y + a_3) % N;

			// calculate fibonacci sequence
			tmp = e;
			e = f;
			f = tmp + f;

			t_2 = t_1;
			t_1 = z;

			System.out.printf("	Testing (x - %d)(x - %d)(x - %d) for x_i where %d<i<%d%n", z, t_1, t_2, e, f);

			// test poly(x_i) for e=<i<f
			do {
				w = (x - a_2) % N;
				y = x * x % N;
				System.out.printf("		Testing (%d + %d)(%d) + %d = %d", y, a_3, w, a_4, (y + a_3) * w + a_4);
				TEST((y + a_3) * w + a_4);
				// get next value of x
				x = G.f(y) % N;
				i++;
			} while (i != f && N != 1);

			// advance the values in the polynomial set
			z = w + a_2 % N;
		} while (N != 1);
	}

	// doesn't actually test value, actually adds to queue to test
	private void TEST(long T) {
		System.out.println();
		if (T % N == 0)
			return;
		C[c] = T % N;
		System.out.printf("		adding C[%d]=%d%n", c, C[c]);
		c++;
		if (c == c_max)
			CHEK();
	}

	// perform GCF on values
	private void CHEK() {
		System.out.println(" Checking...");
		long product = 1;
		int i;
		for (i = 0; i < c; i++) {
			product = product * C[i] % N;
		}
		int k;
		long d;
		long gcd;
		if (CF.euclidGCF(product, N) > 1) {
			// backtrack find factor
			do {
				d = N;
				k = 0;
				for (i = 0; i < c; i++) {
					if (CF.euclidGCF(C[i], N) > 1) {
						gcd = CF.euclidGCF(C[i], d);
						if (gcd > 1) {
							d = gcd;
						}
						k++;
						C[k] = C[i];
					}
				}
				if (k > 0) {
					N = N / d;
					c = k;
					factors.add(d);
				}
			} while (k != 0);
			if (Primality.isPrimeTrial(N)) {
				factors.add(N);
			}
		}
	}

	@Override
	public void factorize() throws PrimeFactoringException {
		MCF(new MyRandFunc(), (long) (Math.random() * N));
	}

	@Override
	public ArrayList<Long> getFactors() {
		return factors;
	}

	@Override
	public void gc() {
		C = null;
		factors = null;
		System.gc();
	}

	@Override
	public String toString() {
		return "Montegomory's Rho";
	}

}
