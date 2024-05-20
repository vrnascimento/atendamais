package com.example.application.views.registro;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Registro")
@Route(
  value = "registro/:samplePersonID?/:action?(edit)",
  layout = MainLayout.class
)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class RegistroView extends Div implements BeforeEnterObserver {

  private final String SAMPLEPERSON_ID = "samplePersonID";
  private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "registro/%s/edit";

  private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

  private TextField firstName;
  private TextField lastName;
  private TextField email;
  private TextField phone;
  private DatePicker dateOfBirth;
  private TextField occupation;
  private TextField role;
  private Checkbox important;
  private TextField deficit;
  private TextArea comments;

  private final Button cancel = new Button("Deletar");
  private final Button save = new Button("Salvar");

  private final BeanValidationBinder<SamplePerson> binder;

  private SamplePerson samplePerson;

  private final SamplePersonService samplePersonService;

  public RegistroView(SamplePersonService samplePersonService) {
    this.samplePersonService = samplePersonService;
    addClassNames("registro-view");

    // Create UI
    SplitLayout splitLayout = new SplitLayout();

    createGridLayout(splitLayout);
    createEditorLayout(splitLayout);

    add(splitLayout);

    // Configure Grid
    grid.addColumn("firstName").setHeader("Nome").setAutoWidth(true);
    grid.addColumn("lastName").setHeader("Sobrenome").setAutoWidth(true);
    grid.addColumn("email").setHeader("Email").setAutoWidth(true);
    grid.addColumn("phone").setHeader("Fone").setAutoWidth(true);
    grid
      .addColumn("dateOfBirth")
      .setHeader("Data de Nascimento")
      .setAutoWidth(true);
    grid.addColumn("occupation").setHeader("Ocupação").setAutoWidth(true);
    grid.addColumn("role").setHeader("Cargo").setAutoWidth(true);
    grid.addColumn("comments").setHeader("Prontuário").setAutoWidth(true);
    grid.addColumn("deficit").setHeader("Necessidade especial").setAutoWidth(true);
    LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
      "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>"
    )
      .withProperty(
        "icon",
        important -> important.isImportant() ? "check" : "minus"
      )
      .withProperty(
        "color",
        important ->
          important.isImportant()
            ? "var(--lumo-primary-text-color)"
            : "var(--lumo-disabled-text-color)"
      );

    grid
      .addColumn(importantRenderer)
      .setHeader("Necessidade especial")
      .setAutoWidth(true);

    grid.setItems(
      query ->
        samplePersonService
          .list(
            PageRequest.of(
              query.getPage(),
              query.getPageSize(),
              VaadinSpringDataHelpers.toSpringDataSort(query)
            )
          )
          .stream()
    );
    grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

    // when a row is selected or deselected, populate form
    grid
      .asSingleSelect()
      .addValueChangeListener(event -> {
        if (event.getValue() != null) {
          UI.getCurrent()
            .navigate(
              String.format(
                SAMPLEPERSON_EDIT_ROUTE_TEMPLATE,
                event.getValue().getId()
              )
            );
        } else {
          clearForm();
          UI.getCurrent().navigate(RegistroView.class);
        }
      });

    // Configure Form
    binder = new BeanValidationBinder<>(SamplePerson.class);

    // Bind fields. This is where you'd define e.g. validation rules

    binder.bindInstanceFields(this);

    cancel.addClickListener(e -> {
      if (samplePerson != null) {
        samplePersonService.delete(samplePerson);
        Notification.show(
          "Contato deletado com sucesso!",
          3000,
          Notification.Position.BOTTOM_CENTER
        ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        clearForm();
        refreshGrid();
      } else {
        Notification.show(
          "Nenhum contato selecionado para deletar.",
          3000,
          Notification.Position.BOTTOM_CENTER
        ).addThemeVariants(NotificationVariant.LUMO_ERROR);
      }
    });

    save.addClickListener(e -> {
      try {
        if (this.samplePerson == null) {
          this.samplePerson = new SamplePerson();
        }
        binder.writeBean(this.samplePerson);
        samplePersonService.update(this.samplePerson);
        clearForm();
        refreshGrid();
        Notification.show("Banco de dados atualizado");
        UI.getCurrent().navigate(RegistroView.class);
      } catch (ObjectOptimisticLockingFailureException exception) {
        Notification n = Notification.show(
          "Erro ao atualizar os dados. Outra pessoa atualizou o registro enquanto você estava fazendo alterações."
        );
        n.setPosition(Position.MIDDLE);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
      } catch (ValidationException validationException) {
        Notification.show(
          "Erro ao atualizar os dados. Verifique novamente se todos os valores são válidos."
        );
      }
    });
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    Optional<Long> samplePersonId = event
      .getRouteParameters()
      .get(SAMPLEPERSON_ID)
      .map(Long::parseLong);
    if (samplePersonId.isPresent()) {
      Optional<SamplePerson> samplePersonFromBackend = samplePersonService.get(
        samplePersonId.get()
      );
      if (samplePersonFromBackend.isPresent()) {
        populateForm(samplePersonFromBackend.get());
      } else {
        Notification.show(
          String.format(
            "O samplePerson solicitado não foi encontrado, ID = %s",
            samplePersonId.get()
          ),
          3000,
          Notification.Position.BOTTOM_START
        );
        // when a row is selected but the data is no longer available,
        // refresh grid
        refreshGrid();
        event.forwardTo(RegistroView.class);
      }
    }
  }

  private void createEditorLayout(SplitLayout splitLayout) {
    Div editorLayoutDiv = new Div();
    editorLayoutDiv.setClassName("editor-layout");

    Div editorDiv = new Div();
    editorDiv.setClassName("editor");
    editorLayoutDiv.add(editorDiv);

    FormLayout formLayout = new FormLayout();
    firstName = new TextField("Nome");
    lastName = new TextField("Sobrenome");
    email = new TextField("Email");
    phone = new TextField("Fone");
    dateOfBirth = new DatePicker("Data de Nascimento");
    occupation = new TextField("Ocupação");
    role = new TextField("Cargo");
    important = new Checkbox("Necessidade especial");
    deficit = new TextField("Necessidade especial");
    comments = new TextArea("Comentários");
    formLayout.add(
      firstName,
      lastName,
      email,
      phone,
      dateOfBirth,
      occupation,
      role,
      important,
      deficit,
      comments
    );

    editorDiv.add(formLayout);
    createButtonLayout(editorLayoutDiv);

    splitLayout.addToSecondary(editorLayoutDiv);
  }

  private void createButtonLayout(Div editorLayoutDiv) {
    HorizontalLayout buttonLayout = new HorizontalLayout();
    buttonLayout.setClassName("button-layout");
    cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    buttonLayout.add(save, cancel);
    editorLayoutDiv.add(buttonLayout);
  }

  private void createGridLayout(SplitLayout splitLayout) {
    Div wrapper = new Div();
    wrapper.setClassName("grid-wrapper");
    splitLayout.addToPrimary(wrapper);
    wrapper.add(grid);
  }

  private void refreshGrid() {
    grid.select(null);
    grid.getDataProvider().refreshAll();
  }

  private void clearForm() {
    populateForm(null);
  }

  private void populateForm(SamplePerson value) {
    this.samplePerson = value;
    binder.readBean(this.samplePerson);
  }
}
