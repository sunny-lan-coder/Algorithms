package data;

public class BigBooleanArray extends IArray<Boolean> {
	private long values[];

	public BigBooleanArray(long size) {
		super(size);
		values = new long[(int) Math.ceil(size / 64d)];
	}

	public Boolean get(long index) {
		if (!isInBounds(index))
			throw new ArrayIndexOutOfBoundsException("index " + index
					+ " was bigger than array size " + length);
		int elementAt = (int) Math.floor(index / 64);
		byte bitnum = (byte) (index % 64);
		return BinaryUtils.getBit(values[elementAt], bitnum);
	}

	public void set(long index, Boolean value) {
		if (!isInBounds(index))
			throw new ArrayIndexOutOfBoundsException("index " + index
					+ " was bigger than array size " + length);
		int elementAt = (int) Math.floor(index / 64);
		byte bitnum = (byte) (index % 64);
		//System.out.print("input={index=" + index + ",value=" + value
			//	+ "},data={block=" + elementAt + ",bitnum=" + bitnum
				//+ ",blockval=" + Long.toBinaryString(values[elementAt]) + "}");
		values[elementAt] = BinaryUtils
				.setBit(values[elementAt], bitnum, value);
		//System.out.println(",output={blockval="
			//	+ Long.toBinaryString(values[elementAt]) + "}");
	}

	public boolean isInBounds(long index) {
		if (index > length)
			return false;
		if (index < 0)
			return false;
		return true;
	}
}
