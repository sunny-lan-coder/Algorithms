package data;


public class VeryBigBooleanArray {
	private boolean[][] values;
	public static final int max_single_arr_size = 10000000;

	public VeryBigBooleanArray(long size) {
		int numberarrays = (int) Math.ceil(size / max_single_arr_size);
		values = new boolean[numberarrays][max_single_arr_size];
	}
	
	public boolean get(long index){
		int y=(int) (index%max_single_arr_size);
		int x=(int) Math.floor(index/max_single_arr_size);
		return values[x][y];
	}
	
	public void set(long index, boolean value){
		int y=(int) (index%max_single_arr_size);
		int x=(int) Math.floor(index/max_single_arr_size);
		values[x][y]=value;
	}
}
