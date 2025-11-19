package com.resumeanalyzer.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

public class ParserUtils {

	public static byte[] generatePdfFromText(String text) throws IOException {
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			text = text.replace('\t', ' ') // replace tabs with a space
					.replace('\u00A0', ' ') // non-breaking space
					.replace('\u2010', '-') // hyphen
					.replace('\u2013', '-') // en dash
					.replace('\u2014', '-') // em dash
					.replace('\u2022', '*'); // bullet

			PDType0Font font = PDType0Font.load(document,
					new ClassPathResource("fonts/DejaVuSans.ttf").getInputStream());
			float fontSize = 12;
			float leading = 14.5f;
			float margin = 50;

			// Split text into paragraphs
			String[] paragraphs = text.split("\n");

			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.beginText();
			contentStream.setFont(font, fontSize);
			contentStream.setLeading(leading);
			float cursorX = margin;
			float cursorY = page.getMediaBox().getHeight() - margin;
			contentStream.newLineAtOffset(cursorX, cursorY);

			for (String paragraph : paragraphs) {
				List<String> lines = wrapText(paragraph, font, fontSize, page.getMediaBox().getWidth() - 2 * margin);

				for (String line : lines) {
					// Check if we reached bottom of page
					if (cursorY <= margin) {
						contentStream.endText();
						contentStream.close();

						page = new PDPage(PDRectangle.A4);
						document.addPage(page);

						contentStream = new PDPageContentStream(document, page);
						contentStream.beginText();
						contentStream.setFont(font, fontSize);
						contentStream.setLeading(leading);
						cursorY = page.getMediaBox().getHeight() - margin;
						contentStream.newLineAtOffset(cursorX, cursorY);
					}

					contentStream.showText(line);
					contentStream.newLine();
					cursorY -= leading;
				}

				// extra space between paragraphs
				cursorY -= leading / 2;
			}

			contentStream.endText();
			contentStream.close();

			document.save(outputStream);
			return outputStream.toByteArray();
		}
	}

	// Wrap long lines to fit within page width
	private static List<String> wrapText(String text, PDType0Font font, float fontSize, float width)
			throws IOException {
		List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();

		for (String word : words) {
			String testLine = line.length() == 0 ? word : line + " " + word;
			float size = font.getStringWidth(testLine) / 1000 * fontSize;
			if (size > width) {
				lines.add(line.toString());
				line = new StringBuilder(word);
			} else {
				line = new StringBuilder(testLine);
			}
		}

		if (!line.toString().isEmpty()) {
			lines.add(line.toString());
		}

		return lines;
	}

	public static String generateTextFromFile(MultipartFile file) throws IOException, TikaException {
		Tika tika = new Tika();
		String content = tika.parseToString(file.getInputStream());
		return content;
	}
	
}
