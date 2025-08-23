package com.example.application.views.myview;

import com.example.application.polyflow.datatypes.*;
import com.example.application.polyflow.datatypes.electricity.InputElectricity;
import com.example.application.polyflow.datatypes.linearroad.InputLinearRoad;
import com.example.application.polyflow.datatypes.nexmark.InputBid;
import com.example.application.polyflow.datatypes.nyctaxi.InputTaxi;
import com.example.application.polyflow.operators.TimeVaryingTuplesOrResult;
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
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import elemental.json.JsonArray;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.streamreasoning.polyflow.api.operators.s2r.execution.assigner.StreamToRelationOperator;
import org.vaadin.addons.visjs.network.event.SelectNodeEvent;
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


import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@PageTitle("PlayToWin")
@Route(value = "my-view", layout = MainLayout.class)
@Uses(Icon.class)
@CssImport(
        themeFor = "vaadin-grid",
        value = "./recipe/dynamicgridrowbackgroundcolor/dynamic-grid-row-background-color.css"
)
public class PlayToWin extends Composite<VerticalLayout> {

    public static final String[] COLORS = new String[]{
            "orange", "lightgreen",
             "cyan", "pink"
    };
    private HorizontalLayout mainRow;

    private HorizontalLayout bottomRow;
    private HorizontalLayout upperCentralRow;
    private HorizontalLayout bottomCentralRow;


    private Class<?> sampleInputClass;
    private List<Tuple> inputGridList;
    private List<Tuple> inputGridListActual;
    private int counterInput = 0;
    List<Tuple> windowHistory = new ArrayList<>();//TODO: remove this line, it's just to make shut up the errors

    private List<WindowRowSummary> windowRowSummaries;
    private Map<String, String> colorGraphs;

    //ALESSANDRO
    /*
    * This List represents fields that belong to a given input class but should not be shown
    * in the input stream view (left of the GUI)
    * */
    private List<String> columnsToRemoveForStream = new ArrayList<>();

    /*
     * This List represents fields that belong to a given input class that are shown
     * in the drop down menu of aggregation
     * */
    private List<String> columnsToShowForAggregation = new ArrayList<>();


    @Autowired()
    private SampleGridService sampleGridService;

    @Autowired()
    private SampleStockService sampleStockService;

    @Autowired()
    private PolyflowService polyflowService;

    public PlayToWin() {

        mainRow = new HorizontalLayout();
        upperCentralRow = new HorizontalLayout();
        bottomCentralRow = new HorizontalLayout();
        bottomRow = new HorizontalLayout();
        ComboBox<String> selectScenarios = new ComboBox<>();
        selectScenarios.setLabel("Scenario");
        selectScenarios.setItems("Electric Grid", "NYC Taxi (DEBS 2015)", "Linear Road", "Nexmark");
        selectScenarios.setValue("Electric Grid");
        columnsToRemoveForStream.addAll(List.of("id", "version", "cursor", "operatorId", "intervalId"));
        columnsToShowForAggregation.addAll(List.of("cons_A", "cons_B"));
        sampleInputClass = InputElectricity.class;




        selectScenarios.addValueChangeListener(event -> {
            mainRow.removeAll();
            bottomRow.removeAll();
            upperCentralRow.removeAll();
            counterInput = 0;
            switch (event.getValue()) {
                case "Electric Grid":
                    columnsToShowForAggregation = new ArrayList<>();
                    columnsToShowForAggregation.addAll(List.of("cons_A", "cons_B"));

                    sampleInputClass = InputElectricity.class;
                    loadPage(selectScenarios, event.getValue());

                    break;

                case "NYC Taxi (DEBS 2015)":
                    sampleInputClass = InputTaxi.class;
                    columnsToShowForAggregation = new ArrayList<>();
                    columnsToShowForAggregation.addAll(List.of("trip_distance", "total_amount",  "tolls_amount"));
                    loadPage(selectScenarios, event.getValue());
                    break;

                case "Linear Road":
                    sampleInputClass = InputLinearRoad.class;
                    columnsToShowForAggregation = new ArrayList<>();
                    columnsToShowForAggregation.addAll(List.of("speed", "exp_way", "lane", "direction", "x_pos"));
                    loadPage(selectScenarios, event.getValue());
                    break;

                case "Nexmark":
                    sampleInputClass = InputBid.class;
                    columnsToShowForAggregation = new ArrayList<>();
                    columnsToShowForAggregation.addAll(List.of("price"));
                    loadPage(selectScenarios, event.getValue());
                    break;

                default: throw new RuntimeException("Error in scenario selection");

            }
        });


        loadPage(selectScenarios, "Electric Grid");
    }

    private static String getWindowAbbrev(String windowDescr){
        if (windowDescr.contains("Time")){
            return "TW";
        } else if (windowDescr.contains("Session")){
            return "SW";
        } else if (windowDescr.contains("Frames")){
           return "F" + windowDescr.split(":")[1].substring(0, 3);
        } else if (windowDescr.contains("Union")){
            return "Uni";
        } else if (windowDescr.contains("Intersection")){
            return "Int";
        } return   null;
    }

    private void loadPage(ComboBox<String> selectScenarios, String scenario) {


        VerticalLayout leftColumn = new VerticalLayout();

        leftColumn.add(selectScenarios);

        Grid basicGrid = new Grid<>(sampleInputClass);




        VerticalLayout centralColumn = new VerticalLayout();
        TabSheet upperCentralTabSheet = new TabSheet();
        bottomCentralRow = new HorizontalLayout();
        VerticalLayout bottomCentralColumn = new VerticalLayout();
        bottomCentralRow.add(bottomCentralColumn);
        bottomCentralColumn.setWidthFull();
        bottomCentralColumn.setHeightFull();


        Map<String,String> defaultQueries = getDefaultQueries(scenario);
        ComboBox<String> queryEditor = new ComboBox<>();
        TextArea queryEditorText = new TextArea();

        queryEditorText.setSizeFull();

        bottomCentralColumn.add(queryEditor);
        bottomCentralColumn.add(queryEditorText);
        queryEditor.setItems(defaultQueries.keySet());
        queryEditor.setValue("Query 1");
        queryEditorText.setValue(defaultQueries.get("Query 1"));

        queryEditor.addValueChangeListener(event -> {
            String query = defaultQueries.get(event.getValue());
            queryEditorText.setValue(query);
        });
        VerticalLayout windowEditor = new VerticalLayout();
        windowEditor.setHeightFull();
        windowEditor.setWidthFull();

        HorizontalLayout compositeUnionLayout1 = new HorizontalLayout();
        compositeUnionLayout1.setWidthFull();
        compositeUnionLayout1.setVisible(false);

        HorizontalLayout compositeUnionLayout2 = new HorizontalLayout();
        compositeUnionLayout2.setWidthFull();
        compositeUnionLayout2.setVisible(false);

        HorizontalLayout windowSelectorLayout = new HorizontalLayout();
        windowSelectorLayout.setWidthFull();

        windowEditor.add(windowSelectorLayout);
        windowEditor.setAlignItems(Alignment.START);
        windowEditor.add(compositeUnionLayout1, compositeUnionLayout2);


        List<Result> result = getResult(windowSelectorLayout, compositeUnionLayout1, compositeUnionLayout2);


        Button windowCreatorButton = new Button();


        windowRowSummaries = new ArrayList<>();

        windowCreatorButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Button removeButton = new Button();
            WindowRowSummary windowRowSummary = new WindowRowSummary();
            removeButton.setText("X");
            removeButton.addSingleClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent1 -> {
                windowEditor.remove(horizontalLayout);
                windowRowSummaries.remove(windowRowSummary);
            });

            String windowTypeValue = result.get(0).selectWindowType().getValue();

            windowRowSummary.setQuery(queryEditorText.getValue());

            if (windowTypeValue.contains("Union") || windowTypeValue.contains("Int")) {
                windowRowSummary.setCompositeInternalWindow1(new WindowRowSummary());
                windowRowSummary.setCompositeInternalWindow2(new WindowRowSummary());

            }

            //String Building Based on the window
            StringBuilder text = new StringBuilder();


            try {
                PlayToWin.getResumeNameAndParams(windowTypeValue, text, windowRowSummary, result, 0);
                horizontalLayout.add(removeButton);
                horizontalLayout.setVerticalComponentAlignment(Alignment.CENTER, removeButton);
                String text1 = text.toString();
                horizontalLayout.add(text1);
                horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
                windowEditor.add(horizontalLayout);
                windowRowSummaries.add(windowRowSummary);
            } catch (NumberFormatException e) {
                Notification.show("No Window Created, Range invalid.").setPosition(Notification.Position.TOP_START);
            }
        });


        TabSheet tabSheet3 = new TabSheet();
        tabSheet3.add("Window Editor", windowEditor);


        //Summary Tab
        HorizontalLayout summaryLayout = new HorizontalLayout();
        VerticalLayout summaryWindows = new VerticalLayout();

        TextArea queryPerspective = new TextArea();
        queryPerspective.setReadOnly(true);
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

                    centralColumn.remove(bottomCentralRow);
                    upperCentralRow.setHeightFull();


                    //TODO: Bug resolution, if not in summary tab, does not start, due to the absence of the number in the window name
                    for (WindowRowSummary windowRowSummary : windowRowSummaries) {
                        String windowType = windowRowSummary.getName().replaceAll("\\d", "");

                        windowCounter.putIfAbsent(windowType, 1);
                        windowRowSummary.setName(windowType+windowCounter.get(windowType));
                        windowCounter.put(windowType, windowCounter.get(windowType)+1);
                    }


                    summaryWindowGrid.setItems(windowRowSummaries);
                    summaryWindowGrid.getDataProvider().refreshAll();
                } else {
                    centralColumn.add(bottomCentralRow);
                    upperCentralRow.setHeight("50%");
                }

            }
        });


        windowCreatorButton.setText("Add");
        windowSelectorLayout.add(windowCreatorButton);

        windowSelectorLayout.setAlignItems(Alignment.START);


        windowSelectorLayout.setVerticalComponentAlignment(Alignment.END, windowCreatorButton);

//        setWindowScenario(scenario, windowEditor);

        Button windowButton = new Button();

        VerticalLayout rightColumn = new VerticalLayout();


        TabSheet tabSheetUpperRight = new TabSheet();
        TabSheet tabSheetBottomRight = new TabSheet();



        Map<String,Grid> resultGrids = new HashMap<>();
        Grid<String> emptyResultGrid = getGridOutputWindowedGrid();

        tabSheetBottomRight.add("Results", emptyResultGrid);



        Map<String,NetworkDiagram> graphs = new HashMap<>();

        NetworkDiagram snapshotGraphSolo =
                new NetworkDiagram(Options.builder().withWidth("100%").withHeight("100%")
                        .withInteraction(Interaction.builder().withMultiselect(true).build()).build());
        tabSheetUpperRight.add("Window State", snapshotGraphSolo);


        windowButton.setText("Register Windows");
        bottomRow.setAlignSelf(Alignment.CENTER, windowButton);
        windowButton.setWidth("min-content");
        windowButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        queryButton.setText("Query");
        windowButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                try {
                    Notification.show("Registered Windows.").setPosition(Notification.Position.TOP_START);

                    boolean hasNameWithoutNumber = windowRowSummaries.stream()
                            .anyMatch(windowRowSummary -> !windowRowSummary.getName().matches(".*\\d.*"));

                    if (hasNameWithoutNumber) {
                        // Handle the case where a name without a number is found
                        HashMap<String, Integer> windowCounter = new HashMap<>();

                        //TODO: Bug resolution, if not in summary tab, does not start, due to the absence of the number in the window name
                        for (WindowRowSummary windowRowSummary : windowRowSummaries) {
                            String windowType = windowRowSummary.getName().replaceAll("\\d", "");

                            windowCounter.putIfAbsent(windowType, 1);
                            windowRowSummary.setName(windowType+windowCounter.get(windowType));
                            windowCounter.put(windowType, windowCounter.get(windowType)+1);
                        }
                    }

                    basicGrid.setItems(inputGridListActual);

                    //For API issues, if the query is a "select *" we substitute the "*" with all the fields..
                    String attributes = String.join(", ", ((List<Grid.Column<?>>) (List<?>) basicGrid.getColumns()).stream()
                            .map(c->c.getKey())
                            .collect(Collectors.toList()));

                    //Replace the "*" with the attributes

                    queryEditorText.setValue(queryEditorText.getValue().replaceFirst("(?i)select\\s*\\*", "SELECT " + attributes));

                    polyflowService.register(scenario, queryEditorText.getValue(), windowRowSummaries);

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

                        //get the column names of the fields selected by the user in the query
                        List<String> resultColumns = extractSelectFields(queryEditorText.getValue());
                        Tab tabRes = new Tab(windowRowSummary.name+" Results");

                        //Create the result section with column names equal to the ones written by the user in the query
                        Grid<Map<String, Object>> resultGrid = new Grid<>();
                        List<Map<String, Object>> items = new ArrayList<>();
                        resultGrid.setWidth("100%");
                        resultGrid.setHeight("100%");
                        resultGrid.getStyle().set("flex-grow", "1");
                        //Add the columns with no value to the result Tab
                        Map<String, Object> row = new LinkedHashMap<>();


                        for(String col : resultColumns){
                            row.put(col, "");
                        }
                        items.add(row);

                        //  determine column names
                        if (!items.isEmpty()) {
                            for (String columnKey : items.get(0).keySet()) {
                                resultGrid.addColumn(r -> String.valueOf(r.get(columnKey)))
                                        .setHeader(columnKey);
                            }
                        }
                        resultGrid.setItems(items);

                        tabSheetBottomRight.add(tabRes, resultGrid);
                        resultGrids.put(windowRowSummary.name, resultGrid);

                        Tab tab = new Tab(windowRowSummary.name+" State");
                        NetworkDiagram snapshotGraphSoloLocal =
                                new NetworkDiagram(Options.builder().withWidth("100%").withHeight("100%")
                                        .withInteraction(Interaction.builder().withMultiselect(true).build()).build());
                        tabSheetUpperRight.add(tab, snapshotGraphSoloLocal);
                        graphs.put(windowRowSummary.name, snapshotGraphSoloLocal);


                    }

                    Tab tab = new Tab("All");
                    NetworkDiagram snapshotAllGraph =
                            new NetworkDiagram(Options.builder().withWidth("100%").withHeight("100%")
                                    .withInteraction(Interaction.builder().withMultiselect(true).build()).build());
                    tabSheetUpperRight.add(tab, snapshotAllGraph);
                    graphs.put("All", snapshotAllGraph);



                    colorGraphs = new HashMap<>();

                    int i=0;
                    for (String windowName : graphs.keySet()) {
                        if (!windowName.equals("All"))
                            colorGraphs.put(windowName, COLORS[i++]);
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
                Notification.show("Inserting Next Event.").setPosition(Notification.Position.TOP_START);

                if (counterInput!=0){
                    Tuple prevGridInput = inputGridListActual.get(counterInput-1);
                    prevGridInput.setCursor("");
                }

                Tuple gridInput = inputGridList.get(counterInput++);
                gridInput.setCursor(">");
                inputGridListActual.add(gridInput);
                basicGrid.getDataProvider().refreshAll();
                polyflowService.nextEvent(gridInput);


                //get the query result
                TuplesOrResult nextOutput = polyflowService.getNextOutput();


                for (String windowName : resultGrids.keySet()) {
                    if (!windowName.contains("Mapping")) {
                        Grid<Map<String, Object>> resultGrid = resultGrids.get(windowName);
                        List<Map<String, Object>> items = new ArrayList<>();

                        List<String> resultColumns = extractSelectFields(queryEditorText.getValue());

                        Map<String, List<List>> resultContainer = nextOutput.getResultContainer();


                        if(resultContainer.containsKey(windowName)) {
                            //Iterate all the Rows in the result
                            for (int i = 0; i < resultContainer.get(windowName).size(); i++) {
                                //For each row, iterate on the columns
                                Map<String, Object> row = new LinkedHashMap<>();
                                for (int j = 0; j < resultColumns.size(); j++) {
                                    row.put(resultColumns.get(j), resultContainer.get(windowName).get(i).get(j));
                                }
                                items.add(row);
                            }
                        }


                        resultGrid.setItems(items);

                        resultGrid.getDataProvider().refreshAll();
                    }
                }

                //Get the windows operators
                List<StreamToRelationOperator<Tuple, Tuple, TuplesOrResult>> s2rList = polyflowService.getStreamToRelationOperatorList();

                //history of the window content to draw in the graph
                windowHistory = new ArrayList<>();
                for(StreamToRelationOperator<Tuple, Tuple, TuplesOrResult> s2r : s2rList){
                    TimeVaryingTuplesOrResult tvg = (TimeVaryingTuplesOrResult)s2r.get();

                    //materialize the history of elements of the current window
                    tvg.materialize_history(s2r.time().getAppTime());

                    //Add everything to the history variable
                    windowHistory.addAll(tvg.getHistory().getWindowContent());
                }

                for (String windowName : graphs.keySet()) {
                    NetworkDiagram diagram = graphs.get(windowName);


                    updateWindowState(diagram, windowHistory.stream().filter(el -> el.getOperatorId()
                            .equals((tabSheetUpperRight.getTab(diagram)).getLabel().split(" ")[0]))
                            .sorted(new Comparator<Tuple>() {
                                @Override
                                public int compare(Tuple o1, Tuple o2) {
                                    return Integer.compare(findFirstNumberInIntervalId(o1.getIntervalId()), findFirstNumberInIntervalId(o2.getIntervalId()));
                                }
                            })
                            .collect(Collectors.toList()), colorGraphs, false);


                    diagram.addSelectNodeListener(event -> {


                        Optional<String> mappingKey = resultGrids.keySet().stream()
                                .filter(key -> key.contains("Mapping"))
                                .findFirst();

                        if (mappingKey.isPresent()) {
                            String keyWithMapping = mappingKey.get();
                            tabSheetBottomRight.remove(resultGrids.get(keyWithMapping));
                            resultGrids.remove(keyWithMapping);
                            // Handle the key with "Mapping" in the name
                        }

                        String nodeId = "Selected Mappings";



                        if (!resultGrids.containsKey(nodeId)) {
                            Tab tabRes = new Tab(nodeId);
                            Grid<GridOutputMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);

                            tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                            resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                        } else {
                            Tab tabRes = new Tab(nodeId);
                            Grid<GridOutputMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);
                            tabSheetBottomRight.remove(resultGrids.get(nodeId));
                            tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                            resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                        }

                    });



                }

                NetworkDiagram diagramAll = graphs.get("All");

                updateWindowState(diagramAll, windowHistory.stream()
                        .sorted(new Comparator<Tuple>() {
                            @Override
                            public int compare(Tuple o1, Tuple o2) {
                                return Integer.compare(findFirstNumberInIntervalId(o1.getIntervalId()), findFirstNumberInIntervalId(o2.getIntervalId()));
                            }
                        })
                        .collect(Collectors.toList()), colorGraphs, true);


                diagramAll.addSelectNodeListener(event -> {

                    Optional<String> mappingKey = resultGrids.keySet().stream()
                            .filter(key -> key.contains("Mapping"))
                            .findFirst();

                    if (mappingKey.isPresent()) {
                        String keyWithMapping = mappingKey.get();
                        tabSheetBottomRight.remove(resultGrids.get(keyWithMapping));
                        resultGrids.remove(keyWithMapping);
                        // Handle the key with "Mapping" in the name
                    }

                    String nodeId = "Selected Mappings";



                    if (!resultGrids.containsKey(nodeId)) {
                        Tab tabRes = new Tab(nodeId);
                        Grid<GridOutputMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);

                        tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                        resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                    } else {
                        Tab tabRes = new Tab(nodeId);
                        Grid<GridOutputMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);
                        tabSheetBottomRight.remove(resultGrids.get(nodeId));
                        tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                        resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                    }

                });


            } else {
                Notification.show("A query must be submitted first.").setPosition(Notification.Position.TOP_START);
            }
        });

        Button buttonPrimary2 = new Button();
        Button buttonPrimary3 = new Button();

        Button buttonPrimary7 = new Button();
        H6 h6 = new H6();
        getContent().addClassName(Padding.XSMALL);
        getContent().setWidthFull();
        getContent().setHeight("90%");
        getContent().getStyle().set("flex-grow", "1");
        mainRow.setWidthFull();
        getContent().setFlexGrow(1.0, mainRow);
        mainRow.addClassName(Gap.MEDIUM);
        mainRow.setWidthFull();
        mainRow.setHeight("100%");
        leftColumn.setHeightFull();
        mainRow.setFlexGrow(1.0, leftColumn);
        leftColumn.addClassName(Gap.XSMALL);
        leftColumn.addClassName(Padding.XSMALL);
        leftColumn.setWidth("25%");
        leftColumn.setHeight("95%");
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
        centralColumn.setWidth("35%");
        centralColumn.setHeight("95%");
        upperCentralRow.setWidthFull();
        centralColumn.setFlexGrow(1.0, upperCentralRow);
        upperCentralRow.addClassName(Gap.SMALL);
        upperCentralRow.setWidth("100%");
        upperCentralRow.setHeight("50%");
        upperCentralRow.setAlignItems(Alignment.START);
        upperCentralRow.setJustifyContentMode(JustifyContentMode.CENTER);
        upperCentralTabSheet.setWidth("100%");
        upperCentralTabSheet.setHeight("50%");
        upperCentralTabSheet.getStyle().set("flex-grow", "0");

        bottomCentralRow.setWidthFull();
        centralColumn.setFlexGrow(1.0, bottomCentralRow);
        bottomCentralRow.setPadding(false);
        bottomCentralRow.setHeight("50%");

        rightColumn.setHeightFull();
        mainRow.setFlexGrow(1.0, rightColumn);
        rightColumn.addClassName(Padding.XSMALL);
        rightColumn.setWidth("40%");
        rightColumn.setHeight("95%");
        tabSheetBottomRight.setWidth("90%");
        tabSheetBottomRight.setHeight("100%");
        tabSheetUpperRight.setWidth("100%");
        tabSheetUpperRight.setHeight("100%");
        tabSheet3.setWidth("100%");
        tabSheet3.setHeight("100%");


        bottomRow.setWidthFull();
        getContent().setFlexGrow(1.0, bottomRow);
        bottomRow.addClassName(Gap.MEDIUM);
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
        horizontalLayout.add(verticalLayout);
        leftColumn.add(horizontalLayout);




        mainRow.add(centralColumn);
        centralColumn.add(upperCentralRow);
        centralColumn.add(bottomCentralRow);
        upperCentralRow.add(tabSheet3);
        mainRow.add(rightColumn);


        HorizontalLayout upperRightHorizontalLayout = new HorizontalLayout();
        HorizontalLayout bottomRightHorizontalLayout = new HorizontalLayout();
        upperRightHorizontalLayout.add(tabSheetUpperRight);
        bottomRightHorizontalLayout.add(tabSheetBottomRight);



        StreamResource streamResource1 = new StreamResource("time_dir.png",
                () -> getClass().getResourceAsStream("/time_dir.png"));
        Image cdcd1 = new Image(streamResource1, "cdcd1");
        VerticalLayout verticalBottomRightLayout = new VerticalLayout();

        verticalBottomRightLayout.setWidth("10%");
        verticalBottomRightLayout.setHeightFull();


        verticalBottomRightLayout.add(new Text("time"));
        verticalBottomRightLayout.add(cdcd1);
        cdcd1.setWidth("40%");
        cdcd1.setHeight("90%");

        rightColumn.add(upperRightHorizontalLayout);
        upperRightHorizontalLayout.setWidthFull();
        upperRightHorizontalLayout.setHeight("60%");

        bottomRightHorizontalLayout.add(verticalBottomRightLayout);
        rightColumn.add(bottomRightHorizontalLayout);
        bottomRightHorizontalLayout.setWidthFull();
        bottomRightHorizontalLayout.setHeight("40%");


        getContent().add(bottomRow);
        bottomRow.add(buttonNext);
        bottomRow.add(buttonPrimary3);
        bottomRow.add(windowButton);
        bottomRow.add(buttonPrimary7);
        bottomRow.add(h6);
    }

    private static void getResumeNameAndParams(String windowTypeValue, StringBuilder text, WindowRowSummary windowRowSummary, List<Result> result, int index) {

        windowRowSummary.setName(getWindowAbbrev(windowTypeValue));
        text.append(windowTypeValue);

        if (windowTypeValue.startsWith("Frames")){
            if (windowTypeValue.equals("Frames:Aggregate")) {
                text.append(" on aggregate ").append(result.get(index).selectAggregate().getValue());
                windowRowSummary.setAttribute(result.get(index).selectAggregate().getValue() + "(" + result.get(index).selectAttribute().getValue() + ")");
                windowRowSummary.setAggregateFunction(result.get(index).selectAggregate.getValue());
            }
            else {
                windowRowSummary.setAttribute(result.get(index).selectAttribute().getValue());
                windowRowSummary.setAggregateFunction("");
            }
            text.append(" over Attribute ").append(result.get(index).selectAttribute().getValue()).append(" ").append(result.get(index).selectOp().getValue()).append(" ").append(result.get(index).threshold().getValue());
            windowRowSummary.setOperator(result.get(index).selectOp().getValue());
            windowRowSummary.setRange(Long.parseLong(result.get(index).threshold().getValue()));
        } else if (windowTypeValue.equals("Time-based")){
            text.append(" with Size ").append(result.get(index).size().getValue()).append(" and Slide ").append(result.get(index).slide().getValue());
            windowRowSummary.setSize(Long.parseLong(result.get(index).size().getValue()));
            windowRowSummary.setSlide(Long.parseLong(result.get(index).slide().getValue()));
        } else if (windowTypeValue.equals("Session")){
            text.append(" with Timeout ").append(result.get(index).timeout().getValue());
            windowRowSummary.setTimeout(Long.parseLong(result.get(index).timeout().getValue()));
        } else if (windowTypeValue.equals("Composite-Union")){

            Result result1 = result.get(index+1);
            Result result2 = result.get(index+2);

            text.append(" of ");

            getResumeNameAndParams(result1.selectWindowType.getValue(), text, windowRowSummary.getCompositeInternalWindow1(), result, index+1);
            text.append(" and ");
            getResumeNameAndParams(result2.selectWindowType.getValue(), text, windowRowSummary.getCompositeInternalWindow2(), result, index+2);

        } else if (windowTypeValue.equals("Composite-Intersection")){
            Result result1 = result.get(index+1);
            Result result2 = result.get(index+2);

            text.append(" of ");

            getResumeNameAndParams(result1.selectWindowType.getValue(), text, windowRowSummary.getCompositeInternalWindow1(), result, index+1);
            text.append(" and ");
            getResumeNameAndParams(result2.selectWindowType.getValue(), text, windowRowSummary.getCompositeInternalWindow2(), result, index+2);
        }
    }

    private List<Result> getResult(HorizontalLayout windowSelectorLayout, HorizontalLayout compositeUnionLayout1, HorizontalLayout compositeUnionLayout2) {
        ComboBox<String> selectWindowType = new ComboBox<>();


        selectWindowType.setLabel("Window Type");
        selectWindowType.setItems("Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate");
        selectWindowType.setValue("Time-based");


        TextField threshold = new TextField();
        threshold.setLabel("Threshold");

        ComboBox<String> selectAggregate = new ComboBox<>();
        selectAggregate.setLabel("Aggregate");

        selectAggregate.setItems("sum", "avg");
        selectAggregate.setValue("sum");
        selectAggregate.addValueChangeListener(event -> {
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
        });

        ComboBox<String> selectOp = new ComboBox<>();
        selectOp.setLabel("Comparator");

        selectOp.setItems("<", ">", "=", ">=", "<=");
        selectOp.setValue(">");
        selectOp.addValueChangeListener(event -> {
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
        });

        ComboBox<String> selectAttribute = new ComboBox<>();
        selectAttribute.setLabel("On Attribute");


        //These are the columns shown on the dropdown menu "On Attribute"
        selectAttribute.setItems(columnsToShowForAggregation);
        selectAttribute.setValue(columnsToShowForAggregation.get(0));
        selectAttribute.addValueChangeListener(event -> {
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
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



            if (event.getValue().equals("Composite-Union") || event.getValue().equals("Composite-Intersection")) {

                compositeUnionLayout1.setVisible(true);
                compositeUnionLayout2.setVisible(true);
            } else if (event.getOldValue().equals("Composite-Union") || event.getOldValue().equals("Composite-Intersection")) {
                compositeUnionLayout1.setVisible(false);
                compositeUnionLayout2.setVisible(false);
            }
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
        });

        Result result1 = getResultNotComposite(compositeUnionLayout1);
        Result result2 = getResultNotComposite(compositeUnionLayout2);
        Result result = new Result(selectWindowType, threshold, selectAggregate, selectOp, selectAttribute, size, slide, timeout);

        windowSelectorLayout.add(result.selectWindowType(), result.size(), result.slide());
        return Arrays.asList(result, result1, result2);
    }

    private Result getResultNotComposite(HorizontalLayout windowSelectorLayout) {
        ComboBox<String> selectWindowType = new ComboBox<>();
        selectWindowType.setLabel("Window Type");
        selectWindowType.setItems("Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate");
        selectWindowType.setValue("Time-based");


        TextField threshold = new TextField();
        threshold.setLabel("Threshold");

        ComboBox<String> selectAggregate = new ComboBox<>();
        selectAggregate.setLabel("Aggregate");

        selectAggregate.setItems("sum", "avg");
        selectAggregate.setValue("sum");
        selectAggregate.addValueChangeListener(event -> {
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
        });

        ComboBox<String> selectOp = new ComboBox<>();
        selectOp.setLabel("Comparator");

        selectOp.setItems("<", ">", "=", ">=", "<=");
        selectOp.setValue("<");
        selectOp.addValueChangeListener(event -> {
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
        });

        ComboBox<String> selectAttribute = new ComboBox<>();
        selectAttribute.setLabel("On Attribute");

        Field[] fields = sampleInputClass.getDeclaredFields();

        List<String> collect = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
        selectAttribute.setItems(collect);
        selectAttribute.setValue(collect.get(0));
        selectAttribute.addValueChangeListener(event -> {
            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
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


            Notification.show(event.getValue()).setPosition(Notification.Position.TOP_START);
        });


        Result result = new Result(selectWindowType, threshold, selectAggregate, selectOp, selectAttribute, size, slide, timeout);
        windowSelectorLayout.add(result.selectWindowType(), result.size(), result.slide());

        return result;
    }

    private record Result(ComboBox<String> selectWindowType, TextField threshold, ComboBox<String> selectAggregate, ComboBox<String> selectOp, ComboBox<String> selectAttribute, TextField size, TextField slide, TextField timeout) {
    }

    private Grid<GridOutputMapping> resetMappingTab(SelectNodeEvent event, List<WindowRowSummary> windowRowSummaries) {
        Map<String, GridOutputMapping> recordMappings = new HashMap<>();

        JsonArray nodes = event.getParams().getArray("nodes");

        for (int i = 0; i < nodes.length(); i++) {
            int finalI = i;
            if (nodes.getString(finalI).contains("r_")) {
                windowHistory.stream()
                        .filter(predicate -> {
                            String string = nodes.getString(finalI);
                            string = string.contains("@") ? string.split("@")[1] : string;
                            return predicate.getRecordId().equals(string);
                        })
                        .forEach(consumer -> {
                            if (recordMappings.containsKey(consumer.getRecordId())) {
                                recordMappings.get(consumer.getRecordId()).add(consumer.getOperatorId(), consumer.getIntervalId());
                            } else {
                                GridOutputMapping g = new GridOutputMapping();
                                g.setRecordId(consumer.getRecordId());
                                g.add(consumer.getOperatorId(), consumer.getIntervalId());
                                recordMappings.put(consumer.getRecordId(), g);
                            }
                        });
            }
        }


        Grid<GridOutputMapping> resultGrid = getGridOutputWindowedMapping(windowRowSummaries.stream().map(wrs -> wrs.getName()).collect(Collectors.toList()));
        resultGrid.setItems(recordMappings.values().stream().sorted(new Comparator<GridOutputMapping>() {
            @Override
            public int compare(GridOutputMapping o1, GridOutputMapping o2) {
                String o1Id = o1.recordId;
                String o2Id = o2.recordId;

                Integer id1 = Integer.parseInt(o1Id.split("_")[1]);
                Integer id2 = Integer.parseInt(o2Id.split("_")[1]);
                return Integer.compare(id1, id2);
            }
        }).collect(Collectors.toUnmodifiableList()));

        return resultGrid;
    }

    private Map<String,String> getDefaultQueries(String scenario) {
        Map<String, String> queries = new HashMap<>();
        if(scenario.equals("Electric Grid"))
            queries.put("Query 1", "SELECT *\nFROM [window]\nWHERE cons_A >= 10 AND cons_B >= 5");
        else if(scenario.equals("Nexmark"))
            queries.put("Query 1", "SELECT *\nFROM [window]\nWHERE price > 20");
        else if(scenario.equals("Linear Road"))
            queries.put("Query 1", "SELECT *\nFROM [window]\nWHERE speed > 15");
        else if(scenario.equals("NYC Taxi (DEBS 2015)"))
            queries.put("Query 1", "SELECT *\nFROM [window]\nWHERE tolls_amount < 10");

        return queries;
    }

    public int findFirstNumberInIntervalId(String intervalId) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(intervalId);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0; // No number found
    }


    private Grid<String> getGridOutputWindowedGrid() {
        Grid<String> grid = new Grid<>();

        // Define at least one column
        grid.addColumn(item -> item).setHeader("Value");

        // No items (empty)
        grid.setItems(Collections.emptyList());

        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.getStyle().set("flex-grow", "1");

        return grid;
    }

    private static Grid<GridOutputMapping> getGridOutputWindowedMapping(Collection<String> operatorsNames) {
        Grid<GridOutputMapping> inputAnnotatedGrid = new Grid<>(GridOutputMapping.class, false);


        inputAnnotatedGrid.addColumn(new ValueProvider<GridOutputMapping, String>() {
            @Override
            public String apply(GridOutputMapping gridOutputWindowedMapping) {
                return gridOutputWindowedMapping.getRecordId();
            }
        }).setHeader("Record Id");



        for (String op: operatorsNames){
            inputAnnotatedGrid.addColumn(new ValueProvider<GridOutputMapping, String>() {
                @Override
                public String apply(GridOutputMapping gridOutputWindowedMapping) {
                    StringBuilder intervalIds = new StringBuilder();
                    String[] split = gridOutputWindowedMapping.opTointervalIds.split(";");
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].split("=")[0].equals(op))
                            intervalIds.append(split[i].split("=")[1].replace("Window ", "")).append("|");
                    }
                    if (!intervalIds.isEmpty())
                        intervalIds.deleteCharAt(intervalIds.length() - 1);

                    return intervalIds.toString();
                }
            }).setHeader(op);
        }

        inputAnnotatedGrid.setWidth("100%");
        inputAnnotatedGrid.setHeight("100%");
        inputAnnotatedGrid.getStyle().set("flex-grow", "1");
        return inputAnnotatedGrid;
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



    public class WindowRowSummary {
        private String name;
        private String attribute;
        private String aggregateFunction;
        private String operator;
        private long range;
        private long size;
        private long slide;
        private long timeout;
        private String query;

        private WindowRowSummary compositeInternalWindow1;
        private WindowRowSummary compositeInternalWindow2;

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

        public void setAggregateFunction(String aggregateFunction){
            this.aggregateFunction = aggregateFunction;
        }
        public String getAggregateFunction(){
            return aggregateFunction;
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

        public WindowRowSummary getCompositeInternalWindow1() {
            return compositeInternalWindow1;
        }

        public void setCompositeInternalWindow1(WindowRowSummary compositeInternalWindow1) {
            this.compositeInternalWindow1 = compositeInternalWindow1;
        }

        public WindowRowSummary getCompositeInternalWindow2() {
            return compositeInternalWindow2;
        }

        public void setCompositeInternalWindow2(WindowRowSummary compositeInternalWindow2) {
            this.compositeInternalWindow2 = compositeInternalWindow2;
        }

        public String getQuery() {
            if (this.query == null) {
                this.query = "SELECT *\n" +
                        "FROM <window>";
            }
            if (this.name.contains("TW"))
                this.query = query.replaceAll("<window>", "HOP(TABLE Stream, DESCRIPTOR(ts), INTERVAL '" + size + "' hours, INTERVAL '" + slide + "' hours)");
            else if (this.name.contains("SW"))
                this.query = this.query.replaceAll("<window>", "SESSION(TABLE Stream, DESCRIPTOR(ts), INTERVAL '" + timeout + "' hours)");
            else if (this.name.contains("F"))
                this.query = this.query.replaceAll("<window>", "FRAMES(TABLE Stream, DESCRIPTOR(ts), " + attribute + " " + operator + " " + range+")");
            else if (this.name.contains("Union"))
                this.query = this.query.replaceAll("<window>", "UNION");
            else if (this.name.contains("Int"))
                this.query = this.query.replaceAll("<window>", "INTERSECTION");

            return query;
        }

        @Override
        public String toString() {
            StringBuilder text = new StringBuilder();
            text.append(this.name);
            if (this.name.contains("F")){
                text.append("Frame over Aggregate/Attribute ").append(this.attribute).append(" ").append(this.operator).append(" ").append(this.range);
            } else if (this.name.contains("TW")){
                text.append("Time Window with Size ").append(this.size).append(" and Slide ").append(this.slide);
            } else if (this.name.contains("SW")){
                text.append("Session Window with Timeout ").append(this.timeout);
            } return text.toString();
        }

        public void setQuery(String query){
            this.query = query;
        }
    }



    public InputElectricity createElectricGridRecord(String recordId, long ts, long consA, long consB){
        InputElectricity gridInput = new InputElectricity();
        gridInput.setRecordId(recordId);
        gridInput.setTimestamp(ts);
        gridInput.setCons_A((long) consA);
        gridInput.setCons_B((long) consB);
        return gridInput;
    }

    public InputBid createNexmarkBidRecord(String recordId, long auction, long bidder, long price, String channel,  long timestamp){
        InputBid inputBid = new InputBid();
        inputBid.setAuction(auction);
        inputBid.setBidder(bidder);
        inputBid.setRecordId(recordId);
        inputBid.setPrice(price);
        inputBid.setChannel(channel);
        inputBid.setTimestamp(timestamp);
        return inputBid;
    }

    public InputLinearRoad createLinearRoad(String recordId, long ts, int car_id, double speed, int exp_way, int lane, int direction, double x_pos){
        InputLinearRoad inputLinearRoad = new InputLinearRoad();
        inputLinearRoad.setRecordId(recordId);
        inputLinearRoad.setTimestamp(ts);
        inputLinearRoad.setCar_id(car_id);
        inputLinearRoad.setSpeed(speed);
        inputLinearRoad.setExp_way(exp_way);
        inputLinearRoad.setDirection(direction);
        inputLinearRoad.setLane(lane);
        inputLinearRoad.setX_pos(x_pos);
        return inputLinearRoad;
    }

    public InputTaxi createNycTaxi(String[] fields){ //We parsed the whole nyx file, so we have a lot of columns.. we only use a few
        InputTaxi input = new InputTaxi();
        input.setRecordId(fields[0]);
        input.setTimestamp(Long.parseLong(fields[1]));
        input.setPickup_datetime(Long.parseLong(fields[4]));
        input.setTrip_distance(Double.parseDouble(fields[7]));
        input.setPayment_type(fields[12]);
        input.setTolls_amount(Double.parseDouble(fields[17]));
        input.setTotal_amount(Double.parseDouble(fields[18]));
        return input;
    }



    private void setGridSampleSimpleData(Grid grid, String scenario) {
        if (scenario.equals("Electric Grid")){
            inputGridList = new ArrayList<>();
            inputGridListActual = new ArrayList<>();

            File file = new File(PlayToWin.class.getResource("/electricity_events.txt").getPath());
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            }catch(FileNotFoundException e){
            }
            while(scanner.hasNext()){
                String input = scanner.nextLine();
                String[] columns = input.split(",");
                inputGridList.add(createElectricGridRecord(columns[0], Long.parseLong(columns[1]), Long.parseLong(columns[2]), Long.parseLong(columns[3])));
            }

            //Remove columns that are not relevant in the input stream columns
            for (String s : columnsToRemoveForStream){
                grid.removeColumn(grid.getColumnByKey(s));
            }


            List<Grid.Column> strings = Arrays.asList(grid.getColumnByKey("recordId"),
                    grid.getColumnByKey("timestamp"), grid.getColumnByKey("cons_A"),
                    grid.getColumnByKey("cons_B"));
            grid.setColumnOrder(strings);


            grid.setItems(inputGridList);
        }


        else if (scenario.equals("Nexmark")){
            inputGridList = new ArrayList<>();
            inputGridListActual = new ArrayList<>();

            File file = new File(PlayToWin.class.getResource("/nexmark_events.txt").getPath());
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            }catch(FileNotFoundException e){
            }

            int rowCounter = 1;
            while(scanner.hasNext()){
                String[] columns = parseNexmarkBid(scanner.nextLine(), rowCounter);
                rowCounter+=1;
                inputGridList.add(createNexmarkBidRecord(columns[0], Long.parseLong(columns[1]), Long.parseLong(columns[2]), Long.parseLong(columns[3]), columns[4], Long.parseLong(columns[5])));
            }

            //Remove columns that are not relevant in the input stream columns
            for (String s : columnsToRemoveForStream){
                grid.removeColumn(grid.getColumnByKey(s));
            }


            List<Grid.Column> strings = Arrays.asList(grid.getColumnByKey("recordId"),
                    grid.getColumnByKey("timestamp"), grid.getColumnByKey("auction"),
                    grid.getColumnByKey("bidder"), grid.getColumnByKey("channel"),
                    grid.getColumnByKey("price"));
            grid.setColumnOrder(strings);


            grid.setItems(inputGridList);
        }

        else if(scenario.equals("Linear Road")){
            inputGridList = new ArrayList<>();
            inputGridListActual = new ArrayList<>();

            File file = new File(PlayToWin.class.getResource("/linear_road_events.txt").getPath());
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            }catch(FileNotFoundException e){
            }

            while(scanner.hasNext()){
                String input = scanner.nextLine();
                String[] columns = input.split(",");
                inputGridList.add(createLinearRoad(columns[0], Long.parseLong(columns[1]), Integer.parseInt(columns[2]), Double.parseDouble(columns[3]), Integer.parseInt(columns[4]),
                        Integer.parseInt(columns[5]), Integer.parseInt(columns[6]), Double.parseDouble(columns[7])));
            }

            //Remove columns that are not relevant in the input stream columns
            for (String s : columnsToRemoveForStream){
                grid.removeColumn(grid.getColumnByKey(s));
            }


            List<Grid.Column> strings = Arrays.asList(grid.getColumnByKey("recordId"),
                    grid.getColumnByKey("timestamp"), grid.getColumnByKey("car_id"),
                    grid.getColumnByKey("speed"), grid.getColumnByKey("exp_way"),
                    grid.getColumnByKey("lane"), grid.getColumnByKey("direction"),
                    grid.getColumnByKey("x_pos"));
            grid.setColumnOrder(strings);


            grid.setItems(inputGridList);

        }
        else if(scenario.equals("NYC Taxi (DEBS 2015)")){
            inputGridList = new ArrayList<>();
            inputGridListActual = new ArrayList<>();

            File file = new File(PlayToWin.class.getResource("/NYC_taxi_events.csv").getPath());
            Scanner scanner = null;
            try {
                scanner = new Scanner(file);
            }catch(FileNotFoundException e){
            }

            int rowCounter = 1;
            while(scanner.hasNext()){
                String[] columns = parseNycTaxi(scanner.nextLine(), rowCounter);
                rowCounter+=1;
                inputGridList.add(createNycTaxi(columns));
            }

            //Remove columns that are not relevant in the input stream columns
            for (String s : columnsToRemoveForStream){
                grid.removeColumn(grid.getColumnByKey(s));
            }


            List<Grid.Column> strings = Arrays.asList(grid.getColumnByKey("recordId"),
                    grid.getColumnByKey("timestamp"), grid.getColumnByKey("pickup_datetime"),
                    grid.getColumnByKey("trip_distance"), grid.getColumnByKey("payment_type"),
                    grid.getColumnByKey("tolls_amount"), grid.getColumnByKey("total_amount"));
            grid.setColumnOrder(strings);


            grid.setItems(inputGridList);
        }



        else {
            grid.setItems(query -> sampleGridService.list(
                            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());
            grid.removeColumn(grid.getColumnByKey("id"));
            grid.removeColumn(grid.getColumnByKey("version"));
            grid.removeColumn(grid.getColumnByKey("cursor"));
        }

        for (Object c : grid.getColumns()) {
            Grid.Column col = (Grid.Column) c;
            col.setAutoWidth(true); //This makes the name of the column show entirely instead of being truncated 
        }


    }



    private void updateWindowState(NetworkDiagram snapshotGraph, List<Tuple> results, Map<String, String> colors, boolean all) {

        List<Edge> edges = new ArrayList<>();
        Map<String, Node> nodes = new HashMap<>();

        Map<String, Node> precedentNodesKeyedOperator = new HashMap<>();



        results.forEach(result -> {
            if (!result.getIntervalId().equals("throw")){
                Node eRecord;
                if (!nodes.keySet().contains(result.getRecordId())){
                    eRecord = new Node(result.getRecordId(), result.getRecordId());
                    eRecord.setShape(Shape.diamond);
                    eRecord.setColor("lightblue");
                    eRecord.setLabelHighlightBold(true);
                    nodes.put(result.getRecordId(), eRecord);
                } else eRecord = nodes.get(result.getRecordId());





                Node eWindow;
                if (!nodes.keySet().contains(all ? result.getOperatorId()+"@"+result.getIntervalId() : result.getIntervalId())) {

                    eWindow = all ? new Node(result.getOperatorId()+"@"+result.getIntervalId(), result.getOperatorId()+"@"+result.getIntervalId()) : new Node(result.getIntervalId(), result.getIntervalId());

                    eWindow.setShape(Shape.ellipse);
                    eWindow.setColor(colors.get(result.getOperatorId()));
                    eWindow.setLabelHighlightBold(true);
                    nodes.put(all ? result.getOperatorId()+"@"+result.getIntervalId() : result.getIntervalId(), eWindow);

                    if (precedentNodesKeyedOperator.containsKey(result.getOperatorId())){
                        if (!precedentNodesKeyedOperator.get(result.getOperatorId()).getLabel().equals(all ? result.getOperatorId()+"@"+result.getIntervalId() : result.getIntervalId())){
                            //connect predecessor with current Node (eWindow)
                            Edge e = new Edge(precedentNodesKeyedOperator.get(result.getOperatorId()), eWindow);
                            e.setColor("gray");
                            e.setArrows(new Arrows(new ArrowHead()));
                            e.setLabel(getIntervalRel(result.getOperatorId()));
                            edges.add(e);
                        }
                    }

                    precedentNodesKeyedOperator.put(result.getOperatorId(), eWindow);


                } else eWindow = nodes.get(all ? result.getOperatorId()+"@"+result.getIntervalId() : result.getIntervalId());



                Edge e = new Edge(eRecord, eWindow);
                e.setColor(colors.get(result.getOperatorId()));
                e.setArrows(new Arrows(new ArrowHead()));
                edges.add(e);
            } else if (!all) {
                Node eRecord = new Node(result.getRecordId(), result.getRecordId());
                eRecord.setShape(Shape.square);
                eRecord.setColor("red");
                eRecord.setLabelHighlightBold(true);
                nodes.put(result.getRecordId(), eRecord);
            }

        });



        snapshotGraph.setNodes(nodes.values());
        snapshotGraph.setEdges(edges);
    }

    private String getIntervalRel(String operatorId) {
        if (operatorId.contains("TW")){
            for (WindowRowSummary sumamry: windowRowSummaries){
                if (operatorId.equals(sumamry.getName())){
                    return sumamry.getSize() == sumamry.getSlide() ? "meet" : "overlap";
                }
            }
        } else if (operatorId.contains("SW")){
            return "after";
        } else if (operatorId.contains("FThr")){
            return "after";
        } else if (operatorId.contains("F")){
            return "meet";
        } else return null;

        return null;
    }

    public static List<String> extractSelectFields(String sql) {
        List<String> fields = new ArrayList<>();

        // Regex: capture everything between SELECT and FROM
        Pattern pattern = Pattern.compile("(?i)select\\s+(.*?)\\s+from", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String selectPart = matcher.group(1);

            // Split by commas (simple case, won't handle commas inside functions)
            String[] parts = selectPart.split("\\s*,\\s*");

            for (String part : parts) {
                String trimmed = part.trim();

                // If there's an "AS", keep only the alias
                Matcher asMatcher = Pattern.compile("(?i)\\s+as\\s+([\\w\\d_]+)$").matcher(trimmed);
                if (asMatcher.find()) {
                    fields.add(asMatcher.group(1));
                } else {
                    // No AS clause, but maybe inline alias without AS: e.g., "col alias"
                    String[] tokens = trimmed.split("\\s+");
                    if (tokens.length > 1) {
                        fields.add(tokens[tokens.length - 1]);
                    } else {
                        fields.add(trimmed);
                    }
                }
            }
        }

        return fields;
    }

    public String[] parseNexmarkBid(String line, int rowCounter){
        int firstComma = line.indexOf(",");
        String bidPart = line.substring(firstComma + 1);

        bidPart = bidPart.substring(4, bidPart.length() - 1);

        // Split fields inside Bid{...}
        Map<String, String> fields = new LinkedHashMap<>();
        String[] parts = bidPart.split(", (?=[a-zA-Z]+=)");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            fields.put(kv[0].trim(), kv[1].trim());
        }

        // Build result array in fixed order (6 fields)
        String[] row = new String[6];
        row[0] = "r_" + rowCounter;                 // recordId
        row[1] = fields.get("auction");                    // auction
        row[2] = fields.get("bidder");                     // bidder
        row[3] = fields.get("price");                      // price
        row[4] = fields.get("channel").replace("'", "");   // channel (remove quotes)
        row[5] = String.valueOf(rowCounter);              // replace dateTime with counter
        return row;

    }

    public String[] parseNycTaxi(String line, int rowCounter){

        String[] csvFields = line.split(",", -1); // -1 keeps empty fields

        // Create a new array with 2 extra slots at the beginning
        String[] result = new String[csvFields.length + 2];

        // Insert the extra fields
        result[0] = "r_"+rowCounter;
        result[1] = String.valueOf(rowCounter);

        // Copy the CSV fields into the new array
        System.arraycopy(csvFields, 0, result, 2, csvFields.length);
        Random rand = new Random();
        result[4] = String.valueOf(rand.nextInt(10000)); //Timestamps in datetime can become random integers
        result[17] = String.valueOf(Math.floor(rand.nextDouble(50)*100)/100); //Tolls are always 0 in the input file..

        return result;

    }

}
