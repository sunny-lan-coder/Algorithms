package primes;

import java.util.ArrayList;

import primes.core.IPrimeFinder;

public class CachingBrutePrimeFinder implements IPrimeFinder {
	// TODO add support for long prime index

	private ArrayList<Long> primes;

	/**
	 * Number of primes to initially load
	 */
	public long b = 4;

	// caching
	private long currentPrimeCandidate = 1;
	private int index_tmpCounter;
	private boolean isprime_tmpFlag;
	private double tmp_stop;
	private long primesFound = 0;
	private long prime_tmpCheck;

	public CachingBrutePrimeFinder() {
		primes = new ArrayList<Long>();
		for (int primeIndex = 0; primeIndex < b; primeIndex++) {
			getNthPrime(primeIndex);
		}
	}

	@Override
	public long getNthPrime(long n) {
		// System.out.println("requesting prime #" + n + ", there are "
		// + primesFound + " primes avail");
		while (!(primesFound > n)) {
			currentPrimeCandidate++;
			isprime_tmpFlag = true;
			tmp_stop = Math.ceil(Math.sqrt(currentPrimeCandidate));
			index_tmpCounter = 0;
			// System.out.println("testing " + currentPrimeCandidate);
			while (index_tmpCounter < primesFound) {
				prime_tmpCheck = primes.get(index_tmpCounter);
				// System.out.println("trail divide " + prime_tmpCheck);

				if (tmp_stop < prime_tmpCheck) {
					// System.out.println("prime larger than root " + tmp_stop);
					break;
				}

				if (currentPrimeCandidate % prime_tmpCheck == 0) {
					// System.out.println("divisible by" + prime_tmpCheck);
					isprime_tmpFlag = false;
					break;
				}

				index_tmpCounter++;
			}

			if (isprime_tmpFlag) {
				// System.out.println(currentPrimeCandidate
				// + " is prime, there are " + primesFound
				// + " primes avail");
				primesFound++;
				primes.add(currentPrimeCandidate);
			}
		}
		return primes.get((int) n);
	}
}
