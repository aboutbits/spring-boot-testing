package it.aboutbits.springboot.testing.web.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.aboutbits.springboot.toolbox.web.response.ErrorResponse;
import it.aboutbits.springboot.toolbox.web.response.ItemResponse;
import it.aboutbits.springboot.toolbox.web.response.ItemResponseWithMeta;
import it.aboutbits.springboot.toolbox.web.response.ListResponse;
import it.aboutbits.springboot.toolbox.web.response.PagedResponse;
import it.aboutbits.springboot.toolbox.web.response.meta.Meta;
import jakarta.servlet.http.Cookie;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@Slf4j
public abstract class Request<R extends MockHttpServletRequestBuilder> {
    protected record UsernameAndPassword(String username, String password) {
    }

    protected record UrlWithVariables(String url, Object... pathVariables) {
    }

    protected final ObjectMapper objectMapper;
    protected final MockMvc mockMvc;
    protected final UrlWithVariables url;
    protected final MediaType contentType;

    protected String body = "";
    protected HttpStatus status = null;
    protected Cookie[] cookies = new Cookie[0];
    protected UsernameAndPassword basicAuth = null;
    protected Map<String, String> parameters = new HashMap<>();
    protected Map<String, String[]> parametersArray = new HashMap<>();

    Request(
            @NonNull MockMvc mockMvc,
            @NonNull ObjectMapper objectMapper,
            @NonNull String url,
            @NonNull MediaType contentType,
            @NonNull Object... pathVariables
    ) {
        this.objectMapper = objectMapper;

        this.mockMvc = mockMvc;
        this.url = new UrlWithVariables(url, pathVariables);
        this.contentType = contentType;
    }

    public Request<R> cookies(@NonNull Cookie... cookies) {
        this.cookies = cookies;
        return this;
    }

    public Request<R> body(@NonNull Object body) {
        if (body instanceof String stringBody) {
            this.body = stringBody;
            return this;
        }

        try {
            this.body = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            log.error("Failed to write body as JSON.", e);
            throw new RuntimeException(e);
        }
        return this;
    }

    public Request<R> param(@NonNull String name, Object value) {
        this.parameters.put(name, String.valueOf(value));
        return this;
    }

    public Request<R> param(@NonNull String name, Collection<?> values) {
        this.parametersArray.put(
                name,
                values.stream()
                        .map(String::valueOf)
                        .toArray(String[]::new)
        );
        return this;
    }

    public Request<R> expectStatus(@NonNull HttpStatus status) {
        this.status = status;
        return this;
    }

    public Request<R> basicAuth(@NonNull String username, @NonNull String password) {
        this.basicAuth = new UsernameAndPassword(username, password);
        return this;
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public <T> ItemResponse<T> returnItem(@NonNull Class<T> clazz) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(ItemResponse.class, clazz);

        try {
            return objectMapper.readValue(resString, javaType);
        } catch (JsonProcessingException e) {
            log.error("Failed to read response as JSON.", e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public <T, M extends Meta> ItemResponseWithMeta<T, M> returnItem(
            @NonNull Class<T> clazz,
            @NonNull Class<M> metaClass
    ) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(ItemResponseWithMeta.class, clazz, metaClass);

        try {
            return objectMapper.readValue(resString, javaType);
        } catch (JsonProcessingException e) {
            log.error("Failed to read response as JSON.", e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public <T> ListResponse<T> returnList(@NonNull Class<T> clazz) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(ListResponse.class, clazz);

        try {
            return objectMapper.readValue(resString, javaType);
        } catch (JsonProcessingException e) {
            log.error("Failed to read response as JSON.", e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public <T> PagedResponse<T> returnPage(@NonNull Class<T> clazz) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = objectMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(PagedResponse.class, clazz);

        try {
            return objectMapper.readValue(resString, javaType);
        } catch (JsonProcessingException e) {
            log.error("Failed to read response as JSON.", e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public ErrorResponse returnError() {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        try {
            return objectMapper.readValue(resString, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to read response as JSON.", e);
            throw new RuntimeException(e);
        }
    }

    public ResultActions returnRaw() {
        return _execute();
    }

    public void execute() {
        _execute();
    }

    @SuppressWarnings({"checkstyle:MethodName", "java:S100"})
    private ResultActions _execute() {
        var requestBuilder = prepareRequestBuilder();

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(requestBuilder);
        } catch (Exception e) {
            log.error("Request execution failed.", e);
            throw new RuntimeException(e);
        }

        maybeCheckStatus(resultActions);

        return resultActions;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    protected R prepareRequestBuilder() {
        var requestBuilder = getRequestBuilder(url)
                .content(body)
                .contentType(contentType);

        if (useCsrf()) {
            requestBuilder.with(csrf().asHeader());
        }

        if (basicAuth != null) {
            requestBuilder.with(
                    httpBasic(
                            basicAuth.username(),
                            basicAuth.password()
                    )
            );
        }

        if (cookies.length > 0) {
            requestBuilder.cookie(cookies);
        }

        for (var entry : parameters.entrySet()) {
            requestBuilder.param(entry.getKey(), entry.getValue());
        }

        for (var entry : parametersArray.entrySet()) {
            requestBuilder.param(entry.getKey(), entry.getValue());
        }

        return (R) requestBuilder;
    }

    @NonNull
    protected abstract R getRequestBuilder(@NonNull UrlWithVariables url);

    protected abstract boolean useCsrf();

    protected void maybeCheckStatus(@NonNull ResultActions resultActions) {
        var mvcResult = resultActions.andReturn();
        if (status != null) {
            assertThat(
                    mvcResult.getResponse().getStatus()
            )
                    .withFailMessage(
                            "Expected status '%s' but was '%s'",
                            status.value(),
                            mvcResult.getResponse().getStatus()
                    )
                    .isEqualTo(status.value());
        }
    }
}
