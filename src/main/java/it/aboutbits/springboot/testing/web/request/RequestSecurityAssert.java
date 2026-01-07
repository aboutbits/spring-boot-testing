package it.aboutbits.springboot.testing.web.request;

import org.assertj.core.api.AbstractAssert;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
public final class RequestSecurityAssert extends AbstractAssert<RequestSecurityAssert, ResultActions> {
    RequestSecurityAssert(ResultActions actual) {
        super(actual, RequestSecurityAssert.class);
    }

    public static RequestSecurityAssert assertThatRequest(ResultActions actual) {
        return new RequestSecurityAssert(actual);
    }

    @SuppressWarnings("unused")
    public void wasUnauthorized() {
        var status = actual.andReturn().getResponse().getStatus();
        assertThat(status).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @SuppressWarnings("unused")
    public void wasDenied() {
        var status = actual.andReturn().getResponse().getStatus();
        assertThat(status).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @SuppressWarnings("unused")
    public void wasGranted() {
        var status = actual.andReturn().getResponse().getStatus();
        assertThat(status).isNotEqualTo(HttpStatus.FORBIDDEN.value());
    }
}
