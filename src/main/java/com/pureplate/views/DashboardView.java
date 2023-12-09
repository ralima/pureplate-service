package com.pureplate.views;

import com.pureplate.domain.enums.IngredientType;
import com.pureplate.service.FoodProductService;
import com.pureplate.service.IngredientService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.EnumSet;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Pure Plate")
public class DashboardView extends VerticalLayout {

    private final IngredientService ingredientService;
    private final FoodProductService foodProductService;

    public DashboardView(IngredientService ingredientService, FoodProductService foodProductService) {
        this.ingredientService = ingredientService;
        this.foodProductService = foodProductService;

        VerticalLayout ingredientsColumn = new VerticalLayout(getIngredientStats(), getIngredientTypeChart());
        VerticalLayout foodProductsColumn = new VerticalLayout(getFoodProductStats(), getFoodProductChart());
        ingredientsColumn.setWidth("50%");
        foodProductsColumn.setWidth("50%");

        HorizontalLayout columnsLayout = new HorizontalLayout(ingredientsColumn, foodProductsColumn);
        columnsLayout.setSizeFull();

        add(columnsLayout);

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.STRETCH);
        setJustifyContentMode(JustifyContentMode.CENTER);

        }

    private Component getIngredientStats() {
        Span stats = new Span(ingredientService.countIngredients() + " ingredients");
        stats.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.Margin.Top.MEDIUM);
        return stats;
    }

    private Component getIngredientTypeChart() {
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Ingredients  classification");
        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Amount of Ingredients");
        configuration.addyAxis(y);


        EnumSet.allOf(IngredientType.class).forEach( ingredientType -> {
            ListSeries serie = new ListSeries(ingredientType.name(), ingredientService.countIngredientsPerType(ingredientType));
           configuration.addSeries(serie);
        });

        return chart;
    }

    private Component getFoodProductStats() {
        Span stats = new Span(foodProductService.countFoodProducts() + " Food Products (broken down by amount of ingredients)");
        stats.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.Margin.Top.MEDIUM);
        return stats;
    }

    private Chart getFoodProductChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        foodProductService.findAllFoodProducts(null).forEach(food -> {
            Integer ingredients = food.getIngredients()!=null? food.getIngredients().size() : 0;
            String foodName = food.getName() + " - " + ingredients;
            dataSeries.add(new DataSeriesItem(foodName, ingredients));
                });
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }

}
