package Arithmatic;

import javafx.stage.FileChooser;

import java.io.*;

public class ArithmeticCompress {
	
	public  void mains() throws IOException {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
		File f = fc.showOpenDialog(null);

		if (f != null) {
			String input = f.getAbsolutePath();
			String output = "Files/Compressed/" + f.getName();
			File inputFile = new File(input);
			File outputFile = new File(output);

			// Read input file once to compute symbol frequencies
			FrequencyTable freqs = getFrequencies(inputFile);
			freqs.increment(256);  // EOF symbol gets a frequency of 1

			// Read input file again, compress with arithmetic coding, and write output file
			try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
				 BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				writeFrequencies(out, freqs);
				compress(freqs, in, out);
			}
            long t = System.nanoTime();
            long at = System.nanoTime();

            System.out.println("Finished compression of: "+inputFile.getName()+" in "+(float)(at-t)/1000000+" ms");
            System.out.println("Original size: "+inputFile.length()+" bytes");
            System.out.println("Compressed size: "+outputFile.length()+" bytes");
            double all=(double)inputFile.length()/(double)outputFile.length()*100;
            System.out.printf("CompressionRatio : %.1f%s\n",all,"%");
		}
	}
	
	// Returns a frequency table based on the bytes in the given file.
	// Also contains an extra entry for symbol 256, whose frequency is set to 0.
	private static FrequencyTable getFrequencies(File file) throws IOException {
		FrequencyTable freqs = new SimpleFrequencyTable(new int[257]);
		try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			while (true) {
				int b = input.read();
				if (b == -1)
					break;
				freqs.increment(b);
			}
		}
		return freqs;
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	public static void writeFrequencies(BitOutputStream out, FrequencyTable freqs) throws IOException {
		for (int i = 0; i < 256; i++)
			writeInt(out, 32, freqs.get(i));
	}
	
	
	// To allow unit testing, this method is package-private instead of private.
	public static void compress(FrequencyTable freqs, InputStream in, BitOutputStream out) throws IOException {
		ArithmeticEncoder enc = new ArithmeticEncoder(32, out);
		while (true) {
			int symbol = in.read();
			if (symbol == -1)
				break;
			enc.write(freqs, symbol);
		}
		enc.write(freqs, 256);  // EOF
		enc.finish();  // Flush remaining code bits
	}
	
	
	// Writes an unsigned integer of the given bit width to the given stream.
	private static void writeInt(BitOutputStream out, int numBits, int value) throws IOException {
		if (numBits < 0 || numBits > 32)
			throw new IllegalArgumentException();
		
		for (int i = numBits - 1; i >= 0; i--)
			out.write((value >>> i) & 1);  // Big endian
	}
	
}
