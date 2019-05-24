package adaptiveHuffman.encoder;

import adaptiveHuffman.BitByteOutputStream;
import adaptiveHuffman.tree.Tree;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Encoder {
	
	public FileInputStream in = null;
	public BitByteOutputStream out = null;
    
    public void encodeFile() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
		File f = fc.showOpenDialog(null);

		if(f != null)
		{
			String input = f.getAbsolutePath();
			String output = "Files/Compressed/"+f.getName()+"a";
			Encoder enc = new Encoder(input,output);
			Tree tree = new Tree();
			File in = new File(input);
			long t = System.nanoTime();
			enc.encode(tree);
			long at = System.nanoTime();
			File out = new File(output);
			System.out.println("Finished compression of: "+in.getName()+" in "+(float)(at-t)/1000000+" ms");
			System.out.println("Original size: "+in.length()+" bytes");
			System.out.println("Compressed size: "+out.length()+" bytes");
			double all=(double)in.length()/(double)out.length()*100;
			System.out.printf("CompressionRatio : %.1f%s\n",all,"%");
		}
	}

	public void encodeFiles() {

            DirectoryChooser chooser = new DirectoryChooser();
			File defaultDirectory = chooser.showDialog(null);
			chooser.setInitialDirectory(defaultDirectory);
			List<String> results = new ArrayList<String>();
			File[] files = new File(defaultDirectory.getAbsolutePath()).listFiles();
			if (defaultDirectory != null) {
				for (File file : files) {
					if (file.isFile()) {
						results.add(file.getAbsolutePath());
						String input = file.getAbsolutePath();
						String output = "Files/Compressed/"+file.getName()+"a";
						Encoder enc = new Encoder(input,output);
						Tree tree = new Tree();
						long t = System.nanoTime();
						enc.encode(tree);
						long at = System.nanoTime();
						File out = new File(output);
						System.out.println("Finished compression of: "+file.getName()+" in "+(float)(at-t)/1000000+" ms");
						System.out.println("Original size: "+file.length()+" bytes");
						System.out.println("Compressed size: "+out.length()+" bytes");
						double all=100-(double)file.length()/(double)out.length()*100;
						System.out.printf("CompressionRatio : %.1f%s\n",all,"%");
					}
				}
			}

		}


    public Encoder(String in, String out) {
    	try {
			this.in = new FileInputStream(in);
			this.out = new BitByteOutputStream(new FileOutputStream(out));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    }
	public Encoder() {
	}

	public void encode(Tree tree) {
		try {
			int c = 0;
			
			while((c = in.read()) != -1) {
				ArrayList<Boolean> buffer = new ArrayList<Boolean>();
				if (tree.contains(c)) {
					
					int len = tree.getCode(c,true,buffer);
					for(len=len-1 ;len>=0;len--){
						out.writeBit(buffer.get(len));
					}
					tree.insertInto((int)c);
				}
				else {
					int len = tree.getCode(c, false,buffer);
					for(len=len-1 ;len>=0;len--){
						out.writeBit(buffer.get(len));
					}
					out.writeByte(c);
					tree.insertInto(c);
				}
			}
			out.flush();
		}
		catch (IOException e) {
			System.err.println("Error reading from input");
			e.printStackTrace();
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				out.close();
			}
		}
	}
	
	
	

}
