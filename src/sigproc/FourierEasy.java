package sigproc;

import java.util.ArrayList;

public class FourierEasy {

	static class Sine {
		public final double phase;
		public final double freq;
		public final double amp;

		public Sine(double phase, double freq, double amp) {
			this.phase = phase;
			this.freq = freq;
			this.amp = amp;
		}
	}

	static ArrayList<Sine> fft(int samples, double[] amplitudes) {
		ArrayList<Sine> res = new ArrayList<>();
		for (int i = 0; i < samples; i++) {
			for (int j = 0; j < samples; j++) {
				Sine n = new Sine((360 / samples) * i * j, j, amplitudes[i] / samples);
				if (n.amp != 0)
					res.add(n);
			}
		}
		return res;
	}

	public static void main(String[] args) {
		ArrayList<Sine> res = fft(4, new double[] { 0, 1, 0, 0 });
		for (Sine s : res) {
			System.out.println(s.freq + ":" + s.phase + ":" + s.amp);
		}
	}

}
