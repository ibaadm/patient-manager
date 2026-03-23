Patient Data App - COMP0004 Coursework 2026

This is a Java web application for viewing and managing patient data from a CSV file.
It uses embedded Tomcat, Maven, and follows the MVC pattern with servlets as controllers
and JSPs as views.

Features implemented:

- Column and DataFrame classes form a general-purpose tabular data structure that holds
  all patient records in memory, with each column named after its CSV header.

- DataLoader reads a CSV file into a DataFrame, using the first row as column names.

- The patient list page shows all patients by name, each linking to a detail page that
  displays all 20 fields for that patient.

- Search works across every field of every patient. Multiple keywords are supported and
  a patient matches if any keyword appears in any of their fields.

- The statistics page shows total patients, deceased count, oldest patient, gender
  breakdown, and a ranked list of cities by patient count.

- Patients can be added, edited, and deleted through the web interface. All changes are
  written back to the CSV file immediately.

- A Save as JSON button on the patient list page exports the full dataset to
  data/patients.json using a JSONWriter class built on Jackson.

- The charts page displays three Chart.js visualisations: age distribution by band,
  gender breakdown as a pie chart, and the top 10 cities by patient count.

To run: mvn compile exec:java
The app runs on http://localhost:8080
