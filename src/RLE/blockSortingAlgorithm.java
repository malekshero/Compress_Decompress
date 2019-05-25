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

public class blockSortingAlgorithm extends Compression{
	public static final int TAILLE_MAX=100000;
	private int _pos=0;
	public void compresser(String fin, boolean v){
		if(v){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("From                    : "+_depart);
			System.out.println("to                  	: "+fin);
		}
		long tps=-System.currentTimeMillis();
		try{
			BufferedInputStream fluxLecture=new BufferedInputStream(new FileInputStream(_depart));
			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fin)));
			int tailleFichier=CSP.tailleDe(_depart);
			int complet=tailleFichier/TAILLE_MAX;
			fluxEcriture.writeInt(complet);

			byte[] donnesOriginales=null, donnesCodes=null;
			for(int i=0;i<complet;i++){
				donnesOriginales=new byte[TAILLE_MAX];
				fluxLecture.read(donnesOriginales); // on lit
				donnesCodes=compresserBout(donnesOriginales); // on code
				fluxEcriture.writeInt(_pos);
				fluxEcriture.writeInt(donnesCodes.length);
				fluxEcriture.write(donnesCodes);
			}
			donnesOriginales=new byte[tailleFichier%TAILLE_MAX];
			fluxLecture.read(donnesOriginales);
			donnesCodes=compresserBout(donnesOriginales);
			fluxEcriture.writeInt(_pos);
			fluxEcriture.writeInt(donnesCodes.length);
			fluxEcriture.write(donnesCodes);
			
			fluxLecture.close();
			fluxEcriture.close();
			
			tps+=System.currentTimeMillis();
			System.out.println("Compressions ended "+tps+" ms");
			CSP.CompressRatio(_depart, fin);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	public void decompresser(String fin, boolean v){

		try{
			DataInputStream fluxLecture=new DataInputStream(new BufferedInputStream(new FileInputStream(_depart)));
			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fin)));
			int complet=fluxLecture.readInt(), pos, taille;
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
		}
		catch(IOException e){
			System.out.println(e);
		}
		
	}
	public byte[] compresserBout(final byte[] donneesClair){
		final int taille=donneesClair.length;
		StringBuffer strB=new StringBuffer();
		for(byte by:donneesClair){
			strB.append((char)by);
		}
		class Colonne{
			int colonne;
			Colonne(int col){
				colonne=col;
			}
			public byte get(int pos){
				return (donneesClair[(taille+pos-colonne)%taille]);
			}
		}
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
		List<Colonne> l=new ArrayList<Colonne>();
		for(int i=0;i<taille;i++){
			l.add(new Colonne(i));
		}
		Collections.sort(l,new ColonneComparator());
		int pos=-1;
		byte[] rep=new byte[taille];
		for(int i=0;i<taille;i++){
			if(l.get(i).colonne==0)
				pos=i;
			rep[i]=l.get(i).get(taille-1);
		}
		_pos=pos;
		return rep;
	}
	
	public byte[] decompresserBout(int pos, int taille, byte[] donneesComp){
		Map<Byte,List<Integer>> indexIni= new HashMap<Byte,List<Integer>>();
		for(int i=0;i<taille;i++){
			if(!indexIni.containsKey(donneesComp[i]))
				indexIni.put(donneesComp[i], new ArrayList<Integer>());
			indexIni.get(donneesComp[i]).add(i);
		}
		List<Byte> listeTrie=new ArrayList<Byte>();
		for(byte by:donneesComp)
			listeTrie.add(by);
		Collections.sort(listeTrie);
		Map<Byte,List<Integer>> indexFin =new HashMap<Byte,List<Integer>>();
		for(int i=0;i<taille;i++){
			if(!indexFin.containsKey(listeTrie.get(i)))
				indexFin.put(listeTrie.get(i), new ArrayList<Integer>());
			indexFin.get(listeTrie.get(i)).add(i);
		}
		byte[] donneesClair=new byte[taille];
		for(int i=0;i<taille;i++){
			byte lu=listeTrie.get(pos);
			donneesClair[i]=lu;

			int nblu=-1, j=0;
			List<Integer> it=indexFin.get(lu);
			while(nblu==-1){
				if(it.get(j)==pos)
					nblu=j;
				j++;
			}
			pos=indexIni.get(lu).get(nblu);
		}
		return donneesClair;
	}

	public void raz(){
		_depart=null;
	}

}
