package it.aboutbits.springboot.testing.assertion.document;

import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public final class PdfAssert {
    private final PDDocument pdDocument;

    private String textContent = null;

    private PdfAssert(PDDocument pdDocument) {
        this.pdDocument = pdDocument;
    }

    @SneakyThrows(IOException.class)
    public static PdfAssert assertThatPdf(InputStream inputStream) {
        assertThat(inputStream).isNotNull();

        var document = Loader.loadPDF(inputStream.readAllBytes());

        return new PdfAssert(document);
    }

    public static PdfAssert assertThatPdf(ByteArrayOutputStream outputStream) {
        assertThat(outputStream).isNotNull();

        return assertThatPdf(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public static PdfAssert assertThatPdf(byte[] bytea) {
        assertThat(bytea).isNotNull();

        return assertThatPdf(new ByteArrayInputStream(bytea));
    }

    public PdfAssert hasNumberOfPages(int expectedNumberOfPages) {
        assertThat(pdDocument.getNumberOfPages()).isEqualTo(expectedNumberOfPages);

        return this;
    }

    public PdfAssert hasContent() {
        assertThat(getTextContent()).isNotBlank();
        assertThat(pdDocument.getNumberOfPages()).isNotZero();

        return this;
    }

    public PdfAssert containsText(String text) {
        assertThat(text).isNotNull();

        assertThat(getTextContent()).contains(cleanTextForComparison(text));

        return this;
    }

    @SneakyThrows(IOException.class)
    private String getTextContent() {
        if (textContent == null) {
            var textStripper = new PDFTextStripper();
            textStripper.setStartPage(0);
            textStripper.setEndPage(pdDocument.getNumberOfPages());

            textContent = textStripper.getText(pdDocument);
        }

        return cleanTextForComparison(textContent);
    }

    // ignore line breaks, and replace multiple following spaces with a single space character
    private String cleanTextForComparison(String text) {
        return text.replace("\\R", "").replaceAll("\\s", "").trim();
    }
}
