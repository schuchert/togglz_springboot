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

public class MainTest {
    private static ConfigurableApplicationContext context;
    private static String serverPort;
    private static String baseUrl;
    private static String EXPRESSION = "3+4";
    private FeatureState initialFeatureState;

    @BeforeAll
    public static void initSpring() {
        context = SpringApplication.run(Main.class);
        serverPort = context.getEnvironment().getProperty("local.server.port");
        baseUrl = String.format("http://localhost:%s/", serverPort);
    }

    @BeforeEach
    public void storeCurrentFeatureToggleState() {
        FeatureManager manager = context.getBean(FeatureManager.class);
        initialFeatureState = manager.getFeatureState(FeatureToggles.Translation);
    }

    @AfterEach
    public void restoreFeatureToggleState() {
        FeatureManager manager = context.getBean(FeatureManager.class);
        manager.setFeatureState(initialFeatureState);
    }

    @Test
    public void togglzConsoleExists() throws NoSuchFieldException {
        String featureToggleLabel = FeatureToggles.class.getField(FeatureToggles.Translation.name()).getAnnotation(Label.class).value();
        RestTemplate template = getRestTemplate();
        String url = baseUrl + "togglz-console/index";
        ResponseEntity<String> response = template.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Cannot hit togglz console at: " + url);
        assertTrue(response.getBody().contains(featureToggleLabel));
    }

    @Test
    public void rejectsTranslationRequestWhenMissingAuthorizationToken() {
        try {
            getTranslationResultResponseEntity(new HttpHeaders());
        } catch (HttpClientErrorException exception) {
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        }
    }

    @Test
    public void performsTranslationWhenEnabled() {
        setTranslationsEnableTo(true);
        ResponseEntity<TranslationResult> entity = performTranslation();
        assertNotEquals(entity.getBody().original, entity.getBody().translated);
    }

    @Test
    public void doesNotTranslateWhenDisabled() {
        setTranslationsEnableTo(false);
        ResponseEntity<TranslationResult> entity = performTranslation();
        assertEquals(entity.getBody().original, entity.getBody().translated);
    }

    private ResponseEntity<TranslationResult> performTranslation() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "token");
        return getTranslationResultResponseEntity(headers);
    }

    private ResponseEntity<TranslationResult> getTranslationResultResponseEntity(HttpHeaders headers) {
        String url = baseUrl + "translate/" + EXPRESSION;
        RestTemplate template = getRestTemplate();

        return template.exchange(url, HttpMethod.GET, new HttpEntity(headers), TranslationResult.class);
    }

    private RestTemplate getRestTemplate() {
        return context.getBean(RestTemplate.class);
    }

    private void setTranslationsEnableTo(boolean status) {
        FeatureManager manager = context.getBean(FeatureManager.class);
        manager.setFeatureState(new FeatureState(FeatureToggles.Translation, status));
    }
}
