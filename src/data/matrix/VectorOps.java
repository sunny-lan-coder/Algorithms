package data.matrix;

import data.IArray;

/**
 * Small vector utility class for {@link MatrixOps}. Operates on 0 indexed,
 * non-empty vectors in the form T[index]
 * 
 * @author sunny
 *
 */
public class VectorOps {
	public static <T extends IArray<K>, K> K dotProduct(T a, T b,
			MatrixElementOperator<K> operator) {
		if (a.length != b.length)
			throw new IllegalArgumentException("Vectors must be same size");
		K total = operator.zero();
		for (int index = 0; index < a.length; index++) {
			total = operator.add(operator.multiply(a.get(index), b.get(index)),
					total);
		}
		return total;
	}
}
