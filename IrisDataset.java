package iris;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class IrisDataset {
    
    ArrayList<Flower> flower;
    
    public IrisDataset() throws FileNotFoundException {
        File file = new File("iris.data.txt");
        Scanner sc = new Scanner(file);
        flower = new ArrayList<>();
        extractData(sc);
    }
    /**
     * gets all of the data from iris.data.txt and makes flower objects
     * @param sc Scanner object that reads from the iris.txt file
     */
    private void extractData(Scanner sc) {
        
        while(sc.hasNextLine()) {
            String[] line = sc.nextLine().split(",");
            flower.add(new Flower(Float.parseFloat(line[0]), Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), line[4]));
        }
    }
    
    public ArrayList<Flower> getFlowers() {
        return flower;
    }
}
