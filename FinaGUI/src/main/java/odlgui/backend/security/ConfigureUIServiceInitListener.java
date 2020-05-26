package odlgui.backend.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.*;
import odlgui.views.login.LoginView;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
        event.addRequestHandler(new RequestHandler() {
            @Override
            public boolean handleRequest(VaadinSession session,
                                         VaadinRequest request,
                                         VaadinResponse response)
                    throws IOException {
                if (request.getPathInfo().contains("app_files")) {
                    String param = request.getParameter("file");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Content-Disposition", "inline; filename=\""+param+"\"");
                    File tmp = new File("wtest2.txt");
                    tmp.createNewFile();
                    String path = tmp.getAbsolutePath().substring(0,tmp.getAbsolutePath().lastIndexOf("w"))+param;
                    tmp.delete();

                    if(param.endsWith(".pdf")) {
                        response.setContentType("application/pdf");
                    }else if(param.endsWith(".doc")){
                        response.setContentType("application/msword");
                    }else if(param.endsWith(".docx")){
                        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    }else if(param.endsWith(".xls")){
                        response.setContentType("application/vnd.ms-excel");
                    }else if(param.endsWith(".xlsx")){
                        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    }
                    OutputStream os = response.getOutputStream();
                    byte[] buffer = new byte[1024];
                    InputStream is = new FileInputStream(path);
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        os.write(buffer, 0, len);
                    }
                    is.close();
                    os.flush();
                    os.close();
                    return true;
                } else
                    return false;
            }
        });
    }

    /**
     * Reroutes the user if they're not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if(!SecurityUtils.isAccessGranted(event.getNavigationTarget())) {
            if(SecurityUtils.isUserLoggedIn()) {
                event.rerouteToError(NotFoundException.class);
            } else {
                event.rerouteTo(LoginView.class);
            }
        }
    }
}