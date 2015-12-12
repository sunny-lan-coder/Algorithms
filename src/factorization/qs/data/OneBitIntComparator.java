package factorization.qs.data;

import data.matrix.MatrixElementComparator;

/**
 * A comparator for one bit (modulus 2) integers
 * 
 * @author sunny
 *
 */
public class OneBitIntComparator implements MatrixElementComparator<Boolean> {

	@Override
	public boolean bigger(Boolean a, Boolean b) {
		return a == true && b == false;
	}

	@Override
	public boolean equal(Boolean a, Boolean b) {
		return a == b;
	}

	@Override
	public boolean less(Boolean a, Boolean b) {
		return b = true && a == false;
	}

}
