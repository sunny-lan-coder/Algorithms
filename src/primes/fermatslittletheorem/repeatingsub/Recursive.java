package primes.fermatslittletheorem.repeatingsub;

import java.util.Scanner;

public class Recursive {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		int N = s.nextInt();
		s.close();
		System.out.println(f(2, N));
	}

	public static int f(int a, int p) {
		for (int i = 2; i < Math.min(p, Math.sqrt(p) + 1); i++) {
			if (p % i == 0) {
				int sublen = i;
				int subcnt = p / i;
				return f((int) (((Math.pow(a, sublen) - a) / sublen) + a), subcnt);
			}
		}
		return (int) (((Math.pow(a, p) - a) / p) + a);
	}

}
