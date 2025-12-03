package it.aboutbits.springboot.testing.web.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RequiredArgsConstructor
public class HttpTestClient {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public Request<MockHttpServletRequestBuilder> get(@NonNull String url, Object... pathVariables) {
        return new Get(mockMvc, objectMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> put(@NonNull String url, Object... pathVariables) {
        return new Put(mockMvc, objectMapper, url, pathVariables);
    }

    public MultipartRequest putMultipart(@NonNull String url, Object... pathVariables) {
        return new PutMultipart(mockMvc, objectMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> post(@NonNull String url, Object... pathVariables) {
        return new Post(mockMvc, objectMapper, url, pathVariables);
    }

    public MultipartRequest postMultipart(@NonNull String url, Object... pathVariables) {
        return new PostMultipart(mockMvc, objectMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> patch(@NonNull String url, Object... pathVariables) {
        return new Patch(mockMvc, objectMapper, url, pathVariables);
    }

    public MultipartRequest patchMultipart(@NonNull String url, Object... pathVariables) {
        return new PatchMultipart(mockMvc, objectMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> delete(@NonNull String url, Object... pathVariables) {
        return new Delete(mockMvc, objectMapper, url, pathVariables);
    }
}
