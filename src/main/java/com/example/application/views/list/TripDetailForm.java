package com.example.application.views.list;

import com.example.application.data.entity.Person;
import com.example.application.data.entity.Place;
import com.example.application.data.entity.TripDetail;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.Duration;
import java.util.List;

public class TripDetailForm extends VerticalLayout {

    private final Label PLACE_OF_DEPARTURE_DECLARATION = new Label("Place Of Departure:");
    private final Label placeOfDeparture = new Label("Place Of Departure");

    private final Label CONTACT_OF_TRIP_ADMIN = new Label("Trip Admin Contact: ");
    private final Label tripAdminContact = new Label("Place Of Departure");


    private final ReadOnlyHasValue<Place> panelTitle =
            new ReadOnlyHasValue<>(place -> {
                if (place != null) placeOfDeparture.setText(place.getName());
            });

    private final ReadOnlyHasValue<Person> personReadOnlyHasValue = new ReadOnlyHasValue<>(
        person -> {
            if (person != null) tripAdminContact.setText(person.getPhoneNumber());
        }
    );

    private final DateTimePicker timeOfDeparture = new DateTimePicker("Date And Time");

    private final ComboBox<Integer> occupancyLeft = new ComboBox<>("Occupancy Left");
    private final ComboBox<Place> toLocation = new ComboBox<>("To destination");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button close = new Button("Cancel");
    Binder<TripDetail> binder = new BeanValidationBinder<>(TripDetail.class);
    private TripDetail tripDetail;
    private final Person currentLoggedUser;
    private final HorizontalLayout buttonPanel;

    public TripDetailForm(List<Place> placeList, Person currentLoggedUser) {
        this.currentLoggedUser = currentLoggedUser;
        addClassName("contact-form");
        List<Integer> possibleVacancies = List.of(1, 2, 3, 4, 5);

        occupancyLeft.setItems(possibleVacancies);
        binder.forField(panelTitle).bind(TripDetail::getPlaceOfDeparture, null);
        binder.forField(personReadOnlyHasValue).bind(TripDetail::getTripCreator, null);
        binder.forField(occupancyLeft)
                .withNullRepresentation(0)
                .bind(TripDetail::getOccupancyLeft, TripDetail::setOccupancyLeft);
        binder.bindInstanceFields(this);

        toLocation.setItems(placeList);
        toLocation.setItemLabelGenerator(Place::getName);
        timeOfDeparture.setLabel("Date and time of departure");
        timeOfDeparture.setStep(Duration.ofMinutes(30));
        buttonPanel = createButtonsLayout();

        add(getPlaceOfDeparture(), timeOfDeparture, occupancyLeft, toLocation, getContactOfTripAdmin());
    }

    private HorizontalLayout getPlaceOfDeparture() {
        PLACE_OF_DEPARTURE_DECLARATION.getStyle().set("font-weight", "bold");
        return new HorizontalLayout(PLACE_OF_DEPARTURE_DECLARATION, placeOfDeparture);
    }

    private HorizontalLayout getContactOfTripAdmin() {
        CONTACT_OF_TRIP_ADMIN.getStyle().set("font-weight", "bold");
        return new HorizontalLayout(CONTACT_OF_TRIP_ADMIN, tripAdminContact);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());

        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, tripDetail)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(tripDetail);
            fireEvent(new SaveEvent(this, tripDetail));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setTripDetails(TripDetail tripDetail) {
        this.tripDetail = tripDetail;
        binder.readBean(tripDetail);
        if (tripDetail != null && tripDetail.getTripCreator() != null && currentLoggedUser != null
                && tripDetail.getTripCreator() .getRollNumber().equals(currentLoggedUser.getRollNumber())) {
            add(buttonPanel);
        }
        else {
            remove(buttonPanel);
        }
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<TripDetailForm> {
        private final TripDetail tripDetail;

        protected ContactFormEvent(TripDetailForm source, TripDetail tripDetail) {
            super(source, false);
            this.tripDetail = tripDetail;
        }

        public TripDetail getContact() {
            return tripDetail;
        }
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(TripDetailForm source, TripDetail tripDetail) {
            super(source, tripDetail);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(TripDetailForm source, TripDetail contact) {
            super(source, contact);
        }
    }

    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(TripDetailForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
