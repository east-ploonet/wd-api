package com.saltlux.aice_fe._baseline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	//-- Global Fields --//
	//
	@Value("${spring.web.resources.static-locations}")
	private String springWebResourcesStaticLocations;
	//
	@Value("${path.file.upload}/")
	private String pathFileUpload;
	//
	@Value("${path.browser.storage}")
	private String pathBrowserStorage;

	@Value("${cors.domain}")
	private String corsDomain;


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

//		registry.addResourceHandler("/static/**").addResourceLocations(springWebResourcesStaticLocations);
		//
		registry.addResourceHandler( pathBrowserStorage + "/**").addResourceLocations("file:///"+pathFileUpload); // "/storage/**"
		registry.addResourceHandler("/static_img/**")
				.addResourceLocations("classpath:/static_img/", "/static_img/");
	}

	@Override
	public void addCorsMappings(CorsRegistry myCorsRegistry){
		myCorsRegistry.addMapping("/**")
				.allowedOrigins(corsDomain);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
