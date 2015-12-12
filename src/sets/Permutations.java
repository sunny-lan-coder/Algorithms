package sets;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Permutations {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		items = new ArrayList<Item>();
		curState = new Stack<Item>();
		System.out.print("n:");
		int n = s.nextInt();
		for (int i = 0; i < n; i++) {
			items.add(new Item(s.next().charAt(0)));
		}
		s.close();

		taken = new boolean[items.size()];
		dislike = new ArrayList[items.size()];

		for (int i = 0; i < n; i++) {
			System.out.println(i);
			while (true) {
				String input = s.next();
				if (input == "")
					break;
				dislike[i].add(new Item(input.charAt(0)));
			}
		}

		solve();
	}

	static ArrayList<Item> items;
	static boolean[] taken;
	static Stack<Item> curState;
	static int n = 0;
	static ArrayList<Item>[] dislike;

	public static void solve() {
		if (n == items.size()) {
			for (Item i : curState) {
				System.out.print(i);
			}
			System.out.println();
			return;
		}
		for (int i = 0; i < items.size(); i++) {
			if ((!taken[i]) && (!(dislike[i].contains(items.get(i))))) {
				curState.push(items.get(i));
				taken[i] = true;
				solve();
				taken[i] = false;
				curState.pop();
			}
		}
	}

	static class Item {
		public Item(char c) {
			letter = c;
		}

		char letter;

		public boolean equals(Object o) {
			if (o instanceof Item) {
				if (((Item) o).letter == letter)
					return true;
			}
			return false;
		}

		public String toString() {
			return Character.toString(letter);
		}
	}
}
