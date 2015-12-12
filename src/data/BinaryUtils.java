package data;

public class BinaryUtils {
	private static final long bitToZero = 0b1111111111111111111111111111111111111111111111111111111111111110L;
	private static final long bitToOne = 0b1L;

	public static long setBit(long value, byte bitnum, boolean bitValue) {
		if (bitValue) {
			return value | circularLShift(bitToOne, bitnum);
		}
		return value & circularLShift(bitToZero, bitnum);
	}

	public static boolean getBit(long value, byte bitnum) {
		if (((value >> bitnum) & 1) == 0) {
			return false;
		}
		return true;
	}

	public static long circularRShift(long value, long shift) {
		return (value >>> shift) | (value << (64 - shift));
	}

	public static long circularLShift(long value, long shift) {
		return (value << shift) | (value >>> (64 - shift));
	}

}
