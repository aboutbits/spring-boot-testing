package it.aboutbits.springboot.testing.web.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class PostMultipart extends MultipartRequest {
    PostMultipart(
            @NonNull MockMvc mockMvc,
            @NonNull ObjectMapper objectMapper,
            @NonNull String url,
            @NonNull Object... pathVariables
    ) {
        super(mockMvc, objectMapper, url, pathVariables);
    }

    @Override
    protected @NonNull MockMultipartHttpServletRequestBuilder getRequestBuilder(@NonNull UrlWithVariables url) {
        return MockMvcRequestBuilders.multipart(HttpMethod.POST, url.url(), url.pathVariables());
    }

    @Override
    protected boolean useCsrf() {
        return true;
    }
}
