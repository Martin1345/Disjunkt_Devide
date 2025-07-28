Dieses Repository ist Bestandteil einer Bachelorarbeit an der Hochschule Landshut

Autor: Martin Schuldes

Titel der Arbeit: Der CVM-Algorithmus im Praxistest

Dieser Code ermöglicht es, die Anzahl disjunkter Werte je Spalte in einer Excel-Datei exakt zu ermitteln

Hierzu wird die Bibliothek apache.poi.ss.usermodel verwendet

Diese muss zuerst vom Hersteller heruntergeladen werden

Danach kann sie mittels Maven in jedes beliebige Projekt eingebunden werden

Zur Auffindung disjunkter Elemente wurde für jede Spalte eine eigene Arraylist erstellt. Die Abarbeitung dieser erfolgt hierbei parallel mittels Thread-Pools

Letztes Upload: 28.07.2025
