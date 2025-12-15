package it.aboutbits.springboot.testing.web.request;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
public class HttpTestClient {
    private final MockMvc mockMvc;
    private final JsonMapper jsonMapper;

    public Request<MockHttpServletRequestBuilder> get(@NonNull String url, Object... pathVariables) {
        return new Get(mockMvc, jsonMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> put(@NonNull String url, Object... pathVariables) {
        return new Put(mockMvc, jsonMapper, url, pathVariables);
    }

    public MultipartRequest putMultipart(@NonNull String url, Object... pathVariables) {
        return new PutMultipart(mockMvc, jsonMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> post(@NonNull String url, Object... pathVariables) {
        return new Post(mockMvc, jsonMapper, url, pathVariables);
    }

    public MultipartRequest postMultipart(@NonNull String url, Object... pathVariables) {
        return new PostMultipart(mockMvc, jsonMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> patch(@NonNull String url, Object... pathVariables) {
        return new Patch(mockMvc, jsonMapper, url, pathVariables);
    }

    public MultipartRequest patchMultipart(@NonNull String url, Object... pathVariables) {
        return new PatchMultipart(mockMvc, jsonMapper, url, pathVariables);
    }

    public Request<MockHttpServletRequestBuilder> delete(@NonNull String url, Object... pathVariables) {
        return new Delete(mockMvc, jsonMapper, url, pathVariables);
    }
}
