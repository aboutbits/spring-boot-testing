package it.aboutbits.springboot.testing.web.response;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.ResultMatcher;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

public final class ResponseBodyMatchers {
    private final static JsonMapper jsonMapper = new JsonMapper();
    private final String jsonPath;

    private String[] fieldNamesToIgnore = new String[0];
    private String[] optionalFieldNamesToIgnore = new String[0];
    private boolean ignoreAudition = false;

    private ResponseBodyMatchers(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public ResponseBodyMatchers ignoringOptionalFields(String... optionalFieldNamesToIgnore) {
        this.optionalFieldNamesToIgnore = optionalFieldNamesToIgnore;
        return this;
    }

    public ResponseBodyMatchers ignoringFields(String... fieldNamesToIgnore) {
        this.fieldNamesToIgnore = fieldNamesToIgnore;
        return this;
    }

    public ResponseBodyMatchers ignoringAudition() {
        this.ignoreAudition = true;
        return this;
    }

    public <T> ResultMatcher containsObjectAsJson(
            Object expectedObject,
            Class<T> targetClass
    ) {
        return mvcResult -> {
            try {
                var json = mvcResult.getResponse().getContentAsString();
                var extractedValue = JsonPath.read(json, jsonPath);
                T actualObject = jsonMapper.readValue(jsonMapper.writeValueAsString(extractedValue), targetClass);

                var ignoredFields = new ArrayList<>(Arrays.asList(fieldNamesToIgnore));
                ignoredFields.addAll(Arrays.asList(optionalFieldNamesToIgnore));
                if (ignoreAudition) {
                    ignoredFields.addAll(Arrays.asList("createdAt", "createdBy", "updatedAt", "updatedBy"));
                }

                assertThat(actualObject)
                        .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                        .usingComparatorForType(ResponseBodyMatchers::compareTemporalByDelta, OffsetDateTime.class)
                        .usingComparatorForType(ResponseBodyMatchers::compareTemporalByDelta, LocalDateTime.class)
                        .usingRecursiveComparison()
                        .ignoringFields(ignoredFields.toArray(new String[0]))
                        .isEqualTo(expectedObject);

                for (var requiredFieldName : fieldNamesToIgnore) {
                    assertThatCode(
                            () -> JsonPath.read(json, jsonPath + "." + requiredFieldName)
                    ).doesNotThrowAnyException();
                }
            } catch (Exception e) {
                fail("Unable to extract result set. Maybe the path does not contain an object of that type?", e);
            }
        };
    }

    // Not every machine has 9 digits precision, so we need to compare with a delta
    private static int compareTemporalByDelta(OffsetDateTime a, OffsetDateTime b) {
        return Math.abs(ChronoUnit.NANOS.between(a, b)) < 1000 ? 0 : a.compareTo(b);
    }

    private static int compareTemporalByDelta(LocalDateTime a, LocalDateTime b) {
        return Math.abs(ChronoUnit.NANOS.between(a, b)) < 1000 ? 0 : a.compareTo(b);
    }

    public static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers("$");
    }

    public static ResponseBodyMatchers responseBodyAt(String expression) {
        return new ResponseBodyMatchers(expression);
    }
}
