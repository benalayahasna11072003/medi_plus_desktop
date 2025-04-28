package services.gestionConsultation;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class StyledPDF {

    public String generatePrescriptionPDF(String fileName, String doctorName, String patientName,
                                        String date, String prescriptionDetails) throws IOException {
        // Create upload directory in resources if it doesn't exist
        String resourcesPath = "src/main/resources";
        String uploadFolderPath = resourcesPath + "/gestionConcultation/upload";

        // Create directory path
        Path dirPath = Paths.get(uploadFolderPath);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
                System.out.println("Upload directory created successfully at: " + dirPath);
            } catch (IOException e) {
                System.err.println("Failed to create upload directory: " + e.getMessage());
                throw e;
            }
        }

        // Ensure the fileName doesn't contain any path separators for security
        fileName = new File(fileName).getName();

        // Create full path for the PDF
        String outputPath = uploadFolderPath + "/" + fileName;
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            outputPath += ".pdf";
        }

        // Initialize PDF writer
        PdfWriter writer = new PdfWriter(outputPath);

        // Initialize PDF document
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Define fonts
        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontRegular = PdfFontFactory.createFont("Helvetica");
        PdfFont fontItalic = PdfFontFactory.createFont("Helvetica-Oblique");

        // Define colors
        DeviceRgb headerColor = new DeviceRgb(0, 51, 102); // Dark blue
        DeviceRgb lineColor = new DeviceRgb(0, 102, 204);  // Medium blue
        DeviceRgb grayColor = new DeviceRgb(100, 100, 100); // Gray for footer

        // Add title
        Paragraph title = new Paragraph("Ordonnance Médicale")
                .setFont(fontBold)
                .setFontSize(20)
                .setFontColor(headerColor)
                .setTextAlignment(TextAlignment.LEFT);
        document.add(title);

        // Add decorative line below title
        document.add(new Paragraph(" ").setHeight(5));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        float y = pdfDoc.getFirstPage().getPageSize().getHeight() - 95;
        canvas.setStrokeColor(lineColor);
        canvas.setLineWidth(2);
        canvas.moveTo(50, y);
        canvas.lineTo(545, y);
        canvas.stroke();

        // Add spacing
        document.add(new Paragraph(" ").setHeight(10));

        // Add doctor info
        Paragraph doctorInfo = new Paragraph()
                .add(new Text("Médecin : ").setFont(fontBold).setFontSize(12))
                .add(new Text(doctorName).setFont(fontRegular).setFontSize(12));
        document.add(doctorInfo);

        // Add patient info
        Paragraph patientInfo = new Paragraph()
                .add(new Text("Nom du patient: ").setFont(fontBold).setFontSize(12))
                .add(new Text(patientName).setFont(fontRegular).setFontSize(12));
        document.add(patientInfo);

        // Add date
        Paragraph dateInfo = new Paragraph()
                .add(new Text("Date: ").setFont(fontBold).setFontSize(12))
                .add(new Text(date).setFont(fontRegular).setFontSize(12));
        document.add(dateInfo);

        // Add spacing
        document.add(new Paragraph(" ").setHeight(10));

        // Add separator line
        canvas.setStrokeColor(ColorConstants.LIGHT_GRAY);
        canvas.setLineWidth(0.5f);
        y = pdfDoc.getFirstPage().getPageSize().getHeight() - 190;
        canvas.moveTo(50, y);
        canvas.lineTo(545, y);
        canvas.stroke();

        // Add spacing
        document.add(new Paragraph(" ").setHeight(10));

        // Add prescription header
        Paragraph prescriptionHeader = new Paragraph("Description de la prescription:")
                .setFont(fontBold)
                .setFontSize(12);
        document.add(prescriptionHeader);

        // Add spacing
        document.add(new Paragraph(" ").setHeight(5));

        // Add prescription content with indentation
        String[] prescriptionLines = prescriptionDetails.split("\n");
        for (String line : prescriptionLines) {
            Paragraph prescriptionLine = new Paragraph(line)
                    .setFont(fontRegular)
                    .setFontSize(12)
                    .setMarginLeft(15);
            document.add(prescriptionLine);
        }

        // Add spacing
        document.add(new Paragraph(" ").setHeight(20));

        // Add bottom separator line
        canvas.setStrokeColor(ColorConstants.LIGHT_GRAY);
        canvas.setLineWidth(0.5f);
        y = pdfDoc.getFirstPage().getPageSize().getHeight() - 400;
        canvas.moveTo(50, y);
        canvas.lineTo(545, y);
        canvas.stroke();

        // Add spacing
        document.add(new Paragraph(" ").setHeight(15));

        // Add footer
        Paragraph footer = new Paragraph("Merci de suivre cette prescription à la lettre. Pour toute question, veuillez contacter votre médecin.")
                .setFont(fontItalic)
                .setFontSize(10)
                .setFontColor(grayColor);
        document.add(footer);

        // Draw border around the page
        Rectangle rect = new Rectangle(30, 30, pdfDoc.getDefaultPageSize().getWidth() - 60,
                pdfDoc.getDefaultPageSize().getHeight() - 60);
        canvas.setStrokeColor(lineColor);
        canvas.setLineWidth(1.5f);
        canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
        canvas.stroke();

        // Close the document
        document.close();

        System.out.println("PDF successfully created at: " + outputPath);
        return outputPath;
    }
}
