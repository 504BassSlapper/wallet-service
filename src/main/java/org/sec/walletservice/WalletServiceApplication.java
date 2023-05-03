package org.sec.walletservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WalletServiceApplication {
	@Autowired
	WalletService walletService;

	public static void main(String[] args) {
		SpringApplication.run(WalletServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner startUp(){
		return args -> {
			walletService.loadData();

		};
	}

}
