package data;

public class Main {

	public static void main(String[] args) {
		long size = 100000000;
		long starttime;
		VeryBigBooleanArray vbba = new VeryBigBooleanArray(size);
		starttime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			vbba.set(i, true);
		}
		System.out.println("vbba set time:"
				+ (System.currentTimeMillis() - starttime));
		starttime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			vbba.get(i);
		}
		System.out.println("vbba get time:"
				+ (System.currentTimeMillis() - starttime));
		BigBooleanArray bba = new BigBooleanArray(size);
		starttime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			bba.set(i, true);
		}
		System.out.println("bba set time:"
				+ (System.currentTimeMillis() - starttime));
		starttime = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			bba.get(i);
		}
		System.out.println("bba get time:"
				+ (System.currentTimeMillis() - starttime));
	}

}
