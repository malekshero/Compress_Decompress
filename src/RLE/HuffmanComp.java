package RLE;
import java.io.*;
import java.util.*;
public class HuffmanComp extends Compression{
	private Map<Short,String> tableCodage;
	private Map<String,Short> tableDecodage;
	private Map<Short,Integer> nombreOccurences;
	private Node<Short> racineArbre;
	private int tailleFichier;
	public final static int TAILLE_ECR=16;
	private long temps=0;
	private byte dernierCarac=-1;

	public HuffmanComp(){
		nombreOccurences=new HashMap<Short,Integer>();
		tableCodage= new HashMap<Short,String>();
		tableDecodage= new HashMap<String,Short>();
		racineArbre=null;
		tailleFichier=0;
		_depart=new String();
	}

	public void raz(){
		nombreOccurences=new HashMap<Short,Integer>();
		tableCodage= new HashMap<Short,String>();
		tableDecodage= new HashMap<String,Short>();
		racineArbre=null;
		tailleFichier=0;
		_depart=new String();
	}

	public HuffmanComp(String adresseFichierDepart){
		this();
		_depart=adresseFichierDepart;
	}

	public void lireFichier(boolean verbeux){
		try{
			BufferedInputStream fluxLectureBuff= new BufferedInputStream(new FileInputStream(_depart));
			DataInputStream fluxLectureData=new DataInputStream(fluxLectureBuff);
			boolean aTermin=false;
			short caracLu=0;


			while(!aTermin){
				try{
					caracLu=fluxLectureData.readShort();

					fluxLectureBuff.mark(1);
					if(!nombreOccurences.containsKey(caracLu)) nombreOccurences.put(caracLu,1);
					else nombreOccurences.put(caracLu,nombreOccurences.get(caracLu)+1);
				}
				catch(EOFException e){//crit�re d'arret :
					aTermin=true;
				}
			}

			File fichier=new File(_depart);
			tailleFichier=(int)fichier.length();

			if(tailleFichier%2==1){
				fluxLectureBuff.reset();
				dernierCarac=(byte)fluxLectureBuff.read();
			}


			fluxLectureData.close();
			fluxLectureBuff.close();
		}
		catch(IOException e){
			System.err.println("Playback error "+e);
		}
	}

	public void monterArbre(boolean v){
		List<Node<Short>> arbre=new ArrayList<Node<Short>>();
		Set<Short> shortsPresents=nombreOccurences.keySet();
		for(Short k : shortsPresents){
			Node<Short> n = new Node<Short>(k,nombreOccurences.get(k));
			arbre.add(n);
		}

		Collections.sort(arbre, new NodeComparator());
		while(arbre.size()>1){
			Node<Short> n= new Node<Short>(arbre.get(0),arbre.get(1));
			arbre.remove(0);
			arbre.remove(0);
			if(arbre.size()==0){
				arbre.add(n);
			}
			else if(n.poid>arbre.get(arbre.size()-1).poid){
				arbre.add(arbre.size(),n);
			}
			else{

				int a=1, b=arbre.size(),c=0;
				int p1=0,p2=n.poid;
				while(b-a>10){
					c=a+(int)((b-a)/2.);
					p1=arbre.get(c).poid;
					if(p1<=p2)a=c;
					else b=c;
				}

				int index=-1;
				a--;
				while(a<arbre.size() && index==-1){
					if(arbre.get(a).poid>=p2)index=a;
					else a++;
				}
				arbre.add(index,n);
			}
		}
		racineArbre=arbre.get(0);

	}


	public void chargerListe(boolean v){

		Set<Short> s=tableCodage.keySet();
		tableDecodage=new HashMap<String,Short>();
		for(short k : s){
			tableDecodage.put(tableCodage.get(k),k);
		}

	}


	public void lireArbre(Node<Short> n, String str){
		if(n.valeur!=n.DEF){
			tableCodage.put(n.valeur, str);
		}
		else{
			lireArbre(n.droite,str+"1");
			lireArbre(n.gauche,str+"0");
		}
	}

	public void compresser(String fin, boolean v){
		temps=-System.currentTimeMillis();
		File f1 = new File(fin);
		if(v){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("From                    : "+_depart);
			System.out.println("To                  : "+"Files/Compressed/"+f1.getName());

		}
		lireFichier(v);
		monterArbre(v);
		lireArbre(racineArbre,"");
		chargerListe(v);
		try{

			DataInputStream fluxLecture=new DataInputStream(new BufferedInputStream(new FileInputStream(_depart)));
			StringBuffer chaneBinaire=new StringBuffer();
			boolean aTermin=false;
			short shortLu=0;
			while(!aTermin){
				try{
					shortLu=fluxLecture.readShort();
					chaneBinaire.append(tableCodage.get(shortLu));
				}
				catch(IOException e){
					aTermin=true;
				}
			}
			fluxLecture.close();
			short[] liste=new short[chaneBinaire.length()/(TAILLE_ECR)+1];
			int nombreBitsEcrits=0;
			int nombreEcritures=0;
			while(chaneBinaire.length()>nombreBitsEcrits+TAILLE_ECR){
				liste[nombreEcritures]=string2Short(chaneBinaire.substring(nombreBitsEcrits, nombreBitsEcrits+TAILLE_ECR));
				nombreBitsEcrits+=TAILLE_ECR;
				nombreEcritures++;
			}
			liste[nombreEcritures]=string2Short(chaneBinaire.substring(nombreBitsEcrits));
			int tailleDernierEntier=TAILLE_ECR-chaneBinaire.length()%TAILLE_ECR;
			int tailleTable=tableDecodage.size();// taille de la table
			int[] valeur16b=new int[tailleTable];// valeurs en clair
			byte[] nbBitValeur16b=new byte[tailleTable];// nb de bit de la valeur cod�e associ�e
			short[] code=new short[tailleTable];// valeur cod�e associ�e
			Set<String> setValeur16=tableDecodage.keySet();
			Iterator<String> iteratorValeur16=setValeur16.iterator();
			for(int i2=0;i2<tailleTable;i2++){
				String s=iteratorValeur16.next();
				valeur16b[i2]=Integer.parseInt(s,2);
				nbBitValeur16b[i2]=(byte)s.length();
				code[i2]=tableDecodage.get(s);
			}
			HuffmanCompWrite hme=new HuffmanCompWrite(valeur16b,nbBitValeur16b,code,liste,tailleFichier,tailleDernierEntier,dernierCarac);

			ObjectOutputStream fluxEcriture=new ObjectOutputStream(new FileOutputStream("Files/Compressed/"+f1.getName()));
			fluxEcriture.writeObject(hme);
			fluxEcriture.close();
			temps+=System.currentTimeMillis();
			System.out.println("Compress ends "+temps+" ms");
			CSP.CompressRatio(_depart, "Files/Compressed/"+f1.getName());
		}
		catch(IOException e){
			System.err.println(e);
		}
	}

	public void decompresser(String fin,boolean v){
		File f1 = new File(_depart);
		temps=-System.currentTimeMillis();
		if(v){
			System.out.println("-------- DECOMPRESSION------");
			System.out.println("from                     : "+"Files/Compressed/"+f1.getName());
			System.out.println("to                  : "+fin);

		}
		try{
			File f2 = new File(fin);
			System.out.println(f2.getName());
			ObjectInputStream fluxLecture=new ObjectInputStream(new BufferedInputStream(new FileInputStream("Files/Compressed/"+f2.getName()+"c")));
			HuffmanCompWrite hme=(HuffmanCompWrite) fluxLecture.readObject();
			fluxLecture.close();
			if(v)System.out.println("OK");
			int tailleTable=hme.decI.length;
			tableDecodage=new HashMap<String,Short>();// 2x plus rapide que TreeMap
			try{
				for(int i=0;i<tailleTable;i++){
					String CaracDecode=int2bin(hme.decI[i],hme.decT[i]);
					tableDecodage.put(CaracDecode, hme.decS[i]);
				}
			}
			catch(Exception e){
			}
			StringBuffer ChaneCode=new StringBuffer();
			String code16b;
			for(short i:hme.d){

				code16b=Integer.toBinaryString(i);
				if(i<0)code16b=code16b.substring(16);
				while(code16b.length()<TAILLE_ECR){
					code16b="0"+code16b;
				}
				ChaneCode.append(code16b);
			}
			ChaneCode.delete(ChaneCode.length()-TAILLE_ECR,ChaneCode.length()-TAILLE_ECR+hme.td);

			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream("Files/Decompressed/"+f2.getName())));
			int t=0;
			Set<String> clef=tableDecodage.keySet();
			int tailleMin=30;
			for(String s:clef){
				if(s.length()<tailleMin)tailleMin=s.length();
			}
			boolean aTermin=false;
			while(!aTermin){
				int j=tailleMin;
				try{
					while(tableDecodage.get(ChaneCode.substring(t, t+j))==null){
						j++;
					}
					fluxEcriture.writeShort(tableDecodage.get(ChaneCode.substring(t, t+j)));
				}
				catch(StringIndexOutOfBoundsException e){
					aTermin=true;
				}
				t+=j;
			}
			if(hme.p!=-1){
				fluxEcriture.write(hme.p);
			}
			temps+=System.currentTimeMillis();
			System.out.println(" decompression ends "+temps+" ms");
			fluxEcriture.close();
		}
		catch(IOException e){
			System.err.println(e+" ");
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
	}

	public static short string2Short(String str){
		if(str.length()==TAILLE_ECR){
			if(str.charAt(0)=='0')return Short.parseShort(str,2);
			else{
				return (short) (Short.parseShort(str.substring(1),2)-Short.MAX_VALUE-1);
			}
		}
		return Short.parseShort(str,2);
	}

}

class HuffmanCompWrite implements Serializable{
	int[] decI;
	byte[] decT;
	short[] decS;
	short[] d;
	int td;
	double t;
	byte p;
	HuffmanCompWrite(int[] chaneBinaire, byte[] tailleChane, short[] valeurChane, short[] donnes, double taille, int tdi, byte dernierByte){
		decI=chaneBinaire;
		decT=tailleChane;
		decS=valeurChane;
		d=donnes;
		t=taille;
		td=tdi;
		p=dernierByte;
	}
}