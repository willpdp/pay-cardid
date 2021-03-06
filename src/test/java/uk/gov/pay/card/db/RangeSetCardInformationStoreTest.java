package uk.gov.pay.card.db;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

@RunWith(MockitoJUnitRunner.class)
public class RangeSetCardInformationStoreTest {

    CardInformationStore cardInformationStore;

    @After
    public void tearDown() {
        cardInformationStore.destroy();
    }

    @Test
    public void shouldUseLoadersToInitialiseData() throws Exception {
        BinRangeDataLoader mockBinRangeLoader = mock(BinRangeDataLoader.class);

        cardInformationStore = new RangeSetCardInformationStore(asList(mockBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        verify(mockBinRangeLoader).loadDataTo(cardInformationStore);
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefix() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(asList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51194812198");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
    }

    @Test
    public void shouldFindCardInformationForCardIdPrefixWith11Digits() throws Exception {

        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(asList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51194912333");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("discover"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("DISCOVER"));
    }

    @Test
    public void shouldFindCardInformationWithRangeLengthLessThan9digits() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(asList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("51122676499");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
    }

    @Test
    public void shouldFindCardInformationWithRange6digitsWhenRangeMinAndMaxAreTheSame() throws Exception {
        URL url = this.getClass().getResource("/worldpay-6-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(asList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("53333699999");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
    }

    @Test
    public void shouldFindCardInformationWithRange9digitsWhenRangeMinAndMaxAreTheSame() throws Exception {

        URL url = this.getClass().getResource("/worldpay-9-digits/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(asList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();

        Optional<CardInformation> cardInformation = cardInformationStore.find("53333333699");
        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("visa"));
        assertThat(cardInformation.get().getType(), is("D"));
        assertThat(cardInformation.get().getLabel(), is("ELECTRON"));
    }

    @Test
    public void put_shouldUpdateRangeLengthTo11Digits() {

        BinRangeDataLoader mockBinRangeLoader = mock(BinRangeDataLoader.class);
        CardInformation cardInformation = mock(CardInformation.class);

        when(cardInformation.getMin()).thenReturn(1L);
        when(cardInformation.getMax()).thenReturn(19L);

        cardInformationStore = new RangeSetCardInformationStore(asList(mockBinRangeLoader));
        cardInformationStore.put(cardInformation);

        verify(cardInformation).updateRangeLength(11);
    }

    @Test
    public void shouldTransformMastercardBrand() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());

        cardInformationStore = new RangeSetCardInformationStore(asList(worldpayBinRangeLoader));
        worldpayBinRangeLoader.loadDataTo(cardInformationStore);

        Optional<CardInformation> cardInformation = cardInformationStore.find("51122666111");

        assertTrue(cardInformation.isPresent());
        assertThat(cardInformation.get().getBrand(), is("master-card"));

    }
}
