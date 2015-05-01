public class pageTable {

	//Creates new page table 
	public int[] pTable = new int[256];
   //Initalizes page table with values
	public void initPTable() {
		for (int i = 0; i < 256; i++) {
			pTable[i] = -1;
		}
	}

	//Allows the program to get the frame
	public int getF(int pNumber) {
		return pTable[pNumber];
	}
	//Allows setting the frame number
	public void setF(int pNumber, int fNumber){
		pTable[pNumber] = fNumber;
	}
	
	
}
