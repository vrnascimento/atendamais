package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

  private final AuthenticatedUser authenticatedUser;

  public LoginView(AuthenticatedUser authenticatedUser) {
    this.authenticatedUser = authenticatedUser;
    setAction(
      RouteUtil.getRoutePath(
        VaadinService.getCurrent().getContext(),
        getClass()
      )
    );

    // Create an image component with your logo
    Image logo = new Image("images/atendamais.png", "AtendaMais Logo");

    // Set the image as the title
    setTitle(logo);

    // Create a LoginI18n object with Portuguese localization
    LoginI18n i18n = LoginI18n.createDefault();
    i18n.getForm().setUsername("Usuário");
    i18n.getForm().setPassword("Senha");
    i18n.getForm().setSubmit("Entrar");
    i18n.getErrorMessage().setTitle("Credenciais inválidas");
    i18n
      .getErrorMessage()
      .setMessage("Verifique suas credenciais e tente novamente.");
    i18n.setAdditionalInformation("user / user ou admin / admin");
    i18n.getErrorMessage().setUsername("Usuário é obrigatório");
    i18n.getErrorMessage().setPassword("Senha é obrigatória");

    // Initialize Header object if null
    if (i18n.getHeader() == null) {
      i18n.setHeader(new LoginI18n.Header());
    }

    // Set the description in the header
    i18n.getHeader().setDescription("Abrindo portas, sempre ao lado");

    setI18n(i18n);

    setForgotPasswordButtonVisible(false);
    setOpened(true);
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (authenticatedUser.get().isPresent()) {
      // Already logged in
      setOpened(false);
      event.forwardTo("");
    }

    setError(
      event
        .getLocation()
        .getQueryParameters()
        .getParameters()
        .containsKey("error")
    );
  }
}
