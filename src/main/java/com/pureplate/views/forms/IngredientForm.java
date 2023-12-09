package com.pureplate.views.forms;

import com.pureplate.domain.Ingredient;
import com.pureplate.domain.enums.IngredientType;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.shared.Registration;

public class IngredientForm extends Dialog {
    FormLayout formLayout = new FormLayout();
    TextField name = new TextField("Name");
    TextField description = new TextField("Description");
    ComboBox<IngredientType> type = new ComboBox<>("Type");

    Span score = new Span("Pending type");

    TextArea reason = new TextArea("Reason");

    Button refreshScore = new Button("Refresh");
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

        score.getElement().getClassList().add("badge-score");
        score.getElement().getClassList().add("transparent");
        type.addValueChangeListener(event -> calculateScoreBadge(event.getValue()));


        reason.setWidthFull();
        reason.setMinHeight("100px");
        reason.setMaxHeight("150px");
        reason.setPlaceholder("Reason will be calculated with AI");
        reason.setReadOnly(true);

        formLayout.add(score, name, description, type, reason, createButtonsLayout());
        add(formLayout);
    }

    public void setIngredient(Ingredient ingredient) {
        binder.setBean(ingredient);
    }


    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshScore.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        refreshScore.addClickListener(event -> fireEvent(new RefreshReasonEvent(this, binder.getBean())));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close, refreshScore);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    private void calculateScoreBadge(IngredientType selectedType) {
        // Remove all previous score styles
        score.getElement().getClassList().remove("badge-score-cosmetic");
        score.getElement().getClassList().remove("badge-score-industrialized");
        score.getElement().getClassList().remove("badge-score-minimally");
        score.getElement().getClassList().remove("badge-score-natural");


        // Add the new score style based on the selected type
        if (selectedType != null) {
            String styleClass = "badge-score-" + selectedType.name().toLowerCase();
            score.getElement().getClassList().add(styleClass);
            score.setText(selectedType.getValue());
        }
    }

    public void openDialog() {
        this.open();
    }

    public void closeDialog() {
        this.close();
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
    public static class RefreshReasonEvent extends IngredientFormEvent {
        RefreshReasonEvent(IngredientForm source, Ingredient ingredient) {
            super(source, ingredient);
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

    public Registration addRefreshReasonListener(ComponentEventListener<RefreshReasonEvent> listener) {
        return addListener(RefreshReasonEvent.class, listener);
    }

}


