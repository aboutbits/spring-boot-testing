package it.aboutbits.springboot.testing.web.request;

import com.google.errorprone.annotations.CheckReturnValue;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
@NullMarked
public class HttpTestClient {
    private final MockMvc mockMvc;
    private final JsonMapper jsonMapper;

    @CheckReturnValue
    public Request<MockHttpServletRequestBuilder> get(String url, Object... pathVariables) {
        return new Get(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public Request<MockHttpServletRequestBuilder> put(String url, Object... pathVariables) {
        return new Put(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public MultipartRequest putMultipart(String url, Object... pathVariables) {
        return new PutMultipart(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public Request<MockHttpServletRequestBuilder> post(String url, Object... pathVariables) {
        return new Post(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public MultipartRequest postMultipart(String url, Object... pathVariables) {
        return new PostMultipart(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public Request<MockHttpServletRequestBuilder> patch(String url, Object... pathVariables) {
        return new Patch(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public MultipartRequest patchMultipart(String url, Object... pathVariables) {
        return new PatchMultipart(mockMvc, jsonMapper, url, pathVariables);
    }

    @CheckReturnValue
    public Request<MockHttpServletRequestBuilder> delete(String url, Object... pathVariables) {
        return new Delete(mockMvc, jsonMapper, url, pathVariables);
    }
}
