package misc;

import java.math.BigInteger;

public class CF {
	
	  /**
     * Gcd between two positive numbers.
     * <p>
     * Gets the greatest common divisor of two numbers, using the "binary gcd" method,
     * which avoids division and modulo operations. See Knuth 4.5.2 algorithm B.
     * This algorithm is due to Josef Stein (1961).
     * </p>
     * Special cases:
     * <ul>
     * <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and {@code gcd(x, 0)} is the value of {@code x}.</li>
     * <li>The invocation {@code gcd(0, 0)} is the only one which returns {@code 0}.</li>
     * </ul>
     *
     * @param a first number, must be &ge; 0
     * @param b second number, must be &ge; 0
     * @return gcd(a,b)
     */
   public static long binaryGCD(long a, long b){
        // both a and b must be positive, it is not checked here
        // gdc(a,0) = a
        if (a == 0) {
            return b;
        } else if (b == 0) {
            return a;
        }

        // make a and b odd, keep in mind the common power of twos
        final long aTwos = Long.numberOfTrailingZeros(a);
        a >>= aTwos;
        final long bTwos = Long.numberOfTrailingZeros(b);
        b >>= bTwos;
        final long shift = Math.min(aTwos, bTwos);

        // a and b >0
        // if a > b then gdc(a,b) = gcd(a-b,b)
        // if a < b then gcd(a,b) = gcd(b-a,a)
        // so next a is the absolute difference and next b is the minimum of current values
        while (a != b) {
            final long delta = a - b;
            b = Math.min(a, b);
            a = Math.abs(delta);
            // for speed optimization:
            // remove any power of two in a as b is guaranteed to be odd throughout all iterations
            a >>= Long.numberOfTrailingZeros(a);
        }

        // gcd(a,a) = a, just "add" the common power of twos
        return a << shift;
    }

	public static BigInteger euclidGCF(BigInteger a, BigInteger b) {
		if (a.compareTo(BigInteger.ZERO) == 0)
			return b;
		if (b.compareTo(BigInteger.ZERO) == 0)
			return a;
		if (a.compareTo(b) == 1)
			return euclidGCF(a.mod(b), b);
		if (a.compareTo(b) == -1)
			return euclidGCF(a, b.mod(a));
		return euclidGCF(a.mod(b), b);
	}

	public static long euclidGCF(long a, long b) {
		if (a == 0)
			return b;
		if (b == 0)
			return a;
		if (a > b)
			return euclidGCF(a % b, b);
		if (a < b)
			return euclidGCF(a, b % a);
		return euclidGCF(a % b, b);
	}
}
