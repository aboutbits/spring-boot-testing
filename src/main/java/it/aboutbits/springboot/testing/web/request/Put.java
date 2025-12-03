package it.aboutbits.springboot.testing.web.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class Put extends Request<MockHttpServletRequestBuilder> {
    Put(
            @NonNull MockMvc mockMvc,
            @NonNull ObjectMapper objectMapper,
            @NonNull String url,
            @NonNull Object... pathVariables
    ) {
        super(mockMvc, objectMapper, url, APPLICATION_JSON, pathVariables);
    }

    @Override
    protected @NonNull MockHttpServletRequestBuilder getRequestBuilder(@NonNull UrlWithVariables url) {
        return MockMvcRequestBuilders.put(url.url(), url.pathVariables());
    }

    @Override
    protected boolean useCsrf() {
        return true;
    }
}
