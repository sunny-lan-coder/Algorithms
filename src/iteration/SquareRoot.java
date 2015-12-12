package iteration;

public class SquareRoot {

	public static double sqrt(double n, long iterations) {
		double x = n / 2;
		double prev = 0;
		for (; x * x != n && iterations >= 0 && prev != x;) {
			x = (x + (n / x)) / 2;
			iterations--;
			prev = x;
		}
		return x;
	}

}
