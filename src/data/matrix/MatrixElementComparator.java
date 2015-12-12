package data.matrix;

public interface MatrixElementComparator<T> {
	/**
	 * 
	 * @param a
	 * @param b
	 * @return true if a>b
	 */
	public boolean bigger(T a, T b);

	/**
	 * 
	 * @param a
	 * @param b
	 * @return true if a=b
	 */
	public boolean equal(T a, T b);

	/**
	 * 
	 * @param a
	 * @param b
	 * @return true if a<b
	 */
	public boolean less(T a, T b);
}
