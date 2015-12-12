package data.matrix;

/**
 * Operators on numerical objects. Used for QS matrices, may have some arbitrary
 * stuffs inside. :)
 * 
 * @author sunny
 *
 * @param <T>
 *            Type of object operated on
 */
public interface MatrixElementOperator<T> {
	public T add(T a, T b);

	public T subtract(T a, T b);

	public T multiply(T a, T b);

	public T divide(T a, T b);

	public T zero();

	public T one();

	/**
	 * 
	 * @return A number n such that (n>m:m is any member of R)
	 */
	public T max();

	/**
	 * 
	 * @return A number n such that (n<m:m is any member of R)
	 */
	public T min();
}
