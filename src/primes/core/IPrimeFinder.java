package primes.core;

public interface IPrimeFinder {
	/**
	 * Finds a prime at a certain index. For example, get(0)=2.
	 * 
	 * @param n
	 *            The index of the number
	 * @return The prime at index n
	 */
	public long getNthPrime(long n);
}
