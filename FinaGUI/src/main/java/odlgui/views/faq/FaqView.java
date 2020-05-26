package odlgui.views.faq;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import odlgui.views.main.MainView;
import org.springframework.security.access.annotation.Secured;

@Route(value = "faq", layout = MainView.class)
@PageTitle("FAQ")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@Secured("ROLE_USER")
public class FaqView extends Div {
    public FaqView(){

        VerticalLayout layout = new VerticalLayout();
        addInformation(layout);
        add(layout);
    }

    private void addInformation(VerticalLayout layout){
        H2 user_guide = new H2();
        user_guide.setText("USER role specific questions");
        H2 admin_guide = new H2();
        admin_guide.setText("ADMIN role specific questions");
        QuestionBlock qb1 = new QuestionBlock("1) What is the main function of Similarity Searcher?","The main purpose of this application is to provide easy acces over elasticsearch and metadata extractor plugin and find similarities in documents based on your selection.");
        QuestionBlock qb2 = new QuestionBlock("5) Where I can settup elasticsearch and how?", "You can set up elasticsearch in /settings tab. If you ES cluster doesnt have enabled security, you dont have to specify username and password. But protocol, hostname, port and metadata index are mandatory.");
        QuestionBlock qb3 = new QuestionBlock("6) What is the usage for \"conosole\"?", "You can perfom basic elasticsearch requests there (no need to use another application for REST request).");
        QuestionBlock qb4 = new QuestionBlock("2) Can I upload multiple documents with different file formats?", "Yes, but you are limited to supported formats by metadata-extractor plugin and this UI. Right now are supported: doc, docx, xls, xlsx and pdf.");
        QuestionBlock qb5 = new QuestionBlock("3) What are \"queries\" and how can I stack them?", "Queries are used for searching over elasticsearch. Multimatch queries are implemented and you can set up field in which you want to find value. Additionally you can provide factor for the query (higher number gives you more relevant results for specified field) and you can choose if query will be \"and\"/\"or\" (must/should) condition. You can add query by pressing button \"Add query\" and remove specific query by clicking on its remove button.");
        QuestionBlock qb6 = new QuestionBlock("4) How can I view specific search result from elasticsearch?", "You only need to click on the row with demanded result and the new pop up dialog will open with json representation of document. You can close the dialog windows by clicking outside it.");
        layout.add(user_guide,qb1,qb4,qb5,qb6,admin_guide,qb2,qb3);
    }
}
