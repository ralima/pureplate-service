package com.pureplate.views.list;

import com.pureplate.domain.Ingredient;
import com.pureplate.service.IngredientService;
import com.pureplate.views.MainLayout;
import com.pureplate.views.forms.IngredientForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "ingredients", layout = MainLayout.class)
@PageTitle("Ingredients | Pure Plate")
public class IngredientListView extends VerticalLayout {
    Grid<Ingredient> grid = new Grid<>(Ingredient.class);
    TextField filterText = new TextField();
    IngredientForm ingredientForm;
    IngredientService ingredientService;

    public IngredientListView(IngredientService ingredientService) {
        addClassName("list-view");

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
        grid.addColumn(ingredient -> ingredient.getIngredientType().getValue()).setHeader("ingredientType");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editIngredient(event.getValue()));
    }

    private void configureForm() {
        ingredientForm = new IngredientForm();
        ingredientForm.setWidth("25em");

        ingredientForm.addSaveListener(this::saveIngredient);
        ingredientForm.addDeleteListener(this::deleteIngredient);
        ingredientForm.addCloseListener(e -> closeEditor());
    }

    private void saveIngredient(IngredientForm.SaveEvent event) {
        ingredientService.saveIngredient(event.getIngredient());
        updateList();
        closeEditor();
    }

    private void deleteIngredient(IngredientForm.DeleteEvent event) {
        ingredientService.deleteIngredient(event.getIngredient());
        updateList();
        closeEditor();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, ingredientForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, ingredientForm);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addIngredientButton = new Button("Add ingredient");
        addIngredientButton.addClickListener(click -> addIngredient());

        var toolbar = new HorizontalLayout(filterText, addIngredientButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void editIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            ingredientForm.closeDialog();
        } else {
            ingredientForm.setIngredient(ingredient);
            ingredientForm.openDialog();
        }
    }

    private void updateList() {
        grid.setItems(ingredientService.findAllIngredients(filterText.getValue()));
    }

    private void closeEditor() {
        ingredientForm.setIngredient(null);
        ingredientForm.closeDialog();
    }

    private void addIngredient() {
        grid.asSingleSelect().clear();
        editIngredient(new Ingredient());
    }
}