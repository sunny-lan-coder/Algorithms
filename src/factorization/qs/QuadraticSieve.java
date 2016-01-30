package factorization.qs;

import java.math.BigInteger;
import java.util.ArrayList;

import misc.CF;
import primes.CachingBrutePrimeFinder;
import primes.core.IPrimeFinder;
import data.BigBooleanArray;
import data.matrix.MatrixElementComparator;
import data.matrix.MatrixElementOperator;
import data.matrix.MatrixEquation;
import data.matrix.MatrixEquationSolution;
import data.matrix.MatrixOps;
import factorization.brute.PrimeFactorizerBrute;
import factorization.core.IPrimeFactorizer;
import factorization.core.PrimeFactoringException;
import factorization.qs.data.OneBitIntComparator;
import factorization.qs.data.OneBitIntOperator;
import factorization.qs.data.SquareDifference;

/**
 * Quadratic sieve naive implementation - 30 digits? Works best with semiprimes
 * (numbers with 2 prime factors).
 * 
 * @author sunny
 *
 */
public class QuadraticSieve implements IPrimeFactorizer {

	public IPrimeFactorizer defaultFactorizer = null;
	public IPrimeFinder primeSource = null;

	private ArrayList<Long> factors;

	/**
	 * Number that is being factored/to factor
	 */
	private long composite;

	// variables
	/**
	 * The size of a interval in the sieving stage
	 */
	public static int blockSize = 10000;

	@Override
	public void setNumber(long number) {
		if (primeSource == null) {
			primeSource = new CachingBrutePrimeFinder();
		}
		if (defaultFactorizer == null) {
			defaultFactorizer = new PrimeFactorizerBrute();
			((PrimeFactorizerBrute) defaultFactorizer).primeSource = primeSource;
		}
		composite = number;
		factors = new ArrayList<Long>();
	}

	@Override
	public void factorize() throws PrimeFactoringException {

		Test.dpln("factorizing " + composite + "...");

		MatrixElementOperator<Boolean> op = new OneBitIntOperator();
		MatrixElementComparator<Boolean> comp = new OneBitIntComparator();

		// initialize params
		long a = (long) Math.ceil(Math.sqrt(composite));
		long c = (long) Math.ceil(Math.sqrt(composite * 2)) + 1;
		long b = (int) Math.pow(
				Math.pow(
						Math.E,
						Math.sqrt(Math.log1p(composite)
								* Math.log1p(Math.log1p(composite)))),
				(3 * Math.sqrt(2)) / 4);
		long bOffset = 10;
		bOffset += b;
		b = (int) (b / Math.log1p(b));

		ArrayList<SquareDifference> squareDifs;
		long numDifs;
		{
			Test.dp("generating squaredifs between " + a + " and " + c + "...");

			squareDifs = new ArrayList<SquareDifference>();
			for (; a < c; a++) {
				SquareDifference s = new SquareDifference(a, composite);
				squareDifs.add(s);
			}
			numDifs = squareDifs.size();

			Test.dpln("done");
		}

		ArrayList<Long> tmpDifferences;
		{
			tmpDifferences = new ArrayList<Long>();
			Test.dp("copying square difs...");
			for (SquareDifference sq : squareDifs) {
				tmpDifferences.add(sq.value);
			}
			Test.dpln("done");
		}

		Long[] primeIndices;
		Long[] baseIndices;
		{
			Test.dp("finding quadratic residue base indexes under "
					+ primeSource.getNthPrime(b) + "...");
			primeIndices = new Long[0];
			baseIndices = new Long[0];
			ArrayList<Long> primeIndicesT = new ArrayList<Long>((int) b);
			ArrayList<Long> baseIndicesT = new ArrayList<Long>((int) b);
			for (long pI = 0; pI < b; pI++) {
				long p = primeSource.getNthPrime(pI);
				if (p > numDifs)
					break;
				for (long i = 0; i < p; i++) {
					if (tmpDifferences.get((int) i) % p == 0) {
						primeIndicesT.add(pI);
						baseIndicesT.add(i);
					}
				}
			}
			primeIndices = primeIndicesT.toArray(primeIndices);
			baseIndices = baseIndicesT.toArray(baseIndices);

			Test.dpln("done");
		}

		BigBooleanArray[] U;
		ArrayList<SquareDifference> smoothDifs = new ArrayList<SquareDifference>();
		long numSmooth;
		{
			Test.dpln("sieving for " + bOffset + " smooth numbers ("
					+ (b * ((bOffset / 8) + (bOffset * 4) + 4))
					+ " bytes needed)...");

			U = MatrixOps.matrix(b, bOffset, new BigBooleanArray(1));
			long pIS = primeIndices.length;
			long blocks = (long) Math.ceil(numDifs / (double) blockSize);
			long blockend = 0;
			long blockOffset = 0;
			numSmooth = 0;

			// TODO implement
			outer: for (long blockIndex = 0; blockIndex < blocks; blockIndex++) {
				blockend = Math.min(numDifs, blockOffset + blockSize);
				Test.dp("	sieving block " + blockOffset + " - " + blockend
						+ "...");
				BigBooleanArray[] factorizations = MatrixOps.matrix(b,
						blockSize, new BigBooleanArray(1));
				for (long iI = 0; iI < pIS; iI++) {
					long pI = primeIndices[(int) iI];
					long p = primeSource.getNthPrime(pI);
					BigBooleanArray primeSeq = factorizations[(int) pI];
					for (long sdI; (sdI = baseIndices[(int) iI]) < blockend; baseIndices[(int) iI] += p) {

						Long v = tmpDifferences.get((int) sdI);
						// make sure isn't already one
						boolean flag = false;
						while (v % p == 0) {
							flag = true;
							v = v / p;
							factorizations[(int) pI].set(
									sdI - blockOffset,
									op.add(primeSeq.get(sdI - blockOffset),
											op.one()));

						}
						tmpDifferences.set((int) sdI, v);
						if (flag && v == 1) {
							smoothDifs.add(squareDifs.get((int) sdI));
							for (int row = 0; row < b; row++) {
								U[row].set(
										numSmooth,
										factorizations[row].get(sdI
												- blockOffset));
							}
							numSmooth++;
							if (numSmooth > bOffset)
								break outer;
						}
					}
				}

				blockOffset = blockend;

				Test.dpln("done. currently " + numSmooth + " smooths");
			}

			Test.dpln("done");
		}

		// Test.dpln("smooth numbers:");
		// for (int col = 0; col < numSmooth; col++) {
		// Test.dp(smoothDifs.get(col));
		// Test.dp(" : ");
		// for (int row = 0; row < b; row++) {
		// if (!U[row].get(col)) {
		// Test.dp("0");
		// } else {
		// Test.dp("1");
		// }
		// Test.dp(" ");
		// }
		// Test.dpln();
		//
		// }
		// Test.dpln("end");

		MatrixEquationSolution<BigBooleanArray, Boolean> solution;

		{
			Test.dp("finding null space of generated equation...");

			BigBooleanArray[] constants = MatrixOps.matrix(U.length, 1, U[0]);

			MatrixEquation<BigBooleanArray> myEq = new MatrixEquation<BigBooleanArray>(
					U, constants);
			solution = MatrixEquationSolution.solve(myEq, op, comp);

			Test.dpln("done");
		}

		{
			Test.dp("searching for non-trivial solution...");

			BigBooleanArray[] cs;

			// look for non-zero solution
			outer: while (true) {
				cs = solution.getNextSolution();
				for (int row = 0; row < cs.length; row++) {
					if (cs[row].get(0)) {
						// found one:
						BigInteger S = BigInteger.ONE;
						for (int row2 = 0; row2 < cs.length; row2++) {
							if (cs[row2].get(0)) {
								S = S.multiply(BigInteger.valueOf(smoothDifs
										.get(row2).value));
								Test.dp("(" + smoothDifs.get(row2).value + ")");
							}
						}
						Test.dp(" = " + S);
						BigInteger SRoot = data.bigdecimal.BigIntegerMath.isqrt(S);
						Test.dp(" | " + SRoot + "^2 = ");
						Test.dpln(SRoot.pow(2));
						if (SRoot.pow(2).compareTo(S) != 0) {
							// throw new PrimeFactoringException(
							// /
							// "QS FATAL ERROR - square subset was not square!");
						}

						BigInteger A = BigInteger.ONE;
						for (int row2 = 0; row2 < cs.length; row2++) {
							if (cs[row2].get(0)) {
								A = A.multiply(BigInteger.valueOf(smoothDifs
										.get(row2).a));
							}
						}

						BigInteger f1 = CF.euclidGCF(
								BigInteger.valueOf(composite),
								A.subtract(SRoot));
						BigInteger f2 = CF.euclidGCF(
								BigInteger.valueOf(composite), A.add(SRoot));
						if (f1.compareTo(BigInteger.ONE) == 0
								|| f1.compareTo(BigInteger.valueOf(composite)) == 0)
							continue outer;

						factors.add(f1.longValue());
						factors.add(f2.longValue());

						break outer;
					}
				}
			}

			Test.dpln("done");
		}
	}

	@Override
	public ArrayList<Long> getFactors() {
		return factors;
	}

	@Override
	public void gc() {
	}
}
