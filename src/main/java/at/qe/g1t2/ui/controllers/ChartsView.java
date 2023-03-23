package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;



import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class ChartsView {

    private LineChartModel lineModel;


    private SensorStation sensorStation;



    public void init() {
        createLineModel();
    }


    public void createLineModel() {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        sensorStation.getSensorData().forEach(x -> values.add(x.getMeasurement()));
        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("SensorData");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);

        List<String> labels = new ArrayList<>();
        sensorStation.getSensorData().forEach(x -> labels.add(x.getCreateDate().toLocalDate().toString()));
        data.setLabels(labels);

        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Line Chart");
        options.setTitle(title);

        lineModel.setOptions(options);
        lineModel.setData(data);


    }


    public LineChartModel getLineModel(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
        init();
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }


}

