package factorization.core;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import factorization.brute.PrimeFactorizerBrute;
import factorization.pollardspminusone.PollardsPMinusOne;
import factorization.pollardsrho.MontegomoryRho;
import factorization.pollardsrho.PollardsRho;
import factorization.qs.FermatPrimeFactorization;

public class OutputWindow {

	private JFrame frame;
	private JTextField txtInput;
	private JComboBox<IPrimeFactorizer> comboBoxAlg;
	private JTextArea txtOutput;
	private JButton btnGo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OutputWindow window = new OutputWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OutputWindow() {
		initialize();

		// add algorithms to box:
		comboBoxAlg.addItem(new MontegomoryRho());
		comboBoxAlg.addItem(new PollardsPMinusOne());
		comboBoxAlg.addItem(new PollardsRho());
		comboBoxAlg.addItem(new PrimeFactorizerBrute());
		comboBoxAlg.addItem(new FermatPrimeFactorization());
	}

	private void runFactorization() {
		btnGo.setEnabled(false);
		frame.repaint();
		try {
			final long input = Long.parseLong(txtInput.getText());

			final IPrimeFactorizer f = (IPrimeFactorizer) comboBoxAlg.getSelectedItem();
			f.gc();
			f.setNumber(input);
			Thread runThread = new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<Long> results;
					long start;
					long stop;
					start = System.nanoTime();
					try {
						f.factorize();
					} catch (PrimeFactoringException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stop = System.nanoTime();
					results = f.getFactors();
					f.gc();
					StringBuilder resultString = new StringBuilder();
					resultString.append("Find took ");
					resultString.append((stop - start) / 1000000d);
					resultString.append(" ms. Factors are:\n");
					for (int i = 0; i < results.size(); i++) {
						resultString.append(results.get(i));
						resultString.append('\n');
					}
					txtOutput.setText(resultString.toString());
				}

			});

			runThread.start();
		} catch (Exception e) {
			txtOutput.append(e.toString());
		} finally {
			btnGo.setEnabled(true);
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 331);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);

		JLabel lblPrimeNumberTo = new JLabel("Number to factor:");
		springLayout.putConstraint(SpringLayout.NORTH, lblPrimeNumberTo, 10, SpringLayout.NORTH,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblPrimeNumberTo, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblPrimeNumberTo, 30, SpringLayout.NORTH,
				frame.getContentPane());
		frame.getContentPane().add(lblPrimeNumberTo);

		txtInput = new JTextField();
		txtInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyChar() == '\n') {
					runFactorization();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, txtInput, 125, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblPrimeNumberTo, -6, SpringLayout.WEST, txtInput);
		springLayout.putConstraint(SpringLayout.NORTH, txtInput, 10, SpringLayout.NORTH, frame.getContentPane());
		frame.getContentPane().add(txtInput);
		txtInput.setColumns(10);

		btnGo = new JButton("Go");
		springLayout.putConstraint(SpringLayout.EAST, txtInput, -6, SpringLayout.WEST, btnGo);
		springLayout.putConstraint(SpringLayout.NORTH, btnGo, -1, SpringLayout.NORTH, lblPrimeNumberTo);
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runFactorization();
			}
		});
		frame.getContentPane().add(btnGo);

		comboBoxAlg = new JComboBox<IPrimeFactorizer>();
		springLayout.putConstraint(SpringLayout.SOUTH, btnGo, 0, SpringLayout.SOUTH, comboBoxAlg);
		springLayout.putConstraint(SpringLayout.NORTH, comboBoxAlg, 6, SpringLayout.SOUTH, lblPrimeNumberTo);
		springLayout.putConstraint(SpringLayout.WEST, comboBoxAlg, 0, SpringLayout.WEST, txtInput);
		springLayout.putConstraint(SpringLayout.EAST, comboBoxAlg, 187, SpringLayout.WEST, txtInput);
		frame.getContentPane().add(comboBoxAlg);

		txtOutput = new JTextArea();
		springLayout.putConstraint(SpringLayout.NORTH, txtOutput, 6, SpringLayout.SOUTH, comboBoxAlg);
		springLayout.putConstraint(SpringLayout.SOUTH, txtOutput, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnGo, 0, SpringLayout.EAST, txtOutput);
		springLayout.putConstraint(SpringLayout.WEST, txtOutput, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtOutput, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(txtOutput);

		JLabel lblAlgorithm = new JLabel("Algorithm:");
		springLayout.putConstraint(SpringLayout.EAST, lblAlgorithm, -338, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblAlgorithm, 6, SpringLayout.SOUTH, lblPrimeNumberTo);
		springLayout.putConstraint(SpringLayout.WEST, lblAlgorithm, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblAlgorithm, -6, SpringLayout.NORTH, txtOutput);
		frame.getContentPane().add(lblAlgorithm);
	}
}
