package by.andersen.coworkingspace.config;

import by.andersen.coworkingspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class AppConfig {
  private final UserRepository userRepository;

  @Autowired
  public AppConfig(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository.findByName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
