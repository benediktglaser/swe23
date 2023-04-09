package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.model.SelectItem;
import jakarta.faces.model.SelectItemGroup;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Scope("view")
public class ChartsView {

    private List<SelectItem> typeInfos;
    private String selection;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;
    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    private Map<String,String> typeInfoMap = new HashMap<>();
    @PostConstruct
    public void init() {
        typeInfos = new ArrayList<>();
        List<SelectItemGroup> groups = new ArrayList<>();
        List<SensorDataType> typeList = Arrays.stream(SensorDataType.values()).collect(Collectors.toList());
        for (SensorDataType type:typeList) {
            groups.add(new SelectItemGroup(type.name()));
        }

        List<List<SelectItem>> selectItemList = new ArrayList<>();

        for(SensorDataType type:typeList){
            List<SensorDataTypeInfo> sensorDataTypeInfo = sensorDataTypeInfoService.getTypeInfoByStationAndType(sessionSensorStationBean.getSensorStation(),type);
            List<SelectItem> items = new ArrayList<>();
            if(!sensorDataTypeInfo.isEmpty()){
                sensorDataTypeInfo.forEach(x -> {
                    items.add(new SelectItem(x.getId(),x.toString()));
                });
            }
            else{
                items.add(new SelectItem(type.name(),"Show Chart"));
            }
            selectItemList.add(items);
            }
        int i = 0;
        for(SelectItemGroup group: groups){
            group.setSelectItems(selectItemList.get(i));
            i++;
            typeInfos.add(group);
        }
    }

    public List<SelectItem> getCategories() {
        return typeInfos;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public SensorDataTypeInfo convertSelection(){

        return sensorDataTypeInfoService.loadSensorDataTypeInfo(selection);
    }

   public void doUpdate(){
        List<String> types = Arrays.stream(SensorDataType.values()).map(x -> x.name()).collect(Collectors.toList());
        if(types.contains(selection)){
            PrimeFaces.current().executeScript("myF('" + sessionSensorStationBean.getSensorStation().getId() + "', '" + "Empty" + "', '" + SensorDataType.valueOf(selection) + "')");
            return;
        }
       PrimeFaces.current().executeScript("myF('" + sessionSensorStationBean.getSensorStation().getId() + "', '" + convertSelection().getId() + "', '" + convertSelection().getType().name() + "')");
    }

}