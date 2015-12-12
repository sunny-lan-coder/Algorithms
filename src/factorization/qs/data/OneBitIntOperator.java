package factorization.qs.data;

import data.matrix.MatrixElementOperator;

/**
 * An operator for one bit (modulus 2) integers
 * 
 * @author sunny
 *
 */
public class OneBitIntOperator implements MatrixElementOperator<Boolean> {

	@Override
	public Boolean add(Boolean a, Boolean b) {
		return a ^ b;
	}

	@Override
	public Boolean subtract(Boolean a, Boolean b) {
		return a ^ b;
	}

	@Override
	public Boolean multiply(Boolean a, Boolean b) {
		// TODO Auto-generated method stub
		return a && b;
	}

	@Override
	public Boolean divide(Boolean a, Boolean b) {
		if (!b)
			throw new ArithmeticException("Cannot divide by 0");
		return a && b;
	}

	@Override
	public Boolean zero() {
		return false;
	}

	@Override
	public Boolean one() {
		return true;
	}

	@Override
	public Boolean max() {
		return true;
	}

	@Override
	public Boolean min() {
		return false;
	}
}
