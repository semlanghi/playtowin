package com.example.application.views.myview;

import com.example.application.data.*;
import com.example.application.polyflow.cgraph.ConsistencyGraph;
import com.example.application.polyflow.cgraph.ConsistencyGraphImpl;
import com.example.application.polyflow.cgraph.ConsistencyNode;
import com.example.application.polyflow.datatypes.EventBean;
import com.example.application.polyflow.datatypes.GridInputWindowed;
import com.example.application.polyflow.datatypes.GridOutputWindowed;
import com.example.application.polyflow.datatypes.Table;
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
    private String query = "SELECT percent(consA,consB),percent(consB,consA),ts\n" +
            "FROM Consumption [RANGE 5 minutes SLIDE 2 minutes]\n" +
            "WHERE consA >= 0 AND consB >= 0";
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

                    this.query = "SELECT percent(consA,consB),percent(consB,consA), max(ts)\n" +
                            "FROM Consumption [RANGE 5 minutes SLIDE 2 minutes]\n" +
                            "WHERE consA >= 0 AND consB >= 0";
                    sampleOutputClass = GridOutput.class;
                    sampleInputClass = GridInputWindowed.class;
                    loadPage(selectScenarios, event.getValue());


                    break;
                case "Stock":
                    sampleOutputClass = StockOutput.class;
                    sampleInputClass = StockInput.class;

                    this.query = "SELECT name, avg(value), max(ts)\n" +
                            "FROM Stock [RANGE 5 minutes SLIDE 2 minutes]\n" +
                            "WHERE name = Apple";
                    loadPage(selectScenarios, event.getValue());
                    break;
                default: loadPage(selectScenarios, "Electric Grid");
            }
        });


        loadPage(selectScenarios, "Electric Grid");
    }

    private void loadPage(ComboBox<String> selectScenarios, String scenario) {


        VerticalLayout leftColumn = new VerticalLayout();




        leftColumn.add(selectScenarios);



        Grid basicGrid = new Grid<>(sampleInputClass);




        VerticalLayout centralColumn = new VerticalLayout();
        TabSheet upperCentralTabSheet = new TabSheet();
        VerticalLayout bottomCentralColumn = new VerticalLayout();
        TextArea queryEditor = new TextArea();
        queryEditor.setValue(query);
        VerticalLayout constraintEditor = new VerticalLayout();
        constraintEditor.setHeightFull();
        constraintEditor.setWidthFull();


        ComboBox<String> selectConstraintType = new ComboBox<>();
        selectConstraintType.setLabel("Window Type");
        selectConstraintType.setItems("Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate");
        selectConstraintType.setValue("Time-based");
        HorizontalLayout constraintSelectorLayout = new HorizontalLayout();

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

        selectConstraintType.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<String>, String>>) event -> {
            int index = 1;

            if (!(event.getOldValue().equals("Frames:Aggregate")) && (event.getValue().startsWith("Frames:Aggregate"))) {
                constraintSelectorLayout.addComponentAtIndex(index++, selectAggregate);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, selectAggregate);
            } else if (!(event.getValue().equals("Frames:Aggregate")) && (event.getOldValue().equals("Frames:Aggregate"))) {
                constraintSelectorLayout.remove(selectAggregate);
            }
            if (!(event.getOldValue().startsWith("Frames")) && (event.getValue().startsWith("Frames"))) {
                constraintSelectorLayout.addComponentAtIndex(index++, selectAttribute);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, selectAttribute);
                constraintSelectorLayout.addComponentAtIndex(index++, selectOp);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, selectOp);
                constraintSelectorLayout.addComponentAtIndex(index, threshold);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, threshold);
            } else if (!(event.getValue().startsWith("Frames")) && (event.getOldValue().startsWith("Frames"))) {
                constraintSelectorLayout.remove(threshold);
                constraintSelectorLayout.remove(selectAttribute);
                constraintSelectorLayout.remove(selectOp);
            }

            if (!(event.getOldValue().equals("Time-based")) && (event.getValue().equals("Time-based"))) {
                constraintSelectorLayout.addComponentAtIndex(index++, size);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, size);
                constraintSelectorLayout.addComponentAtIndex(index, slide);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, slide);
            } else if (!(event.getValue().equals("Time-based")) && (event.getOldValue().equals("Time-based"))) {
                constraintSelectorLayout.remove(size);
                constraintSelectorLayout.remove(slide);
            }

            if (!(event.getOldValue().equals("Session")) && (event.getValue().equals("Session"))) {
                constraintSelectorLayout.addComponentAtIndex(index++, timeout);
                constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, timeout);
            } else if (!(event.getValue().equals("Session")) && (event.getOldValue().equals("Session"))) {
                constraintSelectorLayout.remove(timeout);
            }

            Notification.show(event.getValue());
        });



        Button constraintCreatorButton = new Button();




        constraintCreatorButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                HorizontalLayout horizontalLayout = new HorizontalLayout();
                Button removeButton = new Button();
                removeButton.setText("X");
                removeButton.addSingleClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                    @Override
                    public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                        constraintEditor.remove(horizontalLayout);
                    }
                });

                String constraintTypeValue = selectConstraintType.getValue();

                //String Building Based on the constraint
                StringBuilder text = new StringBuilder();
                text.append(constraintTypeValue);


                try {
                    if (constraintTypeValue.equals("Frames:Aggregate")) {
                        text.append(" on aggregate ").append(selectAggregate.getValue());
                    }
                    if (constraintTypeValue.startsWith("Frames")){
                        text.append(" over Attribute ").append(selectAttribute.getValue()).append(" ").append(selectOp.getValue()).append(" ").append(threshold.getValue());
                    } else if (constraintTypeValue.equals("Time-based")){
                        text.append(" with Size ").append(size.getValue()).append(" and Slide ").append(slide.getValue());
                    } else if (constraintTypeValue.equals("Session")){
                        text.append(" with Timeout ").append(timeout.getValue());
                    }
                    horizontalLayout.add(removeButton);
                    horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
                    String text1 = text.toString();
                    horizontalLayout.add(text1);
                    horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
                    constraintEditor.add(horizontalLayout);
                } catch (NumberFormatException e) {
                    Notification.show("No Constraint Created, Range invalid.");
                }
            }
        });

        constraintCreatorButton.setText("Add");
        constraintSelectorLayout.add(selectConstraintType, size, slide);
        constraintSelectorLayout.add(constraintCreatorButton);

        constraintSelectorLayout.setAlignItems(Alignment.START);

        constraintEditor.add(constraintSelectorLayout);
        constraintEditor.setAlignItems(Alignment.START);
        constraintSelectorLayout.setVerticalComponentAlignment(Alignment.END, constraintCreatorButton);

        setConstraintScenario(scenario, constraintEditor);

        VerticalLayout rightColumn = new VerticalLayout();


        TabSheet tabSheetUpperRight = new TabSheet();
        TabSheet tabSheetBottomRight = new TabSheet();

        TabSheet tabSheet3 = new TabSheet();

        Button buttonNext = new Button();

        NetworkDiagram snapshotGraphSolo =
                new NetworkDiagram(Options.builder().withWidth("100%").withHeight("100%")
                        .withInteraction(Interaction.builder().withMultiselect(true).build()).build());



        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();


        Grid<GridOutputWindowed> inputAnnotatedGrid = new Grid<>(GridOutputWindowed.class);
        inputAnnotatedGrid.setWidth("100%");
        inputAnnotatedGrid.setHeight("100%");
        inputAnnotatedGrid.getStyle().set("flex-grow", "1");
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("version"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("id"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("consA"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("timestamp"));
        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("consB"));

        Grid<GridOutput> outputResultGrid = new Grid<>(GridOutput.class);
        outputResultGrid.setWidth("100%");
        outputResultGrid.setHeight("100%");
        outputResultGrid.getStyle().set("flex-grow", "0");
        outputResultGrid.removeColumn(outputResultGrid.getColumnByKey("version"));
        outputResultGrid.removeColumn(outputResultGrid.getColumnByKey("id"));


        tabSheetUpperRight.add("Graph", snapshotGraphSolo);

        tabSheetBottomRight.add("Results", outputResultGrid);


        Grid<GridOutputAnnotated> outputAnnotatedGrid = new Grid<>(GridOutputAnnotated.class);
        outputAnnotatedGrid.setWidth("100%");
        outputAnnotatedGrid.setHeight("100%");
        outputAnnotatedGrid.getStyle().set("flex-grow", "0");
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("version"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("id"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("percA"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("percB"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("timestamp"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("degree"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("simplifiedPolynomial"));
        outputAnnotatedGrid.removeColumn(outputAnnotatedGrid.getColumnByKey("variablesCardinality"));


        tabSheetBottomRight.add("Annotations", inputAnnotatedGrid);

        Grid<GridOutputAnnotated> outputQuantifiedGrid = new Grid<>(GridOutputAnnotated.class);
        outputQuantifiedGrid.setWidth("100%");
        outputQuantifiedGrid.setHeight("100%");
        outputQuantifiedGrid.getStyle().set("flex-grow", "0");
        outputQuantifiedGrid.removeColumn(outputQuantifiedGrid.getColumnByKey("version"));
        outputQuantifiedGrid.removeColumn(outputQuantifiedGrid.getColumnByKey("id"));
        outputQuantifiedGrid.removeColumn(outputQuantifiedGrid.getColumnByKey("percA"));
        outputQuantifiedGrid.removeColumn(outputQuantifiedGrid.getColumnByKey("percB"));
        outputQuantifiedGrid.removeColumn(outputQuantifiedGrid.getColumnByKey("timestamp"));
        outputQuantifiedGrid.removeColumn(outputQuantifiedGrid.getColumnByKey("polynomial"));


        tabSheetBottomRight.add("Quantification", outputQuantifiedGrid);


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

                inputAnnotatedGrid.setItems(actualOutput);
                List<Grid.Column<GridOutputWindowed>> sgab = Arrays.asList(
                        inputAnnotatedGrid.getColumnByKey("recordId"),
                        inputAnnotatedGrid.getColumnByKey("operatorId"),
                        inputAnnotatedGrid.getColumnByKey("intervalId"));

                inputAnnotatedGrid.getColumnByKey("recordId").setWidth("50px");
                inputAnnotatedGrid.setColumnOrder(sgab);


                inputAnnotatedGrid.getDataProvider().refreshAll();


            } else {
                Notification.show("A query must be submitted first.");
            }
        });

        Button buttonPrimary2 = new Button();
        Button buttonPrimary3 = new Button();
        Button buttonPrimary4 = new Button();
        Button queryButton = new Button();
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
        bottomCentralColumn.setAlignSelf(Alignment.CENTER, queryEditor);
        queryEditor.setWidth("100%");
        queryEditor.setHeight("100%");
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









//        setTabSheetSampleData(tabSheet3);

        tabSheet3.add("Constraints", constraintEditor);
        tabSheet3.add("Query", queryEditor);

        //Summary Tab
        HorizontalLayout summaryLayout = new HorizontalLayout();
        VerticalLayout summaryConstraints = new VerticalLayout();
        tabSheet3.add("Summary", summaryLayout);

        Grid<ConstraintRowSummary> summaryConstraintGrid = new Grid<>(ConstraintRowSummary.class);
        HashMap<String, Integer> constraintCounter = new HashMap<>();
        List<ConstraintRowSummary> constraintRowSummaries = new ArrayList<>();
        summaryConstraintGrid.setItems(constraintRowSummaries);
        summaryConstraintGrid.setHeightFull();
        summaryConstraintGrid.setWidthFull();




        for (int i = 1; i < constraintEditor.getComponentCount(); i++) {
            String constraintDescr = ((Text) ((HorizontalLayout) constraintEditor.getComponentAt(i)).getComponentAt(1)).getText();
            int rangeConstraint = 0;
            String attConstraint;
            String abbrevConstraintType;
            if (constraintDescr.contains("Speed")){

                constraintCounter.putIfAbsent("SC", 1);
                abbrevConstraintType = "SC"+constraintCounter.get("SC");
                constraintCounter.put("SC", constraintCounter.get("SC")+1);
                rangeConstraint = findInteger(constraintDescr);
                attConstraint = findWordAfterAttribute(constraintDescr);
                constraintRowSummaries.add(new ConstraintRowSummary(abbrevConstraintType, rangeConstraint, attConstraint));
                summaryConstraintGrid.getDataProvider().refreshAll();
            } else if (constraintDescr.contains("Primary Key")){
                constraintCounter.putIfAbsent("PK", 1);
                abbrevConstraintType = "PK"+constraintCounter.get("PK");
                constraintCounter.put("PK", constraintCounter.get("PK")+1);
                attConstraint = findWordAfterAttribute(constraintDescr);
                constraintRowSummaries.add(new ConstraintRowSummary(abbrevConstraintType, rangeConstraint, attConstraint));
                summaryConstraintGrid.getDataProvider().refreshAll();
            }

        }
        summaryConstraints.add(summaryConstraintGrid);

        VerticalLayout summaryQuery = new VerticalLayout();
        TextArea textField = new TextArea();
        textField.setSizeFull();
        textField.setValue(query);

        summaryQuery.add(textField);

        textField.setReadOnly(true);





        summaryLayout.add(summaryConstraints, summaryQuery);

        summaryLayout.setHeightFull();
        summaryLayout.setWidthFull();
        summaryConstraints.setWidth("50%");
        summaryConstraints.setHeightFull();
        summaryQuery.setHeightFull();
        summaryQuery.setWidth("50%");
        textField.setWidth("100%");
        textField.setHeight("100%");


        tabSheet3.addSelectedChangeListener(new ComponentEventListener<TabSheet.SelectedChangeEvent>() {
            @Override
            public void onComponentEvent(TabSheet.SelectedChangeEvent selectedChangeEvent) {
                if (selectedChangeEvent.getSelectedTab().getLabel().equals("Summary")){
                    HashMap<String, Integer> constraintCounter = new HashMap<>();
                    List<ConstraintRowSummary> constraintRowSummaries = new ArrayList<>();
                    summaryConstraintGrid.setItems(constraintRowSummaries);
                    for (int i = 1; i < constraintEditor.getComponentCount(); i++) {
                        String constraintDescr = ((Text) ((HorizontalLayout) constraintEditor.getComponentAt(i)).getComponentAt(1)).getText();
                        int rangeConstraint = 0;
                        String attConstraint;
                        String abbrevConstraintType;
                        if (constraintDescr.contains("Speed")){

                            constraintCounter.putIfAbsent("SC", 1);
                            abbrevConstraintType = "SC"+constraintCounter.get("SC");
                            constraintCounter.put("SC", constraintCounter.get("SC")+1);
                            rangeConstraint = findInteger(constraintDescr);
                            attConstraint = findWordAfterAttribute(constraintDescr);
                            constraintRowSummaries.add(new ConstraintRowSummary(abbrevConstraintType, rangeConstraint, attConstraint));
                            summaryConstraintGrid.getDataProvider().refreshAll();
                        } else if (constraintDescr.contains("Primary Key")){
                            constraintCounter.putIfAbsent("PK", 1);
                            abbrevConstraintType = "PK"+constraintCounter.get("PK");
                            constraintCounter.put("PK", constraintCounter.get("PK")+1);
                            attConstraint = findWordAfterAttribute(constraintDescr);
                            constraintRowSummaries.add(new ConstraintRowSummary(abbrevConstraintType, rangeConstraint, attConstraint));
                            summaryConstraintGrid.getDataProvider().refreshAll();
                        }

                    }
                }

            }
        });

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
        buttonPrimary4.setText("Constraints");
        buttonPrimary4.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                Notification.show("Constraints Registered.");
            }
        });
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary4);
        buttonPrimary4.setWidth("min-content");
        buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        queryButton.setText("Query");
        queryButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                try {
                    Notification.show("Registered Query.");
                    basicGrid.setItems(inputGridListActual);
                    polyflowService.register(scenario);
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
        bottomRow.setAlignSelf(Alignment.CENTER, queryButton);
        queryButton.setWidth("min-content");
        queryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary6.setText("LLM");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary6);
        buttonPrimary6.setWidth("min-content");
        buttonPrimary6.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary7.setText("Reset");
        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary7);
        buttonPrimary7.setWidth("min-content");
        buttonPrimary7.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        h6.setText("Samuele Langhi, Angela Bonifati, Riccardo Tommasini");
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
        bottomRow.add(buttonPrimary4);
        bottomRow.add(queryButton);
        bottomRow.add(buttonPrimary6);
        bottomRow.add(buttonPrimary7);
        bottomRow.add(h6);
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

    private void setConstraintScenario(String scenario, VerticalLayout constraintEditor) {
        if (scenario.equals("Electric Grid")){
            extracted(constraintEditor, "5", "5");
            extracted(constraintEditor, "7",  "7");

        }else if (scenario.equals("Stock")){
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Button removeButton = new Button();
            removeButton.setText("X");
            removeButton.addSingleClickListener(new ComponentEventListener<ClickEvent<Button>>() {
                @Override
                public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                    constraintEditor.remove(horizontalLayout);
                }
            });

            //String Building Based on the constraint
            StringBuilder text = new StringBuilder();
            text.append("Time-based Window").append(" with size 5 and slide 5");

            horizontalLayout.add(removeButton);
            horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
            String text1 = text.toString();
            horizontalLayout.add(text1);
            horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            constraintEditor.add(horizontalLayout);
        }else {
            extracted(constraintEditor, "5", "5");
            extracted(constraintEditor, "7",  "7");
        }
    }

    private void extracted(VerticalLayout constraintEditor, String size, String slide) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button removeButton = new Button();
        removeButton.setText("X");
        removeButton.addSingleClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                constraintEditor.remove(horizontalLayout);
            }
        });

        //String Building Based on the constraint
        StringBuilder text = new StringBuilder();
        text.append("Time-based Window").append(" with size ").append(size)
                .append(" and slide ").append(slide);

        horizontalLayout.add(removeButton);
        horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
        String text1 = text.toString();
        horizontalLayout.add(text1);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        constraintEditor.add(horizontalLayout);
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


    public class ConstraintRowSummary {
        private String name;
        private Integer range;
        private String attribute;

        public ConstraintRowSummary(String name, Integer range, String attribute) {
            this.name = name;
            this.range = range;
            this.attribute = attribute;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getRange() {
            return range;
        }

        public void setRange(Integer range) {
            this.range = range;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }
    }

    public int findInteger(String input) {
        int start = input.lastIndexOf("Range ");
        if (start != -1) {
            start += 6; // Move to the beginning of the integer value
            int end = input.length(); // Find the end of the integer value
            if (end != -1) {
                try {
                    return Integer.parseInt(input.substring(start, end));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing integer value.");
                }
            }
        }
        return -1; // Integer value not found
    }

    public int detectDegree(String polynomialString) {

        String[] split = polynomialString.split("\\+");

        Pattern pattern = Pattern.compile("\\^([0-9]+)");
        int degree = 0;
        int[] degrees = new int[split.length];
        for (int i = 0; i < split.length; i++){
            Matcher matcher = pattern.matcher(polynomialString);
            while (matcher.find())
                degree += Integer.parseInt(matcher.group(1));
            degrees[i] = degree;
            degree = 0;
        }

        int maxDegree = -1;
        for (int j : degrees) {
            if (j > maxDegree) {
                maxDegree = j;
            }
        }

        return maxDegree;
    }

    public int detectNumVariables(String polynomialString) {
        Pattern pattern = Pattern.compile("SC\\d+_\\d+");
        Matcher matcher = pattern.matcher(polynomialString);
        int numVariables = 0;
        while (matcher.find()) {
            numVariables++;
        }
        return numVariables;
    }

    public String simplifyPolynomial(String polynomialString) {

        String[] split = polynomialString.split("\\+");


        Pattern pattern =  Pattern.compile("\\*SC1_(\\d+)\\^(\\d+)\\*SC2_\\1\\^(\\d+)");
        for (int i = 0; i < split.length; i++) {
            Matcher matcher = pattern.matcher(split[i]);
            while (matcher.find())
                split[i] = matcher.replaceAll("");
        }

//        String simplifiedPolynomial = polynomialString.replaceAll("\\+?\\d+\\/\\d+SC\\d+_\\d+", "");
//        return simplifiedPolynomial.replaceAll("(?<!\\^)SC\\d+_\\d+", "");

        StringBuilder stringBuilder = new StringBuilder();
        boolean firstPlus = false;
        for (String tmp : split
        ) {
            if (firstPlus)
                stringBuilder.append("+");
            else firstPlus = true;
            stringBuilder.append(tmp);
        }
        return  stringBuilder.toString();
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


    /*private String getNodeId(ConsistencyNode<?> tmp){
        return "r_"+((GridInput)tmp.getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp() + "_" + tmp.getConstraint().getDescription().split("_")[0];
    }*/

    private void updateNode(ConsistencyNode<?> node, List<Node> nodes, List<Edge> edges){


       /* if (node.getConnectedNodes().isEmpty())
            return;
        for (ConsistencyNode<?> tmp: node.getConnectedNodes()
        ) {

            if (nodes.stream().noneMatch(node1 -> node1.getId().equals(getNodeId(tmp)))){
                Node e1 = new Node(getNodeId(tmp), "r_" + ((GridInput) tmp.getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp());
                Edge e = new Edge(getNodeId(node), getNodeId(tmp));
                e1.setColor("lightgrey");
                e1.setShape(Shape.diamond);
                e1.setLabelHighlightBold(true);
                e.setColor("black");
//                if (tmp.getConstraint().getDescription().contains("SC2")){
//                   e1.setColor("red");
//                   e.setColor("red");
//                }
                e.setArrows(new Arrows(new ArrowHead()));
                e.setLabel(tmp.getConstraint().getDescription().split("_")[0]);
                nodes.add(e1);
                if (getNodeId(node).equals("r_6_SC2") &&  getNodeId(tmp).equals("r_2_SC2")){
                    if (!setLocal){
                        edges.add(e);
                        setLocal = true;
                    }
                } else edges.add(e);

                updateNode(tmp, nodes, edges);
            } else {
                Edge e = new Edge(getNodeId(node), getNodeId(tmp));
                e.setColor("black");
//                if (tmp.getConstraint().getDescription().contains("SC2")){
//                    e.setColor("red");
//                }
                e.setArrows(new Arrows(new ArrowHead()));
                e.setLabel(tmp.getConstraint().getDescription().split("_")[0]);
                if (getNodeId(node).equals("r_6_SC2") &&  getNodeId(tmp).equals("r_2_SC2")){
                    if (!setLocal){
                        edges.add(e);
                        setLocal = true;
                    }
                } else edges.add(e);
            }

        }*/
    }

    private void updateSnapshotGraphFromContent(NetworkDiagram snapshotGraph, List<Node> nodes, List<Edge> edges, ConsistencyGraphImpl consistencyGraph) {

        /*nodes.clear();
        edges.clear();



        List<ConsistencyNode> rootNodes = consistencyGraph.getRootNodes();
        Optional<ConsistencyNode> max = rootNodes.stream().max(new Comparator<ConsistencyNode>() {
            @Override
            public int compare(ConsistencyNode o1, ConsistencyNode o2) {
                return Long.compare(((GridInput) o1.getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp(),
                        ((GridInput) o2.getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp());
            }
        });

        long maxTime = ((GridInput) max.get().getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp();

        rootNodes
                .forEach(new Consumer<ConsistencyNode>() {
                    @Override
                    public void accept(ConsistencyNode consistencyNode) {
                        Node e = new Node(getNodeId(consistencyNode), "r_" + ((GridInput) consistencyNode.getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp());
                        e.setShape(Shape.diamond);
                        if (((GridInput) consistencyNode.getConsistencyAnnotatedRecord().getWrappedRecord()).getTimestamp() == maxTime){
                            e.setColor("lightblue");
                        }else{
                            e.setColor("lightgreen");
                        }

                        e.setLabelHighlightBold(true);

                        nodes.add(e);

                        updateNode(consistencyNode, nodes, edges);
                    }
                });

        snapshotGraph.setNodes(nodes);
        snapshotGraph.setEdges(edges);*/

    }




    private void setTabSheetSampleData(TabSheet tabSheet) {
        tabSheet.add("Dashboard", new Div(new Text("This is the Dashboard tab content")));
        tabSheet.add("Payment", new Div(new Text("This is the Payment tab content")));
        tabSheet.add("Shipping", new Div(new Text("This is the Shipping tab content")));
    }
}
