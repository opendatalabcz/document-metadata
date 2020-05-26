package odlgui.views.upload;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import odlgui.elasticsearch.ElasticClient;
import odlgui.views.main.MainView;
import org.springframework.security.access.annotation.Secured;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Route(value = "upload", layout = MainView.class)
@PageTitle("Upload")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@Secured("ROLE_USER")
public class UploadView extends Div {
    public UploadView(){

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);

        //upload.setAcceptedFileTypes("pdf","doc","docx","xls","xlsx");



        Button upload_button = new Button("Upload files");



        upload.addFinishedListener(e->{

            try {
            File tmp = new File("wtest.txt");
            String path = "";

                tmp.createNewFile();
                path = tmp.getAbsolutePath().substring(0,tmp.getAbsolutePath().lastIndexOf("w"));
            File file = new File(path+e.getFileName().replaceAll("[^.a-zA-Z0-9_-]",""));
                tmp.delete();


                FileOutputStream fos = new FileOutputStream(file);
                buffer.getOutputBuffer(e.getFileName()).writeTo(fos);
                fos.close();
            if(ElasticClient.getInstance().extractMetadataRequest("/app_files?file="+e.getFileName().replaceAll("[^.a-zA-Z0-9_-]",""),e.getFileName())>399){
                upload.getElement().executeJs("this.set('files', this.files.filter(function(file){ return file.name !== '" + e.getFileName() + "';}))");


                Div div = new Div();
                div.setWidth("450px");
                div.add(new Label("Could not extract metadata from file: "+e.getFileName()));
                Notification notification = new Notification();
                notification.add(div);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(5000);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
                notification.open();
            }
            file.delete();
            } catch (IOException ex) {
                upload.getElement().executeJs("this.set('files', this.files.filter(function(file){ return file.name !== '" + e.getFileName() + "';}))");

                Div div = new Div();
                div.setWidth("450px");
                div.add(new Label("Could not save file: "+e.getFileName()));
                Notification notification = new Notification();
                notification.add(div);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(5000);
                notification.setPosition(Notification.Position.BOTTOM_CENTER);
                notification.open();
            }
// Image as a file resource
        });
        upload.addAllFinishedListener(e -> {

            Div div = new Div();
            div.setWidth("450px");
            div.add(new Label("Uploading finished"));
            Notification notification = new Notification();
            notification.add(div);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(4000);
            notification.setPosition(Notification.Position.BOTTOM_CENTER);
            notification.open();

        });

        upload_button.setIcon(VaadinIcon.FILE.create());
        upload.setUploadButton(upload_button);
        add(upload);
    }




}
