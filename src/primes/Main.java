package primes;
import java.util.ArrayList;

import primes.core.PrimeFinding;

public class Main {

	public static void main(String[] args) {
		long size = 10000000;
		long start;
		ArrayList<Long> primes;

		start = System.currentTimeMillis();
		primes = PrimeFinding.hMhPfindPrimesUnder(size);
		System.out.println("2nd Find took:"
				+ (System.currentTimeMillis() - start) + "ms");

		for (int i = primes.size() - 1; i < primes.size(); i++) {
			System.out.println(primes.get(i));
		}
	}

}