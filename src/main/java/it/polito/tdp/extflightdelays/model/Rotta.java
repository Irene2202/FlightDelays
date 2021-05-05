package it.polito.tdp.extflightdelays.model;

public class Rotta {
	
	private Airport a1;
	private Airport a2;
	private int numVoli;
	
	public Rotta(Airport a1, Airport a2, int numVoli) {
		this.a1 = a1;
		this.a2 = a2;
		this.numVoli = numVoli;
	}
	
	public Airport getA1() {
		return a1;
	}
	
	public void setA1(Airport a1) {
		this.a1 = a1;
	}
	
	public Airport getA2() {
		return a2;
	}
	
	public void setA2(Airport a2) {
		this.a2 = a2;
	}
	
	public int getNumVoli() {
		return numVoli;
	}
	
	public void setNumVoli(int numVoli) {
		this.numVoli = numVoli;
	}

}
