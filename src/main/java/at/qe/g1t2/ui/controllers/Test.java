package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import jakarta.annotation.PostConstruct;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;

@Controller
@Scope("view")
public class Test {

    @Autowired
    SensorDataService sensorDataService;
    private byte[] chartImage;


    public String init(SensorStation sensorStation) throws IOException {
        // Create the dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for(SensorDataType type: SensorDataType.values()){
            for(SensorData sensorData: sensorDataService.getAllSensorDataBySensorStationType(sensorStation,type)){
                Double value = sensorData.getMeasurement();
                LocalDate date = sensorData.getCreateDate().toLocalDate();
                dataset.addValue(value,type.name(),date);
            }
        }

        // Create the chart
        JFreeChart chart = ChartFactory.createLineChart("Sensor Data","Date","Measurement",dataset, PlotOrientation.VERTICAL,true,true,false);



        // Render the chart to an image
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(chart.createBufferedImage(600, 400), "png", out);
        byte[] bytes = out.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:image/png;base64," + base64;
    }

    public String getChartImage(SensorStation sensorStation) throws IOException {
        return init(sensorStation);
    }
}
