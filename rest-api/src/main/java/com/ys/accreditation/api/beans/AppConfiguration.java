package com.ys.accreditation.api.beans;

import com.ys.accreditation.app.service.accreditation.AccreditationService;
import com.ys.accreditation.app.service.document.DocumentValidatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  public AccreditationService accreditationService(
      DocumentValidatorService documentValidatorService) {
    return new AccreditationService(documentValidatorService);
  }

  @Bean
  public DocumentValidatorService documentValidationService() {
    return new DocumentValidatorService();
  }
}
