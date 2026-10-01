package com.fezlr.fantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/*TODO: if it needs to add an web admin panel then
        (exclude = {UserDetailsServiceAutoConfiguration.class})
        should be deleted.
        And addition to that rest assured that CustomUserDetailsService is implemented
*/

/*TODO: optimize telegramId by not finding it 2 times to each controller
 */

@EnableScheduling
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class FantasyApplication {
	public static void main(String[] args) {
		SpringApplication.run(FantasyApplication.class, args);
	}
}
