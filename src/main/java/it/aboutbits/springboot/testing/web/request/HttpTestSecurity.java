package it.aboutbits.springboot.testing.web.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.mock.web.MockMultipartFile;

import static it.aboutbits.springboot.testing.web.request.RequestSecurityAssert.assertThatRequest;

@Getter
@RequiredArgsConstructor
@NullMarked
public class HttpTestSecurity {
    private final HttpTestClient httpTestClient;

    @SuppressWarnings("unused")
    public void assertGetGranted(String url, Object... pathVariables) {
        var result = httpTestClient.get(url, pathVariables)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertGetDenied(String url, Object... pathVariables) {
        var result = httpTestClient.get(url, pathVariables)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertPostGranted(String url, Object... pathVariables) {
        var result = httpTestClient.post(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertPostDenied(String url, Object... pathVariables) {
        var result = httpTestClient.post(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertPostMultipartGranted(
            String url,
            MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.postMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertPostMultipartDenied(
            String url,
            MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.postMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertPutGranted(String url, Object... pathVariables) {
        var result = httpTestClient.put(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertPutDenied(String url, Object... pathVariables) {
        var result = httpTestClient.put(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertPutMultipartGranted(
            String url,
            MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.putMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertPutMultipartDenied(
            String url,
            MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.putMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertPatchGranted(String url, Object... pathVariables) {
        var result = httpTestClient.patch(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertPatchDenied(String url, Object... pathVariables) {
        var result = httpTestClient.patch(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertPatchMultipartGranted(
            String url,
            MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.patchMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    @SuppressWarnings("unused")
    public void assertPatchMultipartDenied(
            String url,
            MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.patchMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    @SuppressWarnings("unused")
    public void assertDeleteGranted(String url, Object... pathVariables) {
        var result = httpTestClient.delete(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertDeleteDenied(String url, Object... pathVariables) {
        var result = httpTestClient.delete(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }
}
