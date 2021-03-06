package uk.gov.pay.card.it.resources;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.pay.card.app.CardApi;
import uk.gov.pay.card.app.config.CardConfiguration;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.hamcrest.core.Is.is;

public class CardIdResourceITest {

    @Rule
    public DropwizardAppRule<CardConfiguration> app = new DropwizardAppRule<>(
            CardApi.class
            , resourceFilePath("config/config.yaml")
            , config("server.applicationConnectors[0].port", "0")
            , config("server.adminConnectors[0].port", "0")
            , config("worldpayDataLocation", "data/sources/worldpay/")
            , config("discoverDataLocation", "data/sources/discover/")
            , config("testCardDataLocation", "data/sources/test-cards/"));

    @Test
    public void shouldFindDiscoverCardInformation() throws IOException {
        getCardInformation("6221267457963485")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("unionpay"))
                .body("label", is("UNIONPAY"))
                .body("type", is("CD"));
    }

    @Test
    public void shouldFindTestCardInformation() throws IOException {
        getCardInformation("4242424242424242")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("visa"))
                .body("label", is("VISA CREDIT"))
                .body("type", is("C"));
    }

    @Test
    public void shouldFindWorldpayCardInformation() throws IOException {
        getCardInformation("4000020004598361")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("visa"))
                .body("label", is("VISA CREDIT"))
                .body("type", is("C"));

    }

    @Test
    public void shouldFindAmexCardInformation() throws IOException {
        getCardInformation("371449635398431")
                .statusCode(200)
                .contentType(JSON)
                .body("brand", is("american-express"))
                .body("label", is("AMERICAN EXPRESS"))
                .body("type", is("C"));

    }

    @Test
    public void shouldReturn404WhenCardInformationNotFoud() {
        getCardInformation("8282382383829393")
                .statusCode(404);
    }

    private ValidatableResponse getCardInformation(String cardNumber) {
        return given().port(app.getLocalPort())
                .contentType(ContentType.JSON)
                .body(String.format("{\"cardNumber\":%s}", cardNumber))
                .when()
                .post("/v1/api/card")
                .then();
    }
}
