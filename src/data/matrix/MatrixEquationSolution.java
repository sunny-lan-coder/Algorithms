package data.matrix;

import data.IArray;

/**
 * Container for a solution to a matrix equation, allowing for free variables,
 * and such. Can only be instantiated from the factory method,
 * 
 * static <T> MatrixEquationSolution<T> solve(MatrixEquation<T> equation).
 * 
 * @author sunny
 *
 * @param <T>
 *            The type of the matrix
 */
public class MatrixEquationSolution<T extends IArray<K>, K> {
	private MatrixElementOperator<K> operator;
	private MatrixElementComparator<K> comparator;
	private T[] constants;
	private T[] coefficients;
	private T[] multiplier;
	private T[] pivotMults;

	/**
	 * The values of the free variables
	 */
	public final T[] freeMults;

	private MatrixEquationSolution(T[] constants, T[] coefficients,
			T[] multiplier, T[] freeMults, T[] pivotMults,
			MatrixElementOperator<K> operator,
			MatrixElementComparator<K> comparator) {

		if (constants[0].length != 1)
			throw new IllegalArgumentException(
					"Constants' matrix can only have one column");

		if (multiplier[0].length != 1)
			throw new IllegalArgumentException(
					"Multipliers' can only have one column");

		if (freeMults.length > 0)
			if (freeMults[0].length != 1)
				throw new IllegalArgumentException(
						"Free matrix can only have one column");

		if (pivotMults.length > 0)
			if (pivotMults[0].length != 1)
				throw new IllegalArgumentException(
						"Pivot matrix can only have one column");

		if (coefficients.length != coefficients.length)
			throw new IllegalArgumentException(
					"Constants' matrix must have the same number of rows as the coefficients' matrix");

		if (multiplier.length != coefficients[0].length)
			throw new IllegalArgumentException(
					"Multiplier matrix must have same number of rows as columns in the coefficient's matrix");

		if (freeMults.length > multiplier.length)
			throw new IllegalArgumentException(
					"Free matrix cannot have more rows than multiplier matrix");

		if (pivotMults.length > multiplier.length)
			throw new IllegalArgumentException(
					"Free matrix cannot have more rows than multiplier matrix");

		this.constants = constants;
		this.coefficients = coefficients;
		this.multiplier = multiplier;
		this.freeMults = freeMults;
		this.operator = operator;
		this.comparator = comparator;
		this.pivotMults = pivotMults;

		for (int i = 0; i < freeMults.length; i++) {
			freeMults[i].set(0, operator.zero());
		}
	}

	/**
	 * Returns the next solution out of all possible solution sets
	 * 
	 * @return A new solution, unless all solutions have been returned already
	 */
	public T[] getNextSolution() {
		incrementRow(0);
		return getSolution();
	}

	// public double getPossibleSolutions(){
	// TODO implement
	// }

	// recursive increment set of free column multipliers
	private void incrementRow(int rowIndex) {
		if (rowIndex >= freeMults.length)
			return;
		if (rowIndex < 0)
			return;

		if (comparator.equal(freeMults[rowIndex].get(0), operator.max())) {
			freeMults[rowIndex].set(0, operator.min());
			incrementRow(rowIndex + 1);
		} else {
			freeMults[rowIndex].set(0,
					operator.add(freeMults[rowIndex].get(0), operator.one()));
		}
	}

	/**
	 * Skips the current solution, goes to next
	 */
	public void incrementSolution() {
		incrementRow(0);
	}

	/**
	 * 
	 * @param freeVariables
	 *            A set of values for the free variables
	 * @return A solution based on the values of the free variables
	 */
	public T[] getSolution() {

		// reset pivot column multipliers
		for (int i = 0; i < pivotMults.length; i++) {
			pivotMults[i].set(0, operator.zero());
		}

		T[] r1 = MatrixOps.multiply(coefficients, multiplier, operator);

		T[] r2 = MatrixOps.subtract(constants, r1, operator);

		// preserve free column multipliers:
		T[] freeMultsP = null;
		if (freeMults.length > 0) {
			freeMultsP = MatrixOps.matrix(freeMults.length, 1, freeMults[0]);
		}
		
		for (int row = 0; row < freeMults.length; row++) {
			freeMultsP[row].set(0, freeMults[row].get(0));
		}

		for (int row = 0; row < Math.min(multiplier.length, r2.length); row++) {
			multiplier[row].set(0, r2[row].get(0));
		}

		// restore free column multipliers:
		for (int row = 0; row < freeMults.length; row++) {
			freeMults[row].set(0, freeMultsP[row].get(0));
		}

		return multiplier;
	}

	/**
	 * Factory method for {@link MatrixEquationSolution}
	 * 
	 * @param equation
	 *            The equation to solve
	 * @param operator
	 *            The operator for the equation data type
	 * @param comparator
	 *            The comparator for the equation data type
	 * @return The solved equation, data stored in a
	 *         {@link MatrixEquationSolution} object
	 */
	public static <T extends IArray<K>, K> MatrixEquationSolution<T, K> solve(
			MatrixEquation<T> equation, MatrixElementOperator<K> operator,
			MatrixElementComparator<K> comparator) {
		// System.out.println("rows=" + equation.coefficients.length + ",cols="
		// + equation.coefficients[0].length);

		// perform gaussian reduction on input matrix:
		T[] augmented = MatrixOps.augment(equation.coefficients,
				equation.constants);
		T[] reducedMatrix = MatrixOps.gaussianElimination(augmented, operator,
				comparator);

		T[] reducedCoefficients = MatrixOps.getAugmentLeft(reducedMatrix);
		T[] reducedConstants = MatrixOps.getAugmentRight(reducedMatrix);

		// Count free and pivot columns:
		int freeCount = 0;
		int pivotCount = 0;
		for (int pivotIndex = 0; pivotIndex < reducedCoefficients[0].length; pivotIndex++) {
			if (pivotIndex >= reducedCoefficients.length) {
				freeCount++;
				continue;
			}
			if (comparator.equal(
					reducedCoefficients[pivotIndex].get(pivotIndex),
					operator.zero())) {
				freeCount++;
			} else {
				pivotCount++;
			}
		}

		// create object reference map of pivot and free column multipliers to a
		// single multiplier matrix:

		// System.out.println("m=" + reducedCoefficients[0].length);
		T[] multiplier = MatrixOps.matrix(reducedCoefficients[0].length, 1,
				reducedCoefficients[0]);

		T[] freeMults = MatrixOps.matrix(freeCount, 1, reducedCoefficients[0]);

		T[] pivotMults = MatrixOps
				.matrix(pivotCount, 1, reducedCoefficients[0]);

		int freeMI = 0;
		int pivotMI = 0;
		for (int pivotIndex = 0; pivotIndex < reducedCoefficients[0].length; pivotIndex++) {
			if (pivotIndex >= reducedCoefficients.length) {
				freeMults[freeMI] = multiplier[pivotIndex];
				freeMI++;
				continue;
			}
			if (comparator.equal(
					reducedCoefficients[pivotIndex].get(pivotIndex),
					operator.zero())) {
				freeMults[freeMI] = multiplier[pivotIndex];
				freeMI++;
			} else {
				pivotMults[pivotMI] = multiplier[pivotIndex];
				pivotMI++;
			}
		}

		return new MatrixEquationSolution<T, K>(reducedConstants,
				reducedCoefficients, multiplier, freeMults, pivotMults,
				operator, comparator);
	}
}
