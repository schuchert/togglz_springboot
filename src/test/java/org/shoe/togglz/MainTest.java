package org.shoe.togglz;

import org.junit.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.annotation.Label;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.repository.FeatureState;

import static org.junit.Assert.*;

public class MainTest {
    private static String EXPRESSION = "3+4";

    private FeatureState initialFeatureState;

    @BeforeClass
    static public void initSpring() {
    }

    @Before
    public void storeCurrentFeatureToggleState() {
        FeatureManager manager = TestApplicationContext.getBean(FeatureManager.class);
        initialFeatureState = manager.getFeatureState(FeatureToggles.Translation);
    }

    @After
    public void restoreFeatureToggleState() {
        FeatureManager manager = TestApplicationContext.getBean(FeatureManager.class);
        manager.setFeatureState(initialFeatureState);
    }

    @Test
    public void togglzConsoleExists() throws NoSuchFieldException {
        String url = TestApplicationContext.urlFor("togglz-console");
        ResponseEntity<String> response = getRestTemplate().getForEntity(url, String.class);
        assertEquals("Cannot hit togglz console at: " + url, HttpStatus.OK, response.getStatusCode());

        String featureToggleLabel = FeatureToggles.class.getField(FeatureToggles.Translation.name()).getAnnotation(Label.class).value();
        assertTrue(response.getBody().contains(featureToggleLabel));
    }

    @Test
    public void rejectsTranslationRequestWhenMissingAuthorizationToken() {
        try {
            getTranslationResultResponseEntity(new HttpHeaders());
            fail("Should have throw an exception");
        } catch (HttpClientErrorException exception) {
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        }
    }

    @Test
    public void performsTranslationWhenEnabled() {
        setTranslationsEnabledTo(true);
        TranslationResult result = performTranslation();
        assertNotEquals(result.original, result.translated);
    }

    @Test
    public void doesNotTranslateWhenDisabled() {
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
        String url = TestApplicationContext.urlFor("translate/" + EXPRESSION);
        RestTemplate template = getRestTemplate();

        ResponseEntity<TranslationResult> exchange = template.exchange(url, HttpMethod.GET, new HttpEntity(headers), TranslationResult.class);
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
        return exchange.getBody();
    }

    private RestTemplate getRestTemplate() {
        return TestApplicationContext.getBean(RestTemplate.class);
    }

    private void setTranslationsEnabledTo(boolean status) {
        FeatureManager manager = TestApplicationContext.getBean(FeatureManager.class);
        manager.setFeatureState(new FeatureState(FeatureToggles.Translation, status));
    }
}
