package karthik.projects.watermark;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class BatchPDFWatermark {

	public static PDDocument addWatermark(InputStream input, String watermarkText) {
		try {
			PDDocument document = PDDocument.load(input);
			for (var page : document.getPages()) {
				PDRectangle mediaBox = page.getMediaBox();
				float pageWidth = mediaBox.getWidth();
				float pageHeight = mediaBox.getHeight();

				PDPageContentStream contentStream = new PDPageContentStream(document, page,
						PDPageContentStream.AppendMode.APPEND, true, true);

				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 48);
				contentStream.beginText();
				contentStream.newLineAtOffset(pageWidth / 4, pageHeight / 4); // Adjust watermark position
				contentStream.showText(watermarkText);
				contentStream.endText();
				contentStream.close();
			}
			System.out.println("Watermark added to: ");
			return document;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}