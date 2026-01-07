package it.aboutbits.springboot.testing.web.request;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;

@NullMarked
public class PostMultipart extends MultipartRequest {
    PostMultipart(
            MockMvc mockMvc,
            JsonMapper objectMapper,
            String url,
            Object... pathVariables
    ) {
        super(mockMvc, objectMapper, url, pathVariables);
    }

    @Override
    protected MockMultipartHttpServletRequestBuilder getRequestBuilder(UrlWithVariables url) {
        return MockMvcRequestBuilders.multipart(HttpMethod.POST, url.url(), url.pathVariables());
    }

    @Override
    protected boolean useCsrf() {
        return true;
    }
}
