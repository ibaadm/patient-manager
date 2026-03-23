<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <jsp:include page="/meta.jsp"/>
  <title>Patient Data App</title>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <h2>Charts</h2>

  <h3>Age Distribution</h3>
  <canvas id="ageChart" width="400" height="200"></canvas>

  <h3>Gender Breakdown</h3>
  <canvas id="genderChart" width="200" height="200"></canvas>

  <h3>Top 10 Cities</h3>
  <canvas id="cityChart" width="400" height="200"></canvas>
</div>
<jsp:include page="/footer.jsp"/>

<script>
<%
  Map<String, Integer> ageDist   = (Map<String, Integer>) request.getAttribute("ageDistribution");
  Map<String, Integer> genderMap = (Map<String, Integer>) request.getAttribute("genderCounts");
  Map<String, Integer> cityMap   = (Map<String, Integer>) request.getAttribute("cityCounts");

  StringBuilder ageLabels = new StringBuilder();
  StringBuilder ageValues = new StringBuilder();
  for (Map.Entry<String, Integer> e : ageDist.entrySet()) {
    if (ageLabels.length() > 0) { ageLabels.append(","); ageValues.append(","); }
    ageLabels.append("'").append(e.getKey()).append("'");
    ageValues.append(e.getValue());
  }

  StringBuilder genderLabels = new StringBuilder();
  StringBuilder genderValues = new StringBuilder();
  for (Map.Entry<String, Integer> e : genderMap.entrySet()) {
    if (genderLabels.length() > 0) { genderLabels.append(","); genderValues.append(","); }
    String label = e.getKey().isEmpty() ? "Unknown" : e.getKey();
    genderLabels.append("'").append(label).append("'");
    genderValues.append(e.getValue());
  }

  StringBuilder cityLabels = new StringBuilder();
  StringBuilder cityValues = new StringBuilder();
  for (Map.Entry<String, Integer> e : cityMap.entrySet()) {
    if (cityLabels.length() > 0) { cityLabels.append(","); cityValues.append(","); }
    String label = e.getKey().isEmpty() ? "Unknown" : e.getKey();
    cityLabels.append("'").append(label).append("'");
    cityValues.append(e.getValue());
  }
%>
  new Chart(document.getElementById('ageChart'), {
    type: 'bar',
    data: {
      labels: [<%= ageLabels %>],
      datasets: [{ label: 'Patients', data: [<%= ageValues %>], backgroundColor: 'steelblue' }]
    },
    options: { plugins: { legend: { display: false } } }
  });

  new Chart(document.getElementById('genderChart'), {
    type: 'pie',
    data: {
      labels: [<%= genderLabels %>],
      datasets: [{ data: [<%= genderValues %>], backgroundColor: ['#4e79a7','#f28e2b','#e15759'] }]
    }
  });

  new Chart(document.getElementById('cityChart'), {
    type: 'bar',
    data: {
      labels: [<%= cityLabels %>],
      datasets: [{ label: 'Patients', data: [<%= cityValues %>], backgroundColor: 'steelblue' }]
    },
    options: {
      indexAxis: 'y',
      plugins: { legend: { display: false } }
    }
  });
</script>
</body>
</html>
