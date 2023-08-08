package joo.project.my3d;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class My3DApplication {

    public static void main(String[] args) {
        SpringApplication.run(My3DApplication.class, args);
    }

}
