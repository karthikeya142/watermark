package karthik.projects.watermark;

import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
@Controller
public class WatermarkApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatermarkApplication.class, args);
	}

	@PostMapping("/upload")
	public ResponseEntity<Resource> uploadPdf(@RequestParam("pdfFile") MultipartFile pdfFile,
			@RequestParam("watermarkName") String watermarkName, Model model) {
		if (pdfFile.isEmpty()) {
			return ResponseEntity.badRequest()
					.body(new ByteArrayResource("Please select a PDF file to upload".getBytes()));
		}

		try {
			var document = BatchPDFWatermark.addWatermark(pdfFile.getInputStream(), watermarkName);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			document.save(outputStream);
			document.close();

			Resource resource = new ByteArrayResource(outputStream.toByteArray());
			String filename = pdfFile.getOriginalFilename();

			return createDownloadResponse(resource, filename);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ByteArrayResource("Error occurred while processing the PDF file.".getBytes()));
		}
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}

	@RequestMapping(path = "/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadPdf(Model model) {
		byte[] pdfData = (byte[]) model.getAttribute("pdfData");
		String filename = (String) model.getAttribute("filename");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData(filename, filename);

		return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
	}

	private ResponseEntity<Resource> createDownloadResponse(Resource resource, String filename) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData(filename, filename);

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

}
