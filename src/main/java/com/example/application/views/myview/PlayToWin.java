package com.example.application.views.myview;

import com.example.application.data.*;
import com.example.application.polyflow.datatypes.*;
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
//    private String query = "SELECT percent(consA,consB),percent(consB,consA),ts\n" +
//            "FROM Consumption [RANGE 5 minutes SLIDE 2 minutes]\n" +
//            "WHERE consA >= 0 AND consB >= 0";
    private HorizontalLayout bottomRow;
    private HorizontalLayout upperCentralRow;
    private HorizontalLayout bottomCentralRow;
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
    private List<GridOutputWindowed> actualOutput;
    private List<WindowRowSummary> windowRowSummaries;
    private Map<String, String> colorGraphs;




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
        selectScenarios.setItems("Electric Grid", "Stock (Yahoo)", "Linear Road", "DEBS Challenges '12", "DEBS Challenges '16", "DEBS Challenges '22");
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
                case "Stock (Yahoo)":
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

        ComboBox<String> queryEditor = new ComboBox<>();

        Map<String,String> nexMarkQueries = getNexMarkQueries();
        TextArea queryEditorText = new TextArea();

        queryEditorText.setSizeFull();

        bottomCentralColumn.add(queryEditor);
        bottomCentralColumn.add(queryEditorText);
        queryEditor.setItems(nexMarkQueries.keySet());
        queryEditor.setValue("Query 1");
        queryEditorText.setValue(nexMarkQueries.get("Query 1"));

        queryEditor.addValueChangeListener(event -> {
            String query = nexMarkQueries.get(event.getValue());
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
        Grid<GridOutputWindowed> inputAnnotatedGrid = getGridOutputWindowedGrid();

        tabSheetBottomRight.add("Results", inputAnnotatedGrid);



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

                List<Tuple> nextOutput = polyflowService.getNextOutput();
                actualOutput = nextOutput.stream().map(el->{
                    GridOutputWindowed g = new GridOutputWindowed();
                    g.setIntervalId(el.getIntervalId());
                    g.setOperatorId(el.getOperatorId());
                    g.setTimestamp(el.getTimestamp());
                    g.setRecordId(el.getRecordId());
                    return g;
                }).collect(Collectors.toList());



//                inputAnnotatedGrid.setItems(actualOutput);

                for (String windowName : resultGrids.keySet()) {
                    if (!windowName.contains("Mapping")) {
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
                }


                for (String windowName : graphs.keySet()) {
                    NetworkDiagram diagram = graphs.get(windowName);


                    updateWindowState(diagram, actualOutput.stream().filter(el -> el.getOperatorId()
                            .equals((tabSheetUpperRight.getTab(diagram)).getLabel().split(" ")[0]))
                            .sorted(new Comparator<GridOutputWindowed>() {
                                @Override
                                public int compare(GridOutputWindowed o1, GridOutputWindowed o2) {
                                    return Integer.compare(findFirstNumberInIntervalId(o1.getIntervalId()), findFirstNumberInIntervalId(o2.getIntervalId()));
                                }
                            })
                            .collect(Collectors.toList()), colorGraphs, false);


                    //TODO: removal
//                    diagram.addDeselectNodeListener(event -> {
//                        Optional<String> mappingKey = resultGrids.keySet().stream()
//                                .filter(key -> key.contains("Mapping"))
//                                .findFirst();
//
//                        if (mappingKey.isPresent()) {
//                            String keyWithMapping = mappingKey.get();
//
//                            resultGrids.get(keyWithMapping).
//
//                        }
//                    });

                    diagram.addSelectNodeListener(event -> {
//                        Notification.show("Node selected "+ event.getParams().getArray("nodes").getString(0)).setPosition(Notification.Position.TOP_START);



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
                            Grid<GridOutputWindowedMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);

                            tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                            resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                        } else {
                            Tab tabRes = new Tab(nodeId);
                            Grid<GridOutputWindowedMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);
                            tabSheetBottomRight.remove(resultGrids.get(nodeId));
                            tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                            resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                        }

                    });



                }

                NetworkDiagram diagramAll = graphs.get("All");

                updateWindowState(diagramAll, actualOutput.stream()
                        .sorted(new Comparator<GridOutputWindowed>() {
                            @Override
                            public int compare(GridOutputWindowed o1, GridOutputWindowed o2) {
                                return Integer.compare(findFirstNumberInIntervalId(o1.getIntervalId()), findFirstNumberInIntervalId(o2.getIntervalId()));
                            }
                        })
                        .collect(Collectors.toList()), colorGraphs, true);


                diagramAll.addSelectNodeListener(event -> {
//                    Notification.show("Node selected "+ event.getParams().getArray("nodes").getString(0)).setPosition(Notification.Position.TOP_START);

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
                        Grid<GridOutputWindowedMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);

                        tabSheetBottomRight.add(tabRes, gridOutputWindowedMappingGrid);
                        resultGrids.put(nodeId, gridOutputWindowedMappingGrid);
                    } else {
                        Tab tabRes = new Tab(nodeId);
                        Grid<GridOutputWindowedMapping> gridOutputWindowedMappingGrid = resetMappingTab(event, windowRowSummaries);
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

//        Button queryButton = new Button();
//        Button buttonPrimary6 = new Button();
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
//        leftColumn.setMinWidth("250px");
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




//        upperCentralTabSheet.add("Graph", snapshotGraphSolo);
//        upperCentralTabSheet.add("Polynomials", inputAnnotatedGrid);


        bottomCentralRow.setWidthFull();
        centralColumn.setFlexGrow(1.0, bottomCentralRow);
        bottomCentralRow.setPadding(false);
//        bottomCentralRow.setWidth("100%");
        bottomCentralRow.setHeight("50%");
//        bottomCentralRow.setJustifyContentMode(JustifyContentMode.END);
//        bottomCentralRow.setAlignItems(Alignment.CENTER);


//        bottomCentralRow.setAlignSelf(Alignment.CENTER, queryEditor);
//        queryEditor.setWidth("100%");
//        queryEditor.setHeight("100%");
        rightColumn.setHeightFull();
        mainRow.setFlexGrow(1.0, rightColumn);
        rightColumn.addClassName(Padding.XSMALL);
        rightColumn.setWidth("40%");
//        rightColumn.setMinWidth("80%");
        rightColumn.setHeight("95%");
        tabSheetBottomRight.setWidth("90%");
        tabSheetBottomRight.setHeight("100%");
        tabSheetUpperRight.setWidth("100%");
        tabSheetUpperRight.setHeight("100%");
        tabSheet3.setWidth("100%");
        tabSheet3.setHeight("100%");
//        setTabSheetSampleData(tabSheetUpperRight);



//        mainRow.setWidthFull();
//        mainRow.setHeightFull();




        bottomRow.setWidthFull();
        getContent().setFlexGrow(1.0, bottomRow);
        bottomRow.addClassName(Gap.MEDIUM);
//        bottomRow.addClassName(Padding.XSMALL);
//        bottomRow.setWidth("100%");
//        bottomRow.setHeight("1%");
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
//        buttonPrimary6.setText("LLM");
//        bottomRow.setAlignSelf(Alignment.CENTER, buttonPrimary6);
//        buttonPrimary6.setWidth("min-content");
//        buttonPrimary6.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
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
        centralColumn.add(bottomCentralRow);
//        upperCentralRow.add(upperCentralTabSheet);
//        centralColumn.add(tabSheet3);
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
        cdcd1.setWidth("40%");
        cdcd1.setHeight("90%");

//        upperRightHorizontalLayout.add(verticalUpperRightLayout);
        rightColumn.add(upperRightHorizontalLayout);
        upperRightHorizontalLayout.setWidthFull();
        upperRightHorizontalLayout.setHeight("60%");

        bottomRightHorizontalLayout.add(verticalBottomRightLayout);
        rightColumn.add(bottomRightHorizontalLayout);
        bottomRightHorizontalLayout.setWidthFull();
        bottomRightHorizontalLayout.setHeight("40%");


//        rightColumn.setWidth("25%");
//        rightColumn.setHeight("80%");
        getContent().add(bottomRow);
        bottomRow.add(buttonNext);
//        bottomRow.add(buttonPrimary2);
        bottomRow.add(buttonPrimary3);
        bottomRow.add(windowButton);
//        bottomRow.add(queryButton);
//        bottomRow.add(buttonPrimary6);
        bottomRow.add(buttonPrimary7);
        bottomRow.add(h6);
    }

    private static void getResumeNameAndParams(String windowTypeValue, StringBuilder text, WindowRowSummary windowRowSummary, List<Result> result, int index) {

        windowRowSummary.setName(getWindowAbbrev(windowTypeValue));
        text.append(windowTypeValue);
        if (windowTypeValue.equals("Frames:Aggregate")) {
            text.append(" on aggregate ").append(result.get(index).selectAggregate().getValue());
            windowRowSummary.setAttribute(result.get(index).selectAggregate().getValue()+"("+ result.get(index).selectAttribute().getValue()+")");
        }
        if (windowTypeValue.startsWith("Frames")){
            text.append(" over Attribute ").append(result.get(index).selectAttribute().getValue()).append(" ").append(result.get(index).selectOp().getValue()).append(" ").append(result.get(index).threshold().getValue());
            windowRowSummary.setAttribute(result.get(index).selectAttribute().getValue());
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
        selectWindowType.setItems("Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate",
                "Landmark", "Punctuation-based", "Slide-by-Tuple", "Adaptive Window", "Damped Window", "Tilted Window", "Policy-based",
                "Composite-Union", "Composite-Intersection");
        selectWindowType.setValue("Time-based");


        TextField threshold = new TextField();
        threshold.setLabel("Threshold");

        ComboBox<String> selectAggregate = new ComboBox<>();
        selectAggregate.setLabel("Aggregate");

        selectAggregate.setItems("count", "sum", "avg", "max", "min");
        selectAggregate.setValue("count");
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
        selectWindowType.setItems("Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate", "Time-based", "Session", "Frames:Threshold", "Frames:Delta", "Frames:Aggregate",
                "Landmark", "Punctuation-based", "Slide-by-Tuple", "Adaptive Window", "Damped Window", "Tilted Window", "Policy-based");
        selectWindowType.setValue("Time-based");


        TextField threshold = new TextField();
        threshold.setLabel("Threshold");

        ComboBox<String> selectAggregate = new ComboBox<>();
        selectAggregate.setLabel("Aggregate");

        selectAggregate.setItems("count", "sum", "avg", "max", "min");
        selectAggregate.setValue("count");
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

    private Grid<GridOutputWindowedMapping> resetMappingTab(SelectNodeEvent event, List<WindowRowSummary> windowRowSummaries) {
        Map<String,GridOutputWindowedMapping> recordMappings = new HashMap<>();

        JsonArray nodes = event.getParams().getArray("nodes");

        for (int i = 0; i < nodes.length(); i++) {
            int finalI = i;
            if (nodes.getString(finalI).contains("r_")) {
                actualOutput.stream()
                        .filter(predicate -> {
                            String string = nodes.getString(finalI);
                            string = string.contains("@") ? string.split("@")[1] : string;
                            return predicate.getRecordId().equals(string);
                        })
                        .forEach(consumer -> {
                            if (recordMappings.containsKey(consumer.getRecordId())) {
                                recordMappings.get(consumer.getRecordId()).add(consumer.getOperatorId(), consumer.getIntervalId());
                            } else {
                                GridOutputWindowedMapping g = new GridOutputWindowedMapping();
                                g.setRecordId(consumer.getRecordId());
                                g.add(consumer.getOperatorId(), consumer.getIntervalId());
                                recordMappings.put(consumer.getRecordId(), g);
                            }
                        });
            }
        }


        Grid<GridOutputWindowedMapping> resultGrid = getGridOutputWindowedMapping(windowRowSummaries.stream().map(wrs -> wrs.getName()).collect(Collectors.toList()));
        resultGrid.setItems(recordMappings.values().stream().sorted(new Comparator<GridOutputWindowedMapping>() {
            @Override
            public int compare(GridOutputWindowedMapping o1, GridOutputWindowedMapping o2) {
                String o1Id = o1.recordId;
                String o2Id = o2.recordId;

                Integer id1 = Integer.parseInt(o1Id.split("_")[1]);
                Integer id2 = Integer.parseInt(o2Id.split("_")[1]);
                return Integer.compare(id1, id2);
            }
        }).collect(Collectors.toUnmodifiableList()));

        return resultGrid;
    }

    private Map<String,String> getNexMarkQueries() {
        Map<String, String> queries = new HashMap<>();

        queries.put("Query 1", "SELECT *\nFROM [window]\nWHERE consA >= 0 AND consB >= 0");

        queries.put("Query 2", "SELECT Istream(auction, DOLTOEUR(price), bidder, datetime)\nFROM [window]");

        queries.put("Query 3", "SELECT Rstream(auction, price)\nFROM [window] \nWHERE auction = 1007 OR auction = 1020 OR auction = 2001 OR auction = 2019 OR auction = 2087");

        queries.put("Query 4", "SELECT Istream(P.name, P.city, P.state, A.id)\nFROM Auction A [ROWS UNBOUNDED], Person P [ROWS UNBOUNDED]\nWHERE A.seller = P.id AND (P.state = 'OR' OR P.state = 'ID' OR P.state = 'CA') AND A.category = 10");

        queries.put("Query 5", "SELECT Istream(AVG(Q.final))\nFROM Category C, (SELECT Rstream(MAX(B.price) AS final, A.category)\n                  FROM Auction A [ROWS UNBOUNDED], Bid B [ROWS UNBOUNDED]\n                  WHERE A.id=B.auction AND B.datetime < A.expires AND A.expires < CURRENT_TIME\n                  GROUP BY A.id, A.category) Q\nWHERE Q.category = C.id\nGROUP BY C.id");

        queries.put("Query 6", "SELECT Rstream(auction)\nFROM (SELECT B1.auction, count(*) AS num\n      FROM Bid [RANGE 60 MINUTE SLIDE 1 MINUTE] B1\n      GROUP BY B1.auction)\nWHERE num >= ALL (SELECT count(*)\n                  FROM Bid [RANGE 60 MINUTE SLIDE 1 MINUTE] B2\n                  GROUP BY B2.auction)");

        queries.put("Query 7", "SELECT Istream(AVG(Q.final), Q.seller)\nFROM (SELECT Rstream(MAX(B.price) AS final, A.seller)\n      FROM Auction A [ROWS UNBOUNDED], Bid B [ROWS UNBOUNDED]\n      WHERE A.id=B.auction AND B.datetime < A.expires AND A.expires < CURRENT_TIME\n      GROUP BY A.id, A.seller) [PARTITION BY A.seller ROWS 10] Q\nGROUP BY Q.seller;");

        queries.put("Query 8", "SELECT Rstream(B.auction, B.price, B.bidder)\nFROM Bid [RANGE 1 MINUTE SLIDE 1 MINUTE] B\nWHERE B.price = (SELECT MAX(B1.price)\n                 FROM BID [RANGE 1 MINUTE SLIDE 1 MINUTE] B1);");

        queries.put("Query 9", "SELECT Rstream(P.id, P.name, A.reserve)\nFROM Person [RANGE 12 HOUR] P, Auction [RANGE 12 HOUR] A\nWHERE P.id = A.seller;");

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

    private static Grid<GridOutputWindowedMapping> getGridOutputWindowedMapping(Collection<String> operatorsNames) {
        Grid<GridOutputWindowedMapping> inputAnnotatedGrid = new Grid<>(GridOutputWindowedMapping.class, false);

//        inputAnnotatedGrid.setColumns(operatorsNames.toArray(new String[0]));

        inputAnnotatedGrid.addColumn(new ValueProvider<GridOutputWindowedMapping, String>() {
            @Override
            public String apply(GridOutputWindowedMapping gridOutputWindowedMapping) {
                return gridOutputWindowedMapping.getRecordId();
            }
        }).setHeader("Record Id");



        for (String op: operatorsNames){
            inputAnnotatedGrid.addColumn(new ValueProvider<GridOutputWindowedMapping, String>() {
                @Override
                public String apply(GridOutputWindowedMapping gridOutputWindowedMapping) {
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
//        inputAnnotatedGrid.removeColumn(inputAnnotatedGrid.getColumnByKey("opTointervalIds"));
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

        }else if (scenario.equals("Stock (Yahoo)")){
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
        else if (scenario.equals("Stock (Yahoo)"))
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
            int curr_ts = 7;
            for (int i = 1; i < 20; i++) {
                curr_ts +=random.nextInt(0, 5);
                inputGridList.add(createGridRecord("r_"+(7+i), curr_ts, random.nextInt(0, 11), random.nextInt(0, 11)));
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
        else if (scenario.equals("Stock (Yahoo)")){

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



    private void updateWindowState(NetworkDiagram snapshotGraph, List<GridOutputWindowed> results, Map<String, String> colors, boolean all) {

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



}
