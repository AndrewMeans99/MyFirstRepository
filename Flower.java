package iris;

public class Flower {
    private float petalLength;
    private float petalWidth;
    private float sepalLength;
    private float sepalWidth;
    private String species;
    
    public Flower(float petalLength, float petalWidth, float sepalLength, float sepalWidth, String species) {
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.species = species;
    }
    
    
    public float getPetalLength() {
        return petalLength;
    }
    
    public float getPetalWidth() {
        return petalWidth;
    }
    
    public float getSepalLength() {
        return sepalLength;
    }
    
    public float getSepalWidth() {
        return sepalWidth;
    }
    
    public String getSpecies() {
        return species;
    }
}
