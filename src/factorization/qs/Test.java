package factorization.qs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import data.IArray;
import data.matrix.MatrixElementComparator;
import data.matrix.MatrixElementOperator;
import data.matrix.MatrixEquation;
import data.matrix.MatrixEquationSolution;
import data.matrix.MatrixOps;
import factorization.brute.PrimeFactorizerBrute;
import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;

public class Test {
	public static class DoubleArrayWrapper extends IArray<Double> {
		private double[] inner;

		public DoubleArrayWrapper(long length) {
			super(length);
			inner = new double[(int) length];
		}

		@Override
		public Double get(long index) {
			return inner[(int) index];
		}

		@Override
		public void set(long index, Double value) {
			inner[(int) index] = value;
		}

	}

	public static class DoubleComparator implements
			MatrixElementComparator<Double> {

		@Override
		public boolean bigger(Double a, Double b) {
			return a > b;
		}

		@Override
		public boolean equal(Double a, Double b) {
			return a.equals(b); // lol i didn't know this simple error
								// (a==b) != a.equals(b)
								// ew! java wrapper types !!!!!!
		}

		@Override
		public boolean less(Double a, Double b) {
			return a < b;
		}

	}

	public static class DoubleOperator implements MatrixElementOperator<Double> {

		@Override
		public Double add(Double a, Double b) {
			return a + b;
		}

		@Override
		public Double subtract(Double a, Double b) {
			return a - b;
		}

		@Override
		public Double multiply(Double a, Double b) {
			return a * b;
		}

		@Override
		public Double divide(Double a, Double b) {
			return a / b;
		}

		@Override
		public Double zero() {
			return 0.0d;
		}

		@Override
		public Double one() {
			return 1d;
		}

		@Override
		public Double max() {
			return Double.MAX_VALUE;
		}

		@Override
		public Double min() {
			return Double.MIN_VALUE;
		}
	}

	public interface Converter<T> {
		public String convert(T v);

		public T convert(String v);
	}

	public static <T> void printMatrix(IArray<T>[] matrix,
			Converter<T> converter) {
		for (IArray<T> row : matrix) {
			for (int col = 0; col < row.length; col++) {
				System.out.print(converter.convert(row.get(col)));
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	public static <K, T extends IArray<K>> T[] readMatrix(
			Converter<K> converter, T fallback) {
		dpln("Enter m by n sized matrix...");
		dp("m=");
		long rows = dpin.nextLong();
		dp("n=");
		long cols = dpin.nextLong();
		return readMatrix(rows, cols, converter, fallback);
	}

	public static <K, T extends IArray<K>> T[] readMatrix(long rows, long cols,
			Converter<K> converter, T fallback) {
		dpln("Enter " + rows + " by " + cols + " matrix:");
		T[] result = MatrixOps.matrix(rows, cols, fallback);
		for (long row = 0; row < rows; row++) {
			String[] vals = dpin.next().split(",");
			for (long col = 0; col < cols; col++) {
				result[(int) row].set(col, converter.convert(vals[(int) col]));
			}
		}
		return result;
	}

	static void refTest() {
		long[][] a = new long[5][1];
		long[][] b = new long[5][];
		for (int i = 0; i < 5; i++) {
			a[i][0] = i;
			b[i] = a[i];
		}

		for (long[] r : a) {
			for (long e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}

		Arrays.fill(b, new long[] { 0 });

		b[3][0] = 3445;
		for (long[] r : a) {
			for (long e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}
	}

	public static class DoubleConverter implements Converter<Double> {

		@Override
		public String convert(Double v) {
			return v.toString();
		}

		@Override
		public Double convert(String v) {
			return Double.valueOf(v);
		}

	}

	static void QSTest() throws PrimeFactoringException {
		long start;
		long stop;
		long num = 965210765151315827l;

		IPrimeFactorizer s;

		s = new QuadraticSieve();
		s.setNumber(num);
		start = System.nanoTime();
		s.factorize();
		stop = System.nanoTime();
		System.out.println("find took " + ((stop - start) / 1000000) + " ms");
		ArrayList<Long> result = s.getFactors();
		for (long f : result) {
			System.out.print("(" + f + ")");
		}
		System.out.println();

		s = new PrimeFactorizerBrute();
		s.setNumber(num);
		start = System.nanoTime();
		s.factorize();
		stop = System.nanoTime();
		System.out.println("find took " + ((stop - start) / 1000000) + " ms");
		result = s.getFactors();
		for (long f : result) {
			System.out.print("(" + f + ")");
		}
		System.out.println();
	}

	public static void solver() {
		Converter<Double> converter = new DoubleConverter();
		MatrixElementOperator<Double> operator = new DoubleOperator();
		MatrixElementComparator<Double> comparator = new DoubleComparator();
		DoubleArrayWrapper fallback = new DoubleArrayWrapper(1);
		dpln("enter coefficients:");
		DoubleArrayWrapper[] vals = readMatrix(converter, fallback);
		dpln("enter constants:");
		DoubleArrayWrapper[] cons = readMatrix(vals.length, 1, converter,
				fallback);
		MatrixEquation<IArray<Double>> eq = new MatrixEquation<IArray<Double>>(
				vals, cons);
		MatrixEquationSolution<IArray<Double>, Double> solution = MatrixEquationSolution
				.solve(eq, operator, comparator);
		dpln("Solution:");
		printMatrix(solution.getNextSolution(), converter);
	}

	public static void main(String[] args) throws Exception {
		dpin = new Scanner(System.in);
		solver();
		dpin.close();
	}

	public static final boolean debugging = true;

	public static void dp(Object s) {
		if (debugging) {
			System.out.print(s);
		}
	}

	public static void dpln(Object s) {
		if (debugging) {
			System.out.println(s);
		}
	}

	public static void dpln() {
		if (debugging) {
			System.out.println();
		}
	}

	static Scanner dpin;

}