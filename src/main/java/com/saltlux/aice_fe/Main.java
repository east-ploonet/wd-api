package com.saltlux.aice_fe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
import java.util.List;

@EnableAsync
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Main {

    private static final String[] profileList = {"local", "dev", "release", "real", "divide"};

/*
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		Locale.setDefault(Locale.KOREA);
	}
*/

    public static void main(String[] args) {

//        -- 실행시 입력된 인자 갯수 체크
 
    	String profile = "local";
    	if (args.length < 1) { profile = "local"; } 
		else 
		{
			profile = args[0]; 
		}
		
    	//final String profile    = "local";
        //-- 정의된 프로파일 이름인지 체크
        List<String> list = Arrays.asList(profileList);

        if (!list.contains(profile)) {
            printErr();
            return;
        }

        //-- application-{프로파일 이름}.properties 로딩
        if (System.getProperty("spring.profiles.active") == null) {
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, profile);
        }

        System.out.println("============== this profile is [" + System.getProperty("spring.profiles.active") + "] ==============");
        SpringApplication.run(Main.class, args);
    }

    public static void printErr() {

        String msgProfile = "";

        for (String profile : profileList) {
            if (!"".equals(msgProfile)) {
                msgProfile += ", ";
            }
            msgProfile += profile;
        }

        System.out.println("============== Invalid arguments !!! ==============");
        System.out.println("arg[0] : profile name (ex: " + msgProfile + ")");
    }
}
