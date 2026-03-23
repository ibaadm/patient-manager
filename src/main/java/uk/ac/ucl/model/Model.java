package uk.ac.ucl.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Model
{
  private final String fileName;
  private final DataFrame dataFrame;

  public Model(String fileName)
  {
    this.fileName = fileName;
    dataFrame = new DataLoader().load(fileName);
  }

  public List<String> getColumnNames()
  {
    return dataFrame.getColumnNames();
  }

  public void addPatient(Map<String, String> fields)
  {
    fields.put("ID", UUID.randomUUID().toString());
    for (String column : dataFrame.getColumnNames())
    {
      dataFrame.addValue(column, fields.getOrDefault(column, ""));
    }
    saveToCSV();
  }

  public void updatePatient(String id, Map<String, String> fields)
  {
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      if (id.equals(dataFrame.getValue("ID", row)))
      {
        for (String column : dataFrame.getColumnNames())
        {
          dataFrame.putValue(column, row, fields.getOrDefault(column, ""));
        }
        break;
      }
    }
    saveToCSV();
  }

  public void deletePatient(String id)
  {
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      if (id.equals(dataFrame.getValue("ID", row)))
      {
        dataFrame.removeRow(row);
        break;
      }
    }
    saveToCSV();
  }

  public void saveToJSON(String outputFile)
  {
    new JSONWriter().write(dataFrame, outputFile);
  }

  private void saveToCSV()
  {
    try (Writer writer = new FileWriter(fileName);
         CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT))
    {
      List<String> columnNames = dataFrame.getColumnNames();
      printer.printRecord(columnNames);
      for (int row = 0; row < dataFrame.getRowCount(); row++)
      {
        List<String> rowData = new ArrayList<>();
        for (String column : columnNames)
        {
          rowData.add(dataFrame.getValue(column, row));
        }
        printer.printRecord(rowData);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public List<String[]> getPatientList()
  {
    List<String[]> patients = new ArrayList<>();
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      String id = dataFrame.getValue("ID", row);
      String name = dataFrame.getValue("FIRST", row) + " " + dataFrame.getValue("LAST", row);
      patients.add(new String[]{id, name});
    }
    return patients;
  }

  public Map<String, String> getPatientById(String id)
  {
    List<String> columnNames = dataFrame.getColumnNames();
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      if (id.equals(dataFrame.getValue("ID", row)))
      {
        Map<String, String> patient = new LinkedHashMap<>();
        for (String column : columnNames)
        {
          patient.put(column, dataFrame.getValue(column, row));
        }
        return patient;
      }
    }
    return null;
  }

  public int getPatientCount()
  {
    return dataFrame.getRowCount();
  }

  public String[] getOldestPatient()
  {
    String oldestId = null;
    String oldestName = null;
    String oldestBirthdate = null;

    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      String birthdate = dataFrame.getValue("BIRTHDATE", row);
      if (birthdate.isEmpty())
      {
        continue;
      }
      if (oldestBirthdate == null || birthdate.compareTo(oldestBirthdate) < 0)
      {
        oldestBirthdate = birthdate;
        oldestId = dataFrame.getValue("ID", row);
        oldestName = dataFrame.getValue("FIRST", row) + " " + dataFrame.getValue("LAST", row);
      }
    }
    return new String[]{oldestId, oldestName, oldestBirthdate};
  }

  public int getDeceasedCount()
  {
    int count = 0;
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      if (!dataFrame.getValue("DEATHDATE", row).isEmpty())
      {
        count++;
      }
    }
    return count;
  }

  public Map<String, Integer> getCountByColumn(String columnName)
  {
    Map<String, Integer> counts = new LinkedHashMap<>();
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      String value = dataFrame.getValue(columnName, row);
      counts.put(value, counts.getOrDefault(value, 0) + 1);
    }

    List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
    entries.sort((a, b) -> b.getValue() - a.getValue());

    Map<String, Integer> sorted = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> entry : entries)
    {
      sorted.put(entry.getKey(), entry.getValue());
    }
    return sorted;
  }

  public Map<String, Integer> getAgeDistribution()
  {
    Map<String, Integer> distribution = new LinkedHashMap<>();
    distribution.put("0-19", 0);
    distribution.put("20-39", 0);
    distribution.put("40-59", 0);
    distribution.put("60-79", 0);
    distribution.put("80+", 0);

    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      String birthdate = dataFrame.getValue("BIRTHDATE", row);
      if (birthdate.length() < 4)
      {
        continue;
      }
      try
      {
        int age = 2026 - Integer.parseInt(birthdate.substring(0, 4));
        String group;
        if      (age < 20) group = "0-19";
        else if (age < 40) group = "20-39";
        else if (age < 60) group = "40-59";
        else if (age < 80) group = "60-79";
        else               group = "80+";
        distribution.put(group, distribution.get(group) + 1);
      }
      catch (NumberFormatException ignored) {}
    }
    return distribution;
  }

  public List<String[]> searchFor(String keyword)
  {
    List<String[]> results = new ArrayList<>();
    String[] terms = keyword.trim().toLowerCase().split("\\s+");
    List<String> columnNames = dataFrame.getColumnNames();

    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      for (String term : terms)
      {
        boolean matched = false;
        for (String column : columnNames)
        {
          if (dataFrame.getValue(column, row).toLowerCase().contains(term))
          {
            matched = true;
            break;
          }
        }
        if (matched)
        {
          String id = dataFrame.getValue("ID", row);
          String name = dataFrame.getValue("FIRST", row) + " " + dataFrame.getValue("LAST", row);
          results.add(new String[]{id, name});
          break;
        }
      }
    }
    return results;
  }
}
