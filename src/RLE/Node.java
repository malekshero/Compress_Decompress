package RLE;

import java.util.*;

public class Node<T> {
	public Node<T> droite;
	public Node<T> gauche;
	public int poid;
	public T valeur;
	public final T DEF=null;
	
	public Node(Node<T> tdroite, Node<T> tgauche){
		droite=tdroite;
		gauche=tgauche;
		valeur=DEF;
		if(droite!=null && gauche!=null)
			poid=droite.poid+gauche.poid;
		else poid=0;
	}
	
	public Node(T tvaleur, int frequence){
		poid=frequence;
		valeur=tvaleur;
		droite=null;
		gauche=null;
	}
	
	public String toString(){
		return ""+poid;
	}
}

class NodeComparator<T> implements Comparator<Node<T>>{
	public int compare(Node<T> n1, Node<T> n2){
		if(n1.poid>n2.poid)return 1;
		if(n1.poid==n2.poid)return 0;
		return -1;
	}
}
