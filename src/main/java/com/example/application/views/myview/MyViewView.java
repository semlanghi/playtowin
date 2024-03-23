package com.example.application.views.myview;

import com.example.application.data.SampleInput;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@PageTitle("My View")
@Route(value = "my-view", layout = MainLayout.class)
@Uses(Icon.class)
public class MyViewView extends Composite<VerticalLayout> {

    public MyViewView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        Grid basicGrid = new Grid(SampleInput.class);
        VerticalLayout layoutColumn3 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        TabSheet tabSheet = new TabSheet();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        TextArea queryEditor = new TextArea();
        VerticalLayout constraintEditor = new VerticalLayout();


        ComboBox<String> selectConstraintType = new ComboBox<>();
        selectConstraintType.setLabel("Constraint Type");
        selectConstraintType.setItems("Primary Key", "Speed Constraint");
        selectConstraintType.setValue("Primary Key");
        selectConstraintType.addValueChangeListener(event -> {
            Notification.show(event.getValue());
        });

        constraintEditor.add(selectConstraintType);

        ComboBox<String> selectAttribute = new ComboBox<>();
        selectAttribute.setLabel("On Attribute");
        Field[] fields = SampleInput.class.getDeclaredFields();
        List<String> collect = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
        selectAttribute.setItems(collect);
        selectAttribute.addValueChangeListener(event -> {
            Notification.show(event.getValue());
        });

        constraintEditor.add(selectAttribute);


        VerticalLayout layoutColumn5 = new VerticalLayout();
        TabSheet tabSheet2 = new TabSheet();
        TabSheet tabSheet3 = new TabSheet();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        Button buttonPrimary = new Button();

        buttonPrimary.addClickListener(buttonClickEvent -> {

            Notification.show("Count is " + buttonClickEvent.getClickCount()git );

        });

        Button buttonPrimary2 = new Button();
        Button buttonPrimary3 = new Button();
        Button buttonPrimary4 = new Button();
        Button buttonPrimary5 = new Button();
        Button buttonPrimary6 = new Button();
        Button buttonPrimary7 = new Button();
        H6 h6 = new H6();
        getContent().addClassName(Padding.XSMALL);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("1200px");
        layoutRow.setHeight("500px");
        layoutColumn2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.addClassName(Gap.XSMALL);
        layoutColumn2.addClassName(Padding.XSMALL);
        layoutColumn2.setWidth("300px");
        layoutColumn2.setMinWidth("250px");
        layoutColumn2.setHeight("500px");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.START);
        layoutColumn2.setAlignItems(Alignment.START);
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, basicGrid);
        basicGrid.setWidth("300px");
        basicGrid.setHeight("500px");
        basicGrid.getStyle().set("flex-grow", "0");
        setGridSampleData(basicGrid);
        layoutColumn3.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setPadding(false);
        layoutColumn3.setWidth("500px");
        layoutColumn3.setHeight("500px");
        layoutRow2.setWidthFull();
        layoutColumn3.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.SMALL);
        layoutRow2.setWidth("500px");
        layoutRow2.setHeight("250px");
        layoutRow2.setAlignItems(Alignment.START);
        layoutRow2.setJustifyContentMode(JustifyContentMode.CENTER);
        tabSheet.setWidth("500px");
        tabSheet.setHeight("200px");
        tabSheet.getStyle().set("flex-grow", "0");
        tabSheet.add("Graph", new Div(new Text("This is the Dashboard tab content")));
        tabSheet.add("Polynomials", new Div(new Text("This is the Dashboard tab content")));

        layoutColumn4.setWidthFull();
        layoutColumn3.setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setPadding(false);
        layoutColumn4.setWidth("100%");
        layoutColumn4.setHeight("250px");
        layoutColumn4.setJustifyContentMode(JustifyContentMode.END);
        layoutColumn4.setAlignItems(Alignment.CENTER);
        layoutColumn4.setAlignSelf(FlexComponent.Alignment.CENTER, queryEditor);
        queryEditor.setWidth("500px");
        queryEditor.setHeight("180px");
        layoutColumn5.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn5);
        layoutColumn5.addClassName(Padding.XSMALL);
        layoutColumn5.setWidth("300px");
        layoutColumn5.setMinWidth("250px");
        layoutColumn5.setHeight("500px");
        tabSheet2.setWidth("300px");
        tabSheet2.setHeight("500px");
        tabSheet3.setWidth("100%");
        tabSheet3.setHeight("100%");
//        setTabSheetSampleData(tabSheet2);

        tabSheet2.add("Results", new Div(new Text("This is the Dashboard tab content")));
        tabSheet2.add("Annotations", new Div(new Text("This is the Dashboard tab content")));
        tabSheet2.add("Quantification", new Div(new Text("This is the Dashboard tab content")));


//        setTabSheetSampleData(tabSheet3);

        tabSheet3.add("Constraints", constraintEditor);
        tabSheet3.add("Query", queryEditor);
        tabSheet3.add("Summary", new Div(new Text("This is the Dashboard tab content")));

        layoutRow3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.addClassName(Padding.XSMALL);
        layoutRow3.setWidth("100%");
        layoutRow3.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("Next");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary);
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("Break Point");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary2);
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary3.setText("Play");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary3);
        buttonPrimary3.setWidth("min-content");
        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary4.setText("Constraints");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary4);
        buttonPrimary4.setWidth("min-content");
        buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary5.setText("Query");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary5);
        buttonPrimary5.setWidth("min-content");
        buttonPrimary5.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary6.setText("LLM");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary6);
        buttonPrimary6.setWidth("min-content");
        buttonPrimary6.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary7.setText("Reset");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary7);
        buttonPrimary7.setWidth("min-content");
        buttonPrimary7.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        h6.setText("Samuele Langhi, Angela Bonifati, Riccardo Tommasini");
        layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, h6);
        h6.setWidth("max-content");
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(basicGrid);
        layoutRow.add(layoutColumn3);
        layoutColumn3.add(layoutRow2);
        layoutRow2.add(tabSheet);
        layoutColumn3.add(layoutColumn4);
        layoutColumn4.add(tabSheet3);
        layoutRow.add(layoutColumn5);
        layoutColumn5.add(tabSheet2);
        getContent().add(layoutRow3);
        layoutRow3.add(buttonPrimary);
        layoutRow3.add(buttonPrimary2);
        layoutRow3.add(buttonPrimary3);
        layoutRow3.add(buttonPrimary4);
        layoutRow3.add(buttonPrimary5);
        layoutRow3.add(buttonPrimary6);
        layoutRow3.add(buttonPrimary7);
        layoutRow3.add(h6);
    }

    private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }

    @Autowired()
    private SamplePersonService samplePersonService;

    private void setTabSheetSampleData(TabSheet tabSheet) {
        tabSheet.add("Dashboard", new Div(new Text("This is the Dashboard tab content")));
        tabSheet.add("Payment", new Div(new Text("This is the Payment tab content")));
        tabSheet.add("Shipping", new Div(new Text("This is the Shipping tab content")));
    }
}
