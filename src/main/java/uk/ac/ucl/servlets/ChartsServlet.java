package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/charts")
public class ChartsServlet extends HttpServlet
{
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    try
    {
      Model model = ModelFactory.getModel();

      request.setAttribute("ageDistribution", model.getAgeDistribution());
      request.setAttribute("genderCounts", model.getCountByColumn("GENDER"));
      request.setAttribute("cityCounts", topN(model.getCountByColumn("CITY"), 10));

      ServletContext context = getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/charts.jsp");
      dispatch.forward(request, response);
    }
    catch (IOException e)
    {
      request.setAttribute("errorMessage", "Error loading data: " + e.getMessage());
      ServletContext context = getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
      dispatch.forward(request, response);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doGet(request, response);
  }

  private Map<String, Integer> topN(Map<String, Integer> map, int n)
  {
    Map<String, Integer> result = new LinkedHashMap<>();
    int count = 0;
    for (Map.Entry<String, Integer> entry : map.entrySet())
    {
      if (count++ >= n) break;
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }
}
