package com.example.application.views.list;

import com.example.application.data.entity.Person;
import com.example.application.data.entity.TripDetail;
import com.example.application.data.repository.PersonRepository;
import com.example.application.data.service.CrmService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.PermitAll;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@Scope("prototype")
@Route(value="", layout = MainLayout.class)
@PageTitle("Current Trips | TRANSPORT INFORMATION PORTAL")
@PermitAll
public class ListView extends VerticalLayout {
    private Grid<TripDetail> grid = new Grid<>(TripDetail.class);
    private TextField filterText = new TextField();
    private TripDetailForm form;
    private final CrmService service;
    private final SecurityService securityService;
    private Person currentAuthenticatedUser;
    private final PersonRepository personRepository;

    public ListView(CrmService service,
                    SecurityService securityService, PersonRepository personRepository) {
        this.service = service;
        this.securityService = securityService;
        this.personRepository = personRepository;
        addClassName("list-view");
        setSizeFull();
        setCurrentUser();
        configureGrid();
        configureForm();

        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        add(getPageHeader(), getToolbar(), getContent());
        updateList();
        closeEditor();
        grid.asSingleSelect().addValueChangeListener(event ->
                editTrip(event.getValue()));
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new TripDetailForm(service.findAllPlaces(), currentAuthenticatedUser);
        form.setWidth("30%");
        form.addListener(TripDetailForm.SaveEvent.class, this::saveTrip);
        form.addListener(TripDetailForm.DeleteEvent.class, this::deleteTrip);
        form.addListener(TripDetailForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("h.m a");
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(tripDetail -> tripDetail.getFromLocation().getName()).setHeader("From Location");
        grid.addColumn(tripDetail -> tripDetail.getToLocation().getName()).setHeader("To Location");
        grid.addColumn(tripDetail -> tripDetail.getTimeOfDeparture().toLocalDate().toString())
                .setHeader("Date of Departure");
        grid.addColumn(tripDetail -> tripDetail.getTimeOfDeparture().toLocalTime()
                .format(dateTimeFormatter)).setHeader("Time of Departure");
        grid.addColumn(TripDetail::getOccupancyLeft).setHeader("Occupancy Left");
        grid.addColumn(tripDetail -> tripDetail.getTripCreator().getName()).setHeader("Trip Admin");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event ->
                editTrip(event.getValue()));
    }

    public void editTrip(TripDetail tripDetail) {
        if (tripDetail == null) {
            closeEditor();
        } else {
            form.updateForm(tripDetail);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveTrip(TripDetailForm.SaveEvent event) {
        service.saveTrip(event.getTripDetail());
        updateList();
        closeEditor();
    }

    private void deleteTrip(TripDetailForm.DeleteEvent event) {
        service.deleteTrip(event.getTripDetail());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setButtonPanel(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addTripDetails() {
        grid.asSingleSelect().clear();
        TripDetail tripDetail = new TripDetail();
        tripDetail.setTripCreator(currentAuthenticatedUser);
        editTrip(tripDetail);
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by Destination...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");

        if (currentAuthenticatedUser != null) {
            Button addContactButton = new Button("Add Trip");
            addContactButton.addClickListener(event -> addTripDetails());
            toolbar.add(addContactButton);
        }

        return toolbar;
    }

    private void updateList() {
        grid.setItems(service.findAllTripsByString(filterText.getValue()));
    }

    private HorizontalLayout getPageHeader() {
        Label label = new Label("LNMIIT Transport Information");
        label.addClassName("page-header-label");
        HorizontalLayout header = new HorizontalLayout(label);
        header.addClassName("page-header");
        return header;
    }

    private void setCurrentUser() {
        String rollNumber = securityService.getAuthenticatedUser().getUsername();
        Optional<Person> personOptional = personRepository.findByRollNumber(rollNumber);
        currentAuthenticatedUser = personOptional.orElse(null);
    }
}
