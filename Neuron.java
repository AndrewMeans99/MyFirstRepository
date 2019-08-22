package iris;
//neuron will contain sum, output, and bias, error
import java.io.Serializable;

public class Neuron implements Serializable {
    private double sum;
    private double output;
    private double bias;
    private double error;
    private double delta;
    
    public Neuron() {
        bias = Math.random();
    }
    
    public void setSum(double sum) {this.sum = sum;}
    
    public void setOutput(double output) {this.output = output;}
    
    public void setBias() {this.bias = bias;}
    
    public void setDelta(Double delta) {this.delta = delta;}
    
    public void setError(double error) {this.error = error;}
    
    public double getSum() {return sum;}
    
    public double getOutput() {return output;}
    
    public double getBias() {return bias;}
    
    public double getError() { return error;}
    
    public double getDelta() { return delta; }
}
