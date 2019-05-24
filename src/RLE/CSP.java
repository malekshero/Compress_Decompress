package RLE;

import java.io.*;
import java.util.*;
import java.lang.*;

enum TypeFichier{TEXTE, HYBRIDE, DONNEES_COMPRESSEES, INCONNU};

public class CSP {
	public static boolean VERB=true;
	private static final int LETTRE=0,ESPACE=1,MOYENNE=2,ECART_TYPE=3;
	
	public static boolean estLeMeme(String s1, String s2){
		int manquant=0;
		int mauvais=0;
		try{
			FileInputStream f1= new FileInputStream(s1);
			FileInputStream f2= new FileInputStream(s2);
			BufferedInputStream g1=new BufferedInputStream(f1);
			BufferedInputStream g2=new BufferedInputStream(f2);
			int i1=g1.read();
			int i2=g2.read();
			while(i1!=-1){
				if(i2!=-1){
					if(i1!=i2)mauvais++;
					i2=g2.read();
				}
				else manquant++;
				i1=g1.read();
			}
			while(g2.read()!=-1)manquant++;
			f1.close();
			f2.close();
		}
		catch(IOException e){
			System.out.println("FICHIER(S) INTROUVABLE(S)");
		}
		
		return (mauvais==0)&&(manquant==0);
	}
	
	public static double CompressRatio(String depart, String arrive){
		double taux=100-(double)tailleDe(arrive)/tailleDe(depart)*100;
		System.out.printf("CompressionRatio : %.1f%s\n",taux,"%");
		return taux;
	}
	
	public static int tailleDe(String ad){
		int taille=0;
		try{
			FileInputStream f= new FileInputStream(ad);
			BufferedInputStream b=new BufferedInputStream(f);
			while(b.read()!=-1)taille++;
			b.close();
		}
		catch(IOException e){
			System.err.println(e);
		}
		return taille;
	}

}
