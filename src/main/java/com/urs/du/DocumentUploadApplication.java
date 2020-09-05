package com.urs.du;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class DocumentUploadApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DocumentUploadApplication.class, args);
	}
	

	 
}   

