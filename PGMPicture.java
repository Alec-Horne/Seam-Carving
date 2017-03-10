import java.awt.Point;
import java.io.*;
import java.util.Scanner;

public class PGMPicture {
	
	public int height;
	public int width;
	public String name;
	public String magic;
	public String maxPixelSize;
	public int[][] grayscale;
	
	public PGMPicture(String file) {
		
		try {
			name = file;
			Scanner fileScanner = new Scanner(new File(file));
			magic = fileScanner.nextLine();
			fileScanner.nextLine();
			String temp = fileScanner.nextLine();
			maxPixelSize = fileScanner.nextLine();
			
			Scanner scan = new Scanner(temp);
			width = scan.nextInt();
			height = scan.nextInt();
			scan.close();
			
			grayscale = new int[height][width];
			for(int h = 0; h < height; h++)
				for(int w = 0; w < width; w++)
					grayscale[h][w] = fileScanner.nextInt();
			
			fileScanner.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File was not found...");
		}
	}
}