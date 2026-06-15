package de.hitec.nhplus.utils;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Employee;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Role;
import de.hitec.nhplus.model.Treatment;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;


public class PdfTest {
    public static void main(String[] args) {

    }

    /*
     * @param pid Patient id.
     * @param firstName First name of the patient.
     * @param surname Last name of the patient.
     * @param dateOfBirth Date of birth of the patient.
     * @param careLevel Care level of the patient.
     * @param roomNumber Room number of the patient.
     * @param assets Assets of the patient.
     */
    @FXML
    private Button buttonPrint;
    public PdfTest()
    {
        Employee currentUser = Session.getCurrentEmployee();
        if(currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.VERWALTUNG)
        {
            buttonPrint.setVisible(true);
        }
        else
            buttonPrint.setVisible(false);
    }

    public void createPatientPdf(Patient patient, List<Treatment> treatments, Window window)
    {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);

            PDFont helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // =========================
            // 1. Titel / Patient
            // =========================
            cs.beginText();
            cs.setFont(helvetica, 12);
            cs.newLineAtOffset(100, 725);
            cs.showText("Patient: " + patient.getFirstName() + " " + patient.getSurname());
            cs.endText();

            cs.beginText();
            cs.setFont(helvetica, 12);
            cs.newLineAtOffset(100, 700);
            cs.showText("Raum: " + patient.getRoomNumber());
            cs.endText();

            cs.beginText();
            cs.setFont(helvetica, 12);
            cs.newLineAtOffset(100, 675);
            cs.showText("Pflegegrad: " + patient.getCareLevel());
            cs.endText();

            cs.beginText();
            cs.setFont(helvetica, 12);
            cs.newLineAtOffset(100, 650);
            cs.showText("Geburtsdatum: " + patient.getDateOfBirth());
            cs.endText();

            // =========================
            // 2. Trennstrich
            // =========================

            cs.setStrokingColor(0, 0, 0); // schwarz

            cs.moveTo(50, 550);   // links
            cs.lineTo(550, 550);  // rechts
            cs.stroke();

            // =========================
            // 3. Tabellen-Layout
            // =========================

            float startX = 100;
            float startY = 500;
            float rowHeight = 20;

            float col1X = startX;
            float col2X = startX + 150;
            float col3X = startX + 300;

            // Header
            cs.beginText();
            cs.setFont(helvetica, 10);
            cs.newLineAtOffset(col1X, startY);
            cs.showText("Datum");
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(col2X, startY);
            cs.showText("Beginn");
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(col3X, startY);
            cs.showText("Beschreibung");
            cs.endText();

            // =========================
            // 4. Datenzeilen
            // =========================
            float y = startY - rowHeight;

            for (Treatment t : treatments) {

                cs.beginText();
                cs.newLineAtOffset(col1X, y);
                cs.showText(String.valueOf(t.getDate()));
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col2X, y);
                cs.showText(String.valueOf(t.getBegin()));
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col3X, y);
                cs.showText(t.getDescription());
                cs.endText();

                y -= rowHeight;
            }

            cs.close();

            // =========================
            // 5. Speicherdialog
            // =========================
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("PDF speichern");
            fileChooser.setInitialFileName("patient_" + patient.getSurname() + ".pdf");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Datei", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(window);

            if (file != null) {


                document.save(file); //speichern

                System.out.println("PDF gespeichert: " + file.getAbsolutePath());

                // =========================
                // 6. JSON-Datei erstellen
                // =========================
                String jsonPath = file.getAbsolutePath()
                        .replace(".pdf", ".json");

                File jsonFile = new File(jsonPath);

                String json = buildPatientJson(patient, treatments);

                java.nio.file.Files.writeString(
                        jsonFile.toPath(),
                        json
                );

                System.out.println("JSON gespeichert: " + jsonFile.getAbsolutePath());

            } else {
                System.out.println("Speichern abgebrochen");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildPatientJson(Patient patient, List<Treatment> treatments) {

        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"patient\": {\n");
        sb.append("    \"firstName\": \"").append(patient.getFirstName()).append("\",\n");
        sb.append("    \"surname\": \"").append(patient.getSurname()).append("\",\n");
        sb.append("    \"room\": \"").append(patient.getRoomNumber()).append("\",\n");
        sb.append("    \"careLevel\": \"").append(patient.getCareLevel()).append("\",\n");
        sb.append("    \"dateOfBirth\": \"").append(patient.getDateOfBirth()).append("\"\n");
        sb.append("  },\n");

        sb.append("  \"treatments\": [\n");

        for (int i = 0; i < treatments.size(); i++) {
            Treatment t = treatments.get(i);

            sb.append("    {\n");
            sb.append("      \"date\": \"").append(t.getDate()).append("\",\n");
            sb.append("      \"begin\": \"").append(t.getBegin()).append("\",\n");
            sb.append("      \"description\": \"").append(t.getDescription()).append("\"\n");
            sb.append("    }");

            if (i < treatments.size() - 1) {
                sb.append(",");
            }

            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}");

        return sb.toString();
    }
}