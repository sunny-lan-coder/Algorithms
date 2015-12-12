package data;

public abstract class IArray<T> {
	public final long length;

	public IArray(long length) {
		this.length = length;
	}

	public abstract T get(long index);

	public abstract void set(long index, T value);
}
