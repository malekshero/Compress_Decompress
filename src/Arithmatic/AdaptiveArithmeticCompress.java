package Arithmatic;

import javafx.stage.FileChooser;

import java.io.*;

public class AdaptiveArithmeticCompress {

	public  void mains() throws IOException {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
		File f = fc.showOpenDialog(null);

		if(f != null) {
			String input = f.getAbsolutePath();
			String output = "Files/Compressed/"+f.getName();
			File inputFile = new File(input);
			File outputFile = new File(output);


			try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
				 BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				compress(in, out);
			}
		}
	}


	// To allow unit testing, this method is package-private instead of private.
	public static void compress(InputStream in, BitOutputStream out) throws IOException {
		FlatFrequencyTable initFreqs = new FlatFrequencyTable(257);
		FrequencyTable freqs = new SimpleFrequencyTable(initFreqs);
		ArithmeticEncoder enc = new ArithmeticEncoder(32, out);
		while (true) {
			// Read and encode one byte
			int symbol = in.read();
			if (symbol == -1)
				break;
			enc.write(freqs, symbol);
			freqs.increment(symbol);
		}
		enc.write(freqs, 256);  // EOF
		enc.finish();  // Flush remaining code bits
	}

}
