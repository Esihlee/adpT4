
package za.ac.cput.Domain;

import java.io.Serializable;

/**
 *
 * @author Esihle
 */
public class CarVote implements Serializable {
    
    private String carName;
    private int Vote;

    public CarVote(String carName, int Vote) {
        this.carName = carName;
        this.Vote = Vote;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public int getVote() {
        return Vote;
    }

    public void setVote(int Vote) {
        this.Vote = Vote;
    }

    @Override
    public String toString() {
        return "Domain{ carName=" + carName + ", Vote=" + Vote + '}';
    }
    
    
}
