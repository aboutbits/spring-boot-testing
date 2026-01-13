package it.aboutbits.springboot.testing.web.request;

import org.jspecify.annotations.NullMarked;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@NullMarked
public class Delete extends Request<MockHttpServletRequestBuilder> {
    Delete(
            MockMvc mockMvc,
            JsonMapper objectMapper,
            String url,
            Object... pathVariables
    ) {
        super(mockMvc, objectMapper, url, APPLICATION_JSON, pathVariables);
    }

    @Override
    protected MockHttpServletRequestBuilder getRequestBuilder(UrlWithVariables url) {
        return MockMvcRequestBuilders.delete(url.url(), url.pathVariables());
    }

    @Override
    protected boolean useCsrf() {
        return true;
    }
}
