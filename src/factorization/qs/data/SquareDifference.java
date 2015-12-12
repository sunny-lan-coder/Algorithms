package factorization.qs.data;

public class SquareDifference {
	public final long a;
	public final long value;

	public SquareDifference(long a, long n) {
		this.a = a;
		value = (long) (Math.pow(a, 2) - n);
	}

	@Override
	public String toString() {
		return a + "^2 - n = " + value;
	}
}
