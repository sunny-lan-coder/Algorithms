package data.matrix;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import data.IArray;
import factorization.qs.Test.Converter;

/**
 * Utility matrix functions for Quadratic Sieve. Currently, only supports
 * operations that are needed by QS.
 * 
 * Operates on 0 indexed, non-empty matrices in the form T[row][column]
 * 
 * @author sunny
 *
 */
public class MatrixOps {

	/**
	 * Prints a regular matrix using a custom string adaptor
	 * 
	 * @param matrix
	 *            Matrix to print
	 * @param converter
	 *            Custom string gen
	 */
	public static <T> void printMatrix(IArray<T>[] matrix,
			Converter<T> converter) {
		for (IArray<T> row : matrix) {
			for (int col = 0; col < row.length; col++) {
				System.out.print(String.format("%5d",
						converter.convert(row.get(col))));
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	/**
	 * Prints a regular matrix using tostring
	 * 
	 * @param matrix
	 *            The matrix to print
	 */
	public static <T> void printMatrix(IArray<T>[] matrix) {
		for (IArray<T> row : matrix) {
			for (int col = 0; col < row.length; col++) {
				System.out.print(String.format("%5d", row.get(col)));
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	/**
	 * Prints an irregular matrix using a custom string adaptor
	 * 
	 * @param matrix
	 *            Matrix to print
	 * @param converter
	 *            Custom string gen
	 */
	public static <T> void printMatrix(T[][] matrix, Converter<T> converter) {
		for (T[] row : matrix) {
			for (int col = 0; col < row.length; col++) {
				System.out.print(String.format("%5d",
						converter.convert(row[col])));
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	/**
	 * Prints an irregular matrix using tostring
	 * 
	 * @param matrix
	 *            The matrix to print
	 */
	public static <T> void printMatrix(T[][] matrix) {
		for (T[] row : matrix) {
			for (int col = 0; col < row.length; col++) {
				System.out.print(String.format("%5d", row[col]));
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	/**
	 * Creates a matrix in our format - due to java language badness
	 * 
	 * @param rows
	 * @param cols
	 * @param fallback
	 * @return
	 */
	// TODO noooooooo!!!!! java generics are so bad!
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends IArray<K>, K> T[] matrix(long rows, long cols,
			final T fallback) {
		try {
			Constructor instantiater = fallback.getClass()
					.getDeclaredConstructor(long.class);
			T[] result = (T[]) Array.newInstance(fallback.getClass(),
					(int) rows);
			for (long row = 0; row < rows; row++) {
				result[(int) row] = (T) instantiater.newInstance(cols);
			}
			return result;
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Subtracts one matrix from another
	 * 
	 * @param termA
	 *            The original matrix
	 * @param termB
	 *            The matrix to subtract
	 * @param operator
	 * @return termA after subtracting termB
	 */
	public static <T extends IArray<K>, K> T[] subtract(T[] termA, T[] termB,
			MatrixElementOperator<K> operator) {
		long aCols = termA[0].length;
		long bRows = termB.length;

		long bCols = termB[0].length;
		long aRows = termA.length;

		if (!((aCols == bCols) && (aRows == bRows)))
			throw new IllegalArgumentException(
					"Matrices must have equal dimensions");

		T[] result = matrix(aRows, aCols, termA[0]);

		for (long row = 0; row < aRows; row++) {
			for (long col = 0; col < aCols; col++) {
				result[(int) row].set(col, operator.subtract(
						termA[(int) row].get(col), termB[(int) row].get(col)));
			}
		}

		return result;
	}

	/**
	 * Adds two matrices together
	 * 
	 * @param termA
	 *            The first term
	 * @param termB
	 *            The second term
	 * @param operator
	 * @return The sum
	 */
	public static <T extends IArray<K>, K> T[] add(T[] termA, T[] termB,
			MatrixElementOperator<K> operator) {
		long aCols = termA[0].length;
		long bRows = termB.length;

		long bCols = termB[0].length;
		long aRows = termA.length;

		if (!((aCols == bCols) && (aRows == bRows)))
			throw new IllegalArgumentException(
					"Matrices must have equal dimensions");

		T[] result = matrix(aRows, aCols, termA[0]);

		for (long row = 0; row < aRows; row++) {
			for (long col = 0; col < aCols; col++) {
				result[(int) row].set(col, operator.add(
						termA[(int) row].get(col), termB[(int) row].get(col)));
			}
		}

		return result;
	}

	/**
	 * Multiplies two matrices
	 * 
	 * @param factorA
	 *            The first matrix
	 * @param factorB
	 *            The second matrix
	 * @param operator
	 * @return The product factorA*factorB
	 */
	public static <T extends IArray<K>, K> T[] multiply(T[] factorA,
			T[] factorB, MatrixElementOperator<K> operator) {
		long aCols = factorA[0].length;
		long bRows = factorB.length;
		if (aCols != bRows)
			throw new IllegalArgumentException("Invalid matrice sizes");

		long bCols = factorB[0].length;
		long aRows = factorA.length;

		long cols = bCols;
		long rows = aRows;

		T[] result = matrix(rows, cols, factorA[0]);

		for (long row = 0; row < rows; row++) {
			for (long col = 0; col < cols; col++) {
				result[(int) row].set(
						col,
						VectorOps.dotProduct(factorA[(int) row],
								getCol(factorB, col), operator));
			}
		}

		return result;
	}

	/**
	 * Extracts a column from a matrix
	 * 
	 * @param matrix
	 *            The matrix to extract from
	 * @param col
	 *            The index of the column to extract
	 * @return A vector containing the column
	 */

	public static <T extends IArray<K>, K> T getCol(T[] matrix, long col) {
		long rows = matrix.length;
		T result;
		result = matrix(1, rows, matrix[0])[0];

		for (long row = 0; row < rows; row++) {
			result.set(row, matrix[(int) row].get(col));
		}
		return result;
	}

	/**
	 * Forms an augmented matrix
	 * 
	 * @param matrix
	 *            The left side of the augmented matrix
	 * @param augment
	 *            The right side of the augmented matrix
	 * @return The augmented matrix
	 */
	public static <T extends IArray<K>, K> T[] augment(T[] matrix, T[] augment) {
		long mrows = matrix.length;
		long mcols = matrix[0].length + 1;
		long arows = augment.length;
		long acols = augment[0].length;

		if (acols != 1)
			throw new IllegalArgumentException(
					"Right matrix can only have one column");

		if (mrows != arows)
			throw new IllegalArgumentException(
					"Augment must have same number of rows as matrix");

		T[] result = matrix(mrows, mcols, matrix[0]);

		// copy left matrix into result
		for (long x = 0; x < mrows; x++) {
			for (long y = 0; y < mcols - 1; y++) {
				result[(int) x].set(y, matrix[(int) x].get(y));
			}
		}

		// copy right matrix into result
		for (long x = 0; x < arows; x++) {
			result[(int) x].set(mcols - 1, augment[(int) x].get(0));
		}

		return result;
	}

	/**
	 * Gets the left side of an augmented matrix
	 * 
	 * @param augmentedMatrix
	 *            The augmented matrix
	 * @return The left side of the augmented matrix
	 */
	public static <T extends IArray<K>, K> T[] getAugmentLeft(
			T[] augmentedMatrix) {
		long rows = augmentedMatrix.length;
		long cols = augmentedMatrix[0].length - 1;

		T[] result = matrix(rows, cols, augmentedMatrix[0]);

		// copy left side out
		for (long row = 0; row < rows; row++) {
			for (long col = 0; col < cols; col++) {
				result[(int) row].set(col, augmentedMatrix[(int) row].get(col));
			}
		}

		return result;
	}

	/**
	 * Gets the right side of an augmented matrix
	 * 
	 * @param augmentedMatrix
	 *            The augmented matrix
	 * @return The right side of the augmented matrix
	 */
	public static <T extends IArray<K>, K> T[] getAugmentRight(
			T[] augmentedMatrix) {
		long rows = augmentedMatrix.length;
		long col = augmentedMatrix[0].length - 1;

		T[] result = matrix(rows, 1, augmentedMatrix[0]);

		// copy right side out
		for (long row = 0; row < rows; row++) {
			result[(int) row].set(0, augmentedMatrix[(int) row].get(col));
		}

		return result;
	}

	/**
	 * Converts a matrix to row echelon form using gaussian elimination
	 * 
	 * @param result
	 *            The matrix to convert to row echelon form. If the number of
	 *            columns is one more than the number of rows, the function will
	 *            not solve for those extra columns, but will still apply
	 *            operations
	 * @param operator
	 * @return The input matrix in row echelon form
	 */
	public static <T extends IArray<K>, K> T[] gaussianElimination(T[] matrix,
			MatrixElementOperator<K> operator,
			MatrixElementComparator<K> comparator) {
		long cols = matrix[0].length;
		long rows = matrix.length;

		T[] result = matrix.clone();// TODO deepclone

		long n = Math.min(cols, rows);

		int i, k, max;
		long j;

		T tmp;

		for (i = 0; i < n; ++i) {

			// find pivot row:
			// System.out.print("Finding pivot in column " + i + "...");
			max = i;
			for (j = i + 1; j < rows; ++j) {
				if (comparator.bigger(result[(int) j].get(i),
						result[max].get(i))) {
					max = (int) j;
				}
			}
			// System.out.println("Found pivot at row " + max + ", column " +
			// i);

			// swap row:
			// System.out.println("Swapping rows " + i + " and " + max);
			tmp = result[max];
			result[max] = result[i];
			result[i] = tmp;

			// pivot within:
			// make sure value is not zero:
			// System.out.print("Pivot value is now " + result[i][i] + ", so ");
			if (!comparator.equal(result[i].get(i), operator.zero())) {
				// System.out.println("it can be used");

				// make sure all values above i are 0:
				for (k = 0; k < i; ++k) {
					// skip already values that are already 0:
					if (!comparator.equal(result[k].get(i), operator.zero())) {
						K factor = operator.divide(result[k].get(i),
								result[i].get(i));
						// System.out.println("Multiplying row " + i + " by "
						// + result[k][i] + "/" + result[i][i] + "="
						// + factor);
						// System.out.println("Subtracting row " + i
						// + " from row " + k);
						for (j = cols - 1; j >= i; --j) {
							result[k].set(j,
									operator.subtract(
											result[k].get(j),
											operator.multiply(factor,
													result[i].get(j))));
						}
						// System.out.println("Restoring row " + i);
					}
				}

				// make sure all values under i are 0:
				for (k = i + 1; k < rows; ++k) {
					// skip values that are already 0:
					if (!comparator.equal(result[k].get(i), operator.zero())) {
						K factor = operator.divide(result[k].get(i),
								result[i].get(i));
						// System.out.println("Multiplying row " + i + " by "
						// + result[k][i] + "/" + result[i][i] + "="
						// + factor);
						// System.out.println("Subtracting row " + i
						// + " from row " + k);
						for (j = cols - 1; j >= i; --j) {
							result[k].set(j,
									operator.subtract(
											result[k].get(j),
											operator.multiply(factor,
													result[i].get(j))));
						}
						// System.out.println("Restoring row " + i);
					}
				}

				// make sure pivot is 1:
				if (!comparator.equal(result[i].get(i), operator.one())) {
					// System.out.println("Multiplying row " + i + " by 1/"
					// + result[i][i]);
					for (j = cols - 1; j >= i; --j) {
						result[i].set(
								j,
								operator.divide(result[i].get(j),
										result[i].get(i)));
					}
				}

			} else {
				// System.out.println("it cannot be used");
			}
		}

		return result;
	}
}
