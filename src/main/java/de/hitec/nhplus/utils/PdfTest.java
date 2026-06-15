package de.hitec.nhplus.utils;

import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Utility-Klasse zum Erstellen von PDF- und JSON-Dateien
 * für Patientendaten inklusive Behandlungen.
 *
 * <p>Erzeugt eine strukturierte PDF-Datei mit Patientendaten
 * sowie eine ergänzende JSON-Datei im gleichen Verzeichnis.</p>
 */
public class PdfTest {

    /**
     * Startpunkt (aktuell nicht verwendet).
     *
     * @param args Programmargumente
     */
    public static void main(String[] args) {
        // nicht implementiert
    }

    /**
     * Erstellt ein PDF-Dokument mit Patientendaten und Behandlungen
     * und speichert es über einen Dateidialog.
     * Zusätzlich wird eine JSON-Datei mit denselben Daten erzeugt.
     *
     * @param patient   der Patient, dessen Daten exportiert werden
     * @param treatments Liste der Behandlungen des Patienten
     * @param window    JavaFX-Fenster für den Speicherdialog
     */
    public void createPatientPdf(Patient patient, List<Treatment> treatments, Window window) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);

            PDFont helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // =========================
            // Patientendaten
            // =========================
            cs.beginText();
            cs.setFont(helvetica, 12);
            cs.newLineAtOffset(100, 725);
            cs.showText("Patient: " + patient.getFirstName() + " " + patient.getSurname());
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(100, 700);
            cs.showText("Raum: " + patient.getRoomNumber());
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(100, 675);
            cs.showText("Pflegegrad: " + patient.getCareLevel());
            cs.endText();

            cs.beginText();
            cs.newLineAtOffset(100, 650);
            cs.showText("Geburtsdatum: " + patient.getDateOfBirth());
            cs.endText();

            // Trennlinie
            cs.setStrokingColor(0, 0, 0);
            cs.moveTo(50, 550);
            cs.lineTo(550, 550);
            cs.stroke();

            // Tabellenkopf
            float startX = 100;
            float startY = 500;
            float rowHeight = 20;

            float col1X = startX;
            float col2X = startX + 150;
            float col3X = startX + 300;

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

            // Daten
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

            // Speichern
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("PDF speichern");
            fileChooser.setInitialFileName("patient_" + patient.getSurname() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Datei", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(window);

            if (file != null) {

                document.save(file);

                System.out.println("PDF gespeichert: " + file.getAbsolutePath());

                // JSON Export
                String jsonPath = file.getAbsolutePath().replace(".pdf", ".json");
                File jsonFile = new File(jsonPath);

                String json = buildPatientJson(patient, treatments);

                Files.writeString(jsonFile.toPath(), json);

                System.out.println("JSON gespeichert: " + jsonFile.getAbsolutePath());

            } else {
                System.out.println("Speichern abgebrochen");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Erstellt eine JSON-Repräsentation eines Patienten
     * inklusive seiner Behandlungen.
     *
     * @param patient    der Patient
     * @param treatments Liste der Behandlungen
     * @return JSON-String
     */
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