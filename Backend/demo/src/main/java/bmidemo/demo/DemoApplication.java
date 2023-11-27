package bmidemo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

// Name : Shyam Joshi
// Date : 7/11/2023
// Matriculation number 1482098

@SpringBootApplication
@ComponentScan("bmidemo.demo")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
