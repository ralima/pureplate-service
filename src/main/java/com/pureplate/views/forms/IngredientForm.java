package com.pureplate.views.forms;

import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.IngredientType;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;

public class IngredientForm extends FormLayout {
    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    ComboBox<IngredientType> type = new ComboBox<>("Type");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    BeanValidationBinder<Ingredient> binder = new BeanValidationBinder<>(Ingredient.class);

    public IngredientForm() {
        addClassName("ingredient-form");
        binder.bindInstanceFields(this);

        type.setItems(IngredientType.values());
        type.setItemLabelGenerator(IngredientType::getValue);
        binder.bind(type,
                Ingredient::getIngredientType,
                Ingredient::setIngredientType);

        add(name, description, type, createButtonsLayout());
    }

    public void setIngredient(Ingredient ingredient) {
        binder.setBean(ingredient);
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
    public static abstract class IngredientFormEvent extends ComponentEvent<IngredientForm> {

        private Ingredient ingredient;

        protected IngredientFormEvent(IngredientForm source, Ingredient ingredient){
            super(source, false);
            this.ingredient = ingredient;
        }
        public Ingredient getIngredient() {
            return ingredient;
        }
    }

    public static class SaveEvent extends IngredientFormEvent {
        SaveEvent(IngredientForm source, Ingredient ingredient) {
            super(source, ingredient);
        }
    }

    public static class DeleteEvent extends IngredientFormEvent {
        DeleteEvent(IngredientForm source, Ingredient ingredient) {
            super(source, ingredient);
        }

    }

    public static class CloseEvent extends IngredientFormEvent {
        CloseEvent(IngredientForm source) {
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


