package primes.core;

import java.util.ArrayList;

import data.BigBooleanArray;
import data.VeryBigBooleanArray;

/**
 * Methods for prime finding. Each method has 4 letter prefix: <value> - l/m/h -
 * low medium high <value>M<value>P (how memory efficient, how fast)
 * 
 * @author sunny
 *
 */
public class PrimeFinding {
	public static ArrayList<Long> hMhPfindPrimesUnder(long n) {
		ArrayList<Long> primes = new ArrayList<Long>();
		boolean isprime;
		for (long number = 2; number < n; number++) {
			isprime = true;
			for (int index = 0; index < primes.size(); index++) {
				long item = primes.get(index).longValue();
				if (number % item == 0) {
					isprime = false;
					break;
				}
			}
			if (isprime) {
				System.out.println(number);
				primes.add(number);
			}
		}
		return primes;
	}

	public static ArrayList<Long> lMmPfindPrimesUnder(long n) {
		BigBooleanArray bools = new BigBooleanArray(n + 1);
		ArrayList<Long> results = new ArrayList<Long>();
		for (long i = 2; i < n; i++) {
			if (!bools.get(i)) {
				results.add(Long.valueOf(i));
				for (long m = i * i; m < n; m += i) {
					bools.set(m, true);
				}
			}
		}
		return results;
	}

	public static ArrayList<Long> lMhPfindPrimesUnder(long n) {
		VeryBigBooleanArray bools = new VeryBigBooleanArray(n + 1);
		ArrayList<Long> results = new ArrayList<Long>();
		for (long i = 2; i < n; i++) {
			if (!bools.get(i)) {
				results.add(Long.valueOf(i));
				for (long m = i * i; m < n; m += i) {
					bools.set(m, true);
				}
			}
		}
		return results;
	}
}
