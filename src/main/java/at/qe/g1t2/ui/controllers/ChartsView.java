package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.ChartService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Scope("view")
public class ChartsView {

    private LineChartModel lineModel;
    @Autowired
    ChartService chartService;
    private SensorStation sensorStation;

    private LocalDateTime start;
    private LocalDateTime end;



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


    private LineChartModel lineChartModel;

    public void createModel(SensorStation sensorStation){
        this.sensorStation = sensorStation;

        this.lineChartModel = chartService.getLineChartForAllTypes(sensorStation,start,end);
    }
    public LineChartModel getLineModelAllTypes(SensorStation sensorStation) {
        if(lineChartModel == null){
            createModel(sensorStation);
        }
        return this.lineChartModel;
    }

    private int sliderValue;

    public int getSliderValue() {
        return sliderValue;
    }

    public void setSliderValue(int sliderValue) {
        this.sliderValue = sliderValue;
    }

    public void updateModel(SensorStation sensorStation) {
        int days = sliderValue; // sliderValue ist ein Integer-Attribut im chartsView-Bean, das die aktuelle Position des Schiebereglers speichert.
        start = LocalDateTime.now();
        end = start.plusDays(days);
        createModel(sensorStation);
    }
    public LineChartModel getLineModelForGas(SensorStation sensorStation) {
        this.sensorStation = sensorStation;

        return chartService.createDataForType(SensorDataType.GAS,sensorStation);
    }
    public LineChartModel getLineModelForTemp(SensorStation sensorStation) {
        this.sensorStation = sensorStation;

        return chartService.createDataForType(SensorDataType.TEMPERATURE,sensorStation);
    }

    public LineChartModel getLineModelForLight(SensorStation sensorStation) {
        this.sensorStation = sensorStation;

        return chartService.createDataForType(SensorDataType.LIGHT,sensorStation);
    }

    public LineChartModel getLineModelForSoil(SensorStation sensorStation) {
        this.sensorStation = sensorStation;

        return chartService.createDataForType(SensorDataType.SOIL,sensorStation);
    }

    public LineChartModel getLineModelForHumidity(SensorStation sensorStation) {
        this.sensorStation = sensorStation;

        return chartService.createDataForType(SensorDataType.HUMIDITY,sensorStation);
    }



    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }


}