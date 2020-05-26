package odlgui.views.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import odlgui.elasticsearch.ElasticClient;
import odlgui.views.main.MainView;
import org.elasticsearch.client.RequestOptions;
import org.springframework.security.access.annotation.Secured;

import java.text.NumberFormat;
import java.util.Locale;

@Route(value = "settings", layout = MainView.class)
@PageTitle("Settings")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@Secured("ROLE_ADMIN")
public class SettingsView extends Div {
    private final String MARGIN_RIGHT = "10px";
    private final String MARGIN_LEFT = "10px ";
    private final String PADDING = "20px 10px 20px 10px";
    private final String BUTTON_HEIGHT = "40px";
    private final String BUTTON_WIDTH = "200px";
    private final String LABEL_WIDTH = "500px";
    private final String LABEL_HEIGHT = "40px";

    public SettingsView() {

        HorizontalLayout resetDialogActions = new HorizontalLayout();
        HorizontalLayout saveDialogActions = new HorizontalLayout();
        VerticalLayout resetDialogLayout = new VerticalLayout();
        VerticalLayout saveDialogLayout = new VerticalLayout();

        FormLayout formLayout = new FormLayout();

        Binder<ElasticClient> binder = new Binder<>();
        HorizontalLayout actions = new HorizontalLayout();

// The object that will be edited
        ElasticClient client = ElasticClient.getInstance();
        // Create the fields
        TextField username = new TextField();
        username.setValueChangeMode(ValueChangeMode.EAGER);
        PasswordField password = new PasswordField();
        password.setValueChangeMode(ValueChangeMode.EAGER);
        TextField hostname = new TextField();
        hostname.setValueChangeMode(ValueChangeMode.EAGER);

        ComboBox<String> protocol = new ComboBox<>();
        protocol.setItems("http","https");
        protocol.setValue("http");
        TextField port = new TextField();
        port.setValueChangeMode(ValueChangeMode.EAGER);
        Button saveDialogOK = new Button("OK");
        saveDialogOK.setIcon(VaadinIcon.CHECK.create());
        Button saveDialogCancel = new Button("Cancel");
        saveDialogCancel.setIcon(VaadinIcon.CLOSE.create());
        Dialog saveDialog = new Dialog();
        Button resetDialogOK = new Button("OK");
        resetDialogOK.setIcon(VaadinIcon.CHECK.create());
        Button resetDialogCancel = new Button("Cancel");
        resetDialogCancel.setIcon(VaadinIcon.CLOSE.create());
        Dialog resetDialog = new Dialog();


        ComboBox<String> metadataIndex = new ComboBox<>();

        Label infoLabel = new Label();
        Button save = new Button("Save");
        Button testConnection = new Button("Test connection");
        Button reset = new Button("Reset to default");
        formLayout.addFormItem(username, "Username");
        formLayout.addFormItem(password, "Password");
        formLayout.addFormItem(protocol, "Protocol");
        formLayout.addFormItem(hostname, "Hostname");
        formLayout.addFormItem(port,"Port");
        formLayout.addFormItem(metadataIndex, "Metadata index");
        formLayout.getStyle().set("padding", PADDING);
        actions.getStyle().set("padding", PADDING);
        infoLabel.getStyle().set("marginRight",MARGIN_RIGHT);
        infoLabel.getStyle().set("marginLeft", MARGIN_LEFT);

        save.setHeight(BUTTON_HEIGHT);
        save.setWidth(BUTTON_WIDTH);
        save.setIcon(VaadinIcon.INBOX.create());
        testConnection.setHeight(BUTTON_HEIGHT);
        testConnection.setWidth(BUTTON_WIDTH);
        testConnection.setIcon(VaadinIcon.CONNECT.create());
        reset.setHeight(BUTTON_HEIGHT);
        reset.setWidth(BUTTON_WIDTH);
        reset.setIcon(VaadinIcon.TIME_BACKWARD.create());
        infoLabel.setHeight(LABEL_HEIGHT);
        infoLabel.setWidth(LABEL_WIDTH);
        actions.add(testConnection,reset,save);

        metadataIndex.addCustomValueSetListener(
                event -> metadataIndex.setValue(event.getDetail()));
        metadataIndex.setItems(client.getIndices());




        add(formLayout,actions,infoLabel);


        // Button bar

        SerializablePredicate<String> protocolPredicate = value -> protocol.getValue()!=null&&!protocol.getValue().trim().isEmpty();
        SerializablePredicate<String> portPredicate = value -> !port.getValue().trim().isEmpty();
        SerializablePredicate<String> hostnamePredicate = value -> !hostname.getValue().trim().isEmpty();
        SerializablePredicate<String> metadataIndexPredicate = value -> metadataIndex.getValue()!=null&&!metadataIndex.getValue().trim().isEmpty();
        SerializablePredicate<String> metadataIndexPredicate2 = value -> metadataIndex.getValue()!=null&&!metadataIndex.getValue().trim().startsWith(".");


        protocol.addValueChangeListener(e->{
            if(e.getValue()==null||(!e.getValue().equals("https")&&!e.getValue().equals("http"))){
                protocol.setValue(e.getOldValue());
            }
        });
        // E-mail and phone have specific validators
        Binder.Binding<ElasticClient, String> hostnameBinder = binder.forField(hostname)
                .withValidator(hostnamePredicate,"Hostname required")
                .bind(ElasticClient::getHostname, ElasticClient::setHostname);

        Binder.Binding<ElasticClient, String> protocolBinder = binder.forField(protocol)
                .withValidator(protocolPredicate,"Protocol required")
                .bind(ElasticClient::getProtocol, ElasticClient::setProtocol);

        Binder.Binding<ElasticClient, String> metadataIndexBinder = binder.forField(metadataIndex)
                .withValidator(metadataIndexPredicate,"Metadata index required")
                .withValidator(metadataIndexPredicate2,"System index starting with dot not allowed")
                .bind(ElasticClient::getMetadata_index, ElasticClient::setMetadata_index);

        StringToIntegerConverter plainIntegerConverter = new StringToIntegerConverter("Input value should be an integer") {
            protected NumberFormat getFormat(Locale locale) {
                NumberFormat format = super.getFormat(locale);
                format.setGroupingUsed(false);
                return format;
            };
        };
        Binder.Binding<ElasticClient, Integer> portBinder = binder.forField(port)
                .withValidator(portPredicate,"Port required")
                .withConverter(plainIntegerConverter)
                .withValidator(integer -> integer > 0, "Input value should be a positive integer")
                .bind(ElasticClient::getPort, ElasticClient::setPort);

        Binder.Binding<ElasticClient, String> usernameBinder = binder.forField(username)
                .bind(ElasticClient::getUsername, ElasticClient::setUsername);

        Binder.Binding<ElasticClient, String> passwordBinder = binder.forField(password)
                .bind(ElasticClient::getPassword, ElasticClient::setPassword);

// Trigger cross-field validation when the other field is changed
        username.addValueChangeListener(event -> usernameBinder.validate());
        password.addValueChangeListener(event -> passwordBinder.validate());
        port.addValueChangeListener(event -> portBinder.validate());
        hostname.addValueChangeListener(event -> hostnameBinder.validate());
        metadataIndex.addValueChangeListener(event -> metadataIndexBinder.validate());

        binder.readBean(client);

// First name and last name are required fields
        hostname.setRequiredIndicatorVisible(true);
        hostname.setRequired(true);
        protocol.setRequiredIndicatorVisible(true);
        protocol.setRequired(true);
        protocol.setAllowCustomValue(false);
        protocol.setPreventInvalidInput(true);
        port.setRequiredIndicatorVisible(true);
        port.setRequired(true);
        metadataIndex.setRequiredIndicatorVisible(true);
        metadataIndex.setRequired(true);

// Click listeners for the buttons
        testConnection.addClickListener(event -> {

           if(binder.isValid()){
               try {
                   if(ElasticClient.getInstance().createElasticsearchClient(password.getValue(),username.getValue(),hostname.getValue(),protocol.getValue(),Integer.parseInt(port.getValue())).ping(RequestOptions.DEFAULT)){
                       //infoLabel.getStyle().set("color","green");
                       //infoLabel.setText("ES connection establish");
                       Div div = new Div();
                       div.setWidth("450px");
                       div.add(new Label("ES connection establish"));
                       Notification notification = new Notification();
                       notification.add(div);
                       notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                       notification.setDuration(4000);
                       notification.setPosition(Notification.Position.TOP_CENTER);
                       notification.open();
                   }else{
                       //infoLabel.getStyle().set("color","red");
                       //infoLabel.setText("Cant establish ES connection");
                       Div div = new Div();
                       div.setWidth("450px");
                       div.add(new Label("Cant establish ES connection"));
                       Notification notification = new Notification();
                       notification.add(div);
                       notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                       notification.setDuration(4000);
                       notification.setPosition(Notification.Position.TOP_CENTER);
                       notification.open();
                   }
               } catch (Exception e) {
                   //infoLabel.getStyle().set("color","red");
                   //infoLabel.setText("Cant establish ES connection: "+e.getMessage());
                   Div div = new Div();
                   div.setWidth("450px");
                   div.add(new Label("Cant establish ES connection: "+e.getMessage()));
                   Notification notification = new Notification();
                   notification.add(div);
                   notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                   notification.setDuration(4000);
                   notification.setPosition(Notification.Position.TOP_CENTER);
                   notification.open();

               }
           }else {
               Div div = new Div();
               div.setWidth("450px");
               div.add(new Label("Validation problem in some field/s"));
               Notification notification = new Notification();
               notification.add(div);
               notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
               notification.setDuration(4000);
               notification.setPosition(Notification.Position.TOP_CENTER);
               notification.open();
               //infoLabel.getStyle().set("color","red");
               //infoLabel.setText("Validation problem in some field/s");
           }
        });
        reset.addClickListener(event -> {
            resetDialog.open();
        });
        save.addClickListener(event -> {
            if (binder.isValid()) {
                saveDialog.open();
            } else {
                Div div = new Div();
                div.setWidth("450px");
                div.add(new Label("Validation problem in some field/s"));
                Notification notification = new Notification();
                notification.add(div);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(4000);
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.open();
                //infoLabel.getStyle().set("color","red");
                //infoLabel.setText("Validation problem in some field/s");
            }
        });



        resetDialogActions.setVerticalComponentAlignment(FlexComponent.Alignment.END,resetDialogCancel);
        resetDialogActions.add(resetDialogOK,resetDialogCancel);
        //resetDialog.setWidth("400px");
        //resetDialog.setHeight("150px");
        resetDialogLayout.add(new Label("Do you really want to reset into default settings?"),resetDialogActions);
        resetDialog.add(resetDialogLayout);
        resetDialogCancel.addClickListener(event -> {
            resetDialog.close();
        });
        resetDialogOK.addClickListener(event -> {
            resetDialog.close();
            client.setToDefault();
            metadataIndex.setItems(client.getIndices());
            binder.readBean(client);
            Div div = new Div();
            div.setWidth("450px");
            div.add(new Label("ES settings updated to default values"));
            Notification notification = new Notification();
            notification.add(div);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(4000);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.open();
            //infoLabel.getStyle().set("color","green");
            //infoLabel.setText("ES settings updated to default values");
        });


        saveDialogActions.setVerticalComponentAlignment(FlexComponent.Alignment.END,saveDialogCancel);
        saveDialogActions.add(saveDialogOK,saveDialogCancel);
        saveDialogLayout.add(new Label("Do you really want to save new settings?"),saveDialogActions);
        saveDialog.add(saveDialogLayout);
        saveDialogCancel.addClickListener(event -> {
            saveDialog.close();
        });
        saveDialogOK.addClickListener(event -> {

            saveDialog.close();
            try {
                String tmp = metadataIndex.getValue();
                binder.writeBean(client);
                ElasticClient.getInstance().createElasticSearchClient();
                metadataIndex.setItems(client.getIndices());
                metadataIndex.setValue(tmp);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            Div div = new Div();
            div.setWidth("450px");
            div.add(new Label("ES settings updated"));
            Notification notification = new Notification();
            notification.add(div);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(4000);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.open();
            //infoLabel.getStyle().set("color","green");
            //infoLabel.setText("ES settings updated");
        });





    }

}
