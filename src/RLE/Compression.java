package RLE;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Compression {
	protected String _depart;
	
	public abstract void compresser(String fin, boolean v);
	public abstract void decompresser(String fin, boolean v);
	public abstract void raz();
	public String int2bin(int i,int t){
		StringBuffer str=new StringBuffer(Integer.toBinaryString(i));
		while(str.length()<t)
			str.insert(0,"0");
		return str.toString();
	}
	public void tester(String dep,String arr, boolean v){
		raz();
		_depart=dep;
		compresser(dep+"c", v);
		raz();
		_depart=dep+"c";
		decompresser(arr,v);
		CSP.estLeMeme(dep,arr);
	}
	public void testerTout(boolean v){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
		File f = fc.showOpenDialog(null);
		if(f != null)
		{
			String fich = f.getAbsolutePath();
			String ff = "Files/Decompressed/"+f.getName();
			tester(fich,ff,v);
		}
		else {
			DirectoryChooser chooser = new DirectoryChooser();
			File defaultDirectory = chooser.showDialog(null);
			chooser.setInitialDirectory(defaultDirectory);
			List<String> results = new ArrayList<String>();
			File[] files = new File(defaultDirectory.getAbsolutePath()).listFiles();

			if (defaultDirectory != null) {
				for (File file : files) {
					if (file.isFile()) {
						results.add(file.getAbsolutePath());
						String fich = file.getAbsolutePath();
						String ff = "Files/Decompressed/" + file.getName();
						tester(fich, ff, v);
					}
				}
			}
		}
	}
}