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

	// private int[] BlockLanczos(int[][] matrixB, int matrixBlength) {
	// int i, j, k;
	// int oldDiagonalSSt, newDiagonalSSt;
	// int index, indexC, mask;
	// int[] matrixD = new int[32];
	// int[] matrixE = new int[32];
	// int[] matrixF = new int[32];
	// int[] matrixWinv = new int[32];
	// int[] matrixWinv1 = new int[32];
	// int[] matrixWinv2 = new int[32];
	// int[] matrixVtV0 = new int[32];
	// int[] matrixVt1V0 = new int[32];
	// int[] matrixVt2V0 = new int[32];
	// int[] matrixVtAV = new int[32];
	// int[] matrixVt1AV1 = new int[32];
	// int[] matrixAV = new int[matrixBlength];
	// int[] matrixCalcParenD = new int[32];
	// int[] vectorIndex = new int[64];
	// int[] matrixV = new int[matrixBlength];
	// int[] matrixV1 = new int[matrixBlength];
	// int[] matrixV2 = new int[matrixBlength];
	// int[] matrixXmY = new int[matrixBlength];
	// // Matrix that holds temporary data
	// int[] matrixCalc3 = new int[matrixBlength];
	// int[] matrixTemp;
	// int[] matrixCalc1 = new int[32]; // Matrix that holds temporary data
	// int[] matrixCalc2 = new int[32]; // Matrix that holds temporary data
	// int[] matr;
	// int rowMatrixV;
	// int rowMatrixXmY;
	// long seed;
	// int Temp, Temp1;
	// int stepNbr = 0;
	// int currentOrder, currentMask;
	// int row, col;
	// int leftCol, rightCol;
	// int minind, min, minanswer;
	// int[] rowMatrixB;
	//
	// newDiagonalSSt = oldDiagonalSSt = -1;
	//
	// /* Initialize matrix X-Y and matrix V_0 with random data */
	// seed = 123456789L;
	// for (i = matrixXmY.length - 1; i >= 0; i--) {
	// matrixXmY[i] = (int) seed;
	// seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	// matrixXmY[i] += (int) (seed * 6543265L);
	// seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	// matrixV[i] = (int) seed;
	// seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	// matrixV[i] += (int) (seed * 6543265L);
	// seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	// }
	// // Compute matrix Vt(0) * V(0)
	// MatrTranspMult(matrixV, matrixV, matrixVtV0);
	// for (;;) {
	// if (ecmApplet.getTerminateThread()) {
	// throw new ArithmeticException();
	// }
	// oldDiagonalSSt = newDiagonalSSt;
	// stepNbr++;
	// // Compute matrix A * V(i)
	// MultiplyAByMatrix(matrixB, matrixV, matrixCalc3, matrixAV,
	// matrixBlength);
	// // Compute matrix Vt(i) * A * V(i)
	// MatrTranspMult(matrixV, matrixAV, matrixVtAV);
	//
	// /* If Vt(i) * A * V(i) = 0, end of loop */
	// for (i = matrixVtAV.length - 1; i >= 0; i--) {
	// if (matrixVtAV[i] != 0) {
	// break;
	// }
	// }
	// if (i < 0) {
	// break;
	// } /* End X-Y calculation loop */
	//
	// /* Selection of S(i) and W(i) */
	//
	// matrixTemp = matrixWinv2;
	// matrixWinv2 = matrixWinv1;
	// matrixWinv1 = matrixWinv;
	// matrixWinv = matrixTemp;
	//
	// mask = 1;
	// for (j = 31; j >= 0; j--) {
	// matrixD[j] = matrixVtAV[j]; /* D = VtAV */
	// matrixWinv[j] = mask; /* Winv = I */
	// mask *= 2;
	// }
	//
	// index = 31;
	// indexC = 31;
	// for (mask = 1; mask != 0; mask *= 2) {
	// if ((oldDiagonalSSt & mask) != 0) {
	// matrixE[index] = indexC;
	// matrixF[index] = mask;
	// index--;
	// }
	// indexC--;
	// }
	// indexC = 31;
	// for (mask = 1; mask != 0; mask *= 2) {
	// if ((oldDiagonalSSt & mask) == 0) {
	// matrixE[index] = indexC;
	// matrixF[index] = mask;
	// index--;
	// }
	// indexC--;
	// }
	// newDiagonalSSt = 0;
	// for (j = 0; j < 32; j++) {
	// currentOrder = matrixE[j];
	// currentMask = matrixF[j];
	// for (k = j; k < 32; k++) {
	// if ((matrixD[matrixE[k]] & currentMask) != 0) {
	// break;
	// }
	// }
	// if (k < 32) {
	// i = matrixE[k];
	// Temp = matrixWinv[i];
	// matrixWinv[i] = matrixWinv[currentOrder];
	// matrixWinv[currentOrder] = Temp;
	// Temp1 = matrixD[i];
	// matrixD[i] = matrixD[currentOrder];
	// matrixD[currentOrder] = Temp1;
	// newDiagonalSSt |= currentMask;
	// for (k = 31; k >= 0; k--) {
	// if (k != currentOrder
	// && ((matrixD[k] & currentMask) != 0)) {
	// matrixWinv[k] ^= Temp;
	// matrixD[k] ^= Temp1;
	// }
	// } /* end for k */
	// } else {
	// for (k = j; k < 32; k++) {
	// if ((matrixWinv[matrixE[k]] & currentMask) != 0) {
	// break;
	// }
	// }
	// i = matrixE[k];
	// Temp = matrixWinv[i];
	// matrixWinv[i] = matrixWinv[currentOrder];
	// matrixWinv[currentOrder] = Temp;
	// Temp1 = matrixD[i];
	// matrixD[i] = matrixD[currentOrder];
	// matrixD[currentOrder] = Temp1;
	// for (k = 31; k >= 0; k--) {
	// if ((matrixWinv[k] & currentMask) != 0) {
	// matrixWinv[k] ^= Temp;
	// matrixD[k] ^= Temp1;
	// }
	// } /* end for k */
	// } /* end if */
	// } /* end for j */
	// /* Compute D(i), E(i) and F(i) */
	// if (stepNbr >= 3) {
	// // F = -Winv(i-2) * (I - Vt(i-1)*A*V(i-1)*Winv(i-1)) * ParenD *
	// // S*St
	// MatrixMultiplication(matrixVt1AV1, matrixWinv1, matrixCalc2);
	// index = 31; /* Add identity matrix */
	// for (mask = 1; mask != 0; mask *= 2) {
	// matrixCalc2[index] ^= mask;
	// index--;
	// }
	// MatrixMultiplication(matrixWinv2, matrixCalc2, matrixCalc1);
	// MatrixMultiplication(matrixCalc1, matrixCalcParenD, matrixF);
	// MatrMultBySSt(matrixF, newDiagonalSSt, matrixF);
	// }
	// // E = -Winv(i-1) * Vt(i)*A*V(i) * S*St
	// if (stepNbr >= 2) {
	// MatrixMultiplication(matrixWinv1, matrixVtAV, matrixE);
	// MatrMultBySSt(matrixE, newDiagonalSSt, matrixE);
	// }
	// // ParenD = Vt(i)*A*A*V(i) * S*St + Vt(i)*A*V(i)
	// // D = I - Winv(i) * ParenD
	// MatrTranspMult(matrixAV, matrixAV, matrixCalc1); // Vt(i)*A*A*V(i)
	// MatrMultBySSt(matrixCalc1, newDiagonalSSt, matrixCalc1);
	// MatrixAddition(matrixCalc1, matrixVtAV, matrixCalcParenD);
	// MatrixMultiplication(matrixWinv, matrixCalcParenD, matrixD);
	// index = 31; /* Add identity matrix */
	// for (mask = 1; mask != 0; mask *= 2) {
	// matrixD[index] ^= mask;
	// index--;
	// }
	//
	// /* Update value of X - Y */
	// MatrixMultiplication(matrixWinv, matrixVtV0, matrixCalc1);
	// MatrixMultAdd(matrixV, matrixCalc1, matrixXmY);
	//
	// /* Compute value of new matrix V(i) */
	// // V(i+1) = A * V(i) * S * St + V(i) * D + V(i-1) * E + V(i-2) * F
	// MatrMultBySSt(matrixAV, newDiagonalSSt, matrixCalc3);
	// MatrixMultAdd(matrixV, matrixD, matrixCalc3);
	// if (stepNbr >= 2) {
	// MatrixMultAdd(matrixV1, matrixE, matrixCalc3);
	// if (stepNbr >= 3) {
	// MatrixMultAdd(matrixV2, matrixF, matrixCalc3);
	// }
	// }
	// /* Compute value of new matrix Vt(i)V0 */
	// // Vt(i+1)V(0) = Dt * Vt(i)V(0) + Et * Vt(i-1)V(0) + Ft *
	// // Vt(i-2)V(0)
	// MatrTranspMult(matrixD, matrixVtV0, matrixCalc2);
	// if (stepNbr >= 2) {
	// MatrTranspMult(matrixE, matrixVt1V0, matrixCalc1);
	// MatrixAddition(matrixCalc1, matrixCalc2, matrixCalc2);
	// if (stepNbr >= 3) {
	// MatrTranspMult(matrixF, matrixVt2V0, matrixCalc1);
	// MatrixAddition(matrixCalc1, matrixCalc2, matrixCalc2);
	// }
	// }
	// matrixTemp = matrixV2;
	// matrixV2 = matrixV1;
	// matrixV1 = matrixV;
	// matrixV = matrixCalc3;
	// matrixCalc3 = matrixTemp;
	// matrixTemp = matrixVt2V0;
	// matrixVt2V0 = matrixVt1V0;
	// matrixVt1V0 = matrixVtV0;
	// matrixVtV0 = matrixCalc2;
	// matrixCalc2 = matrixTemp;
	// matrixTemp = matrixVt1AV1;
	// matrixVt1AV1 = matrixVtAV;
	// matrixVtAV = matrixTemp;
	// } /* end while */
	//
	// /* Find matrix V1:V2 = B * (X-Y:V) */
	// for (row = matrixBlength - 1; row >= 0; row--) {
	// matrixV1[row] = matrixV2[row] = 0;
	// }
	// for (row = matrixBlength - 1; row >= 0; row--) {
	// rowMatrixB = matrixB[row];
	// rowMatrixXmY = matrixXmY[row];
	// rowMatrixV = matrixV[row];
	// // The vector rowMatrixB includes the indexes of the columns set to
	// // '1'.
	// for (index = rowMatrixB.length - 1; index >= 0; index--) {
	// col = rowMatrixB[index];
	// matrixV1[col] ^= rowMatrixXmY;
	// matrixV2[col] ^= rowMatrixV;
	// }
	// }
	// rightCol = 64;
	// leftCol = 0;
	// while (leftCol < rightCol) {
	// for (col = leftCol; col < rightCol; col++) { // For each column find
	// // the first row
	// // which has a '1'.
	// // Columns outside
	// // this range must
	// // have '0' in all
	// // rows.
	// matr = (col >= 32 ? matrixV1 : matrixV2);
	// mask = 0x80000000 >>> (col & 31);
	// vectorIndex[col] = -1; // indicate all rows in zero in advance.
	// for (row = 0; row < matr.length; row++) {
	// if ((matr[row] & mask) != 0) { // First row for this mask is
	// // found. Store it.
	// vectorIndex[col] = row;
	// break;
	// }
	// }
	// }
	// for (col = leftCol; col < rightCol; col++) {
	// if (vectorIndex[col] < 0) { // If all zeros in col 'col',
	// // exchange it with first column
	// // with
	// // data different from zero
	// // (leftCol).
	// colexchange(matrixXmY, matrixV, matrixV1, matrixV2,
	// leftCol, col);
	// vectorIndex[col] = vectorIndex[leftCol];
	// vectorIndex[leftCol] = -1; // This column now has zeros.
	// leftCol++; // Update leftCol to exclude that column.
	// }
	// }
	// if (leftCol == rightCol) {
	// break;
	// }
	// // At this moment all columns from leftCol to rightCol are non-zero.
	// // Get the first row that includes a '1'.
	// min = vectorIndex[leftCol];
	// minind = leftCol;
	// for (col = leftCol + 1; col < rightCol; col++) {
	// if (vectorIndex[col] < min) {
	// min = vectorIndex[col];
	// minind = col;
	// }
	// }
	// minanswer = 0;
	// for (col = leftCol; col < rightCol; col++) {
	// if (vectorIndex[col] == min) {
	// minanswer++;
	// }
	// }
	// if (minanswer > 1) { // Two columns with the same first row to '1'.
	// for (col = minind + 1; col < rightCol; col++) {
	// if (vectorIndex[col] == min) { // Add first column which has
	// // '1' in the same row to
	// // the other columns so they
	// // have '0' in this row
	// // after
	// // this operation.
	// coladd(matrixXmY, matrixV, matrixV1, matrixV2, minind,
	// col);
	// }
	// }
	// } else {
	// rightCol--;
	// colexchange(matrixXmY, matrixV, matrixV1, matrixV2, minind,
	// rightCol);
	// }
	// }
	// leftCol = 0; /* find linear independent solutions */
	// while (leftCol < rightCol) {
	// for (col = leftCol; col < rightCol; col++) { // For each column find
	// // the first row
	// // which has a '1'.
	// matr = (col >= 32 ? matrixXmY : matrixV);
	// mask = 0x80000000 >>> (col & 31);
	// vectorIndex[col] = -1; // indicate all rows in zero in advance.
	// for (row = 0; row < matrixV1.length; row++) {
	// if ((matr[row] & mask) != 0) { // First row for this mask is
	// // found. Store it.
	// vectorIndex[col] = row;
	// break;
	// }
	// }
	// }
	// for (col = leftCol; col < rightCol; col++) { // If all zeros in col
	// // 'col', exchange
	// // it with last
	// // column with
	// // data different
	// // from zero
	// // (rightCol).
	// if (vectorIndex[col] < 0) {
	// rightCol--; // Update rightCol to exclude that column.
	// colexchange(matrixXmY, matrixV, matrixV1, matrixV2,
	// rightCol, col);
	// vectorIndex[col] = vectorIndex[rightCol];
	// vectorIndex[rightCol] = -1; // This column now has zeros.
	// }
	// }
	// if (leftCol == rightCol) {
	// break;
	// }
	// // At this moment all columns from leftCol to rightCol are non-zero.
	// // Get the first row that includes a '1'.
	// min = vectorIndex[leftCol];
	// minind = leftCol;
	// for (col = leftCol + 1; col < rightCol; col++) {
	// if (vectorIndex[col] < min) {
	// min = vectorIndex[col];
	// minind = col;
	// }
	// }
	// minanswer = 0;
	// for (col = leftCol; col < rightCol; col++) {
	// if (vectorIndex[col] == min) {
	// minanswer++;
	// }
	// }
	// if (minanswer > 1) { // At least two columns with the same first row
	// // to '1'.
	// for (col = minind + 1; col < rightCol; col++) {
	// if (vectorIndex[col] == min) { // Add first column which has
	// // '1' in the same row to
	// // the other columns so they
	// // have '0' in this row
	// // after
	// // this operation.
	// coladd(matrixXmY, matrixV, matrixV1, matrixV2, minind,
	// col);
	// }
	// }
	// } else {
	// colexchange(matrixXmY, matrixV, matrixV1, matrixV2, minind,
	// leftCol);
	// leftCol++;
	// }
	// }
	// return matrixV;
	// }

}
