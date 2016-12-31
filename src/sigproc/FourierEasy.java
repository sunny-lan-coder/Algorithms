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

	static ArrayList<Sine> dft(int samples, double[] amplitudes) {
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

	static final double epsilon = 1e-10;

	static double[][] dft(double[] data) {
		int n = data.length;
		double[][] res = new double[2][n];
		for (int freq = 0; freq < n; freq++) {
			double real = 0;
			double imaginary = 0;

			for (int sample = 0; sample < n; sample++) {
				
				
				double phase = (2 * Math.PI)/n * freq * sample;

				real += data[sample]*Math.cos(phase);
				imaginary +=data[sample]* Math.sin(phase);
			}

			if (Math.abs(real) < epsilon)
				real = 0;

			if (Math.abs(imaginary) < epsilon)
				imaginary = 0;

			real /= n;
			imaginary /= n;

			res[0][freq] = real;
			res[1][freq] = imaginary*Math.PI*2;

		}
		return res;
	}

	public static void main(String[] args) {
		System.out.println("dft1");
		ArrayList<Sine> res = dft(4, new double[] { 0, 1, 0, 0 });
		for (Sine s : res) {
			System.out.println(s.freq + ":" + s.phase + ":" + s.amp);
		}
		System.out.println("dft2");
		double[][] res2=dft(new double[]{0,1,0,0});
		for(int i=0;i<res2[0].length;i++){
			System.out.println(i+":"+Math.toDegrees(res2[1][i])+":"+res2[0][i]);
		}
	}

}
