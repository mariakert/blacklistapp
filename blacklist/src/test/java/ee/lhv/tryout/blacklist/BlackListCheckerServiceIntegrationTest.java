package ee.lhv.tryout.blacklist;

import ee.lhv.tryout.blacklist.service.BlackListCheckerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BlackListCheckerServiceIntegrationTest {

    @Autowired
    private BlackListCheckerService blackListCheckerService;

    @Test
    public void testMatcher() {
        List<String> matches = blackListCheckerService.getMatches("Osama Laden");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("Osama Bin Laden");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("Bin Laden, Osama");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("Laden Osama Bin");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("to the osama bin laden");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("osama and bin laden");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("the osama and of laden at bin");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("osamabinladen");
        assertMatches(matches);
        matches = blackListCheckerService.getMatches("osama bin laden man");
        assertMatches(matches);
    }

    private void assertMatches(List<String> matches) {
        assertEquals(1, matches.size());
        assertEquals("Osama Bin Laden", matches.get(0));
    }

}
