/*
This program is specifically made to try and predict whether or
not a flower is an Iris or not, based on: sepal-length, sepal-width, petal-lenth, petal-width and Class
*/

package iris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.*;


public class Iris {
    
    private static int numTrials = 0;
    private static int numCorrect = 0;
    
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, CloneNotSupportedException
	{
            //holds all the data from the file so we can manipulate the flowers easier
            IrisDataset data = new IrisDataset();
            Flower flower;
            Scanner sc = new Scanner(System.in);
            
            Random rand = new Random();
            
            File file = new File("BestNetwork.ser");
            
            //holds the number of correct guesses of the current network
            double percentageCorrect;
            //Holds the current best neural network
            NeuralNetwork oldNetwork;
            
            if(file.exists()) {
                oldNetwork = new NeuralNetwork(readBestNetwork());
                percentageCorrect = oldNetwork.getPercentageCorrect();
                System.out.println(oldNetwork.getPercentageCorrect());

            } else {
                percentageCorrect = 0;
                oldNetwork = null;
            }
            
            System.out.println("Would you like to test the best network saved or would you like to run the program again and create a new network?\n (enter 1 for old network or enter 2 for new network)");
            int response = sc.nextInt();
            if(response == 1) {
                for(int k = 0; k < 200; k++) {
                    randomSelectionTestAtan(rand.nextInt(149) + 0, data, oldNetwork);
                }
                System.out.println("Out of 500 guesses it got " + (1.0*numCorrect/numTrials*1.0)*100 + "% correct.");
                System.out.println("Performing live test...");
                while(true) {
                    liveTest(new Scanner(System.in), data, oldNetwork);
                }
            } else if(response == 2) {
                createNewNetwork(percentageCorrect, data, rand);
            } else {
                
            }
            
            
	}
        
        /**
         * Divides out the training set from the whole flower database
         * @param data holds all of the flower in the dataset
         * @return ArrayList that contains the training set to iterate through
         */
        public static ArrayList trainingSet(IrisDataset data) {
            ArrayList<Flower> trainingSet = new ArrayList<Flower>();
            for(int i = 0; i < 15; i++) {
                trainingSet.add(data.getFlowers().get(i));
            }
            for(int i = 50; i < 65; i++) {
                trainingSet.add(data.getFlowers().get(i));
            }
            for(int i = 100; i < 115; i++) {
                trainingSet.add(data.getFlowers().get(i));
            }
            return trainingSet;
        }
        
        
        public static void randomSelectionTestTanh(int index, IrisDataset data, NeuralNetwork network) {
            numTrials++;
            Flower flower = data.getFlowers().get(index);
            double[] guesses = network.iterateTanh(flower);
            network.backpropagateTanh(flower);
            numCorrect += checkCorrectness(guesses, flower.getSpecies());
        }

        //give an upper and lower bound to network for the data
        public static void trainNetworkTanh(int lower, int upper, ArrayList<Flower> data, NeuralNetwork network) {
            double[] guesses;
            for(int i = lower; i < upper; i++) {
                Flower flower = data.get(i);
                guesses = network.iterateTanh(flower);
                network.backpropagateTanh(flower);
            }
        }
        
        //Tests the network to see how good it is
        public static void randomSelectionTestAtan(int index, IrisDataset data, NeuralNetwork network) {
            numTrials++;
            Flower flower = data.getFlowers().get(index);
            System.out.println("Giving the network a " + flower.getSpecies() + "...");
            double[] guesses = network.iterateAtan(flower);
            network.backpropagateAtan(flower);
            if(checkCorrectness(guesses, flower.getSpecies()) == 1) {
                System.out.println("It guessed correctly!");
                numCorrect++;
            } else {
                System.out.println("It guess wrong");
            }
        }
        
        //give an upper and lower bound to network for the data
        public static void trainNetworkAtan(int lower, int upper, ArrayList<Flower> data, NeuralNetwork network) {
            double[] guesses;
            for(int i = lower; i < upper; i++) {
                Flower flower = data.get(i);
                double startTime = System.nanoTime();
                guesses = network.iterateAtan(flower);
                network.backpropagateAtan(flower);
                double endTime = System.nanoTime();
                //System.out.println((endTime - startTime)/1000000.0);
            }
        }
        
        
        public static void testNetwork(int lower, int upper, IrisDataset data, NeuralNetwork network) {
            for(int i = lower; i < upper; i++) {
                numTrials++;
                Flower flower = data.getFlowers().get(i);
                double[] guesses = network.iterateAtan(flower);
                network.backpropagateAtan(flower);
                numCorrect += checkCorrectness(guesses, flower.getSpecies());
            }
        }
         
        public static boolean liveTest(Scanner sc, IrisDataset data, NeuralNetwork network) {
            System.out.println("Enter a number between 0 and 149 to select a flower and check to see if it guesses the type correctly");
            Flower flower = data.getFlowers().get(sc.nextInt());
            double[] guesses = network.iterateAtan(flower);
            if(checkCorrectness(guesses, flower.getSpecies()) == 1) {
                System.out.println("HAHA it is an " + flower.getSpecies());
            } else {
                System.out.println("Darn I was wrong");
            }
            //network.backpropagateAtan(flower);
            return true;
        }
        
        /**
         * Finds out which type the network believes is most correct and returns whether or not it was correct
         * @param guesses takes in the guesses for each flower type
         * @param correctFlower The flower that was the correct guess
         * @return returns 1 or 0 for right or wrong
         */
        public static int checkCorrectness(double[] guesses, String correctFlower) {
            String flowerType = "";
            if(guesses[0] > guesses[1] && guesses[0] > guesses[2]) {
                flowerType = "Iris-setosa";
            } else if(guesses[1] > guesses[2]) {
                flowerType = "Iris-versicolor";
            } else {
                flowerType = "Iris-virginica";
            }
            if(flowerType.equals(correctFlower)) {
                return 1;
            } else {
                return 0;
            }
        }
        
        
        public static Graph readBestNetwork() {
            Graph result = null;
            try {
                        // read object from file
                FileInputStream fis = new FileInputStream("BestNetwork.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                
                result = (Graph) ois.readObject();
                ois.close();
                      
                
            } catch (FileNotFoundException e) {
		e.printStackTrace();
            } catch (IOException e) {
		e.printStackTrace();
            } catch (ClassNotFoundException e) {
		e.printStackTrace();
            }        
            return result;
        }
        
        public static void saveNewBestNetwork(NeuralNetwork network) throws ClassNotFoundException, CloneNotSupportedException {
            try {

                // write object to file
                FileOutputStream fos = new FileOutputStream("BestNetwork.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject((Graph)network.getBrain());
                oos.close();
                

            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }
        
        /**
         * Creates a new neural network from scratch
         * @param percentageCorrect the percentageCorrect that the old network had
         * @param data the whole iris dataset
         * @param rand the random class
         * @throws ClassNotFoundException
         * @throws CloneNotSupportedException 
         */
        public static void createNewNetwork(double percentageCorrect, IrisDataset data, Random rand) throws ClassNotFoundException, CloneNotSupportedException {
            //.5 and .7 worked exceptionally well
            
            double momentum = .4;
            double learningRate = .7;

            //Used for finding a best value for momentum and learningrate
            double bestMomentum = 0;
            double bestLearningRate = 0;      
            
            double newPercentage = 0;
            
            //The new untrained neural network
            NeuralNetwork network = new NeuralNetwork(momentum, learningRate);
            
            ArrayList trainingSet = trainingSet(data);
            for(int i = 0; i < 1; i++) {
                for(int j = 0; j < 1; j++) {
                    for(int q = 0; q < 40000; q++) {
                        for(int s = 0; s < trainingSet.size(); s += 1) {
                            for(int t = 0; t < 1; t++) {
                                trainNetworkAtan(s, s+1, trainingSet, network);
                            }
                        }
                    }
                    
                    for(int k = 0; k < 200; k++) {
                        randomSelectionTestAtan(rand.nextInt(149) + 0, data, network);
                    }
                    if(percentageCorrect < (1.0*numCorrect/numTrials*1.0)) {
                        network.setPercentageCorrect(1.0*numCorrect/numTrials*1.0);
                        saveNewBestNetwork(network);
                        bestMomentum = momentum;
                        bestLearningRate = learningRate;
                    }
                    
                    newPercentage = (1.0*numCorrect/numTrials*1.0);
                    numCorrect = 0;
                    numTrials = 0;
                }
            }
            if(bestMomentum == 0) {
                System.out.println("It got this amount correct out of 500 random tests " + newPercentage * 100.0 + "%. Not as good as the current best network which got " + percentageCorrect * 100.0 + "%");
            } else {
                System.out.println("It got this amount correct out of 500 random tests " + newPercentage * 100.0 + "%. That's better than the current best network which got " + percentageCorrect * 100.0 + "%\nThis will be the new best newtork");
            }
            
        }
}