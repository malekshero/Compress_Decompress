package Arithmatic;/*
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */

import javafx.stage.FileChooser;

import java.io.*;

import static Arithmatic.PpmDecompress.decompress;


/**
 * Decompression application using adaptive arithmetic coding.
 * <p>Usage: java AdaptiveArithmeticDecompress InputFile OutputFile</p>
 * <p>This decompresses files generated by the "AdaptiveArithmeticCompress" application.</p>
 */
public class AdaptiveArithmeticDecompress {
	
	public  void mains() throws IOException {

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
				decompress(in, out);
			}
		}
	}
	// To allow unit testing, this method is package-private instead of private.
	public static void decompress(BitInputStream in, OutputStream out) throws IOException {
		FlatFrequencyTable initFreqs = new FlatFrequencyTable(257);
		FrequencyTable freqs = new SimpleFrequencyTable(initFreqs);
		ArithmeticDecoder dec = new ArithmeticDecoder(32, in);
		while (true) {
			// Decode and write one byte
			int symbol = dec.read(freqs);
			if (symbol == 256)  // EOF symbol
				break;
			out.write(symbol);
			freqs.increment(symbol);
		}
	}
	
}