package odlgui.views.console;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import odlgui.elasticsearch.ElasticClient;
import odlgui.views.main.MainView;
import org.springframework.security.access.annotation.Secured;

@Route(value = "console", layout = MainView.class)
@PageTitle("Console")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@Secured("ROLE_ADMIN")
public class ConsoleView extends SplitLayout {
    public ConsoleView(){
        setSplitterPosition(50);
        VerticalLayout layout = new VerticalLayout();
        TextArea input = new TextArea("code for REST requests");
        input.getStyle().set("minHeight", "500px");
        input.setPlaceholder("GET /");
        TextArea output = new TextArea("Elasticsearch response");
        output.setReadOnly(true);
        addToPrimary();
        Button submit = new Button("Submit request");
        submit.setIcon(VaadinIcon.CHEVRON_RIGHT.create());
        submit.setWidth("100%");
        input.setWidth("100%");
        submit.addClickListener(event->{
            try {
            String restMethod = input.getValue().trim().substring(0,input.getValue().trim().indexOf(" "));
            String endPoint = "";
            String dataInput = "";

                if (input.getValue().contains("{")) {
                    endPoint = input.getValue().trim().substring(input.getValue().trim().indexOf(" ") + 1, input.getValue().trim().indexOf("{"));
                } else {
                    endPoint = input.getValue().trim().substring(input.getValue().trim().indexOf(" ") + 1);
                }
                if (input.getValue().contains("{") && input.getValue().contains("}")) {
                    dataInput = input.getValue().trim().substring(input.getValue().trim().indexOf("{"), input.getValue().trim().lastIndexOf("}") + 1);
                }
                restMethod = restMethod.replace("\n", "").replace("\r", "").replace("\t", "");
                endPoint = endPoint.replace("\n", "").replace("\r", "").replace("\t", "");
                dataInput = dataInput.replace("\n", "").replace("\r", "").replace("\t", "");
                if((!restMethod.toLowerCase().equals("get")&&!restMethod.toLowerCase().equals("put")&&!restMethod.toLowerCase().equals("post")&&!restMethod.toLowerCase().equals("delete"))||(endPoint.trim().isEmpty())){
                    throw new Exception("Wrong request");
                }
                String out = ElasticClient.getInstance().doRequest(restMethod, endPoint, dataInput);
                output.setValue(out);
            }catch (Exception e){
                Div div = new Div();
                div.setWidth("450px");
                div.add(new Label("Request is not valid"));
                Notification notification = new Notification();
                notification.add(div);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(5000);
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.open();
            }
        });
        layout.add(input,submit);
        addToPrimary(layout);
        addToSecondary(output);
    }
}
