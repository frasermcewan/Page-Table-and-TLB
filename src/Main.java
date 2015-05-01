import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 
 * v1.0 Creating program to do the ace
 * 
 * v1.1 Creating input/output using a buffered reader to read in the file line by line
 * 
 * v1.2 Keeping all of the data within one class to minimize complexity, will modularise later
 * 
 * v1.3 Creating page table and initiazling it full of variables to check that if a page has been inserted into the table
 * 
 * v1.4 Created IF/THEN/ELSE loop to check to see if the page was in the page table or not
 * 
 * v1.5 Created backing storage using the code given to us with OSCWJ, all credit to the authors
 * 
 * v1.6 If page is not in the page table then page fault happens, and then we use backing memory and a page fault happens
 * 
 * v1.7 Program fully working without TLB
 * 
 * v1.8 Started work on modularisation, moved page table to its own class
 * 
 * v1.9 Ace 3 Draft Version 
 * 
 * v2.0 Moved page table into its own class, using a combination of getters and setters to set the correct frame number
 * 
 * v2.1 Started work on the TLB, using hash map and a queue to put it into operation
 * 
 * v2.2 Started trying to write a LRU algorithm for replacement, however could not get this to work
 * 
 * v2.3 Abandoned LRU for a FIFO algorithm, managed to get it working 
 * 
 * v2.4 Created print statements to print out the values and the addresses
 * 
 * v2.5 Finished Ace
 * 
 * 
 * 
 * In making this submission I confirm that I worked on this project individually and that all the code was written by me.
 *  I did not use any frameworks to generate code nor copy code.
 *  
 *  Signed Fraser Mcewan
 * 
 * @author Fraser McEwan 201240408
 *
 */

public class Main {

	// Initiizes variables for use in the program
	
	String input = "InputFile.txt";
	String number;
	int pNumber, fNumber, offset, pAddress, vAddress, counter, fcounter,
			value = 0;
	double pFaultCounter, TLBCounter, address, pFaultRate, TLBRate = 0;

	RandomAccessFile physMemory = null;
	pageTable pTable = new pageTable();

	byte[] backingStorage = new byte[256 * 256];
	Map<Integer, Integer> TLB = new HashMap<Integer, Integer>(16);
	Queue<Integer> TLBQ = new LinkedList<Integer>();

	
	// Main method to run program
	
	public static void main(String[] args) {
		try {
			new Main().run();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void run() throws NumberFormatException, IOException {

		// Creating Buffered Reader to read in the input file
		BufferedReader br = new BufferedReader(new FileReader(new File(input)));
		
		//Initializes values in the page table so that we can check to see if valid or not 
		pTable.initPTable();

		
		//Loops through file reading in each line until the end 
		while ((number = br.readLine()) != null) {

			//Takes the bottom 16 bits for the page number
			pNumber = (Integer.parseInt(number) >> 8);
		    //Takes the value for the offset by using the number and bitmasking it
			offset = (Integer.parseInt(number) & 0xFF);

			
			//Checks to see if the page is in the page table and if so enters loop
			if (TLB.containsKey(pNumber)) {
				fNumber = TLB.get(pNumber);
				pAddress = fNumber + offset;

				value = backingStorage[pAddress];

				System.out.println("Virtual Address: " + number + " "
						+ "Physical Address: " + pAddress + " " + "Value: "
						+ value);

				//Incrementing page table hits
				TLBCounter = TLBCounter + 1;
				//Incrementing the amount of addresses visited
				address = address + 1;

				
				
				//Checks to see if the page is in the page table
			} else if (pTable.getF(pNumber) != -1) {
				//Sets totals
				fNumber = pTable.getF(pNumber);
				pAddress = fNumber + offset;
				value = backingStorage[pAddress];
				address = address + 1;

				System.out.println("Virtual Address: " + number + " "
						+ "Physical Address: " + pAddress + " " + "Value: "
						+ value);
			}
			
			//Else page fault happens and file needs to be written

			else {
				//Creates a new file 
				physMemory = new RandomAccessFile(new File("BACKING_STORE"),
						"r");
				physMemory.seek(pNumber * 256);
				physMemory.read(backingStorage, counter, 256);

				counter += 256;
				// Sets frame number
				pTable.setF(pNumber, (fcounter * 256));
				fNumber = pTable.getF(pNumber);
				pAddress = fNumber + offset;
				value = backingStorage[pAddress];

				System.out.println("Virtual Address: " + number + " "
						+ "Physical Address: " + pAddress + " " + "Value: "
						+ value);

				//Increments totals to keep track locations and closes memory
				pFaultCounter = pFaultCounter + 1;
				fcounter = fcounter + 1;
				address = address + 1;
				physMemory.close();

			}
			//Employs a FIFO algorithm to replenish the TLB
			if (TLB.size() != 16) {
				TLB.put(pNumber, fNumber);
				TLBQ.add(pNumber);
			} else {
				TLB.remove(TLBQ.poll());
			}

		}

		br.close();
		//Calculates totals from variables and prints them out
		pFaultRate = (pFaultCounter / address) * 100;
		TLBRate = (TLBCounter / address) * 100;
		System.out.println("Page Faults Rate:" + (pFaultRate) + "%");
		System.out.println("TLB Hit Rate:" + (TLBRate) + "%");

	}

}
