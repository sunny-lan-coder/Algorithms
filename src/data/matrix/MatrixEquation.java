package data.matrix;

import data.IArray;

/**
 * Container class for a non-empty matrix equation
 * 
 * @author sunny
 *
 */
public class MatrixEquation<T extends IArray<?>> {
	public T[] coefficients;
	public T[] constants;

	public MatrixEquation(long numberVariables, T[] coefficients,
			T[] constants) {
		long mcols = coefficients[0].length;
		long mrows = coefficients.length;
		long cnum = constants.length;
		if (mcols != numberVariables)
			throw new IllegalArgumentException(
					"Number of variables must be equal to the number of columns in the coefficients' matrix");
		if (mrows != cnum)
			throw new IllegalArgumentException(
					"Number of rows in the coefficients' matrix must be equal to the number of constants");

		this.coefficients = coefficients;
		this.constants = constants;
	}

	/**
	 * Same constructor, except infers the number of variables
	 * 
	 * @param coefficients
	 *            The coefficients' matrix
	 * @param constants
	 *            The constants' matrix/vector
	 */
	public MatrixEquation(T[] coefficients, T[] constants) {
		this(coefficients[0].length, coefficients, constants);
	}
}
