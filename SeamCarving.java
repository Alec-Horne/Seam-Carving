import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SeamCarving {
	
	private int[][] energyMatrix;
	private EnergyPixel[][] seams;
	
	//function to calculate the energy map of a PGMPicture
	public void calculateEnergyMatrix(PGMPicture p)
	{
		energyMatrix = new int[p.height][p.width];
		for(int w = 0; w < p.height; w++){
			for(int h = 0; h < p.width; h++){
				if((w-1) < 0){
					//bottom left corner
					if((h-1) < 0)
						energyMatrix[w][h] = 0 + Math.abs(p.grayscale[w][h] - p.grayscale[w+1][h]) + 
							0 + Math.abs(p.grayscale[w][h] - p.grayscale[w][h+1]);
					//top left corner
					else if((h+1) >= p.width)
						energyMatrix[w][h] = 0 + Math.abs(p.grayscale[w][h] - p.grayscale[w+1][h]) + 
						Math.abs(p.grayscale[w][h] - p.grayscale[w][h-1]) + 0;
					//left side
					else
						energyMatrix[w][h] = 0 + Math.abs(p.grayscale[w][h] - p.grayscale[w+1][h]) + 
							Math.abs(p.grayscale[w][h] - p.grayscale[w][h-1]) + Math.abs(p.grayscale[w][h] - 
							p.grayscale[w][h+1]);
				}
					
				else if((w+1) >= p.height){
					//bottom right corner
					if(h-1 < 0)
						energyMatrix[w][h] = Math.abs(p.grayscale[w][h] - p.grayscale[w-1][h]) + 0 + 
							0 + Math.abs(p.grayscale[w][h] - p.grayscale[w][h+1]);
					//top right corner
					else if((h+1) >= p.width)
						energyMatrix[w][h] = Math.abs(p.grayscale[w][h] - p.grayscale[w-1][h]) + 0 + 
							Math.abs(p.grayscale[w][h] - p.grayscale[w][h-1]) + 0;
					//right side
					else
						energyMatrix[w][h] = Math.abs(p.grayscale[w][h] - p.grayscale[w-1][h]) + 0 + 
							Math.abs(p.grayscale[w][h] - p.grayscale[w][h-1]) + Math.abs(p.grayscale[w][h] -
							p.grayscale[w][h+1]);
				}
				//top side
				else if((h-1) < 0)
					energyMatrix[w][h] = Math.abs(p.grayscale[w][h] - p.grayscale[w-1][h]) +
							Math.abs(p.grayscale[w][h] - p.grayscale[w+1][h]) + 0 + Math.abs(p.grayscale[w][h] - 
							p.grayscale[w][h+1]);
				//bottom side
				else if((h+1) >= p.width)
					energyMatrix[w][h] = Math.abs(p.grayscale[w][h] - p.grayscale[w-1][h]) +
							Math.abs(p.grayscale[w][h] - p.grayscale[w+1][h]) + Math.abs(p.grayscale[w][h] - 
							p.grayscale[w][h-1]) + 0;
				//standard case	
				else
					energyMatrix[w][h] = Math.abs(p.grayscale[w][h] - p.grayscale[w-1][h]) +
							Math.abs(p.grayscale[w][h] - p.grayscale[w+1][h]) + Math.abs(p.grayscale[w][h] - 
							p.grayscale[w][h-1]) + Math.abs(p.grayscale[w][h] - p.grayscale[w][h+1]);
			}
		}
	}
	
	//function to compute the vertical seams matrix
	public void computeVerticalSeams(PGMPicture p){
		seams = new EnergyPixel[p.height][p.width];
		for (int i = 0; i < p.width; i++) {
			seams[0][i] = new EnergyPixel();
			seams[0][i].weight = energyMatrix[0][i];
			seams[0][i].previous = new Point(-1,-1);
		}
		
		for (int x = 1; x < p.height; x++) {
			for (int y = 0; y < p.width; y++) {
				seams[x][y] = new EnergyPixel();
				seams[x][y].weight = energyMatrix[x][y];
				if (y == 0) {
					seams[x][y].weight += min(seams[x-1][y].weight, seams[x-1][y+1].weight);
					if(seams[x-1][y].weight > seams[x-1][y+1].weight)
						seams[x][y].previous = new Point(x-1, y+1);
					else
						seams[x][y].previous = new Point(x-1, y);
				} else if (y == (seams[0].length - 1)) {
					seams[x][y].weight += min(seams[x-1][y].weight, seams[x-1][y-1].weight);
					if(seams[x-1][y].weight > seams[x-1][y-1].weight)
						seams[x][y].previous = new Point(x-1,y-1);
					else
						seams[x][y].previous = new Point(x-1, y);
				} else {
					seams[x][y].weight += min(seams[x-1][y-1].weight, seams[x-1][y].weight, seams[x-1][y+1].weight);
					Point pt;
					if(min(seams[x-1][y].weight, seams[x-1][y-1].weight, seams[x-1][y+1].weight) == seams[x-1][y-1].weight)
						pt = new Point(x-1,y-1);
					else if(min(seams[x-1][y-1].weight, seams[x-1][y].weight, seams[x-1][y+1].weight) == seams[x-1][y].weight)
						pt = new Point(x-1,y);
					else
						pt = new Point(x-1,y+1);
					
					seams[x][y].previous = pt;
				}
			}
		}
	}
	
	//function to compute the horizontal seams matrix
	public void computeHorizontalSeams(PGMPicture p){
		seams = new EnergyPixel[p.height][p.width];
		for (int i = 0; i < p.height; i++) {
			seams[i][0] = new EnergyPixel();
			seams[i][0].weight = energyMatrix[i][0];
			seams[i][0].previous = new Point(-1,-1);
		}

		for (int x = 1; x < p.width; x++) {
			for (int y = 0; y < p.height; y++) {
				seams[y][x] = new EnergyPixel();
				seams[y][x].weight = energyMatrix[y][x];
				
				if(y == 0){
					seams[y][x].weight += min(seams[y][x-1].weight, seams[y+1][x-1].weight);
					if(seams[y][x-1].weight < seams[y+1][x-1].weight)
						seams[y][x].previous = new Point(y, x-1);
					else
						seams[y][x].previous = new Point(y+1, x-1);
				
				} else if(y == p.height - 1) {
					seams[y][x].weight += min(seams[y][x-1].weight, seams[y-1][x-1].weight);
					if(seams[y][x-1].weight < seams[y-1][x-1].weight)
						seams[y][x].previous = new Point(y, x-1);
					else
						seams[y][x].previous = new Point(y-1, x-1);
				} else {
					seams[y][x].weight += min(seams[y-1][x-1].weight, seams[y][x-1].weight, seams[y+1][x-1].weight);
					Point pt;
					if(min(seams[y-1][x-1].weight, seams[y][x-1].weight, seams[y+1][x-1].weight) == seams[y-1][x-1].weight)
						pt = new Point(y-1,x-1);
					else if(min(seams[y-1][x-1].weight, seams[y][x-1].weight, seams[y+1][x-1].weight) == seams[y][x-1].weight)
						pt = new Point(y,x-1);
					else
						pt = new Point(y+1,x-1);
				
					seams[y][x].previous = pt;
				}
			}
		}
	}
	
	//function to find the seam path for vertical seam removal
	private int findVerticalSeamPath(EnergyPixel[][] seams) {
		int smallest = seams[seams.length - 1][0].weight;
		int start = 0;
		for(int x = 1; x < seams[0].length; x++) {
			if(seams[seams.length - 1][x].weight < smallest) {
				smallest = seams[seams.length - 1][x].weight;
				start = x;
			}
		}
		return start;
	}
	
	//function to find the seam path for horizontal seam removal
	private int findHorizontalSeamPath(EnergyPixel[][] seams) {
		int smallest = seams[0][seams[0].length - 1].weight;
		int start = 0;
		for(int x = 1; x < seams.length; x++) {
			if(seams[x][seams[0].length - 1].weight < smallest) {
				smallest = seams[x][seams[0].length-1].weight;
				start = x;
			}
		}
		return start;
	}
	
	//function to remove a vertical seam based on path of lowest energy
	public void removeVerticalSeam(PGMPicture p) {
		int[][] pixels = new int[p.height][p.width-1];
		int start = findVerticalSeamPath(seams);
		Point pt = new Point(seams.length - 1, start);
		while(pt.getX() != -1){
			seams[(int) pt.getX()][(int) pt.getY()].weight = -1;
			pt = seams[(int) pt.getX()][(int) pt.getY()].previous;
		}
		
		boolean increment = false;
		for(int x = 0; x < p.height; x++) {
			for(int y = 0; y < p.width - 1; y++){
				if(seams[x][y].weight == -1)
					increment = true;
				if(increment == false)
						pixels[x][y] = p.grayscale[x][y];
				else 
						pixels[x][y] = p.grayscale[x][y+1];
			}
			increment = false;
		}
		
		//update the PGMPictures parameters with the new data
		p.width -= 1;
		p.grayscale = new int[p.height][p.width];
		for(int x = 0; x < pixels.length; x++)
			for(int y = 0; y < pixels[0].length; y++)
				p.grayscale[x][y] = pixels[x][y];
	}
	
	//function to remove a horizontal seam based on path of lowest energy
	public void removeHorizontalSeam(PGMPicture p) {
		int[][] pixels = new int[p.height-1][p.width];
		int start = findHorizontalSeamPath(seams);
		Point pt = new Point(start, p.width - 1);
		while(pt.getX() != -1){
			seams[(int) pt.getX()][(int) pt.getY()].weight = -1;
			pt = seams[(int) pt.getX()][(int) pt.getY()].previous;
		}
		
		boolean increment = false;
		for(int x = 0; x < p.width; x++) {
			for(int y = 0; y < p.height-1; y++){
				if(seams[y][x].weight == -1)
					increment = true;
				if(increment == false)
						pixels[y][x] = p.grayscale[y][x];
				else 
						pixels[y][x] = p.grayscale[y+1][x];
			}
			increment = false;
		}
		
		//update the PGMPictures parameters with the new data
		p.height -= 1;
		p.grayscale = new int[p.height][p.width];
		for(int x = 0; x < pixels.length; x++)
			for(int y = 0; y < pixels[0].length; y++)
				p.grayscale[x][y] = pixels[x][y];
	}
	
	//function that creates a new seam carved image. Takes a file and a matrix of pixels
	private static void createSeamCarvedImage(File f, int[][] pixels){
		try {
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("P2"); bw.newLine();
			bw.write(pixels[0].length + " " + pixels.length); bw.newLine();
			bw.write("255"); bw.newLine();
			for(int x = 0; x < pixels.length; x++)
				for(int y = 0; y < pixels[0].length; y++)
					bw.write(pixels[x][y] + " ");
			bw.close();
			System.out.println("Seam carved image has been created!");
		} catch (IOException e) {
			System.out.println("Error: Unable to create new file");
		}
	}
	
	//function to find the min of two integers
	private static int min(int a, int b) {
		int result = Math.min(a, b);
		return result;
	}
	
	//function to find the min of three integers
	private static int min(int a, int b, int c) {
		int result;
		result = Math.min(a, b);
		result = Math.min(result, c);
		return result;
	}
	
	public static void main(String[] args) {
    	//get user input from command line
		String fileName = "";
    	String numVerSeams = "";
    	String numHorSeams = "";
    	 try {
    		 fileName = args[0];
    	     numVerSeams = args[1];
    	     numHorSeams = args[2];
    	 }
    	 catch (ArrayIndexOutOfBoundsException e){
    		 System.out.println("Command not found");
    	 }
    	if(args.length != 3)
    		System.out.println("Arguments are invalid...");
    	//if args are not valid, do not execute
		else if(Integer.parseInt(numVerSeams) < 0 || Integer.parseInt(numHorSeams) < 0){
    		System.out.println("Arguments are invalid...");
    		System.out.println("Args: " + fileName + ", " + numVerSeams + ", " + numHorSeams);
    	} else {
    		PGMPicture p = new PGMPicture(fileName);
    		SeamCarving sc = new SeamCarving();
    		File file = new File(p.name.substring(0, p.name.indexOf('.')) + "_processed.pgm");
    		//remove vertical seams
    		for(int x = 0; x < Integer.parseInt(numVerSeams); x++){
    			if(x == 0)
    				System.out.println("Removing vertical seams...");
    			sc.calculateEnergyMatrix(p);
    			sc.computeVerticalSeams(p);
    			sc.removeVerticalSeam(p);
    		}
    		//remove horizontal seams
    		for(int x = 0; x < Integer.parseInt(numHorSeams); x++){
    			if(x == 0)
    				System.out.println("Removing horizontal seams...");
				sc.calculateEnergyMatrix(p);
				sc.computeHorizontalSeams(p);
				sc.removeHorizontalSeam(p);
			}
			createSeamCarvedImage(file, p.grayscale);
    	}
	}
}