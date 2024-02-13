package pl.touk.sputnik.processor.spotbugs;
import java.nio.charset.StandardCharsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import edu.umd.cs.findbugs.SystemProperties;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SpotBugsProcessorTest extends TestEnvironment {

    private static final String GRADLE = "gradle";

    private SpotBugsProcessor spotBugsProcessor;

    @BeforeEach
    void setUp() {
        config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.BUILD_TOOL.getKey(), GRADLE,
                GeneralOption.SPOTBUGS_LOAD_PROPERTIES_FROM.getKey(), "src/test/resources/spotbugs/spotbugs-config.properties"
        ));
        spotBugsProcessor = new SpotBugsProcessor(config);
    }

    @Test
    void shouldReturnBasicViolationsOnEmptyClass() {
        List<ReviewFile> files = ImmutableList.of(new ReviewFile("src/test/java/toreview/TestClass.java"));
        Review review = new Review(files, ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = spotBugsProcessor.process(review);
        List<String> extractedMessages = reviewResult.getViolations().stream()
                .map(Violation::getMessage)
                .collect(Collectors.toList());


        extractedMessages.set(0,StringUtils.stripAccents(extractedMessages.get(0)));
        extractedMessages.set(1,StringUtils.stripAccents(extractedMessages.get(1)));
        assertThat(reviewResult).isNotNull();
        assertThat(extractedMessages)
                .isNotEmpty()
                .hasSize(2)
                .containsOnly("DLS: Alimentation a perte d'une variable locale dans la methode toreview.TestClass.incorrectAssignmentInIfCondition()","QBA: La methode toreview.TestClass.incorrectAssignmentInIfCondition() assigne une valeur booleenne fixe dans une expression booleenne"
                );
    }

    @Test
    void shouldReturnEmptyWhenNoFilesToReview() {
        ReviewResult reviewResult = spotBugsProcessor.process(nonExistentReview());

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    void shouldLoadPropertiesFromExternalLocation() {
        ReviewResult reviewResult = spotBugsProcessor.process(nonExistentReview());

        assertThat(SystemProperties.getBoolean("findbugs.de.comment")).isTrue();
    }
}
