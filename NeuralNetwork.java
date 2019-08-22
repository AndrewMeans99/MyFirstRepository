package iris;

import java.io.PrintWriter;
import java.io.Serializable;


public class NeuralNetwork extends Graph implements Serializable {
    
    /**
     * Creates a new neural network and takes in custom momentum and learningRate
     * @param momentum
     * @param learningRate 
     */
    public NeuralNetwork(double momentum, double learningRate) {
        super(momentum, learningRate);
        
    }
    /**
     * Creates a neural network from a past neural network
     * @param network 
     */
    public NeuralNetwork(Graph network) {
        super(network.getInputConnections(), network.getNeuronConnections(), network.getLayers(), network.getNeuronLayers(), network.getPercentageCorrect(), network.getOutputNeurons());
    }
    
    /**
     * Calculates the guess for the current flower
     * @param flower takes flower object
     * @return double[] of size three, each index is the guess for the iteration in this order: Iris-setosa, Iris-versicolor, Iris-virginica
     */
    public double[] iterateTanh(Flower flower) {
        //Holds the guess value for each neuron
        double[] guesses = new double[inputConnections[0].length];
        
        double[] flowerAttributes = new double[]{flower.getPetalLength(), flower.getPetalWidth(), flower.getSepalLength(), flower.getSepalWidth()};

        for(int i = 0; i < inputConnections.length; i++) {
            for(int j = 0; j < inputConnections[0].length; j++) {
                guesses[j] += flowerAttributes[i] * inputConnections[i][j];
            }
        }      
        
        //add in the bias from the neuron and run it through the tanh function
        for(int i = 0; i < guesses.length; i++) {
            //get the sum inside each guess and store it in the neuron
            neuronLayers[0][i].setSum(guesses[i] + neuronLayers[0][i].getBias());
            //apply the non-linear function
            //store the output inside of the neuron
            neuronLayers[0][i].setOutput(Math.tanh(neuronLayers[0][i].getSum()));
        }
        double sum;
        for(int i = 0; i < neuronLayers.length-1; i++) {
            for(int j = 0; j < neuronLayers[0].length; j++) {
                sum = 0;
                for(int k = 0; k < layers[0][0].length; k++) {
                    //IN QUESTION
                    sum += neuronLayers[i][k].getOutput() * layers[i][k][j];
                }
                neuronLayers[i+1][j].setSum(sum + neuronLayers[i+1][j].getBias());
                neuronLayers[i+1][j].setOutput(Math.tanh(neuronLayers[i+1][j].getSum()));
            }
        } 
        
        //run throught the output layer of nodes
        for(int i = 0; i < outputNeurons.length; i++) {
            sum = 0;
            for(int j = 0; j < neuronLayers[0].length; j++) {
                sum += neuronLayers[neuronLayers.length-1][j].getOutput() * neuronConnections[j][i];
            }
            //store the sum for each output neuron
            outputNeurons[i].setSum(sum + outputNeurons[i].getBias());
            //store the output of the final guess in the outputNeuron
            outputNeurons[i].setOutput(Math.tanh(outputNeurons[i].getSum()));
        } 
        
        double[] finalGuess = new double[3];
        for(int i = 0; i < finalGuess.length; i++) {
            finalGuess[i] = outputNeurons[i].getOutput();
        }
        return finalGuess;
    }
    
    /**
     * Uses tanh as the function to calculate the gradient (Haven't had success)
     * @param flower flower object that was used in the iteration phase
     */
    public void backpropagateTanh(Flower flower) {
        int[] correctFlower = flowerType(flower);
        double[] flowerAttributes = new double[]{flower.getPetalLength(), flower.getPetalWidth(), flower.getSepalLength(), flower.getSepalWidth()};
        //calculate the deltas for the output nodes
        for(int i = 0; i < outputNeurons.length; i++) {
            //calculate the error of the neuron
            outputNeurons[i].setError(Math.pow(outputNeurons[i].getOutput() - correctFlower[i], 2));
            //use the derivative of tanh and multiply it by the error to find the delta
            outputNeurons[i].setDelta(-outputNeurons[i].getError() * (1 - Math.pow(Math.tanh(outputNeurons[i].getSum()), 2)));
        }
        
        //implementing 3d array// still in beta
        //calculate the delta for the last layer of neurons
        double sumOfWeights;
        for(int i = 0; i < neuronLayers[0].length; i++) {
            sumOfWeights = 0;
            //sum up all weighted going from the neuron to the outputNeurons
            for(int j = 0; j < outputNeurons.length; j++) {
                sumOfWeights += neuronConnections[i][j] * outputNeurons[j].getDelta();
            }
            neuronLayers[neuronLayers.length-1][i].setDelta(sumOfWeights * (1 - Math.pow(Math.tanh(neuronLayers[layers.length-1][i].getSum()), 2)));
        }
        
        //using the last layers of neurons as the starting point backpropogate the deltas
        for(int i = neuronLayers.length-2; i >= 0; i--) {
            for(int j = 0; j < layers[0].length; j++) {
                sumOfWeights = 0;
                for(int k = 0; k < layers[0][0].length; k++) {
                    sumOfWeights += layers[i][j][k] * neuronLayers[i+1][k].getDelta();
                }
                neuronLayers[i][j].setDelta(sumOfWeights * (1 - Math.pow(Math.tanh(neuronLayers[i][j].getSum()), 2)));
            }
        }
        
        //calculate the change of weights for each connection running from layer n
        for(int i = 0; i < neuronConnections.length; i++) {
            for(int j = 0; j < neuronConnections[0].length; j++) {
                //LearningRate * Gradient + momentum * previousChangeOfWeight
                //MIGHT BE WRONG
                neuronWeightChange[i][j] = learningRate * neuronLayers[neuronLayers.length-1][i].getOutput() * outputNeurons[j].getDelta() + momentum * neuronWeightChange[i][j];
                neuronConnections[i][j] += neuronWeightChange[i][j];
            }
        }
        
        //Implementing 3d array to act as layers for neural network// untested
        for(int i = layers.length-1; i >= 0; i--) {
            for(int j = 0; j < layers[0].length; j++) {
                for(int k = 0; k < layers[0][0].length; k++) {
                    layerWeightChange[i][j][k] = learningRate * neuronLayers[i][j].getOutput() * neuronLayers[i+1][k].getDelta() + momentum * layerWeightChange[i][j][k];
                    layers[i][j][k] += layerWeightChange[i][j][k];
                }
            }
        }
        
        for(int i = 0; i < inputConnections.length; i++) {
            for(int j = 0; j < inputConnections[0].length; j++) {
                inputWeightChange[i][j] = learningRate * flowerAttributes[i] * neuronLayers[0][j].getDelta() + momentum * inputWeightChange[i][j];
                inputConnections[i][j] += inputWeightChange[i][j];
            }
        }
        
    }
    /**
     * Uses arctan as the nonlinear function (Most Successful Implementation)
     * @param flower Flower object that holds all the details of the current flower
     * @return double[] of size three, each index is the guess for the iteration in this order: Iris-setosa, Iris-versicolor, Iris-virginica
     */
    public double[] iterateAtan(Flower flower) {
        //Holds the guess value for each neuron
        double[] guesses = new double[inputConnections[0].length];
        
        double[] flowerAttributes = new double[]{flower.getPetalLength(), flower.getPetalWidth(), flower.getSepalLength(), flower.getSepalWidth()};

        for(int i = 0; i < inputConnections.length; i++) {
            for(int j = 0; j < inputConnections[0].length; j++) {
                guesses[j] += flowerAttributes[i] * inputConnections[i][j];
            }
        }      
        
        //add in the bias from the neuron and run it through the tanh function
        for(int i = 0; i < guesses.length; i++) {
            //get the sum inside each guess and store it in the neuron
            neuronLayers[0][i].setSum(guesses[i] + neuronLayers[0][i].getBias());
            //apply the non-linear function
            //store the output inside of the neuron
            neuronLayers[0][i].setOutput(Math.atan(neuronLayers[0][i].getSum()));
        }
        double sum;
        for(int i = 0; i < neuronLayers.length-1; i++) {
            for(int j = 0; j < neuronLayers[0].length; j++) {
                sum = 0;
                for(int k = 0; k < layers[0][0].length; k++) {
                    //IN QUESTION
                    sum += neuronLayers[i][k].getOutput() * layers[i][k][j];
                }
                neuronLayers[i+1][j].setSum(sum + neuronLayers[i+1][j].getBias());
                neuronLayers[i+1][j].setOutput(Math.atan(neuronLayers[i+1][j].getSum()));
            }
        } 
        
        
        //run throught the output layer of nodes
        for(int i = 0; i < outputNeurons.length; i++) {
            sum = 0;
            for(int j = 0; j < neuronLayers[0].length; j++) {
                sum += neuronLayers[neuronLayers.length-1][j].getOutput() * neuronConnections[j][i];
            }
            //store the sum for each output neuron
            outputNeurons[i].setSum(sum + outputNeurons[i].getBias());
            //store the output of the final guess in the outputNeuron
            outputNeurons[i].setOutput(Math.atan(outputNeurons[i].getSum()));
        } 
        
        double[] finalGuess = new double[3];
        for(int i = 0; i < finalGuess.length; i++) {
            finalGuess[i] = outputNeurons[i].getOutput();
        }
        return finalGuess;
    }
    
    /**
     * 
     * @param flower 
     */
    public void backpropagateAtan(Flower flower) {
        int[] correctFlower = flowerType(flower);
        double[] flowerAttributes = new double[]{flower.getPetalLength(), flower.getPetalWidth(), flower.getSepalLength(), flower.getSepalWidth()};
        //calculate the deltas for the output nodes
        for(int i = 0; i < outputNeurons.length; i++) {
            //calculate the error of the neuron
            //cant decide whether or not to make it squared
            outputNeurons[i].setError(Math.pow(outputNeurons[i].getOutput() - correctFlower[i], 1));
            //use the derivative of tanh and multiply it by the error to find the delta
            outputNeurons[i].setDelta(-outputNeurons[i].getError() * (1/(1 + Math.pow(outputNeurons[i].getSum(), 2))));
        }
        
        //implementing 3d array// still in beta
        //calculate the delta for the last layer of neurons
        double sumOfWeights;
        for(int i = 0; i < neuronLayers[0].length; i++) {
            sumOfWeights = 0;
            //sum up all weighted going from the neuron to the outputNeurons
            for(int j = 0; j < outputNeurons.length; j++) {
                sumOfWeights += neuronConnections[i][j] * outputNeurons[j].getDelta();
            }
            neuronLayers[neuronLayers.length-1][i].setDelta(sumOfWeights * (1/(1 + Math.pow(neuronLayers[layers.length-1][i].getSum(), 2))));
        }
        
        //using the last layers of neurons as the starting point backpropogate the deltas
        for(int i = neuronLayers.length-2; i >= 0; i--) {
            for(int j = 0; j < layers[0].length; j++) {
                sumOfWeights = 0;
                for(int k = 0; k < layers[0][0].length; k++) {
                    sumOfWeights += layers[i][j][k] * neuronLayers[i+1][k].getDelta();
                }
                neuronLayers[i][j].setDelta(sumOfWeights * (1/(1 + Math.pow(neuronLayers[i][j].getSum(), 2))));
            }
        }
        
        //calculate the change of weights for each connection running from layer n
        for(int i = 0; i < neuronConnections.length; i++) {
            for(int j = 0; j < neuronConnections[0].length; j++) {
                //LearningRate * Gradient + momentum * previousChangeOfWeight
                //MIGHT BE WRONG
                neuronWeightChange[i][j] = learningRate * neuronLayers[neuronLayers.length-1][i].getOutput() * outputNeurons[j].getDelta() + momentum * neuronWeightChange[i][j];
                neuronConnections[i][j] += neuronWeightChange[i][j];
            }
        }
        
        //Implementing 3d array to act as layers for neural network// untested
        for(int i = layers.length-1; i >= 0; i--) {
            for(int j = 0; j < layers[0].length; j++) {
                for(int k = 0; k < layers[0][0].length; k++) {
                    layerWeightChange[i][j][k] = learningRate * neuronLayers[i][j].getOutput() * neuronLayers[i+1][k].getDelta() + momentum * layerWeightChange[i][j][k];
                    layers[i][j][k] += layerWeightChange[i][j][k];
                }
            }
        }
        
        for(int i = 0; i < inputConnections.length; i++) {
            for(int j = 0; j < inputConnections[0].length; j++) {
                inputWeightChange[i][j] = learningRate * flowerAttributes[i] * neuronLayers[0][j].getDelta() + momentum * inputWeightChange[i][j];
                inputConnections[i][j] += inputWeightChange[i][j];
            }
        }
    }
    
    //returns an array identifying the actual flower that was used with a 1 and the other elements are zeros
    private int[] flowerType(Flower flower) {
        int[] flowerType;
        switch(flower.getSpecies()) {
            case "Iris-setosa":
                flowerType = new int[]{1, 0, 0};
                break;
            case "Iris-versicolor":
                flowerType = new int[]{0, 1, 0};
                break;
            case "Iris-virginica":
                flowerType = new int[]{0,0,1};
                break; 
            //shouldn't ever reach this point
            default:
                flowerType = new int[3];
            
        }
        return flowerType;
    }
    
    public Graph getBrain() throws CloneNotSupportedException {
        Graph graph = (Graph) super.clone();
        System.out.println(graph.getPercentageCorrect());
        return graph;
    }
//w[i,j] -= gamma * o[i] * delta[j]
//bias[j] -= gamma_bias * 1 * delta[j]
}
