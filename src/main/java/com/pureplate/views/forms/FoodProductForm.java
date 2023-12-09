package com.pureplate.views.forms;

import com.pureplate.domain.FoodProduct;
import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.FoodClassification;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class FoodProductForm extends FormLayout {
    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    ComboBox<FoodClassification> score = new ComboBox<>("Score");

    MultiSelectComboBox<Ingredient> ingredientsComboBox = new MultiSelectComboBox<>(
            "Ingredients");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    BeanValidationBinder<FoodProduct> binder = new BeanValidationBinder<>(FoodProduct.class);

    public FoodProductForm(List<Ingredient> ingredients) {
        addClassName("food-product-form");
        binder.bindInstanceFields(this);

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

        add(name, description, score, ingredientsComboBox, createButtonsLayout());
    }

    public void setFoodProduct(FoodProduct foodProduct) {
        binder.setBean(foodProduct);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

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

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }

}


