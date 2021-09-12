package com.ironhack.midtermproject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MidtermprojectApplicationTests {

	@Autowired
	MidtermprojectApplication midtermprojectApplication;

	@Test
	void contextLoads() {
		assertNotNull(midtermprojectApplication);
	}

	@Test
	void main() {
		MidtermprojectApplication.main(new String[] {});
	}

}
