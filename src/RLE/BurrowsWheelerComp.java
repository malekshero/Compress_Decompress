package RLE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class BurrowsWheelerComp extends Compression{
	// taille de chaque "bout de compression"
	public static final int TAILLE_MAX=100000;
	// position du debut de la boucle de permutation, pour �viter de la rechercher plus tard
	private int _pos=0;
	
	public BurrowsWheelerComp(String ad){
		_depart=ad;
	}
	
	// compresser un fichier entier
	public void compresser(String fin, boolean v){
		if(v){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("De                    : "+_depart);
			System.out.println("Vers                  : "+fin);
			System.out.println("Methode               : BWT");
		}
		long tps=-System.currentTimeMillis();
		try{
			if(v)System.out.print(">> Compression des blocs   ");
			BufferedInputStream fluxLecture=new BufferedInputStream(new FileInputStream(_depart));
			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fin)));
			
			// on cherche le nombre de bout que l'on devra coder enti�rement :
			int tailleFichier=CSP.tailleDe(_depart);
			int complet=tailleFichier/TAILLE_MAX;
			fluxEcriture.writeInt(complet);
			System.out.println(" ("+(complet+1)+")");
			
			byte[] donnesOriginales=null, donnesCodes=null;
			
			// on effecture chaque bout complet
			for(int i=0;i<complet;i++){
				donnesOriginales=new byte[TAILLE_MAX];
				fluxLecture.read(donnesOriginales); // on lit
				donnesCodes=compresserBout(donnesOriginales); // on code
				// on ecrit la position, la taille et les donn�es
				fluxEcriture.writeInt(_pos);
				fluxEcriture.writeInt(donnesCodes.length);
				fluxEcriture.write(donnesCodes);
			}
			// puis on fait pareil pour le dernier, qui est incomplet :
			donnesOriginales=new byte[tailleFichier%TAILLE_MAX];
			fluxLecture.read(donnesOriginales);
			donnesCodes=compresserBout(donnesOriginales);
			fluxEcriture.writeInt(_pos);
			fluxEcriture.writeInt(donnesCodes.length);
			fluxEcriture.write(donnesCodes);
			
			fluxLecture.close();
			fluxEcriture.close();
			
			tps+=System.currentTimeMillis();
			System.out.println("Fin de la compression en "+tps+" ms");
			CSP.tauxComp(_depart, fin);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	// decompresser un fichier entier
	public void decompresser(String fin, boolean v){
		if(v){
			System.out.println("------- DECOMPRESSION-------");
			System.out.println("De                    : "+_depart);
			System.out.println("Vers                  : "+fin);
			System.out.println("Methode               : BWT");
		}
		long temps=-System.currentTimeMillis();
		try{
			if(v)System.out.print(">> D�compression des blocs   ");
			
			DataInputStream fluxLecture=new DataInputStream(new BufferedInputStream(new FileInputStream(_depart)));
			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fin)));
			
			// on lit le nombre de bouts complets
			int complet=fluxLecture.readInt(), pos, taille;
			System.out.println("("+(complet+1)+")");
			
			// on proc�de comme pour la compression
			byte[] donneesComp, donneesClair;
			for(int i=0;i<complet;i++){
				pos=fluxLecture.readInt();
				taille=fluxLecture.readInt();
				donneesComp=new byte[taille];
				fluxLecture.read(donneesComp);
				donneesClair=decompresserBout(pos,taille,donneesComp);
				fluxEcriture.write(donneesClair);
			}
			pos=fluxLecture.readInt();
			taille=fluxLecture.readInt();
			donneesComp=new byte[taille];
			fluxLecture.read(donneesComp);
			donneesClair=decompresserBout(pos,taille,donneesComp);
			fluxEcriture.write(donneesClair);
			
			fluxLecture.close();
			fluxEcriture.close();
			temps+=System.currentTimeMillis();
			System.out.println("Fin de la d�compression en "+temps+" ms");
			CSP.tauxComp(_depart, fin);
		}
		catch(IOException e){
			System.out.println(e);
		}
		
	}
	
	// compresse un bout du fichier
	public byte[] compresserBout(final byte[] donneesClair){
		final int taille=donneesClair.length;
		
		// on converti les octets en caract�res
		StringBuffer strB=new StringBuffer();
		for(byte by:donneesClair){
			strB.append((char)by);
		}
		
		// on d�finit la classe Colonne, qui correspond � une translation de la cha�ne donneesClair.
		class Colonne{
			int colonne;// d�calage avec la cha�ne d'origine
			Colonne(int col){
				colonne=col;
			}
			// renvoie le caract�re � la position pos de cette translation
			public byte get(int pos){
				return (donneesClair[(taille+pos-colonne)%taille]);
			}
		}
		
		// on d�finit un comparateur sur Colonne qui les classe par ordre alphabetique
		class ColonneComparator implements Comparator<Colonne>{
			public int compare(Colonne c1, Colonne c2){
				for(int i=0;i<taille;i++){
					if(c1.get(i)>c2.get(i))
						return 1;
					if(c1.get(i)<c2.get(i))
						return -1;
				}
				return 0;
			}
		}
		
		// on fait toutes les translations possibles et on les classe
		List<Colonne> l=new ArrayList<Colonne>();
		for(int i=0;i<taille;i++){
			l.add(new Colonne(i));
		}
		Collections.sort(l,new ColonneComparator());
		
		// on cherche la position de la colonne d'origine
		int pos=-1;
		byte[] rep=new byte[taille];
		for(int i=0;i<taille;i++){
			if(l.get(i).colonne==0)
				pos=i;
			rep[i]=l.get(i).get(taille-1);
		}
		_pos=pos;
		// on renvoie la derni�re colonne :
		return rep;
	}
	
	public byte[] decompresserBout(int pos, int taille, byte[] donneesComp){
		// On fait une map avec les index initiaux :
		// elle associe � chaque Byte les index de toutes ses apparitions
		Map<Byte,List<Integer>> indexIni= new HashMap<Byte,List<Integer>>();
		for(int i=0;i<taille;i++){
			if(!indexIni.containsKey(donneesComp[i]))
				indexIni.put(donneesComp[i], new ArrayList<Integer>());
			indexIni.get(donneesComp[i]).add(i);
		}
		
		// on trie donneesComp dans une une liste :
		List<Byte> listeTrie=new ArrayList<Byte>();
		for(byte by:donneesComp)
			listeTrie.add(by);
		Collections.sort(listeTrie);
		
		// on cr�er une nouvelle Map des index sur le liste tri�e :
		Map<Byte,List<Integer>> indexFin =new HashMap<Byte,List<Integer>>();
		for(int i=0;i<taille;i++){
			if(!indexFin.containsKey(listeTrie.get(i)))
				indexFin.put(listeTrie.get(i), new ArrayList<Integer>());
			indexFin.get(listeTrie.get(i)).add(i);
		}
		
		// on decode la cha�ne :
		byte[] donneesClair=new byte[taille];
		for(int i=0;i<taille;i++){
			// on ajoute � donneesClair le caract�re � l'index pos
			byte lu=listeTrie.get(pos);
			donneesClair[i]=lu;
			
			// On cherche � conna�tre quelle num�ros d'apparition du caract�re nous avons lu � la position pos
			// on utilise la table indexFin pour aller plus vite. Elle est not�e nblu
			int nblu=-1, j=0;
			List<Integer> it=indexFin.get(lu);
			while(nblu==-1){
				if(it.get(j)==pos)
					nblu=j;
				j++;
			}
			// la nouvelle position est celle de la nblu-i�me apparition du caract�re lu.
			pos=indexIni.get(lu).get(nblu);
		}
		return donneesClair;
	}
	
	public double estimationComp(){
		System.out.println("Pas de changement de taille notable");
		return 1.;
	}
	
	public void raz(){
		_depart=null;
	}
	
	public static void main(String[] args){
		BurrowsWheelerComp bwc=new BurrowsWheelerComp("");
		bwc.tester("test/h.html","test/f",true);
	}
}
