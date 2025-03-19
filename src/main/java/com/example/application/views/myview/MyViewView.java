package com.example.application.views.myview;

import com.example.application.data.*;
import com.example.application.polyflow.cgraph.ConsistencyGraphImpl;
import com.example.application.polyflow.cgraph.ConsistencyNode;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.GridOutputWindowed;
import com.example.application.services.PolyflowService;
import com.example.application.services.SampleGridService;
import com.example.application.services.SampleStockService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.vaadin.addons.visjs.network.main.Edge;
import org.vaadin.addons.visjs.network.main.NetworkDiagram;
import org.vaadin.addons.visjs.network.main.Node;
import org.vaadin.addons.visjs.network.options.Interaction;
import org.vaadin.addons.visjs.network.options.Options;
import org.vaadin.addons.visjs.network.options.edges.ArrowHead;
import org.vaadin.addons.visjs.network.options.edges.Arrows;
import org.vaadin.addons.visjs.network.options.edges.Edges;
import org.vaadin.addons.visjs.network.util.Shape;
//import org.vaadin.addons.visjs.network.main.Edge;
//import org.vaadin.addons.visjs.network.main.NetworkDiagram;
//import org.vaadin.addons.visjs.network.main.Node;
//import org.vaadin.addons.visjs.network.options.Interaction;
//import org.vaadin.addons.visjs.network.options.Options;
//import org.vaadin.addons.visjs.network.options.edges.ArrowHead;
//import org.vaadin.addons.visjs.network.options.edges.Arrows;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@PageTitle("My View")
@Route(value = "my-view", layout = MainLayout.class)
@Uses(Icon.class)
@CssImport(
        themeFor = "vaadin-grid",
        value = "./recipe/dynamicgridrowbackgroundcolor/dynamic-grid-row-background-color.css"
)
public class MyViewView extends Composite<VerticalLayout> {

    private HorizontalLayout mainRow;
//    private String query = "SELECT percent(consA,consB),percent(consB,consA),ts\n" +
//            "FROM Consumption [RANGE 5 minutes SLIDE 2 minutes]\n" +
//            "WHERE consA >= 0 AND consB >= 0";
    private HorizontalLayout bottomRow;
    private HorizontalLayout upperCentralRow;
    private Class<?> sampleOutputClass;
    private Class<?> sampleInputClass;
    private List<GridInputWindowed> inputGridList;
    private List<GridInputWindowed> inputGridListActual;
    private List<GridInputWindowed> outputGridList;
    private List<GridInputWindowed> inputAnnotatedGridList;
    private List<GridOutputAnnotated> outputAnnotatedGridList;
    private List<GridOutputQuantified> outputQuantifiedGridList;
    private List<StockInput> stockInputArrayList;
    private boolean setLocal = false;
    private int counterInput = 0;


    public MyViewView() {

        mainRow = new HorizontalLayout();
        upperCentralRow = new HorizontalLayout();
        bottomRow = new HorizontalLayout();
        ComboBox<String> selectScenarios = new ComboBox<>();
        selectScenarios.setLabel("Scenario");
        selectScenarios.setItems("Electric Grid", "Stock", "GPS", "Movie Reviews");
        selectScenarios.setValue("Electric Grid");
        sampleOutputClass = GridOutput.class;
        sampleInputClass = GridInputWindowed.class;




        selectScenarios.addValueChangeListener(event -> {
            mainRow.removeAll();
            bottomRow.removeAll();
            upperCentralRow.removeAll();
            switch (event.getValue()) {
                case "Electric Grid":

//                    this.query = "SELECT percent(consA,consB),percent(consB,consA), max(ts)\n" +
//                            "FROM Consumption [RANGE 5 minutes SLIDE 2 minutes]\n" +
//                            "WHERE consA >= 0 AND consB >= 0";
                    sampleOutputClass = GridOutput.class;
                    sampleInputClass = GridInputWindowed.class;
                    loadPage(selectScenarios, event.getValue());


                    break;
                case "Stock":
                    sampleOutputClass = StockOutput.class;
                    sampleInputClass = StockInput.class;

//                    this.query = "SELECT name, avg(value), max(ts)\n" +
//                            "FROM Stock [RANGE 5 minutes SLIDE 2 minutes]\n" +
//                            "WHERE name = Apple";
                    loadPage(selectScenarios, event.getValue());
                    break;
                default: loadPage(selectScenarios, "Electric Grid");
            }
        });


        loadPage(selectScenarios, "Electric Grid");
    }

    private String getWindowAbbrev(String windowDescr){
        if (windowDescr.contains("Time")){
            return "TW";
        } else if (windowDescr.contains("Session")){
            return "SW";
        } else if (windowDescr.contains("Frames")){
           return "F" + windowDescr.split(":")[1].substring(0, 3);
        } return   null;
    }

    private void loadPage(ComboBox<String> selectScenarios, String scenario) {


        VerticalLayout leftColumn = new VerticalLayout();




        leftColumn.add(selectScenarios);



        Grid basicGrid = new Grid<>(sampleInputClass);




        VerticalLayout centralColumn = new VerticalLayout();
        TabSheet upperCentralTabSheet = new TabSheet();
        VerticalLayout bottomCentralColumn = new VerticalLayout();
//        TextArea queryEditor = new TextArea();
//        queryEditor.setValue(query);
        VerticalLayout windowEditor = new VerticalLayout();
        windowEditor.setHeightFull();
        windowEditor.setWidthFull();


        ComboBox<String> selectWindowType = new ComboBox<>();
        selectWindowType.setLabel("Window Type");
        selectWindowType.setItems("Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate");
        selectWindowType.setValue("Time-based");
        HorizontalLayout windowSelectorLayout = new HorizontalLayout();

        TextField threshold = new TextField();
        threshold.setLabel("Threshold");

        ComboBox<String> selectAggregate = new ComboBox<>();
        selectAggregate.setLabel("Aggregate");

        selectAggregate.setItems("count", "sum", "avg", "max", "min");
        selectAggregate.setValue("count");
        selectAggregate.addValueChangeListener(event -> {
            Notification.show(event.getValue());
        });

        ComboBox<String> selectOp = new ComboBox<>();
        selectOp.setLabel("Comparator");

        selectOp.setItems("<", ">", "=", ">=", "<=");
        selectOp.setValue("<");
        selectOp.addValueChangeListener(event -> {
            Notification.show(event.getValue());
        });

        ComboBox<String> selectAttribute = new ComboBox<>();
        selectAttribute.setLabel("On Attribute");

        Field[] fields = sampleInputClass.getDeclaredFields();

        List<String> collect = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
        selectAttribute.setItems(collect);
        selectAttribute.setValue(collect.get(0));
        selectAttribute.addValueChangeListener(event -> {
            Notification.show(event.getValue());
        });

        TextField size = new TextField();
        size.setLabel("Size");

        TextField slide = new TextField();
        slide.setLabel("Slide");

        TextField timeout = new TextField();
        timeout.setLabel("Timeout");

        selectWindowType.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<String>, String>>) event -> {
            int index = 1;

            if (!(event.getOldValue().equals("Frames:Aggregate")) && (event.getValue().startsWith("Frames:Aggregate"))) {
                windowSelectorLayout.addComponentAtIndex(index++, selectAggregate);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, selectAggregate);
            } else if (!(event.getValue().equals("Frames:Aggregate")) && (event.getOldValue().equals("Frames:Aggregate"))) {
                windowSelectorLayout.remove(selectAggregate);
            }
            if (!(event.getOldValue().startsWith("Frames")) && (event.getValue().startsWith("Frames"))) {
                windowSelectorLayout.addComponentAtIndex(index++, selectAttribute);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, selectAttribute);
                windowSelectorLayout.addComponentAtIndex(index++, selectOp);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, selectOp);
                windowSelectorLayout.addComponentAtIndex(index, threshold);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, threshold);
            } else if (!(event.getValue().startsWith("Frames")) && (event.getOldValue().startsWith("Frames"))) {
                windowSelectorLayout.remove(threshold);
                windowSelectorLayout.remove(selectAttribute);
                windowSelectorLayout.remove(selectOp);
            }

            if (!(event.getOldValue().equals("Time-based")) && (event.getValue().equals("Time-based"))) {
                windowSelectorLayout.addComponentAtIndex(index++, size);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, size);
                windowSelectorLayout.addComponentAtIndex(index, slide);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, slide);
            } else if (!(event.getValue().equals("Time-based")) && (event.getOldValue().equals("Time-based"))) {
                windowSelectorLayout.remove(size);
                windowSelectorLayout.remove(slide);
            }

            if (!(event.getOldValue().equals("Session")) && (event.getValue().equals("Session"))) {
                windowSelectorLayout.addComponentAtIndex(index++, timeout);
                windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, timeout);
            } else if (!(event.getValue().equals("Session")) && (event.getOldValue().equals("Session"))) {
                windowSelectorLayout.remove(timeout);
            }

            Notification.show(event.getValue());
        });



        Button windowCreatorButton = new Button();


        List<WindowRowSummary> windowRowSummaries = new ArrayList<>();

        windowCreatorButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                HorizontalLayout horizontalLayout = new HorizontalLayout();
                Button removeButton = new Button();
                WindowRowSummary windowRowSummary = new WindowRowSummary();
                removeButton.setText("X");
                removeButton.addSingleClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                    @Override
                    public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                        windowEditor.remove(horizontalLayout);
                        windowRowSummaries.remove(windowRowSummary);
                    }
                });

                String windowTypeValue = selectWindowType.getValue();
                windowRowSummary.setName(getWindowAbbrev(windowTypeValue));

                //String Building Based on the window
                StringBuilder text = new StringBuilder();
                text.append(windowTypeValue);


                try {
                    if (windowTypeValue.equals("Frames:Aggregate")) {
                        text.append(" on aggregate ").append(selectAggregate.getValue());
                        windowRowSummary.setAttribute(selectAggregate.getValue()+"("+selectAttribute.getValue()+")");
                    } else if (windowTypeValue.startsWith("Frames")){
                        text.append(" over Attribute ").append(selectAttribute.getValue()).append(" ").append(selectOp.getValue()).append(" ").append(threshold.getValue());
                        windowRowSummary.setAttribute(selectAttribute.getValue());
                        windowRowSummary.setOperator(selectOp.getValue());
                        windowRowSummary.setRange(Long.parseLong(threshold.getValue()));
                    } else if (windowTypeValue.equals("Time-based")){
                        text.append(" with Size ").append(size.getValue()).append(" and Slide ").append(slide.getValue());
                        windowRowSummary.setSize(Long.parseLong(size.getValue()));
                        windowRowSummary.setSlide(Long.parseLong(slide.getValue()));
                    } else if (windowTypeValue.equals("Session")){
                        text.append(" with Timeout ").append(timeout.getValue());
                        windowRowSummary.setTimeout(Long.parseLong(timeout.getValue()));
                    }
                    horizontalLayout.add(removeButton);
                    horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
                    String text1 = text.toString();
                    horizontalLayout.add(text1);
                    horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
                    windowEditor.add(horizontalLayout);
                    windowRowSummaries.add(windowRowSummary);
                } catch (NumberFormatException e) {
                    Notification.show("No Window Created, Range invalid.");
                }
            }
        });


        TabSheet tabSheet3 = new TabSheet();
        tabSheet3.add("Windows", windowEditor);


//        HashMap<String, Integer> windowCounter = new HashMap<>();
//
//        for (WindowRowSummary windowRowSummary : windowRowSummaries) {
//            String windowType = windowRowSummary.getName().replaceAll("\\d", "");
//
//            windowCounter.putIfAbsent(windowType, 1);
//            windowRowSummary.setName(windowType +"_"+windowCounter.get(windowRowSummary.getName()));
//            windowCounter.put(windowType, windowCounter.get(windowType)+1);
//        }

        //Summary Tab
        HorizontalLayout summaryLayout = new HorizontalLayout();
        VerticalLayout summaryWindows = new VerticalLayout();

        TextArea queryPerspective = new TextArea();
        tabSheet3.add("Summary", summaryLayout);

        Grid<WindowRowSummary> summaryWindowGrid = new Grid<>(WindowRowSummary.class);


        

        summaryWindowGrid.setItems(windowRowSummaries);
        summaryWindowGrid.setHeightFull();
        summaryWindowGrid.setWidthFull();
        summaryWindows.add(summaryWindowGrid, queryPerspective);
        
        summaryWindowGrid.addItemClickListener(event -> {
            WindowRowSummary item = event.getItem();
            queryPerspective.setValue(item.getQuery());
        });
        summaryLayout.add(summaryWindows);//, summaryQuery);

        summaryLayout.setHeightFull();
        summaryLayout.setWidthFull();
        summaryWindows.setWidth("100%");
        summaryWindows.setHeightFull();

        queryPerspective.setLabel("Query Perspective");
        queryPerspective.setHeight("100%");
        queryPerspective.setWidth("100%");



        tabSheet3.addSelectedChangeListener(new ComponentEventListener<TabSheet.SelectedChangeEvent>() {
            @Override
            public void onComponentEvent(TabSheet.SelectedChangeEvent selectedChangeEvent) {
                if (selectedChangeEvent.getSelectedTab().getLabel().equals("Summary")){
                    HashMap<String, Integer> windowCounter = new HashMap<>();

                    for (WindowRowSummary windowRowSummary : windowRowSummaries) {
                        String windowType = windowRowSummary.getName().replaceAll("\\d", "");

                        windowCounter.putIfAbsent(windowType, 1);
                        windowRowSummary.setName(windowType+windowCounter.get(windowType));
                        windowCounter.put(windowType, windowCounter.get(windowType)+1);
                    }

                    summaryWindowGrid.setItems(windowRowSummaries);
                    summaryWindowGrid.getDataProvider().refreshAll();
                }

            }
        });


        windowCreatorButton.setText("Add");
        windowSelectorLayout.add(selectWindowType, size, slide);
        windowSelectorLayout.add(windowCreatorButton);

        windowSelectorLayout.setAlignItems(Alignment.START);

        windowEditor.add(windowSelectorLayout);
        windowEditor.setAlignItems(Alignment.START);
        windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, windowCreatorButton);

//        setWindowScenario(scenario, windowEditor);

        Button windowButton = new Button();

        VerticalLayout rightColumn = new VerticalLayout();


        TabSheet tabSheetUpperRight = new TabSheet();
        TabSheet tabSheetBottomRight = new TabSheet();



        Map<String,Grid> resultGrids = new HashMap<>();
        Grid<GridOutputWindowed> inputAnnotatedGrid = getGridOutputWindowedGrid();

        tabSheetBottomRight.add("Results", inputAnnotatedGrid);



        Map<String,NetworkDiagram> graphs = new HashMap<>();

        NetworkDiagram snapshotGraphSolo =
                new NetworkDiagram(Options.builder().withWidth("100%").withHeight("100%")
                        .withInteraction(Interaction.builder().withMultiselect(true).build()).build());
        tabSheetUpperRight.add("Window State", snapshotGraphSolo);


        windowButton.setText("Windows");
        bottomRow.setAlignSelf(Alignment.CENTER, windowButton);
        windowButton.setWidth("min-content");
        windowButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        queryButton.setText("Query");
        windowButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                try {
                    Notification.show("Registered Windows.");
                    basicGrid.setItems(inputGridListActual);
                    polyflowService.register(scenario);

                    if (tabSheetUpperRight.getTabAt(0).getLabel().equals("Window State")){
                        tabSheetUpperRight.remove(0);
                    }

                    if (tabSheetBottomRight.getTabAt(0).getLabel().equals("Results")){
                        tabSheetBottomRight.remove(0);
                    }

                    for (NetworkDiagram diagram: graphs.values()){
                        tabSheetUpperRight.remove(diagram.getParent().get());
                    }

                    for (Grid d: resultGrids.values()){
                        tabSheetBottomRight.remove(d.getParent().get());
                    }

                    graphs.clear();
                    resultGrids.clear();

                    for (WindowRowSummary windowRowSummary : windowRowSummaries) {

                        Tab tabRes = new Tab(windowRowSummary.name+" Results");
                        Grid<GridOutputWindowed> resultGrid = getGridOutputWindowedGrid();
                        tabSheetBottomRight.add(tabRes, resultGrid);
                        resultGrids.put(windowRowSummary.name, resultGrid);

                        Tab tab = new Tab(windowRowSummary.name+" State");
                        NetworkDiagram snapshotGraphSoloLocal =
                                new NetworkDiagram(Options.builder().withWidth("100%").withHeight("100%")
                                        .withInteraction(Interaction.builder().withMultiselect(true).build()).build());
                        tabSheetUpperRight.add(tab, snapshotGraphSoloLocal);
                        graphs.put(windowRowSummary.name, snapshotGraphSoloLocal);
                    }

                } catch (ConfigurationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });





        Button buttonNext = new Button();






        buttonNext.addClickListener(buttonClickEvent -> {

            if (polyflowService.isRegistered()) {
                Notification.show("Inserting Next Event.");

                if (counterInput!=0){
                    GridInputWindowed prevGridInput = inputGridListActual.get(counterInput-1);
                    prevGridInput.setCursor("");
                }

                GridInputWindowed gridInput = inputGridList.get(counterInput++);
                gridInput.setCursor(">");
                inputGridListActual.add(gridInput);
                basicGrid.getDataProvider().refreshAll();
                polyflowService.nextEvent(gridInput);



                //ConsistencyGraph currentGraph = polyflowService.getCurrentGraph();

                //updateSnapshotGraphFromContent(snapshotGraphSolo, nodes, edges, (ConsistencyGraphImpl) currentGraph);

                List<GridInputWindowed> nextOutput = polyflowService.getNextOutput();
                List<GridOutputWindowed> actualOutput =  nextOutput.stream().map(el->{
                    GridOutputWindowed g = new GridOutputWindowed();
                    g.setIntervalId(el.getIntervalId());
                    g.setConsA(el.getConsA());
                    g.setConsB(el.getConsB());
                    g.setOperatorId(el.getOperatorId());
                    g.setTimestamp(el.getTimestamp());
                    g.setRecordId(el.getRecordId());
                    return g;
                }).collect(Collectors.toList());

//                inputAnnotatedGrid.setItems(actualOutput);

                for (String windowName : resultGrids.keySet()) {
                    Grid<GridOutputWindowed> resultGrid = resultGrids.get(windowName);

                    List<Grid.Column<GridOutputWindowed>> sgab = Arrays.asList(
                            resultGrid.getColumnByKey("recordId"),
                            resultGrid.getColumnByKey("operatorId"),
                            resultGrid.getColumnByKey("intervalId"));

                    resultGrid.getColumnByKey("recordId").setWidth("50px");
                    resultGrid.setColumnOrder(sgab);

                    //ERRORE TAB NON E PARENT DI GRID
                    resultGrid.setItems(actualOutput.stream().filter(el -> el.getOperatorId()
                            .equals((tabSheetBottomRight.getTab(resultGrid)).getLabel().split(" ")[0])).collect(Collectors.toList()));

                    resultGrid.getDataProvider().refreshAll();
                }

                for (String windowName : graphs.keySet()) {
                    NetworkDiagram diagram = graphs.get(windowName);

                    updateWindowState(diagram, actualOutput.stream().filter(el -> el.getOperatorId()
                            .equals((tabSheetUpperRight.getTab(diagram)).getLabel().split(" ")[0])).collect(Collectors.toList()));
                }


            } else {
                Notification.show("A query must be submitted first.");
            }
        });

        Button buttonPrimary2 = new Button();
        Button buttonPrimary3 = new Button();

//        Button queryButton = new Button();
        Button buttonPrimary6 = new Button();
        Button buttonPrimary7 = new Button();
        H6 h6 = new H6();
        getContent().addClassName(Padding.XSMALL);
        getContent().setWidthFull();
        getContent().setHeight("90%");
        getContent().getStyle().set("flex-grow", "1");
        mainRow.setWidthFull();
        getContent().setFlexGrow(1.0, mainRow);
        mainRow.addClassName(Gap.MEDIUM);
//        layoutRow.setWidth("100%");
        mainRow.setHeightFull();
        leftColumn.setHeightFull();
        mainRow.setFlexGrow(1.0, leftColumn);
        leftColumn.addClassName(Gap.XSMALL);
        leftColumn.addClassName(Padding.XSMALL);
        leftColumn.setWidth("20%");
//        leftColumn.setMinWidth("250px");
        leftColumn.setHeight("80%");
        leftColumn.setJustifyContentMode(JustifyContentMode.START);
        leftColumn.setAlignItems(Alignment.START);
        leftColumn.setAlignSelf(Alignment.CENTER, basicGrid);
        basicGrid.setWidth("97%");
        basicGrid.setHeight("100%");
        basicGrid.getStyle().set("flex-grow", "0");
        setGridSampleSimpleData(basicGrid, scenario);

        basicGrid.setClassNameGenerator(monthlyExpense -> {
            if (monthlyExpense.toString().contains("/"))
                return "warn";
            else if (monthlyExpense.toString().contains(">"))
                return "warn1";
            else return null;
        });
        centralColumn.setHeightFull();
        mainRow.setFlexGrow(1.0, centralColumn);
        centralColumn.setPadding(false);
        centralColumn.setWidth("30%");
        centralColumn.setHeight("80%");
        upperCentralRow.setWidthFull();
        centralColumn.setFlexGrow(1.0, upperCentralRow);
        upperCentralRow.addClassName(Gap.SMALL);
        upperCentralRow.setWidth("100%");
        upperCentralRow.setHeight("70%");
        upperCentralRow.setAlignItems(Alignment.START);
        upperCentralRow.setJustifyContentMode(JustifyContentMode.CENTER);
        upperCentralTabSheet.setWidth("100%");
        upperCentralTabSheet.setHeight("100%");
        upperCentralTabSheet.getStyle().set("flex-grow", "0");




//        upperCentralTabSheet.add("Graph", snapshotGraphSolo);
//        upperCentralTabSheet.add("Polynomials", inputAnnotatedGrid);


        bottomCentralColumn.setWidthFull();
        centralColumn.setFlexGrow(1.0, bottomCentralColumn);
        bottomCentralColumn.setPadding(false);
        bottomCentralColumn.setWidth("100%");
        bottomCentralColumn.setHeight("50%");
        bottomCentralColumn.setJustifyContentMode(JustifyContentMode.END);
        bottomCentralColumn.setAlignItems(Alignment.CENTER);
//        bottomCentralColumn.setAlignSelf(Alignment.CENTER, queryEditor);
//        queryEditor.setWidth("100%");
//        queryEditor.setHeight("100%");
        rightColumn.setHeightFull();
        mainRow.setFlexGrow(1.0, rightColumn);
        rightColumn.addClassName(Padding.XSMALL);
        rightColumn.setWidth("30%");
//        rightColumn.setMinWidth("80%");
        rightColumn.setHeight("100%");
        tabSheetBottomRight.setWidth("95%");
        tabSheetBottomRight.setHeight("100%");
        tabSheetUpperRight.setWidth("100%");
        tabSheetUpperRight.setHeight("100%");
        tabSheet3.setWidth("100%");
        tabSheet3.setHeight("100%");
//        setTabSheetSampleData(tabSheetUpperRight);



        mainRow.setWidthFull();
        mainRow.setHeightFull();

        bottomRow.setWidthFull();
        getContent().setFlexGrow(1.0, bottomRow);
        bottomRow.addClassName(Gap.MEDIUM);
//        bottomRow.addClassName(Padding.XSMALL);
        bottomRow.setWidth("100%");
        bottomRow.getStyle().set("flex-grow", "1");
        buttonNext.setText("Next");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonNext);
        buttonNext.setWidth("min-content");
        buttonNext.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("Break Point");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary2);
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary3.setText("Play");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary3);
        buttonPrimary3.setWidth("min-content");
        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

//        bottomRow.setAlignSelf(Alignment.CENTER, queryButton);
//        queryButton.setWidth("min-content");
//        queryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary6.setText("LLM");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary6);
        buttonPrimary6.setWidth("min-content");
        buttonPrimary6.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary7.setText("Reset");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary7);
        buttonPrimary7.setWidth("min-content");
        buttonPrimary7.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        h6.setText("Alessandro Ferri, Mauro Fama, Samuele Langhi, Riccardo Tommasini, Angela Bonifati");
        bottomRow.setAlignSelf(Alignment.CENTER, h6);
        h6.setWidth("max-content");
        getContent().add(mainRow);
        mainRow.add(leftColumn);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setHeightFull();
        horizontalLayout.add(basicGrid);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("3%");
        verticalLayout.setPadding(false);
        verticalLayout.setHeightFull();
        StreamResource streamResource = new StreamResource("time_dir.png",
                () -> getClass().getResourceAsStream("/time_dir.png"));
        Image cdcd = new Image(streamResource, "cdcd");
        cdcd.setHeightFull();
        cdcd.setWidthFull();
        verticalLayout.add(new Text("time"));
        verticalLayout.add(cdcd);
//        verticalLayout.add(new Icon(VaadinIcon.BOLT));
        horizontalLayout.add(verticalLayout);
        leftColumn.add(horizontalLayout);




        mainRow.add(centralColumn);
        centralColumn.add(upperCentralRow);
//        upperCentralRow.add(upperCentralTabSheet);
        centralColumn.add(tabSheet3);
        upperCentralRow.add(tabSheet3);
        mainRow.add(rightColumn);


        HorizontalLayout upperRightHorizontalLayout = new HorizontalLayout();
        HorizontalLayout bottomRightHorizontalLayout = new HorizontalLayout();
//        upperRightHorizontalLayout.setHeightFull();
//        upperRightHorizontalLayout.setWidthFull();
        upperRightHorizontalLayout.add(tabSheetUpperRight);
        bottomRightHorizontalLayout.add(tabSheetBottomRight);



        StreamResource streamResource1 = new StreamResource("time_dir.png",
                () -> getClass().getResourceAsStream("/time_dir.png"));
        Image cdcd1 = new Image(streamResource1, "cdcd1");
        VerticalLayout verticalUpperRightLayout = new VerticalLayout();
        VerticalLayout verticalBottomRightLayout = new VerticalLayout();

        verticalBottomRightLayout.setWidth("10%");
        verticalBottomRightLayout.setHeightFull();

//        verticalUpperRightLayout.setWidth("10%");
//        verticalUpperRightLayout.setHeightFull();

        verticalBottomRightLayout.add(new Text("time"));
        verticalBottomRightLayout.add(cdcd1);
        cdcd1.setWidthFull();
        cdcd1.setHeightFull();

//        upperRightHorizontalLayout.add(verticalUpperRightLayout);
        rightColumn.add(upperRightHorizontalLayout);
        upperRightHorizontalLayout.setWidthFull();
        upperRightHorizontalLayout.setHeight("50%");

        bottomRightHorizontalLayout.add(verticalBottomRightLayout);
        rightColumn.add(bottomRightHorizontalLayout);
        bottomRightHorizontalLayout.setWidthFull();
        bottomRightHorizontalLayout.setHeight("50%");


        rightColumn.setWidth("25%");
        rightColumn.setHeight("80%");
        getContent().add(bottomRow);
        bottomRow.add(buttonNext);
//        bottomRow.add(buttonPrimary2);
        bottomRow.add(buttonPrimary3);
        bottomRow.add(windowButton);
//        bottomRow.add(queryButton);
        bottomRow.add(buttonPrimary6);
        bottomRow.add(buttonPrimary7);
        bottomRow.add(h6);
    }



    private static Grid<GridOutputWindowed> getGridOutputWindowedGrid() {
        Grid<GridOutputWindowed> inputAnnotatedGrid = new Grid<>(GridOutputWindowed.class);
        inputAnnotatedGrid.setWidth("100%");
        inputAnnotatedGrid.setHeight("100%");
        inputAnnotatedGrid.getStyle().set("flex-grow", "1");
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("version"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("id"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("consA"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("timestamp"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("consB"));
        return inputAnnotatedGrid;
    }

    public static int findTimestamp(String input) {
        int index = input.indexOf("timestamp=");
        if (index != -1) {
            int startIndex = index + "timestamp=".length();
            int endIndex = startIndex;
            while (endIndex < input.length() && Character.isDigit(input.charAt(endIndex))) {
                endIndex++;
            }
            String timestampStr = input.substring(startIndex, endIndex);
            try {
                return Integer.parseInt(timestampStr);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing timestamp: " + e.getMessage());
            }
        }
        return -1;
    }

    private void setWindowScenario(String scenario, VerticalLayout windowEditor) {
        if (scenario.equals("Electric Grid")){
            extracted(windowEditor, "5", "5");
            extracted(windowEditor, "7",  "7");

        }else if (scenario.equals("Stock")){
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Button removeButton = new Button();
            removeButton.setText("X");
            removeButton.addSingleClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                @Override
                public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                    windowEditor.remove(horizontalLayout);
                }
            });

            //String Building Based on the window
            StringBuilder text = new StringBuilder();
            text.append("Time-based Window").append(" with size 5 and slide 5");

            horizontalLayout.add(removeButton);
            horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
            String text1 = text.toString();
            horizontalLayout.add(text1);
            horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            windowEditor.add(horizontalLayout);
        }else {
            extracted(windowEditor, "5", "5");
            extracted(windowEditor, "7",  "7");
        }
    }

    private void extracted(VerticalLayout windowEditor, String size, String slide) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button removeButton = new Button();
        removeButton.setText("X");
        removeButton.addSingleClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                windowEditor.remove(horizontalLayout);
            }
        });

        //String Building Based on the window
        StringBuilder text = new StringBuilder();
        text.append("Time-based Window").append(" with size ").append(size)
                .append(" and slide ").append(slide);

        horizontalLayout.add(removeButton);
        horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
        String text1 = text.toString();
        horizontalLayout.add(text1);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        windowEditor.add(horizontalLayout);
    }


    public String findWordAfterAttribute(String input) {
        int attributeIndex = input.indexOf("Attribute");
        if (attributeIndex != -1) {
            int start = attributeIndex + "Attribute".length() + 1; // Move to the character after 'Attribute'
            int end = input.indexOf(' ', start); // Find the end of the word
            if (end != -1) {
                return input.substring(start, end);
            } else {
                // If there's no space after 'Attribute', return the substring from 'Attribute' to the end of the input
                return input.substring(start);
            }
        }
        return null; // No word found after 'Attribute'
    }


    public class WindowRowSummary {
        private String name;
        private String attribute;
        private String operator;
        private long range;
        private long size;
        private long slide;
        private long timeout;

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getSlide() {
            return slide;
        }

        public void setSlide(long slide) {
            this.slide = slide;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public WindowRowSummary() {
        }

        public WindowRowSummary(String name, String attribute, String operator, Long range) {
            this.name = name;
            this.attribute = attribute;
            this.operator = operator;
            this.range = range;
        }

        public WindowRowSummary(String name, Long timeout) {
            this.name = name;
            this.timeout = timeout;
            this.attribute = "ND";
            this.operator = "ND";
        }

        public WindowRowSummary(String name, Long size, Long slide) {
            this.name = name;
            this.size = size;
            this.slide = slide;
            this.attribute = "ND";
            this.operator = "ND";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getRange() {
            return range;
        }

        public void setRange(long range) {
            this.range = range;
        }

        public String getAttribute() {
            if (this.name.contains("F"))
                return attribute;
            else return "nd";
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getQuery() {
            StringBuilder query = new StringBuilder();

            query.append("SELECT *\n" +
                    "FROM");
            if (this.name.contains("TW"))
                query.append(" HOP(TABLE Stream, DESCRIPTOR(ts), INTERVAL '" + size + "' minutes, INTERVAL '" + slide + "' minutes)");
            else if (this.name.contains("SW"))
                query.append(" SESSION(TABLE Stream, DESCRIPTOR(ts), INTERVAL '" + timeout + "' minutes)");
            else if (this.name.contains("F"))
                query.append(" FRAMES(TABLE Stream, DESCRIPTOR(ts), " + attribute + " " + operator + " " + range);

            return query.toString();
        }
    }



    public Long findSize(String input) {
        int start = input.lastIndexOf("Size ");
        if (start != -1) {
            start += 6; // Move to the beginning of the integer value
            int end = input.length(); // Find the end of the integer value
            if (end != -1) {
                try {
                    return Long.parseLong(input.substring(start, end));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing integer value.");
                }
            }
        }
        return (long) -1; // Integer value not found
    }

    public Long findSlide(String input) {
        int start = input.lastIndexOf("Slide ");
        if (start != -1) {
            start += 6; // Move to the beginning of the integer value
            int end = input.length(); // Find the end of the integer value
            if (end != -1) {
                try {
                    return Long.parseLong(input.substring(start, end));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing integer value.");
                }
            }
        }
        return (long) -1; // Integer value not found
    }

    public Long findTimeout(String input) {
        int start = input.lastIndexOf("Timeout ");
        if (start != -1) {
            start += 6; // Move to the beginning of the integer value
            int end = input.length(); // Find the end of the integer value
            if (end != -1) {
                try {
                    return Long.parseLong(input.substring(start, end));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing integer value.");
                }
            }
        }
        return (long) -1; // Integer value not found
    }




    private void setGridSampleData(Grid grid, String scenario) {
        if (scenario.equals("Electric Grid"))
            grid.setItems(query -> sampleGridService.list(
                            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());
        else if (scenario.equals("Stock"))
            grid.setItems(query -> sampleStockService.list(
                            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());
        else grid.setItems(query -> sampleGridService.list(
                            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());
    }

    public GridInputWindowed createGridRecord(String recordId, long ts, long consA, long consB){
        GridInputWindowed gridInput = new GridInputWindowed();
        gridInput.setRecordId(recordId);
        gridInput.setTimestamp(ts);
        gridInput.setConsA((long) consA);
        gridInput.setConsB((long) consB);
        return gridInput;
    }

    public StockInput createStockRecord(long ts, String name, int dollars, String recordId){
        StockInput stockInput = new StockInput();
        stockInput.setRecordId(recordId);
        stockInput.setTimestamp(ts);
        stockInput.setDollars(dollars);
        stockInput.setName(name);
        return stockInput;
    }



    private void setGridSampleSimpleData(Grid grid, String scenario) {
        if (scenario.equals("Electric Grid")){
            inputGridList = new ArrayList<>();
            inputGridListActual = new ArrayList<>();

            GridInputWindowed firstGridRecord = createGridRecord("r_"+0, 0, 8, 2);
//            firstGridRecord.setCursor(">");
            inputGridList.add(firstGridRecord);
            inputGridList.add(createGridRecord("r_"+1, 1, 8, 2));
            inputGridList.add(createGridRecord("r_"+2, 2, 8, 2));
            inputGridList.add(createGridRecord("r_"+3, 3, 8, 2));
            inputGridList.add(createGridRecord("r_"+4, 4, 5, 5));
            inputGridList.add(createGridRecord("r_"+5, 5, 3, 7));
            inputGridList.add(createGridRecord("r_"+6, 6, 1, 10));
            inputGridList.add(createGridRecord("r_"+7, 7, 0, 10));
            Random random = new Random(0L);
            for (int i = 1; i < 20; i++) {
                inputGridList.add(createGridRecord("r_"+(7+i), 7+i, random.nextInt(0, 11), random.nextInt(0, 11)));
            }

//            grid.addColumn(String.valueOf(VaadinIcon.BOLT.create()));

            grid.removeColumn(grid.getColumnByKey("id"));
            grid.removeColumn(grid.getColumnByKey("version"));
            //grid.removeColumn(grid.getColumnByKey("label"));
            //grid.removeColumn(grid.getColumnByKey("time"));
            //grid.removeColumn(grid.getColumnByKey("value"));
            grid.removeColumn(grid.getColumnByKey("cursor"));
            grid.removeColumn(grid.getColumnByKey("intervalId"));
            grid.removeColumn(grid.getColumnByKey("operatorId"));
//            grid.setSortableColumns("recordId", "timestamp", "consA", "consB");


            List<Grid.Column> strings = Arrays.asList(grid.getColumnByKey("recordId"),
                    grid.getColumnByKey("timestamp"), grid.getColumnByKey("consA"),
                    grid.getColumnByKey("consB"));
            grid.setColumnOrder(strings);


            grid.setItems(inputGridList);
        }
        else if (scenario.equals("Stock")){

            List<String> strings = Arrays.asList("AAPL", "GOOGL", "AMZN");
            Random randomName = new Random(0L);

            Random randomDollars = new Random(0L);

            stockInputArrayList = new ArrayList<>();

            StockInput stockRecord = createStockRecord(0, strings.get(randomName.nextInt(0, 3)), randomDollars.nextInt(0, 100), "r_0");
            stockRecord.setCursor(">");
            stockInputArrayList.add(stockRecord);
            for (int i = 1; i < 40; i++) {
                stockInputArrayList.add(createStockRecord(i, strings.get(randomName.nextInt(0, 3)), randomDollars.nextInt(0, 100), "r_"+i));
            }


            grid.removeColumn(grid.getColumnByKey("id"));
            grid.removeColumn(grid.getColumnByKey("version"));
            grid.removeColumn(grid.getColumnByKey("cursor"));

            List<Grid.Column> strings3 = Arrays.asList(grid.getColumnByKey("recordId"),
                    grid.getColumnByKey("timestamp"), grid.getColumnByKey("name"), grid.getColumnByKey("dollars"));
            grid.setColumnOrder(strings3);
            grid.setItems(stockInputArrayList);
        }

        else {
            grid.setItems(query -> sampleGridService.list(
                            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());
            grid.removeColumn(grid.getColumnByKey("id"));
            grid.removeColumn(grid.getColumnByKey("version"));
            grid.removeColumn(grid.getColumnByKey("cursor"));
        }
    }


    @Autowired()
    private SampleGridService sampleGridService;

    @Autowired()
    private SampleStockService sampleStockService;

    @Autowired()
    private PolyflowService polyflowService;


    private void updateWindowState(NetworkDiagram snapshotGraph, List<GridOutputWindowed> results) {

        List<Edge> edges = new ArrayList<>();
        Map<String, Node> nodes = new HashMap<>();

        results.forEach(result -> {
            Node eRecord;
            if (!nodes.keySet().contains(result.getRecordId())){
                eRecord = new Node(result.getRecordId(), result.getRecordId());
                eRecord.setShape(Shape.diamond);
                eRecord.setColor("lightblue");
                eRecord.setLabelHighlightBold(true);
                nodes.put(result.getRecordId(), eRecord);
            } else eRecord = nodes.get(result.getRecordId());

            Node eWindow;
            if (!nodes.keySet().contains(result.getIntervalId())) {
                eWindow = new Node(result.getIntervalId(), result.getIntervalId());
                eWindow.setShape(Shape.ellipse);
                eWindow.setColor("lightgreen");
                eWindow.setLabelHighlightBold(true);
                nodes.put(result.getIntervalId(), eWindow);
            } else eWindow = nodes.get(result.getIntervalId());

            Edge e = new Edge(eRecord, eWindow);
            e.setArrows(new Arrows(new ArrowHead()));
            edges.add(e);
        });



        snapshotGraph.setNodes(nodes.values());
        snapshotGraph.setEdges(edges);
    }

}
