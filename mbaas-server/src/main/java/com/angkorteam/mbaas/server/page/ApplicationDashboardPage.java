//package com.angkorteam.mbaas.server.page;
//
//import com.angkorteam.mbaas.server.wicket.MasterPage;
//import com.googlecode.wickedcharts.highcharts.options.*;
//import com.googlecode.wickedcharts.highcharts.options.color.HexColor;
//import com.googlecode.wickedcharts.highcharts.options.series.Series;
//import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;
//import com.googlecode.wickedcharts.wicket7.highcharts.Chart;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.time.DateFormatUtils;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.joda.time.DateTime;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.util.*;
//
///**
// * Created by socheat on 3/1/16.
// */
//@AuthorizeInstantiation({"administrator", "backoffice"})
//public class ApplicationDashboardPage extends MasterPage {
//
//    @Override
//    public String getPageHeader() {
//        return "Dashboard";
//    }
//
//    @Override
//    public String getPageDescription() {
//        return "Summary Report";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//        networkChart();
//        cpuChart();
//    }
//
//    protected void cpuChart() {
//        ChartOptions chartOptions = new ChartOptions();
//        chartOptions.setType(SeriesType.LINE);
//        // Java code
//        Options options = new Options();
//        options.setTitle(new Title("CPU Report"));
//        Axis x = new Axis();
//        DateTime today = new DateTime();
//        DateTime lastMonday = today.minusMonths(1);
//        List<String> labels = new LinkedList<>();
//        List<String> param = new LinkedList<>();
//        while (true) {
//            lastMonday = lastMonday.plusDays(1);
//            labels.add(DateFormatUtils.format(lastMonday.toDate(), "yyyy-MM-dd"));
//            param.add("'" + DateFormatUtils.format(lastMonday.toDate(), "yyyy-MM-dd") + "'");
//            if (lastMonday.equals(today)) {
//                break;
//            }
//        }
//        x.setCategories(labels);
//        options.addxAxis(x);
//
//        PlotLine plotLines = new PlotLine();
//        plotLines.setValue(0f);
//        plotLines.setWidth(1);
//        plotLines.setColor(new HexColor("#999999"));
//        Axis y = new Axis();
//        y.setPlotLines(Collections.singletonList(plotLines));
//        options.setChartOptions(chartOptions);
//        options.addyAxis(y);
//
//        JdbcTemplate jdbcTemplate = getJdbcTemplate();
//
//        List<Map<String, Object>> items = jdbcTemplate.queryForList("select AVG (idle) as idle,AVG (user) as user, DATE_FORMAT(date_created,'%Y-%m-%d') as date_created from cpu WHERE DATE_FORMAT(date_created,'%Y-%m-%d') IN (" + StringUtils.join(param, ", ") + ") GROUP BY DATE_FORMAT(date_created,'%Y-%m-%d')");
//
//        Map<String, Number> idleData = new HashMap<>();
//        Map<String, Number> userData = new HashMap<>();
//        for (Map<String, Object> item : items) {
//            idleData.put((String) item.get("date_created"), (Number) item.get("idle"));
//            userData.put((String) item.get("date_created"), (Number) item.get("user"));
//        }
//
//        {
//            Series<Number> idleSeries = new SimpleSeries();
//            idleSeries.setName("Idle");
//            List<Number> datas = new LinkedList<>();
//            for (String label : labels) {
//                Number idle = idleData.get(label);
//                if (idle == null) {
//                    datas.add(0);
//                } else {
//                    datas.add(idle);
//                }
//            }
//            idleSeries.setData(datas);
//            options.addSeries(idleSeries);
//        }
//        {
//            Series<Number> userSeries = new SimpleSeries();
//            userSeries.setName("User");
//            List<Number> datas = new LinkedList<>();
//            for (String label : labels) {
//                Number user = userData.get(label);
//                if (user == null) {
//                    datas.add(0);
//                } else {
//                    datas.add(user);
//                }
//            }
//            userSeries.setData(datas);
//            options.addSeries(userSeries);
//        }
//
//        Chart chart = new Chart("cpu", options);
//        add(chart);
//    }
//
//    protected void networkChart() {
//        ChartOptions chartOptions = new ChartOptions();
//        chartOptions.setType(SeriesType.LINE);
//        // Java code
//        Options options = new Options();
//        options.setTitle(new Title("Network Report"));
//        Axis x = new Axis();
//        DateTime today = new DateTime();
//        DateTime lastMonday = today.minusMonths(1);
//        List<String> labels = new LinkedList<>();
//        List<String> param = new LinkedList<>();
//        while (true) {
//            lastMonday = lastMonday.plusDays(1);
//            labels.add(DateFormatUtils.format(lastMonday.toDate(), "yyyy-MM-dd"));
//            param.add("'" + DateFormatUtils.format(lastMonday.toDate(), "yyyy-MM-dd") + "'");
//            if (lastMonday.equals(today)) {
//                break;
//            }
//        }
//
//        x.setCategories(labels);
//        options.addxAxis(x);
//
//        PlotLine plotLines = new PlotLine();
//        plotLines.setValue(0f);
//        plotLines.setWidth(1);
//        plotLines.setColor(new HexColor("#999999"));
//        Axis y = new Axis();
//        y.setPlotLines(Collections.singletonList(plotLines));
//        options.setChartOptions(chartOptions);
//        options.addyAxis(y);
//
//        JdbcTemplate jdbcTemplate = getJdbcTemplate();
//
//        List<Map<String, Object>> items = jdbcTemplate.queryForList("select count(network_id) as quantity, DATE_FORMAT(date_created,'%Y-%m-%d') as date_created from network WHERE DATE_FORMAT(date_created,'%Y-%m-%d') IN (" + StringUtils.join(param, ", ") + ") GROUP BY DATE_FORMAT(date_created,'%Y-%m-%d')");
//        Map<String, Number> series = new HashMap<>();
//        for (Map<String, Object> item : items) {
//            series.put((String) item.get("date_created"), (Number) item.get("quantity"));
//        }
//
//        Series<Number> networkSeries = new SimpleSeries();
//        networkSeries.setName("Network");
//        List<Number> datas = new LinkedList<>();
//        for (String label : labels) {
//            Number quantity = series.get(label);
//            if (quantity == null) {
//                datas.add(0);
//            } else {
//                datas.add(quantity);
//            }
//        }
//        networkSeries.setData(datas);
//        options.addSeries(networkSeries);
//
//        Chart chart = new Chart("network", options);
//        add(chart);
//    }
//}
