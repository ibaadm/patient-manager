package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;

public class Model
{
  private final DataFrame dataFrame;

  public Model(String fileName)
  {
    dataFrame = new DataLoader().load(fileName);
  }

  public DataFrame getDataFrame()
  {
    return dataFrame;
  }

  public List<String> getPatientNames()
  {
    List<String> names = new ArrayList<>();
    for (int row = 0; row < dataFrame.getRowCount(); row++)
    {
      String first = dataFrame.getValue("FIRST", row);
      String last = dataFrame.getValue("LAST", row);
      names.add(first + " " + last);
    }
    return names;
  }

  public List<String> searchFor(String keyword)
  {
    return new ArrayList<>();
  }
}
