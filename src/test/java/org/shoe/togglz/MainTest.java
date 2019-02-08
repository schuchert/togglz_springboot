package org.shoe.togglz;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    private static ConfigurableApplicationContext context;
    private static String serverPort;
    private static String baseUrl;
    private static String EXPRESSION = "3+4";

    @BeforeAll
    public static void initSpring() {
        context = SpringApplication.run(Main.class, new String[]{});
        serverPort = context.getEnvironment().getProperty("local.server.port");
        baseUrl = String.format("http://localhost:%s/", serverPort);
    }

    @Test
    public void appHasAGreeting() {
        Main classUnderTest = new Main();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }

    @Test
    public void togglzConsoleExists() {
        RestTemplate template = getRestTemplate();
        String url = baseUrl + "togglz-console/index";
        ResponseEntity<String> response = template.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Cannot hit togglz console at: " + url);
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
        String url = baseUrl + "translate/" + EXPRESSION;
        RestTemplate template = getRestTemplate();
        return template.getForEntity(url, TranslationResult.class);
    }

    private RestTemplate getRestTemplate() {
        return context.getBean(RestTemplate.class);
    }

    private void setTranslationsEnableTo(boolean status) {
        FeatureManager manager = context.getBean(FeatureManager.class);
        manager.setFeatureState(new FeatureState(FeatureToggles.Translation, status));
    }
}
