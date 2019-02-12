package org.shoe.togglz;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.annotation.Label;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private static ConfigurableApplicationContext context;
    private static String baseUrl;
    private static String EXPRESSION = "3+4";

    private FeatureState initialFeatureState;

    @BeforeAll
    static void initSpring() {
        context = SpringApplication.run(Main.class);
        String serverPort = context.getEnvironment().getProperty("local.server.port");
        baseUrl = String.format("http://localhost:%s/", serverPort);
    }

    @BeforeEach
    void storeCurrentFeatureToggleState() {
        FeatureManager manager = context.getBean(FeatureManager.class);
        initialFeatureState = manager.getFeatureState(FeatureToggles.Translation);
    }

    @AfterEach
    void restoreFeatureToggleState() {
        FeatureManager manager = context.getBean(FeatureManager.class);
        manager.setFeatureState(initialFeatureState);
    }

    @Test
    void togglzConsoleExists() throws NoSuchFieldException {
        String url = baseUrl + "togglz-console";
        ResponseEntity<String> response = getRestTemplate().getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Cannot hit togglz console at: " + url);

        String featureToggleLabel = FeatureToggles.class.getField(FeatureToggles.Translation.name()).getAnnotation(Label.class).value();
        assertTrue(response.getBody().contains(featureToggleLabel));
    }

    @Test
    void rejectsTranslationRequestWhenMissingAuthorizationToken() {
        try {
            getTranslationResultResponseEntity(new HttpHeaders());
            fail("Should have throw an exception");
        } catch (HttpClientErrorException exception) {
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        }
    }

    @Test
    void performsTranslationWhenEnabled() {
        setTranslationsEnabledTo(true);
        TranslationResult result = performTranslation();
        assertNotEquals(result.original, result.translated);
    }

    @Test
    void doesNotTranslateWhenDisabled() {
        setTranslationsEnabledTo(false);
        TranslationResult result = performTranslation();
        assertEquals(result.original, result.translated);
    }

    private TranslationResult performTranslation() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "token");
        return getTranslationResultResponseEntity(headers);
    }

    private TranslationResult getTranslationResultResponseEntity(HttpHeaders headers) {
        String url = baseUrl + "translate/" + EXPRESSION;
        RestTemplate template = getRestTemplate();

        ResponseEntity<TranslationResult> exchange = template.exchange(url, HttpMethod.GET, new HttpEntity(headers), TranslationResult.class);
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        return exchange.getBody();
    }

    private RestTemplate getRestTemplate() {
        return context.getBean(RestTemplate.class);
    }

    private void setTranslationsEnabledTo(boolean status) {
        FeatureManager manager = context.getBean(FeatureManager.class);
        manager.setFeatureState(new FeatureState(FeatureToggles.Translation, status));
    }
}
