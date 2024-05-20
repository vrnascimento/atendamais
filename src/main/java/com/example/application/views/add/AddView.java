package com.example.application.views.add;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Adicionar contato")
@Route(value = "adicionar", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AddView extends Div {

  private final TextField firstName = new TextField("Nome");
  private final TextField lastName = new TextField("Sobrenome");
  private final TextField email = new TextField("Email");
  private final TextField phone = new TextField("Fone");
  private final DatePicker dateOfBirth = new DatePicker("Data de Nascimento");
  private final TextField occupation = new TextField("Ocupação");
  private final TextField role = new TextField("Cargo");
  private final Checkbox important = new Checkbox("Necessidade especial");
  private final TextField deficit = new TextField("Necessidade especial");
  private final TextArea comments = new TextArea("Comentários");
  private final Button saveButton = new Button("Salvar");
  private final Button cancelButton = new Button("Limpar");

  private final SamplePersonService samplePersonService;

  public AddView(SamplePersonService samplePersonService) {
    this.samplePersonService = samplePersonService;
    addClassName("add-view");

    // Create UI
    FormLayout formLayout = new FormLayout();
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

    // Add horizontal margin to form components
    formLayout.getStyle().set("margin-top", "1rem");
    formLayout.getStyle().set("margin-right", "8rem");
    formLayout.getStyle().set("margin-left", "8rem");
    formLayout.getStyle().set("margin-bottom", "1rem");

    add(formLayout);

    // Create a button layout
    HorizontalLayout buttonLayout = new HorizontalLayout();
    saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    buttonLayout.add(saveButton, cancelButton);

    // Add horizontal margin to the button layout
    buttonLayout.getStyle().set("margin-right", "8rem");
    buttonLayout.getStyle().set("margin-left", "8rem");

    add(buttonLayout);

    saveButton.addClickListener(event -> saveContact());
    cancelButton.addClickListener(event -> clearForm());
  }

  private void saveContact() {
    SamplePerson samplePerson = new SamplePerson();
    samplePerson.setFirstName(firstName.getValue());
    samplePerson.setLastName(lastName.getValue());
    samplePerson.setEmail(email.getValue());
    samplePerson.setPhone(phone.getValue());
    samplePerson.setDateOfBirth(dateOfBirth.getValue());
    samplePerson.setOccupation(occupation.getValue());
    samplePerson.setRole(role.getValue());
    samplePerson.setImportant(important.getValue());
    samplePerson.setComments(comments.getValue());

    samplePersonService.update(samplePerson);

    Notification.show(
      "Contato salvo com sucesso!",
      3000,
      Notification.Position.BOTTOM_CENTER
    ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

    clearForm();
  }

  private void clearForm() {
    firstName.clear();
    lastName.clear();
    email.clear();
    phone.clear();
    dateOfBirth.clear();
    occupation.clear();
    role.clear();
    important.setValue(false);
    comments.clear();
    deficit.clear();
  }
}
