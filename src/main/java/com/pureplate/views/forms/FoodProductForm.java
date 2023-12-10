package com.pureplate.views.forms;

import com.pureplate.domain.FoodProduct;
import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.FoodClassification;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class FoodProductForm extends Dialog {
    FormLayout formLayout = new FormLayout();
    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    ComboBox<FoodClassification> score = new ComboBox<>("Score");

    MultiSelectComboBox<Ingredient> ingredientsComboBox = new MultiSelectComboBox<>(
            "Ingredients");

    TextArea scoreReason = new TextArea("scoreReason");

    private Chart gaugeChart;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    BeanValidationBinder<FoodProduct> binder = new BeanValidationBinder<>(FoodProduct.class);

    public FoodProductForm(List<Ingredient> ingredients) {
        addClassName("food-product-form");
        binder.bindInstanceFields(this);

        setMinWidth(600, Unit.PIXELS);
        setWidth("auto");
        setHeight("auto");

        score.setItems(FoodClassification.values());
        score.setItemLabelGenerator(FoodClassification::getValue);

        ingredientsComboBox.setItems(ingredients);
        ingredientsComboBox.setItemLabelGenerator(Ingredient::getName);

        binder.bind(score,
                FoodProduct::getScore,
                FoodProduct::setScore);

        binder.bind(ingredientsComboBox,
                FoodProduct::getIngredients,
                FoodProduct::setIngredients);

        initializeGaugeChart();
        score.addValueChangeListener(event -> updateGaugeChart(event.getValue()));

        formLayout.setWidth(100, Unit.PERCENTAGE);

        scoreReason.setWidthFull();
        scoreReason.setMinHeight("100px");
        scoreReason.setMaxHeight("150px");
        scoreReason.setPlaceholder("Reason will be calculated with AI");
        scoreReason.setReadOnly(true);

        formLayout.add(gaugeChart, name, description, score, ingredientsComboBox, scoreReason, createButtonsLayout());
        add(formLayout);
    }

    public void setFoodProduct(FoodProduct foodProduct) {
        binder.setBean(foodProduct);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);;

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void openDialog() {
        this.open();
    }

    public void closeDialog() {
        this.close();
    }

    private void initializeGaugeChart() {
        gaugeChart = new Chart(ChartType.SOLIDGAUGE);
        Configuration configuration = gaugeChart.getConfiguration();

        Pane pane = configuration.getPane();
        pane.setCenter(new String[] {"50%", "50%"});
        pane.setStartAngle(-90);
        pane.setEndAngle(90);

        Background paneBackground = new Background();
        paneBackground.setInnerRadius("60%");
        paneBackground.setOuterRadius("100%");
        paneBackground.setShape(BackgroundShape.ARC);
        pane.setBackground(paneBackground);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTickAmount(2);
        yAxis.setMinorTickInterval("null");
        yAxis.getTitle().setY(-50);
        yAxis.getLabels().setY(16);
        yAxis.setMin(0);
        yAxis.setMax(100);
        yAxis.setTitle("Score");
        yAxis.setStops(new Stop[] {
                new Stop(0.25f, new SolidColor("#008000")),  // Green
                new Stop(0.5f, new SolidColor("#FFFF00")),   // Yellow
                new Stop(0.75f, new SolidColor("#FFA500")),  // Orange
                new Stop(1f, new SolidColor("#FF0000"))      // Red
        });

        DataSeries series = new DataSeries();
        DataSeriesItem item = new DataSeriesItem("Score", 0);
        series.add(item);
        configuration.setSeries(series);

        gaugeChart.setWidth("75%");
        gaugeChart.setHeight("200px");
    }

    private void updateGaugeChart(FoodClassification classification) {
        double gaugeValue = calculateGaugeValue(classification);

        DataSeries series = (DataSeries) gaugeChart.getConfiguration().getSeries().get(0);
        DataSeriesItem item = series.get(0);
        item.setY(gaugeValue);
        gaugeChart.drawChart();
    }

    private double calculateGaugeValue(FoodClassification classification) {
        switch (classification) {
            case PROCESSED_CULINARY_INGREDIENTS:
                return 50;
            case PROCESSED:
                return 75;
            case ULTRA_PROCESSED:
                return 100;
            case UNPROCESSED:
                return 25;
            default:
                return 0;
        }
    }

    // EVENTS
    public static abstract class FoodProductFormEvent extends ComponentEvent<FoodProductForm> {

        private FoodProduct foodProduct;

        protected FoodProductFormEvent(FoodProductForm source, FoodProduct foodProduct){
            super(source, false);
            this.foodProduct = foodProduct;
        }
        public FoodProduct getFoodProduct() {
            return foodProduct;
        }
    }

    public static class SaveEvent extends FoodProductFormEvent {
        SaveEvent(FoodProductForm source, FoodProduct foodProduct) {
            super(source, foodProduct);
        }
    }

    public static class DeleteEvent extends FoodProductFormEvent {
        DeleteEvent(FoodProductForm source, FoodProduct foodProduct) {
            super(source, foodProduct);
        }

    }

    public static class CloseEvent extends FoodProductFormEvent {
        CloseEvent(FoodProductForm source) {
            super(source, null);
        }
    }

    public static class RefreshReasonEvent extends FoodProductForm.FoodProductFormEvent {
        RefreshReasonEvent(FoodProductForm source, FoodProduct foodProduct) {
            super(source, foodProduct);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

    public Registration addRefreshReasonListener(ComponentEventListener<FoodProductForm.RefreshReasonEvent> listener) {
        return addListener(FoodProductForm.RefreshReasonEvent.class, listener);
    }

}


