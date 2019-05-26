package shanonn;

import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;


public class Shannonfano {

	ArrayList<Character> orgst = new ArrayList<Character>();
	ArrayList<Character> comst = new ArrayList<Character>();
	ArrayList<Character> charst = new ArrayList<Character>(100);
	List<String> staftercomp = new ArrayList<String>();
	String st;
	HashMap<Character, Double> charfreq = new HashMap<Character, Double>();
	HashMap<Character, Double> charproi = new HashMap<Character, Double>();
	HashMap<Character, Double> sortedcharfreq = new HashMap<Character, Double>();
	HashMap<Character, Double> sortedcharproi = new HashMap<Character, Double>();
	HashMap<Character, String> compressedResult = new HashMap<Character, String>();
	HashMap<Character, ArrayList<Integer>> charloc = new HashMap<Character, ArrayList<Integer>>();


	public void compress() throws IOException
	{

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
		File f = fc.showOpenDialog(null);

		if(f != null)
		{
			String input = f.getAbsolutePath();


			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(input), "UTF8"));
			while ((st = br.readLine()) != null)
			{
				for (int i = 0; i < st.length(); i++)
					orgst.add(st.charAt(i));
				orgst.add('\n');

			}
			br.close();
		}

		String output = "Files/Compressed/"+f.getName();
		calculateFrequency(output);
	}

	void calculateFrequency (String output)
	{
		for(Character c : orgst)
		{
			if(!charfreq.containsKey(c))
				charfreq.put(c,1.0);
			else {
				charfreq.put(c,charfreq.get(c) + 1.0);
			}
		}

		sortedcharfreq = charfreq
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(
						toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
								LinkedHashMap::new));

		charfreq = sortedcharfreq;
		calculateprobability(output);
	}

	void calculateprobability (String output)
	{
		int numoforgst= orgst.size();
		for (Character c : charfreq.keySet())
		{
			charproi.put(c,(double) (charfreq.get(c).doubleValue()/numoforgst));
		}
		sortedcharproi = charproi
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Entry.comparingByValue()))
				.collect(
						toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
								LinkedHashMap::new));

		charproi = sortedcharproi;
		compressString(output);
	}

	private void appendBit(HashMap<Character, String> result, List<Character> charList, boolean up,String output) {

		String bit = "";
		if (!result.isEmpty()) {
			bit = (up) ? "0" : "1";
		}

		for (Character c : charList) {
			String s = (result.get(c) == null) ? "" : result.get(c);
			result.put(c, s + bit);

		}

		if (charList.size() >= 2) {
			int separator = (int) Math.floor((float) charList.size() / 2.0);

			List<Character> upList = charList.subList(0, separator);
			appendBit(result, upList, true , output);
			List<Character> downList = charList.subList(separator, charList.size());
			appendBit(result, downList, false,output);
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output), "utf-8")))
		{
			for (Character c : compressedResult.keySet())
				writer.write(compressedResult.get(c).toString()+' ');
			writer.close();
		}
		catch (IOException e) {	e.printStackTrace(); }
	}

	private void compressString(String output)
	{
		List<Character> charList = new ArrayList<Character>();
		Iterator<Entry<Character, Double>> entries = charfreq.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<Character, Double> entry = entries.next();
			charList.add(entry.getKey());
		}
		appendBit(compressedResult, charList, true,output);
	}

	void characterlocation()
	{
		for(Character cc : orgst)
		{
			ArrayList<Integer> loc = new ArrayList<Integer>();
			for (int i = 0 ; i<orgst.size() ; i++)
			{
				char c = orgst.get(i);
				if(cc == c)
					loc.add(i);
			}
			if(!charloc.containsKey(cc))
				charloc.put(cc, loc);
		}
	}

	public void extract()
	{

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*") );
		fc.setInitialDirectory(new File("C:\\Users\\Malek Shero\\Desktop\\multimidia_project\\Files\\Compressed\\"));
		File f = fc.showOpenDialog(null);
		try {
			if(f != null) {

				String input = f.getAbsolutePath();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(input), "UTF8"));
				while ((st = br.readLine()) != null) {
					staftercomp = Arrays.asList(st.split(" "));
				}
				br.close();
			}
		}
		catch (IOException e) {	e.printStackTrace(); }
		String output = "Files/Decompressed/" + f.getName();
		read(output);
	}

	void read(String output)
	{
		for (char c : compressedResult.keySet())
			comst.add(c);
		add(output);
	}

	void add(String output)
	{
		characterlocation();
		char intt[] = new char[orgst.size()];
		for(Character co : comst)
			for(Character cf : charloc.keySet())
				if (cf == co)
					for (int i = 0 ; i<charloc.get(cf).size() ; i++)
						intt[charloc.get(cf).get(i)]=co;

		String s = "";
		for (int i = 0 ; i<intt.length ; i++)
			s=s+intt[i];

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output), "utf-8"))) {
			writer.write(s);
			writer.close();
		}
		catch (IOException e) {	e.printStackTrace(); }
	}

}

