package data.matrix;

import java.util.Scanner;

import data.IArray;

public class solve {

	static class mycomp implements MatrixElementComparator<Integer> {

		@Override
		public boolean bigger(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a > b;
		}

		@Override
		public boolean equal(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a == b;
		}

		@Override
		public boolean less(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a < b;
		}

	}

	static class myop implements MatrixElementOperator<Integer> {

		@Override
		public Integer add(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a + b;
		}

		@Override
		public Integer subtract(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a - b;
		}

		@Override
		public Integer multiply(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a * b;
		}

		@Override
		public Integer divide(Integer a, Integer b) {
			// TODO Auto-generated method stub
			return a / b;
		}

		@Override
		public Integer zero() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Integer one() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public Integer max() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
		}

		@Override
		public Integer min() {
			// TODO Auto-generated method stub
			return Integer.MIN_VALUE;
		}

	}

	static class myintarr extends IArray<Integer> {

		private int[] wrap;

		public myintarr(long length) {
			super(length);
			wrap = new int[(int) length];
		}

		@Override
		public Integer get(long index) {
			// TODO Auto-generated method stub
			return wrap[(int) index];
		}

		@Override
		public void set(long index, Integer value) {
			// TODO Auto-generated method stub
			wrap[(int) index] = value;
		}

	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		int m = s.nextInt();
		int n = s.nextInt();
		IArray<Integer>[] coefficients = MatrixOps.matrix(m, n, new myintarr(1));
		IArray<Integer>[] constants = MatrixOps.matrix(m, 1, new myintarr(1));
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				coefficients[i].set(j, s.nextInt());
			}
		}
		for (int i = 0; i < m; i++) {
			constants[i].set(0, s.nextInt());
		}
		MatrixEquation<IArray<Integer>> myeq = new MatrixEquation<>(coefficients, constants);
		MatrixEquationSolution<IArray<Integer>, Integer> sol = MatrixEquationSolution.solve(myeq, new myop(),
				new mycomp());
		while (s.hasNext()) {
			System.out.println("Sol");
			MatrixOps.printMatrix(sol.getNextSolution());
		}

		s.close();
	}

}
