package bmidemo.demo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

// Name : Shyam Joshi
// Date : 7/11/2023
// Matriculation number 1482098

@EntityScan
public class BmiData {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Getter @Setter @Column public Long id;
    @Getter @Setter @Column public String name;
    @Getter @Setter @Column public Long weight;
    @Getter @Setter @Column public float height;
    @Getter @Setter @Column public float bmi;
    @Override
    public String toString() {
        return "BmiData [UserName=" + name + ", Weight=" + weight + ", Height=" + height + "]";
    }
    public BmiData(String userName, Long w, Long h) {
        name = userName;
        weight = w;
        height = h;
    }
    public BmiData() {
    }

    
}
