package it.aboutbits.springboot.testing.web.request;

import org.jspecify.annotations.NullMarked;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@NullMarked
public abstract class MultipartRequest extends Request<MockMultipartHttpServletRequestBuilder> {
    protected List<MockPart> parts = new ArrayList<>();
    protected List<MockMultipartFile> files = new ArrayList<>();

    MultipartRequest(
            MockMvc mockMvc,
            JsonMapper objectMapper,
            String url,
            Object... pathVariables
    ) {
        super(mockMvc, objectMapper, url, MULTIPART_FORM_DATA, pathVariables);
    }

    @SuppressWarnings("unused")
    public MultipartRequest part(MockPart part) {
        parts.add(part);
        return this;
    }

    @SuppressWarnings("unused")
    public MultipartRequest file(MockMultipartFile file) {
        files.add(file);
        return this;
    }

    @Override
    protected MockMultipartHttpServletRequestBuilder prepareRequestBuilder() {
        var requestBuilder = super.prepareRequestBuilder();

        for (var file : files) {
            requestBuilder.file(file);
        }
        for (var part : parts) {
            requestBuilder.part(part);
        }

        return requestBuilder;
    }
}
