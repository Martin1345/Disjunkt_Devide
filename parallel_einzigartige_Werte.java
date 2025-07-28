import java.io.FileInputStream;
import java.util.ArrayList;//Aufruf der Klasse zur Arbeit mit .xlsx-Dateien
import java.util.List;
//Aufruf der Klasse zum Einlesen von Dateien
import java.util.concurrent.Callable;
//Aufruf der Klasse zur Arbeit mit Listen und Sammlungen
import java.util.concurrent.ExecutorService;
//Aufruf der Klasse zur Arbeit mit paralleler Verarbeitung und Threads
import java.util.concurrent.Executors;
//Aufruf der Klasse zur Arbeit mit paralleler Verarbeitung und Threads
import java.util.concurrent.Future;
//Aufruf der Klasse zur Arbeit mit paralleler Verarbeitung und Threads
import org.apache.poi.ss.usermodel.Cell;
//Aufruf der Klasse zum Zugriff auf Excel-Dateien, Zeilen und Zellen
import org.apache.poi.ss.usermodel.Row;
//Aufruf der Klasse zum Zugriff auf Excel-Dateien, Zeilen und Zellen
import org.apache.poi.ss.usermodel.Sheet;
//Aufruf der Klasse zum Zugriff auf Excel-Dateien, Zeilen und Zellen
import org.apache.poi.ss.usermodel.Workbook;
//Aufruf der Klasse zum Zugriff auf Excel-Dateien, Zeilen und Zellen
import org.apache.poi.util.IOUtils;
//Aufruf der Klasse zum Einlesen von Dateien
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//Aufruf der Klasse zum Zugriff auf Excel-Dateien, Zeilen und Zellen

public class parallel_einzigartige_Werte {
    public static void main(String[] args) throws Exception {
        IOUtils.setByteArrayMaxOverride(200_000_000); 
        // Erhöhung des maximalen Puffers für Byte-Arrays, um große Excel-Dateien zu verarbeiten
        String Excel_Pfad = "C:\\Users\\marti\\Desktop\\Datensätze\\Hotelbuchungen.xlsx";
        // Pfad zur Excel-Datei, die verarbeitet werden soll


        int Durchlaeufe = 3;
        // Anzahl der Durchläufe, die durchgeführt werden sollen, um die Leistung zu messen

        long Gesamtzeit = 0;
        //  Speicherung der Gesamtzeit über alle Durchläufe
        long Gesammtspeicher = 0;
        //  Speicherung des Gesamtspeichers über alle Durchläufe

        for (int d = 1; d <= Durchlaeufe; d++) {
            System.gc();// Aufruf des Garbage Collectors, um den Speicher zu bereinigen und sicherzustellen, dass keine alten Objekte im Speicher verbleiben
            Thread.sleep(100);
            // Einfügung einer kurzen Pause, um dem Garbage Collector Zeit zu geben, den Speicher zu bereinigen
            long Speicher_vorher = getUsedMemory();
            // Ermitteln des aktuellen Speicherverbrauchs vor der Ausführung des Codes
            long startzeit = System.nanoTime();
            // Ermittlung der Zeit in Nanosekunden vor der Ausführung des Codes

            FileInputStream fis = new FileInputStream(Excel_Pfad);
            // Öffnung der Excel-Datei mit einem FileInputStream, um auf die Daten zuzugreifen
            Workbook wb = new XSSFWorkbook(fis);
            // Erstellung eines Workbook-Objekts, das die Excel-Datei repräsentiert
            Sheet Arbeitsblatt = wb.getSheetAt(0);
            // Erstellung eines Sheet-Objekts, das auf das erste Arbeitsblatt der Excel-Datei zugreift

            int Spalte = Arbeitsblatt.getRow(0).getLastCellNum();
            // Ermittlung der Anzahl der Spalten im Arbeitsblatt, indem die letzte Zelle der ersten Zeile abgefragt wird
            List<List<String>> Daten = new ArrayList<>();
            // Erstellung einer Liste von Listen, um die Daten jeder Spalte zu speichern

            for (int i = 0; i < Spalte; i++) Daten.add(new ArrayList<>());
            // Initialisierung einer eigenen Liste für jede Spalte, um die Werte zu speichern

            for (Row Zeile : Arbeitsblatt) {
                // Iterieren über jede Zeile im Arbeitsblatt
                for (int i = 0; i < Spalte; i++) {
                    // Iterieren über jede Spalte in der aktuellen Zeile
                    Cell Zelle = Zeile.getCell(i);
                    // Zugriff auf alle Zellen in der aktuellen Zeile und Spalte
                    Daten.get(i).add(Zelle != null ? Zelle.toString() : "");
                    // Prüfung ob die Zelle null ist, sonst wird der Wert der Zelle als String hinzugefügt, andernfalls wird ein leerer String hinzugefügt
                }
            }

            ExecutorService Pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            // Erstellung eines Thread-Pools mit der Anzahl von verfügbaren Kernen des Systems, um parallele Aufgaben auszuführen
            List<Future<String>> Ergebnisse = new ArrayList<>();
            // Erstellung einer ArrayList um die Ergebnisse der parallelen Ausführung zu speichern

            for (int sp = 0; sp < Daten.size(); sp++) {
                // Iterierung über alle Spalten in den Daten
                final int Spaltenindex = sp;
                // Speicherung des aktuellen Spaltenindex für den Zugriff in der parallelen Aufgabe
                List<String> Spaltenwerte = Daten.get(Spaltenindex);
                // Zugriff auf die Werte der aktuellen Spalte

                Callable<String> Aufgabe = () -> {
                    // Definition einer Callable-Aufgabe, die in einem separaten Thread ausgeführt wird
                    int einzigartig = 0;
                    // Erstellung eines Zählers für eindeutige Werte in der Spalte
                    for (int i = 0; i < Spaltenwerte.size(); i++) {
                        //Iterierung über alle Werte in der aktuellen Spalte
                        String Wert = Spaltenwerte.get(i);
                        // Abrufen des aktuellen Wertes in der Spalte
                        int Zähler = 0;
                        // Erstellung eines Zählers für die Häufigkeit des aktuellen Wertes
                        for (String s : Spaltenwerte) {
                            // Iterierung über alle Werte in der Spalte
                            if (s.equals(Wert)) Zähler++;
                            // Prüfung, ob der aktuelle Wert gleich dem zu zählenden Wert ist, und Erhöhung des Zählers
                        }
                        if (Zähler == 1) einzigartig++;
                        // Prüfung, ob der Zähler für den aktuellen Wert gleich 1 ist, was bedeutet, dass der Wert eindeutig ist
                    }
                    return "Spalte " + (Spaltenindex + 1) + ": " + einzigartig + " eindeutige Werte";
                    // Rückgabe der Anzahl der eindeutigen Werte in der aktuellen Spalte als String
                };

                Ergebnisse.add(Pool.submit(Aufgabe));
                // Weitergabe der Aufgabe an den Thread-Pool zur parallelen Ausführung und Speicherung des Future-Objekts in der Liste der Ergebnisse
            }

            Pool.shutdown();
            // Erst wenn alle Aufgaben abgeschlossen sind, wird der Thread-Pool heruntergefahren
            for (Future<String> Ergebnis : Ergebnisse) {
                // Iterierung über alle Future-Objekte in der Liste der Ergebnisse
                System.out.println("Durchlauf " + d + " – " + Ergebnis.get());
                // Ausgabe des Ergebnisses der parallelen Aufgabe, die die Anzahl der eindeutigen Werte in der Spalte angibt
            }

            wb.close();
            // Schließung des Workbook-Objekts, um die Excel-Datei freizugeben und Ressourcen zu sparen
            fis.close();
            // Schließung des FileInputStream, um die Datei freizugeben und Ressourcen zu sparen

            long Endzeit = System.nanoTime();
            // Erfassung der Endzeit in Nanosekunden nach der Ausführung des Codes
            long Laufzeit = (Endzeit - startzeit) / 1_000_000;
            // Berechnung der Laufzeit in Millisekunden, indem die Differenz zwischen Endzeit und Startzeit genommen und durch 1.000.000 geteilt wird
            long Speicher_nachher = getUsedMemory();
            // Ermittlung des aktuellen Speicherverbrauchs nach der Ausführung des Codes
            long Speicher_Delta = (Speicher_nachher - Speicher_vorher) / 1024;
            // Berechnung des Speicherverbrauchs in Kilobyte, indem die Differenz zwischen dem aktuellen und dem vorherigen Speicherverbrauch genommen und durch 1024 geteilt wird

            Gesamtzeit += Laufzeit;
            // Hinzufügen der Laufzeit des aktuellen Durchlaufs zur Gesamtlaufzeit
            Gesammtspeicher += Speicher_Delta;
            // Hinzufügen des Speicherverbrauchs des aktuellen Durchlaufs zum Gesamtspeicher

            System.out.println(" Laufzeit Durchlauf " + d + ": " + Laufzeit + " ms");
            // Ausgabe der gemessenen Laufzeit des aktuellen Durchlaufs in Millisekunden
            System.out.println(" Speicherverbrauch Durchlauf " + d + ": " + Speicher_Delta + " KB");
            // Ausgabe des gemessenen Speicherverbrauchs des aktuellen Durchlaufs in Kilobyte
            System.out.println("------------------------------------");
            // Einfügen einer Trennlinie zur besseren Lesbarkeit der Ausgabe
        }

        System.out.println(" Durchschnittliche Laufzeit: " + (Gesamtzeit / Durchlaeufe) + " ms");
        // Errechnung und Ausgabe der durchschnittlichen Laufzeit über alle Durchläufe in Millisekunden
        System.out.println(" Durchschnittlicher Speicherverbrauch: " + (Gesamtspeicher / Durchlaeufe) + " KB");
        // Errechnung und Ausgabe des durchschnittlichen Speicherverbrauchs über alle Durchläufe in Kilobyte
    }

    public static long getUsedMemory() {// Erstellung einer Methode, um den aktuell verwendeten Speicher zu ermitteln
        Runtime rt = Runtime.getRuntime();// Zugriff auf die Runtime-Instanz, um Informationen über die Java-Anwendung zu erhalten
        return rt.totalMemory() - rt.freeMemory();// Berechnung des aktuell verwendeten Speichers durch Subtraktion des freien Speichers vom gesamten Speicher
    }
}
