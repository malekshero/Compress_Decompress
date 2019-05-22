package RLE;
import java.util.*;
import java.io.*;

public class DicoComp16 extends Compression{
	private Map<List<Byte>,String> dictionnaire=null;
	// une map qui faut correspondre rapidement un mot avec son index
	private List<List<Byte>> listeMotsPresents=null;
	// la liste ordonn�e des mots de la map (uniques)
	private List<List<Byte>> texteMotParMot=null;
	// la listes des mots dans le fichier, ordonn� par apparition (non uniques)
	private int nbBit;
	// la taille necessaire pour coder l'index
	
	public DicoComp16(String adresseFichierDepart){
		_depart=adresseFichierDepart;
	}
	
	// compresse le fichier vers adresseFichierFinal
	public void compresser(String adresseFichierFinal, boolean verbeux){
		long temps=-System.currentTimeMillis();
		if(verbeux){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("De                    : "+_depart);
			System.out.println("Vers                  : "+adresseFichierFinal);
			System.out.println("Methode               : Dictionnaire");
		}
		try{
			if(verbeux)System.out.print(">> Dictionnaire...         ");
			dictionnaire=new HashMap<List<Byte>,String>();
			listeMotsPresents=new ArrayList<List<Byte>>();
			texteMotParMot=new ArrayList<List<Byte>>();
			
			DataInputStream fluxLecture=new DataInputStream(new BufferedInputStream(new FileInputStream(_depart)));
			
			// le Set sert � obtenir rapidement tous les mots (plus rapide que le .put des maps) :
			Set<List<Byte>> SetMotsPresents=new HashSet<List<Byte>>();
			
			// puis on separe les mots en mettant � jour SetMotsPresents et texteMotParMot :
			List<Byte> bytesDepuisEspace=new ArrayList<Byte>();
			byte byteLu=0;
			while((byteLu=(byte)fluxLecture.read())!=-1){
				if(byteLu==' '){
					SetMotsPresents.add(bytesDepuisEspace);
					texteMotParMot.add(bytesDepuisEspace);
					bytesDepuisEspace=new ArrayList<Byte>();
				}
				else{
					bytesDepuisEspace.add(byteLu);
				}
			}
			// pour le dernier mot :
			SetMotsPresents.add(bytesDepuisEspace);
			texteMotParMot.add(bytesDepuisEspace);
			
			fluxLecture.close();
			if(verbeux)System.out.println("OK ("+SetMotsPresents.size()+")");
			
			// on calcule le nombre de bit pour coder les index :
			if(verbeux)System.out.print(">> Poid des index...       ");
			nbBit=1;
			while(SetMotsPresents.size()>Math.pow(2, nbBit))nbBit++;
			if(verbeux)System.out.println("OK ("+nbBit+")");
			
			// on ordonne les mots de Set avec listeMotsPresents et on met � jour le dictionnaire
			if(verbeux)System.out.print(">> Indexation...           ");
			listeMotsPresents=new ArrayList<List<Byte>>(SetMotsPresents);
			int i=0;
			for(List<Byte> l:listeMotsPresents){
				dictionnaire.put(l,int2bin(i,nbBit));// int2bin renvoie une String de '0' et de '1' correspondant au texte
				i++;
			}
			if(verbeux)System.out.println("OK");
			
			if(verbeux)System.out.print(">> Encodage...             ");
			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(adresseFichierFinal)));
			
			// on ajoute au debut du fichier :
			fluxEcriture.writeInt(listeMotsPresents.size());// le nombre de mots du dictionnaire
			fluxEcriture.writeInt(nbBit);// la taille des index 
			fluxEcriture.writeInt(texteMotParMot.size());// le nombre de mots du texte
			
			// on ecrit la taille de chaque mot sur 16 bit
			// (pas de s�parateur entre les mots dans le fichier compress�)
			for(List<Byte> mot:listeMotsPresents)
				fluxEcriture.writeShort(mot.size());
			
			// puis on �crit tous les mots du dictionnaire
			byte[][] dictionnaireByteArray=new byte[listeMotsPresents.size()][];
			for(int j=0;j<listeMotsPresents.size();j++){
				dictionnaireByteArray[j]=new byte[listeMotsPresents.get(j).size()];
				// dans certains cas, on obtient une erreur si on ecrit tous les bits � la fois :
				for(int k=0;k<listeMotsPresents.get(j).size();k++){
					dictionnaireByteArray[j][k]=listeMotsPresents.get(j).get(k);
					fluxEcriture.writeByte(dictionnaireByteArray[j][k]);
				}
			}
			
			//on ecrit les index dans une cha�ne :
			StringBuffer chaneAEcrire=new StringBuffer();
			for(List<Byte> l:texteMotParMot){
				chaneAEcrire.append(dictionnaire.get(l));
			}
			
			// on separe la cha�ne en strings de '0' et de '1' de taille 8 et on ecrit le nombre correspondant
			byte[] donneesI=new byte[(texteMotParMot.size()*nbBit)/8+1];
			for(int j=0;j<donneesI.length-1;j++){
				donneesI[j]=string2Byte(chaneAEcrire.substring(j*8, j*8+8));
			}
			// le dernier caract�re :
			donneesI[donneesI.length-1]=string2Byte(chaneAEcrire.substring(donneesI.length*8-8));
			for(byte b:donneesI)
				fluxEcriture.writeByte(b);
			
			fluxEcriture.close();
			System.out.println("OK");
			temps+=System.currentTimeMillis();
			System.out.println("Fin de la compression en "+temps+" ms");
			CSP.tauxComp(_depart, adresseFichierFinal);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	
	
	public void decompresser(String adresseFichierFinal, boolean verbeux){
		long temps=-System.currentTimeMillis();
		if(verbeux){
			System.out.println("------- DECOMPRESSION-------");
			System.out.println("De                    : "+_depart);
			System.out.println("Vers                  : "+adresseFichierFinal);
			System.out.println("Methode               : Dictionnaire");
		}
		try{
			dictionnaire=new HashMap<List<Byte>,String>();
			listeMotsPresents=new ArrayList<List<Byte>>();
			texteMotParMot=new ArrayList<List<Byte>>();
			
			DataInputStream fluxLecture=new DataInputStream(new BufferedInputStream(new FileInputStream(_depart)));
			
			// on lit la taille du dictionnaire, des index et du fichier :
			if(verbeux)System.out.print(">> Taille dico...          ");
			int taille=fluxLecture.readInt();
			if(verbeux)System.out.println("OK ("+taille+")");
			if(verbeux)System.out.print(">> Poid des index...       ");
			nbBit=fluxLecture.readInt();
			if(verbeux)System.out.println("OK ("+nbBit+")");
			int nbMots=fluxLecture.readInt();
			
			// on lit la taille des mots du dictionnaire :
			if(verbeux)System.out.print(">> Lecture des tailles...  ");
			int[] tailles=new int[taille];
			for(int i=0;i<taille;i++){
				tailles[i]=fluxLecture.readShort();
			}
			if(verbeux)System.out.println("OK");
			
			// on lit le dictionnaire
			if(verbeux)System.out.print(">> Lecture du dico...      ");
			for(int i=0;i<taille;i++){
				List<Byte> mot=new ArrayList<Byte>();
				for(int j=0;j<tailles[i];j++){
					mot.add(fluxLecture.readByte());
				}
				listeMotsPresents.add(mot);
			}
			if(verbeux)System.out.println("OK");
			if(verbeux)System.out.print(">> Encodage...             ");
			
			// on lit tous les index, en les ajoutant sous forme de cha�ne de '0' et de '1' � cha�neAEcrire :
			StringBuffer chaneAEcrire=new StringBuffer();
			try{// attendre EOF avec try est plus rapide qu'une boucle
				while(true){
					byte by=fluxLecture.readByte();
					chaneAEcrire.append(int2bin(by,8).substring(int2bin(by,8).length()-8));
				}
			}
			catch(EOFException e){
			}
			
			// on enl�ve les bits en trop :
			int bitEnTrop=chaneAEcrire.length()-nbMots*nbBit;
			if(bitEnTrop!=0){
				chaneAEcrire.delete(chaneAEcrire.length()-8+1, 1+chaneAEcrire.length()-8+bitEnTrop);
			}
			
			fluxLecture.close();
			if(verbeux)System.out.println("OK");
			if(verbeux)System.out.print(">> Ecriture...             ");
			
			DataOutputStream fluxEcriture=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(adresseFichierFinal)));
			
			// on ecrit chaque mot :
			boolean premierPassage=true;
			int i=0;
			try{
				while(true){
					if(premierPassage){
						// le premier mot ne commence pas par un espace si il est n'est pas "" :
						premierPassage=false;
					}
					else{
						fluxEcriture.writeByte(' ');
					}
					// on decode le numeros d'index en base 10 :
					int index=Integer.parseInt(chaneAEcrire.substring(i*nbBit,(i+1)*nbBit),2);
					// on recup�re le mot et on l'�crit :
					List<Byte> mot=listeMotsPresents.get(index);
					for(byte b:mot)
						fluxEcriture.writeByte(b);
					i++;
				}
			}
			catch(StringIndexOutOfBoundsException e){
				// fin de la boucle cassique
			}
			fluxEcriture.close();
			
			if(verbeux)System.out.println("OK");
			temps+=System.currentTimeMillis();
			System.out.println("Fin de la d�compression en "+temps+" ms");
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void raz(){
		_depart=null;
		dictionnaire=null;
		listeMotsPresents=null;
		texteMotParMot=null;
	}
	
	public static void main(String[] args){
		DicoComp16 dc16=new DicoComp16("test/k.html");
		dc16.testerToutTexte(true);
	}
	
}
