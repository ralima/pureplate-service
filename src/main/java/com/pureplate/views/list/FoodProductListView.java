package com.pureplate.views.list;

import com.pureplate.domain.FoodProduct;
import com.pureplate.service.FoodProductService;
import com.pureplate.service.IngredientService;
import com.pureplate.views.MainLayout;
import com.pureplate.views.forms.FoodProductForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "food-products", layout = MainLayout.class)
@PageTitle("Food Products | Pure Plate")
public class FoodProductListView extends VerticalLayout {
    Grid<FoodProduct> grid = new Grid<>(FoodProduct.class);
    TextField filterText = new TextField();
    FoodProductForm foodProductForm;
    FoodProductService foodProductService;
    IngredientService ingredientService;

    public FoodProductListView(FoodProductService foodProductService, IngredientService ingredientService) {
        addClassName("list-view");

        this.foodProductService = foodProductService;
        this.ingredientService = ingredientService;

        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());

        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("generic-grid");
        grid.setSizeFull();
        grid.setColumns("name", "description");
        grid.addColumn(foodProduct -> foodProduct.getScore().getValue()).setHeader("score");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editFoodProduct(event.getValue()));
    }

    private void configureForm() {
        foodProductForm = new FoodProductForm(ingredientService.findAllIngredients(null));
        foodProductForm.setWidth("25em");

        foodProductForm.addSaveListener(this::saveFoodProduct);
        foodProductForm.addDeleteListener(this::deleteFoodProduct);
        foodProductForm.addCloseListener(e -> closeEditor());
    }

    private void saveFoodProduct(FoodProductForm.SaveEvent event) {
        foodProductService.saveFoodProduct(event.getFoodProduct());
        updateList();
        closeEditor();
    }

    private void deleteFoodProduct(FoodProductForm.DeleteEvent event) {
        foodProductService.deleteFoodProduct(event.getFoodProduct());
        updateList();
        closeEditor();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, foodProductForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, foodProductForm);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addFoodProductButton = new Button("Add food product");
        addFoodProductButton.addClickListener(click -> addFoodProduct());

        var toolbar = new HorizontalLayout(filterText, addFoodProductButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editFoodProduct(FoodProduct foodProduct) {
        if (foodProduct == null) {
            foodProductForm.closeDialog();
        } else {
            foodProductForm.setFoodProduct(foodProduct);
            foodProductForm.openDialog();
        }
    }

    private void updateList() {
        grid.setItems(foodProductService.findAllFoodProducts(filterText.getValue()));
    }

    private void closeEditor() {
        foodProductForm.setFoodProduct(null);
        foodProductForm.closeDialog();
    }

    private void addFoodProduct() {
        grid.asSingleSelect().clear();
        editFoodProduct(new FoodProduct());
    }
}