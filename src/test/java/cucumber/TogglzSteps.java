package cucumber;

import io.cucumber.java8.En;
import org.shoe.togglz.FeatureToggles;
import org.shoe.togglz.TestApplicationContext;
import org.shoe.togglz.TranslationResult;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.annotation.Label;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import static org.junit.Assert.*;

public class TogglzSteps implements En {
    private static String EXPRESSION = "3+4";
    private FeatureState initialFeatureState;

    public TogglzSteps() {
        Given("the system is running", () -> {
            FeatureManager manager = TestApplicationContext.getBean(FeatureManager.class);
            initialFeatureState = manager.getFeatureState(FeatureToggles.Translation);
            setTranslationsEnabledTo(true);
            TranslationResult result = performTranslation();
            assertNotEquals(result.original, result.translated);
        });

        Then("the togglz ui should be present", () -> {
            String url = TestApplicationContext.urlFor("togglz-console");
            ResponseEntity<String> response = getRestTemplate().getForEntity(url, String.class);

            String featureToggleLabel = FeatureToggles.class.getField(FeatureToggles.Translation.name()).getAnnotation(Label.class).value();
            assertTrue(response.getBody().contains(featureToggleLabel));
        });
    }

    private RestTemplate getRestTemplate() {
        return TestApplicationContext.getBean(RestTemplate.class);
    }

    private void setTranslationsEnabledTo(boolean status) {
        FeatureManager manager = TestApplicationContext.getBean(FeatureManager.class);
        manager.setFeatureState(new FeatureState(FeatureToggles.Translation, status));
    }

    private TranslationResult performTranslation() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "token");
        return getTranslationResultResponseEntity(headers);
    }

    private TranslationResult getTranslationResultResponseEntity(HttpHeaders headers) {
        String url = TestApplicationContext.urlFor("translate/" + EXPRESSION);
        RestTemplate template = getRestTemplate();

        ResponseEntity<TranslationResult> exchange = template.exchange(url, HttpMethod.GET, new HttpEntity(headers), TranslationResult.class);
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        return exchange.getBody();
    }

}
