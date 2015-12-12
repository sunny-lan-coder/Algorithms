package factorization.brute;

import java.util.ArrayList;

import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;

public class Main {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out
					.println("Expected one argument containing integer to factor");
			return;
		}
		long number;
		try {
			number = Long.parseLong(args[0]);
		} catch (NumberFormatException e) {
			System.out.println("Invalid input");
			return;
		}
		System.out.println("Factorising " + number);
		ArrayList<Long> results;
		IPrimeFactorizer factorizer = new PrimeFactorizerBrute();
		factorizer.setNumber(number);
		long start;
		long stop;
		start = System.currentTimeMillis();
		try {
			factorizer.factorize();
		} catch (PrimeFactoringException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop = System.currentTimeMillis() - start;
		System.out.print("Find took ");
		System.out.print(stop);
		System.out.println(" millis. Prime factors:");
		results = factorizer.getFactors();
		for (Long num : results) {
			System.out.println(num);
		}
	}

}
