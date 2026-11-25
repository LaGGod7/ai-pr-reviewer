package org.gd.backend.webhook;

import org.gd.backend.config.GitHubProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookSignatureVerifierTest {

    private final WebhookSignatureVerifier verifier = new WebhookSignatureVerifier(
            new GitHubProperties("https://api.github.com","token","It's a Secret to Everybody")
    );
    private static final String GITHUB_EXAMPLE_SIGNATURE ="sha256=757107ea0eb2509fc211221cce984b8a37570b6d7586c22c46f4379c8b043e17";

    @Test
    void isValid_acceptsGitHubDocumentedExample(){
        assertTrue(verifier.isValid("Hello, World!",GITHUB_EXAMPLE_SIGNATURE));
    }

    @Test
    void isValid_rejectsChangedBody() {
        assertFalse(verifier.isValid("Hello, World?", GITHUB_EXAMPLE_SIGNATURE));
    }

    @Test
    void isValid_rejectsMissingHeader() {
        assertFalse(verifier.isValid("Hello, World!", null));
    }


}