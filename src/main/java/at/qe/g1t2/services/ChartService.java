package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataRepository;


import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Scope("application")
public class ChartService {

    @Autowired
    SensorDataRepository sensorDataRepository;

    List<LineChartModel> lineChartModels;



    public LineChartModel getLineChartForAllTypes(SensorStation sensorStation){
        LineChartModel model = new LineChartModel();

        model.setData(createData(sensorStation));

        return model;
    }

    public ChartData createData(SensorStation sensorStation){
        ChartData data = new ChartData();
        List<String> labels = new ArrayList<>();
        Map<SensorDataType, List<Object>> dataSets = new HashMap<>();
        Set<LocalDateTime> allTimestamps = new TreeSet<>();

        for(SensorData sensorData: sensorDataRepository.findSensorDataBySensorStationOrderByCreateDate(sensorStation)){
            allTimestamps.add(sensorData.getCreateDate());
        }

        for(SensorDataType type: SensorDataType.values()){
            List<Object> values = new ArrayList<>();
            for(LocalDateTime timestamp: allTimestamps){
                SensorData sensorData = sensorDataRepository.findFirstBySensorStationAndTypeAndCreateDate(sensorStation, type, timestamp);
                if(sensorData != null){
                    values.add(sensorData.getMeasurement());
                } else {
                    values.add(null);
                }
            }
            dataSets.put(type, values);
        }

        for(Map.Entry<SensorDataType,List<Object>> dataSet: dataSets.entrySet()){
            LineChartDataSet chartSet = new LineChartDataSet();
            Random rand = new Random();
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            String randomBorderColor = String.format("rgb(%d,%d,%d)", r, g, b);
            chartSet.setData(dataSet.getValue());
            chartSet.setFill(true);
            chartSet.setLabel(dataSet.getKey().name() + " Unit:" + dataSet.getKey().getUnit());
            chartSet.setBorderColor(randomBorderColor);
            chartSet.setTension(0.1);
            data.addChartDataSet(chartSet);
        }

        for(LocalDateTime timestamp: allTimestamps){
            labels.add(timestamp.toString());
        }
        data.setLabels(labels);

        return data;
    }

    public LineChartModel createDataForType(SensorDataType type,SensorStation sensorStation){
        ChartData data = new ChartData();
        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        LineChartDataSet lineChartDataSet = new LineChartDataSet();

        for(SensorData sensorData: sensorDataRepository.findSensorDataBySensorStationAndTypeOrderByCreateDate(sensorStation,type)){
            System.out.println(sensorData.getMeasurement() + " " +sensorData.getCreateDate());
            values.add(sensorData.getMeasurement());
            labels.add(sensorData.getCreateDate().toString());

        }

        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        String randomBorderColor = String.format("rgb(%d,%d,%d)", r, g, b);
        lineChartDataSet.setData(values);
        lineChartDataSet.setFill(false);
        lineChartDataSet.setLabel(type.name() + " Unit:" + type.getUnit());
        lineChartDataSet.setBorderColor(randomBorderColor);
        lineChartDataSet.setTension(0.1);
        data.addChartDataSet(lineChartDataSet);
        data.setLabels(labels);
        LineChartModel lineChartModel = new LineChartModel();
        lineChartModel.setData(data);
        return lineChartModel;
    }
}

