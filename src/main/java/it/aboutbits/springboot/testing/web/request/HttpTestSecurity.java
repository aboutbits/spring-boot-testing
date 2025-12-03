package it.aboutbits.springboot.testing.web.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;

import static it.aboutbits.springboot.testing.web.request.RequestSecurityAssert.assertThatRequest;

@Getter
@RequiredArgsConstructor
public class HttpTestSecurity {
    private final HttpTestClient httpTestClient;

    public void assertGetGranted(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.get(url, pathVariables)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertGetDenied(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.get(url, pathVariables)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertPostGranted(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.post(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertPostDenied(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.post(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertPostMultipartGranted(
            @NonNull String url,
            @NonNull MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.postMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertPostMultipartDenied(
            @NonNull String url,
            @NonNull MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.postMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertPutGranted(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.put(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertPutDenied(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.put(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertPutMultipartGranted(
            @NonNull String url,
            @NonNull MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.putMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertPutMultipartDenied(
            @NonNull String url,
            @NonNull MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.putMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertPatchGranted(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.patch(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertPatchDenied(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.patch(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertPatchMultipartGranted(
            @NonNull String url,
            @NonNull MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.patchMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertPatchMultipartDenied(
            @NonNull String url,
            @NonNull MockMultipartFile multipartFile,
            Object... pathVariables
    ) {
        var result = httpTestClient.patchMultipart(url, pathVariables)
                .file(multipartFile)
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }

    public void assertDeleteGranted(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.delete(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasGranted();
    }

    public void assertDeleteDenied(@NonNull String url, Object... pathVariables) {
        var result = httpTestClient.delete(url, pathVariables)
                .body("{}")
                .returnRaw();

        assertThatRequest(result).wasDenied();
    }
}
