package it.aboutbits.springboot.testing.assertion.document;

import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public final class XlsxAssert {
    private final XSSFWorkbook workbook;

    private String textContent = null;

    private XlsxAssert(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    @SneakyThrows(IOException.class)
    public static XlsxAssert assertThatXlsx(InputStream inputStream) {
        assertThat(inputStream).isNotNull();

        var workbook = new XSSFWorkbook(inputStream);

        return new XlsxAssert(workbook);
    }

    public static XlsxAssert assertThatXlsx(ByteArrayOutputStream outputStream) {
        assertThat(outputStream).isNotNull();

        return assertThatXlsx(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public static XlsxAssert assertThatXlsx(byte[] bytea) {
        assertThat(bytea).isNotNull();

        return assertThatXlsx(new ByteArrayInputStream(bytea));
    }

    public XlsxAssert hasNumberOfSheets(int expectedNumberOfPages) {
        assertThat(workbook.getNumberOfSheets()).isEqualTo(expectedNumberOfPages);

        return this;
    }

    public XlsxAssert hasContent() {
        assertThat(getTextContent()).isNotBlank();
        assertThat(workbook.getNumberOfSheets()).isNotZero();

        return this;
    }

    public XlsxAssert containsText(String text) {
        assertThat(text).isNotNull();

        assertThat(getTextContent()).contains(text);

        return this;
    }

    private String getTextContent() {
        if (textContent == null) {
            var allContent = new StringBuilder();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);

                for (var row : sheet) {
                    for (var cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                allContent.append(cell.getStringCellValue()).append("\t");
                                break;
                            case NUMERIC:
                                allContent.append(cell.getNumericCellValue()).append("\t");
                                break;
                            case BOOLEAN:
                                allContent.append(cell.getBooleanCellValue()).append("\t");
                                break;
                            case FORMULA:
                                allContent.append(cell.getCellFormula()).append("\t");
                                break;
                            default:
                                allContent.append("\t");
                        }
                    }
                    allContent.append("\n");
                }
            }

            textContent = allContent.toString();
        }

        return textContent;
    }
}
