package sets;
import java.util.Scanner;

public class Subset {

	public static void main(String[] args) {

		Scanner s = new Scanner(System.in);
		String input = s.nextLine();

		s.close();

		char set[] = new char[input.length()];
		for (int i = 0; i < set.length; i++) {
			set[i] = input.charAt(i);
		}
		int range = 2 << set.length - 1;

		for (int i = 1; i < range; i++) {
			int tmp = i;
			System.out.print("{");
			for (int cI = 0; cI < set.length; cI++) {
				int v = tmp & 1;
				if (v == 1) {
					System.out.print(set[cI] + " ");
				}
				tmp = tmp >> 1;
			}
			System.out.println("}");
		}

	}
}
