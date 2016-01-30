package primes.fermatslittletheorem.repeatingsub;

import java.util.Scanner;

public class Brute {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		int N = s.nextInt();
		s.close();

		int lim = 1 << N;

		int mask = 0;

		for (int i = 0; i < N; i++) {
			mask |= 1 << i;
		}

		int count = 0;

		boolean[] dp = new boolean[lim];
		int[] ref = new int[lim];
		for (int i = 0; i < lim; i++) {
			System.out.print(Integer.toBinaryString(i));
			if (!dp[i]) {
				System.out.println(" is unique");
				count++;
				int tmp = i;
				for (int j = 0; j < N; j++) {
					dp[tmp] = true;
					ref[tmp] = i;
					tmp = tmp << 1;
					tmp |= tmp >> N;
					tmp = tmp & mask;
				}
			} else {
				System.out.println(" is a copy of " + Integer.toBinaryString(ref[i]));
			}
		}

		System.out.println(count);
	}

}
