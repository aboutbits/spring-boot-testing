package it.aboutbits.springboot.testing.web.request;

import it.aboutbits.springboot.toolbox.web.response.ErrorResponse;
import it.aboutbits.springboot.toolbox.web.response.ItemResponse;
import it.aboutbits.springboot.toolbox.web.response.ItemResponseWithMeta;
import it.aboutbits.springboot.toolbox.web.response.ListResponse;
import it.aboutbits.springboot.toolbox.web.response.PagedResponse;
import it.aboutbits.springboot.toolbox.web.response.meta.Meta;
import jakarta.servlet.http.Cookie;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@SuppressWarnings("java:S1192")
@Slf4j
@NullMarked
public abstract class Request<R extends AbstractMockHttpServletRequestBuilder<R>> {
    protected record UsernameAndPassword(String username, String password) {
    }

    protected record UrlWithVariables(String url, Object... pathVariables) {
    }

    protected final JsonMapper jsonMapper;
    protected final MockMvc mockMvc;
    protected final UrlWithVariables url;
    protected final MediaType contentType;

    protected String body = "";
    @Nullable
    protected HttpStatus status = null;
    protected Cookie[] cookies = new Cookie[0];
    @Nullable
    protected UsernameAndPassword basicAuth = null;
    protected Map<String, String> parameters = new HashMap<>();
    protected Map<String, String[]> parametersArray = new HashMap<>();

    Request(
            MockMvc mockMvc,
            JsonMapper jsonMapper,
            String url,
            MediaType contentType,
            Object... pathVariables
    ) {
        this.jsonMapper = jsonMapper;

        this.mockMvc = mockMvc;
        this.url = new UrlWithVariables(url, pathVariables);
        this.contentType = contentType;
    }

    @SuppressWarnings("unused")
    public Request<R> cookies(Cookie... cookies) {
        this.cookies = cookies;
        return this;
    }

    @SuppressWarnings("unused")
    public Request<R> body(Object body) {
        if (body instanceof String stringBody) {
            this.body = stringBody;
            return this;
        }

        try {
            this.body = jsonMapper.writeValueAsString(body);
        } catch (JacksonException e) {
            log.error("Failed to write body as JSON.", e);
            throw new RequestBodyException(e);
        }
        return this;
    }

    @SuppressWarnings("unused")
    public Request<R> param(String name, Object value) {
        this.parameters.put(name, String.valueOf(value));
        return this;
    }

    @SuppressWarnings("unused")
    public Request<R> param(String name, Collection<?> values) {
        this.parametersArray.put(
                name,
                values.stream()
                        .map(String::valueOf)
                        .toArray(String[]::new)
        );
        return this;
    }

    @SuppressWarnings("unused")
    public Request<R> expectStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    @SuppressWarnings("unused")
    public Request<R> basicAuth(String username, String password) {
        this.basicAuth = new UsernameAndPassword(username, password);
        return this;
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    @SuppressWarnings("unused")
    public <T> ItemResponse<T> returnItem(Class<T> clazz) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = jsonMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(ItemResponse.class, clazz);

        try {
            return jsonMapper.readValue(resString, javaType);
        } catch (JacksonException e) {
            log.error("Failed to read response as JSON.", e);
            throw new ResponseBodyException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    @SuppressWarnings("unused")
    public <T, M extends Meta> ItemResponseWithMeta<T, M> returnItem(
            Class<T> clazz,
            Class<M> metaClass
    ) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = jsonMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(ItemResponseWithMeta.class, clazz, metaClass);

        try {
            return jsonMapper.readValue(resString, javaType);
        } catch (JacksonException e) {
            log.error("Failed to read response as JSON.", e);
            throw new ResponseBodyException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    @SuppressWarnings("unused")
    public <T> ListResponse<T> returnList(Class<T> clazz) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = jsonMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(ListResponse.class, clazz);

        try {
            return jsonMapper.readValue(resString, javaType);
        } catch (JacksonException e) {
            log.error("Failed to read response as JSON.", e);
            throw new ResponseBodyException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    @SuppressWarnings("unused")
    public <T> PagedResponse<T> returnPage(Class<T> clazz) {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        var typeFactory = jsonMapper.getTypeFactory();
        var javaType = typeFactory.constructParametricType(PagedResponse.class, clazz);

        try {
            return jsonMapper.readValue(resString, javaType);
        } catch (JacksonException e) {
            log.error("Failed to read response as JSON.", e);
            throw new ResponseBodyException(e);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    @SuppressWarnings("unused")
    public ErrorResponse returnError() {
        var res = _execute();

        var resString = res.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        try {
            return jsonMapper.readValue(resString, ErrorResponse.class);
        } catch (JacksonException e) {
            log.error("Failed to read response as JSON.", e);
            throw new ResponseBodyException(e);
        }
    }

    @SuppressWarnings("unused")
    public ResultActions returnRaw() {
        return _execute();
    }

    @SuppressWarnings("unused")
    public void execute() {
        _execute();
    }

    @SuppressWarnings({"checkstyle:MethodName", "java:S100"})
    private ResultActions _execute() {
        var requestBuilder = prepareRequestBuilder();

        ResultActions resultActions;
        try {
            resultActions = mockMvc.perform(requestBuilder);
        } catch (Exception e) {
            log.error("Request execution failed.", e);
            throw new RequestExecutionException(e);
        }

        maybeCheckStatus(resultActions);

        return resultActions;
    }

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

        return requestBuilder;
    }

    protected abstract R getRequestBuilder(UrlWithVariables url);

    protected abstract boolean useCsrf();

    protected void maybeCheckStatus(ResultActions resultActions) {
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

    public static class RequestBodyException extends RuntimeException {
        public RequestBodyException(Throwable cause) {
            super(cause);
        }
    }

    public static class ResponseBodyException extends RuntimeException {
        public ResponseBodyException(Throwable cause) {
            super(cause);
        }
    }

    public static class RequestExecutionException extends RuntimeException {
        public RequestExecutionException(Throwable cause) {
            super(cause);
        }
    }
}
