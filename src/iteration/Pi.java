package iteration;

import java.math.BigDecimal;

import data.bigdecimal.BigDecimalMath;

public class Pi {

	public static BigDecimal iterate(long iterations) {
		BigDecimal two = BigDecimal.valueOf(2);
		BigDecimal one = BigDecimal.ONE;
		BigDecimal yPrev = BigDecimal.valueOf(Math.sqrt(2));
		BigDecimal y;
		BigDecimal zPrev = BigDecimal.valueOf(Math.pow(2, 1 / 4));
		BigDecimal z;
		BigDecimal piPrev = yPrev.add(two);
		BigDecimal pi = null;
		BigDecimal ySqrt;
		BigDecimal yA1;
		BigDecimal zA1;
		for (long i = 0; i < iterations; i++) {
			yA1 = one.add(yPrev);
			zA1 = one.add(zPrev);
			ySqrt = BigDecimalMath.sqrt(yPrev);
			y = BigDecimalMath.divideRound(yA1, two.multiply(ySqrt));
			z = BigDecimalMath.divideRound(one.add(yPrev.multiply(zPrev)),
					zA1.multiply(ySqrt));
			pi = piPrev.multiply(BigDecimalMath.divideRound(yA1, zA1));

			yPrev = y;
			zPrev = z;
			piPrev = pi;
		}
		return pi;
	}
}
