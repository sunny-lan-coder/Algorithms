package sets;

public class Combination {
	public Combination(char[] set, int size) {
		src = set;
		dst = new char[size];
	}

	private char[] src;
	private char[] dst;
	private int sI = 0;
	private int dI = 0;

	public void findComb() {
		if (dst.length == dI) {
			// result found
			for (char c : dst) {
				System.out.print(c);
			}
			System.out.println();
			return;
		}
		if (sI < src.length) {
			dst[dI] = src[sI];
			sI++;
			dI++;
			findComb();
			dI--;
			findComb();
			sI--;
		}
	}

	public static void main(String[] args) {
		Combination c = new Combination(new char[] { 'A', 'B', 'C', 'D', 'E' },
				3);
		c.findComb();
	}

}
