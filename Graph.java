/*
The number of neurons and the number of layers was a complete guess on my part but I have had very good success with 17 neurons and 3 layers
*/

package iris;

import java.io.File;
import java.util.Random;
import java.io.Serializable;

public class Graph implements Cloneable, Serializable {
    
    private double percentageCorrect;
    //FOR IRIS DATA SET
    private final int numInputs = 4;
    //for tanh best number was 8-10
    //for arctan best number 10-17
    private final int numNeurons = 17;
    private final int numGuessNodes = 3;
    
    /*input connections holds the weights for each edge from input to each neuron in this order
        PetalLength, PetalWidth, SepalLength, SepalWidth*/
    protected double[][] inputConnections;
    protected double[][] inputWeightChange;
            
    protected double[][] neuronConnections;
    protected double[][] neuronWeightChange;
    
    protected Neuron[] neurons;
    protected Neuron[] outputNeurons;
    
    protected int numLayers = 3;
    protected double[][][] layers;
    protected double[][][] layerWeightChange;
    protected Neuron[][] neuronLayers;
    
    //for tanh best number was about .2
    //for arctan best number is about .085
    protected double learningRate = .2;
    //for tanh best number was .1
    //for arctan best number .7
    protected double momentum = .085;
    
    
    /**
     * Creates a neural network from scratch and uses default learningRate and momentum
     */
    public Graph() {
        inputConnections = new double[numInputs][numNeurons];
        inputWeightChange = new double[numInputs][numNeurons];
        
        neuronConnections = new double[numNeurons][numGuessNodes];
        neuronWeightChange = new double[numNeurons][numGuessNodes];
        
        layers = new double[numLayers-1][numNeurons][numNeurons];
        layerWeightChange = new double[numLayers-1][numNeurons][numNeurons];
        neuronLayers = new Neuron[numLayers][numNeurons];
        
        neurons = new Neuron[numNeurons];
        outputNeurons = new Neuron[numGuessNodes];
        
        initializeConnectionWeight();
        initializeNeurons();

    }
    
    /**
     * Creates a neural network from an existing network
     * @param inputConnections adjacency matrix showing the connection between input and the first layer of neurons
     * @param neuronConnections adjacency matrix showing the connection between the final layer and the output neurons
     * @param layers array of adjacency lists that shows the connections between each layer
     * @param neuronLayers holds the values of each neuron in the network
     * @param percentageCorrect the total number that the network correctly guessed
     * @param outputNeurons the final guess neurons that make the final guess
     */
    public Graph(double[][] inputConnections, double[][] neuronConnections, double[][][] layers, Neuron[][] neuronLayers, double percentageCorrect, Neuron[] outputNeurons) {
        this.inputConnections = inputConnections;
        inputWeightChange = new double[numInputs][numNeurons];
        
        this.neuronConnections = neuronConnections;
        neuronWeightChange = new double[numNeurons][numGuessNodes];
        
        this.layers = layers;
        layerWeightChange = new double[numLayers-1][numNeurons][numNeurons];
        this.neuronLayers = neuronLayers;
        
        neurons = new Neuron[numNeurons];
        this.outputNeurons = outputNeurons;
        
        this.percentageCorrect = percentageCorrect;
    }
    
    /**
    Creates a new neural network and uses the given learningRate and momentum
    */
    public Graph(double learningRate, double momentum) {
        this.learningRate = learningRate;
        this.momentum = momentum;
        
        inputConnections = new double[numInputs][numNeurons];
        inputWeightChange = new double[numInputs][numNeurons];
        
        neuronConnections = new double[numNeurons][numGuessNodes];
        neuronWeightChange = new double[numNeurons][numGuessNodes];
        
        layers = new double[numLayers-1][numNeurons][numNeurons];
        layerWeightChange = new double[numLayers-1][numNeurons][numNeurons];
        neuronLayers = new Neuron[numLayers][numNeurons];
        
        neurons = new Neuron[numNeurons];
        outputNeurons = new Neuron[numGuessNodes];
        File file = new File("FUTURE BRAIN");
        if(!file.exists()) {
            initializeConnectionWeight();
            initializeNeurons();
        }
        
    }
    
    /**
     * Initializes all of the connections weights in the network
     */
    public void initializeConnectionWeight() {
        for (double[] inputConnection : inputConnections) {
            for (int j = 0; j < inputConnections[0].length; j++) {
                //inputConnections[i][j] = Math.random();
                inputConnection[j] = (Math.random()*((2/inputConnections.length)+1))+1/inputConnections.length;
            }
        }
        
        for (double[][] layer : layers) {
            for (int j = 0; j < numNeurons; j++) {
                for (int k = 0; k < numNeurons; k++) {
                    layer[j][k] = (Math.random()*((2/numNeurons)+1))+1/numNeurons;
                }
            }
        }
        
        for (double[] neuronConnection : neuronConnections) {
            for (int j = 0; j < neuronConnections[0].length; j++) {
                //neuronConnections[i][j] = Math.random();
                neuronConnection[j] = (Math.random()*((2/neuronConnections.length)+1))+1/neuronConnections.length;
            }
        }
    }
    /**
     * Resets all of the past changes so it does not affect the next backpropagation
     */
    public void resetPastChanges() {
        inputWeightChange = new double[inputWeightChange.length][inputWeightChange[0].length];
        neuronWeightChange = new double[neuronWeightChange.length][neuronWeightChange[0].length];
        layerWeightChange = new double[layers.length][layers[0].length][layers[0][0].length];
    }
    
    /**
     * Initializes all of the neurons in the network
     */
    public void initializeNeurons() {
        for(int i = 0; i < numNeurons; i++) {
            neurons[i] = new Neuron();
        }
        for(int i = 0; i < outputNeurons.length; i++) {
            outputNeurons[i] = new Neuron();
        }
        for(int i = 0; i < numLayers; i++) {
            for(int j = 0; j < numNeurons; j++) {
                neuronLayers[i][j] = new Neuron();
            }
        }
    }
    
    public double[][] getInputConnections() {
        return inputConnections;
    }
            
    public double[][] getNeuronConnections() {
        return neuronConnections;
    }
    
    public double[][][] getLayers() {
        return layers;
    }
         
    public Neuron[][] getNeuronLayers() {
        return neuronLayers;
    }
    
    public Neuron[] getNeurons() {
        return neurons;
    }
    
    public Neuron[] getOutputNeurons() {
        return outputNeurons;
    }
    
    public double getPercentageCorrect() {
        return percentageCorrect;
    }
    
    public void setPercentageCorrect(double percentageCorrect) {
        this.percentageCorrect = percentageCorrect;
    }
    
    public void displayNetwork() {
        for(int i = 0; i < inputConnections[0].length; i++) {
            System.out.print("\t\t\t" + i);
        }
        System.out.println();
        for(int i = 0; i < inputConnections.length; i++) {
            System.out.print(i + "\t");
            for(int j = 0; j < inputConnections[0].length; j++) {
                System.out.print(inputConnections[i][j] + "\t");
            }
            System.out.println();
        }
        for(int i = 0; i < neuronConnections[0].length; i++) {
            System.out.print("\t\t\t" + i);
        }
        System.out.println();
        for(int i = 0; i < neuronConnections.length; i++) {
            System.out.print(i + "\t");
            for(int j = 0; j < neuronConnections[0].length; j++) {
                System.out.print(neuronConnections[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
