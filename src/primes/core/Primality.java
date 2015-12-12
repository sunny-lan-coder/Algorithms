package primes.core;

public class Primality {
	public static boolean isPrimeTrial(long N)

	{

		for (int i = 2; i <= Math.sqrt(N); i++)

			if (N % i == 0)

				return false;

		return true;

	}
}
