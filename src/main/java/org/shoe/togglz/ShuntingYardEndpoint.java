package org.shoe.togglz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.togglz.core.manager.FeatureManager;


@Controller
public class ShuntingYardEndpoint {
    FeatureManager featureToggles;

    @Autowired
    public ShuntingYardEndpoint(FeatureManager featureToggles) {
        this.featureToggles = featureToggles;
    }

    @GetMapping("/translate/{expression}")
    public ResponseEntity<TranslationResult> translate(@PathVariable("expression") String expression) {
        boolean active = featureToggles.isActive(FeatureToggles.Translation);

        String result = active ? performTranslationOn(expression) : expression;

        return ResponseEntity.ok(new TranslationResult(expression, result, active));
    }

    private String performTranslationOn(String expression) {
        return new StringBuilder(expression).reverse().toString();
    }
}
