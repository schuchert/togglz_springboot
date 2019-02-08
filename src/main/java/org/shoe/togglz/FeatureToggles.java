package org.shoe.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;

public enum FeatureToggles implements Feature {
    @EnabledByDefault
    @Label("Perform Translation")
    Translation
}
