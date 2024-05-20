package com.example.application.views.home;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

  public HomeView() {
    setSpacing(false);

    Image img = new Image("images/atendamais.png", "logo");
    img.setWidth("400px");
    add(img);

    H2 header = new H2("Abrindo portas, sempre ao lado");
    header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
    add(header);
    add(
      new Paragraph(
        "Produto da disciplina de Projetos 2 no curso de Gestão de Tecnologia da Informação na Cesar School."
      )
    );

    setSizeFull();
    setJustifyContentMode(JustifyContentMode.CENTER);
    setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    getStyle().set("text-align", "center");
  }
}
