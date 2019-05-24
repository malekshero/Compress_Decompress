package RLE;
import java.util.*;
import java.io.*;
public class RLE extends Compression {
	private List<Byte> _neutre=null;
	private static int _octet=1;
	
	public RLE(String ad){
		raz();
		_depart=ad;
	}
	
	public void raz(){
		_neutre=null;
		_octet=1;
	}
	
	public void chercherNeutre(boolean v) throws IOException{
		BufferedInputStream fluxLecture=new BufferedInputStream(new FileInputStream(_depart));
		
		Set<List<Byte>> dejaVu=new HashSet<List<Byte>>();
		byte[] buff=new byte[_octet];
		while(fluxLecture.read(buff)==_octet){
			List<Byte> l=new ArrayList<Byte>();
			for(byte by:buff)
				l.add(by);
			dejaVu.add(l);
		}
		fluxLecture.close();
		List<Byte> l=new ArrayList<Byte>();
		for(int i=0;i<_octet;i++){
			l.add(Byte.MIN_VALUE);
		}

		try{
			while(dejaVu.contains(l)){
				l=incrementer(l);
			}
			_neutre=l;

		}
		catch(NullPointerException e){
			System.out.println(e.getMessage());
			_octet++;
			chercherNeutre(v);
		}
	}
	
	public void compresser(String fin, boolean v){
		long temps=-System.currentTimeMillis();
		if(v){
			System.out.println("-------- COMPRESSION--------");
			System.out.println("from                    : "+_depart);
			System.out.println("To                  : "+fin);
		}
		try{
			chercherNeutre(v);
			
			BufferedInputStream fluxLecture=new BufferedInputStream(new FileInputStream(_depart));
			BufferedOutputStream fluxEcriture=new BufferedOutputStream(new FileOutputStream(fin));
			fluxEcriture.write(_octet);
			for(byte by:_neutre){
				fluxEcriture.write(by);
			}
			
			int nb=0;
			byte[] buff=new byte[_octet];
			byte[] prebuff=new byte[_octet];

			fluxLecture.mark(_octet);
			fluxLecture.read(buff);
			List<Byte> lu=new ArrayList<Byte>(),dernier=new ArrayList<Byte>();
			for(byte by:buff){
				lu.add(by);
				dernier.add(by);
			}
			fluxLecture.reset();
			int nbLu=0;
			

			while((nbLu=fluxLecture.read(buff))==_octet){
				lu=new ArrayList<Byte>();
				for(byte by:buff)
					lu.add(by);
				if(dernier.equals(lu) && nb<Byte.MAX_VALUE-1){
					nb++;
				}
				else{
					ecrire(dernier,nb,fluxEcriture);
					for(int i=0;i<lu.size();i++){
						dernier.set(i, lu.get(i));
					}
					nb=1;
				}
				for(int i=0;i<_octet;i++){
					prebuff[i]=buff[i];
				}
			}

			if(nbLu!=-1){ 
				fluxEcriture.write(prebuff);
				if(nbLu!=_octet)
					for(int i=0;i<nbLu;i++)
						fluxEcriture.write(buff[i]);
			}
			else{
				ecrire(dernier,nb-1,fluxEcriture);
			}
			
			fluxLecture.close();
			fluxEcriture.close();
			System.out.println("OK");
			temps+=System.currentTimeMillis();
			System.out.println("Compress ends "+temps+" ms");
			CSP.CompressRatio(_depart, fin);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void ecrire(List<Byte> carac, int nb, BufferedOutputStream b2) throws IOException{
		if(nb==1)
			for(Byte by:carac)
				b2.write(by);
		else if(nb==2){
			for(Byte by:carac)
				b2.write(by);
			for(Byte by:carac)
				b2.write(by);
		}
		else{
			for(Byte by:_neutre)
				b2.write(by);
			b2.write(nb);
			for(Byte by:carac)
				b2.write((byte)by);
		}
	}
	
	public List<Byte> incrementer(List<Byte> l) throws NullPointerException {
		for(int i=0;i<l.size();i++){
			if(l.get(i)==Byte.MAX_VALUE){
				l.set(i, Byte.MIN_VALUE);
			}
			else{
				l.set(i, (byte)(l.get(i)+1));
				return l;
			}
		}
		throw new NullPointerException("No ("+_octet+")");
	}
	
	public void decompresser(String fin, boolean v){
		long temps=-System.currentTimeMillis();
		if(v){
			System.out.println("-------- DECOMPRESSION------");
			System.out.println("from                    : "+_depart);
			System.out.println("To                      : "+fin);
		}
		try{
			BufferedInputStream fluxLecture=new BufferedInputStream(new FileInputStream(_depart));
			BufferedOutputStream fluxEcriture=new BufferedOutputStream(new FileOutputStream(fin));
			
			_octet=fluxLecture.read();
			byte[] buff=new byte[_octet];
			fluxLecture.read(buff);
			byte[] neutre=new byte[_octet];

			for(int i=0;i<_octet;i++) {
				neutre[i] = buff[i];
			}
			int nbLu=0;
			while((nbLu=fluxLecture.read(buff))==_octet){
				int i=0;
				boolean different=false;
				while(i<_octet && !different){
					if(buff[i]!=neutre[i])
						different=true;
					i++;
				}
				if(different){
					fluxEcriture.write(buff);
				}
				else{
					int nb=fluxLecture.read();
					fluxLecture.read(buff);
					for(int j=0;j<nb;j++)
						fluxEcriture.write(buff);
				}
			}

			if(nbLu==-1){
				fluxEcriture.write(buff);
			}
			else if(nbLu!=_octet)
				for(int i=0;i<nbLu;i++)
					fluxEcriture.write(buff[i]);
			
			fluxLecture.close();
			fluxEcriture.close();

			temps+=System.currentTimeMillis();
			System.out.println("End of decompression "+temps+" ms");
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
}
