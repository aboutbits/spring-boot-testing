package it.aboutbits.springboot.testing.assertion.document;

import lombok.SneakyThrows;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public final class DocxAssert {
    private final XWPFDocument document;

    private DocxAssert(XWPFDocument document) {
        this.document = document;
    }

    @SneakyThrows(IOException.class)
    public static DocxAssert assertThatDocx(InputStream inputStream) {
        assertThat(inputStream).isNotNull();

        var document = new XWPFDocument(inputStream);

        return new DocxAssert(document);
    }

    public static DocxAssert assertThatDocx(ByteArrayOutputStream outputStream) {
        assertThat(outputStream).isNotNull();

        return assertThatDocx(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public static DocxAssert assertThatDocx(byte[] bytea) {
        assertThat(bytea).isNotNull();

        return assertThatDocx(new ByteArrayInputStream(bytea));
    }

    public DocxAssert hasContent() {
        assertThat(getTextContent()).isNotBlank();

        return this;
    }

    public DocxAssert containsText(String text) {
        assertThat(text).isNotNull();

        assertThat(getTextContent()).contains(text);

        return this;
    }

    private String getTextContent() {
        var sb = new StringBuilder();
        var paragraphs = document.getParagraphs();

        for (var paragraph : paragraphs) {
            var runs = paragraph.getRuns();
            for (var run : runs) {
                sb.append(run.getText(0)).append(" ");
            }
        }

        return sb.toString().trim(); // Remove leading/trailing whitespaces
    }
}
