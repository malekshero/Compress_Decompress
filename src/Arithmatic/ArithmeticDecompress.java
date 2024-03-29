package Arithmatic;/*
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */

import javafx.stage.FileChooser;

import java.io.*;


/**
 * Decompression application using static arithmetic coding.
 * <p>Usage: java ArithmeticDecompress InputFile OutputFile</p>
 * <p>This decompresses files generated by the "ArithmeticCompress" application.</p>
 */
public class ArithmeticDecompress {
	
	public  void mains() throws IOException {
		// Handle command line arguments

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		fc.setInitialDirectory(new File("C:\\Users\\Malek Shero\\Desktop\\multimidia_project\\Files\\Compressed\\"));
		File f = fc.showOpenDialog(null);
		if (f != null) {

			String input = f.getAbsolutePath();
			String output = "Files/Decompressed/" + f.getName();
			File inputFile = new File(input);
			File outputFile = new File(output);

			// Perform file decompression
			try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
				 OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
				FrequencyTable freqs = readFrequencies(in);
				decompress(freqs, in, out);
			}
		}

	}
	// To allow unit testing, this method is package-private instead of private.
	public static FrequencyTable readFrequencies(BitInputStream in) throws IOException {
		int[] freqs = new int[257];
		for (int i = 0; i < 256; i++)
			freqs[i] = readInt(in, 32);
		freqs[256] = 1;  // EOF symbol
		return new SimpleFrequencyTable(freqs);
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	public static void decompress(FrequencyTable freqs, BitInputStream in, OutputStream out) throws IOException {
		ArithmeticDecoder dec = new ArithmeticDecoder(32, in);
		while (true) {
			int symbol = dec.read(freqs);
			if (symbol == 256)  // EOF symbol
				break;
			out.write(symbol);
		}
	}
	
	
	// Reads an unsigned integer of the given bit width from the given stream.
	private static int readInt(BitInputStream in, int numBits) throws IOException {
		if (numBits < 0 || numBits > 32)
			throw new IllegalArgumentException();
		
		int result = 0;
		for (int i = 0; i < numBits; i++)
			result = (result << 1) | in.readNoEof();  // Big endian
		return result;
	}
	
}
