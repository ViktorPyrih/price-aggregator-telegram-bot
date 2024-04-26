package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class ApiClientProperties {

    @URL
    @NotBlank
    private String url;
    @NotBlank
    private String apiKey;

}
