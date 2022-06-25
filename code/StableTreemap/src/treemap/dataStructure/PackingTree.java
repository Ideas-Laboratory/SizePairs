package treemap.dataStructure;

import java.util.ArrayList;

public class PackingTree {
	private PackingTree leftChild;
	private PackingTree rightChild;
	private double changingTotal;
	private ArrayList<Double> changing;
	
	public double getChangingTotal() {
		return changingTotal;
	}
	
	public ArrayList<Double> getChanging(){
		return changing;
	}	

	public PackingTree getLeftChild() {
		return leftChild;
	}
	
	public PackingTree getRightChild() {
		return rightChild;
	}
	
	public void setChangingTotal(double changingTotal) {
		this.changingTotal = changingTotal;
	}
	
	public void setChanging(double changing[]) {
		for (int i=0; i<changing.length; i++) {
			this.changing.set(i,changing[i]);
		}
	}
	
	public void setLeftChild(PackingTree leftChild) {
		this.leftChild = leftChild;
	}
	
	public void setRightChild(PackingTree rightChild) {
		this.rightChild = rightChild;
	}
}
