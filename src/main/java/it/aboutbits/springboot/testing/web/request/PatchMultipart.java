package it.aboutbits.springboot.testing.web.request;

import lombok.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.json.JsonMapper;

public class PatchMultipart extends MultipartRequest {
    PatchMultipart(
            @NonNull MockMvc mockMvc,
            @NonNull JsonMapper objectMapper,
            @NonNull String url,
            @NonNull Object... pathVariables
    ) {
        super(mockMvc, objectMapper, url, pathVariables);
    }

    @Override
    protected @NonNull MockMultipartHttpServletRequestBuilder getRequestBuilder(@NonNull UrlWithVariables url) {
        return MockMvcRequestBuilders.multipart(HttpMethod.PATCH, url.url(), url.pathVariables());
    }

    @Override
    protected boolean useCsrf() {
        return true;
    }
}
