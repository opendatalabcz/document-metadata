package odlgui.views.search;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import odlgui.backend.model.History;
import odlgui.backend.objects.ESDocument;
import odlgui.backend.objects.Query;
import odlgui.elasticsearch.ElasticClient;
import odlgui.views.main.MainView;
import org.springframework.security.access.annotation.Secured;

import java.util.*;

/**
 * Search view - also default one
 */

@Route(value = "", layout = MainView.class)
@PageTitle("Search")
@CssImport("styles/views/masterdetail/master-detail-view.css")
@Secured("ROLE_USER")
public class SearchView extends Div {


    private Grid<ESDocument> documents;
    private Dialog overview;
    private TextArea overview_info;
    private Div wrapper;


    public SearchView() {
        setId("master-detail-view");
        // Configure Grid
        overview = new Dialog();
        overview_info = new TextArea();
        overview.add(overview_info);
        overview_info.setMinWidth("500px");
        overview_info.setReadOnly(true);


        ArrayList<ESDocument> tmp = new ArrayList<>();
        if(!History.getInstance().getResults().containsKey(VaadinSession.getCurrent().getSession().getId())){
            History.getInstance().getResults().put(VaadinSession.getCurrent().getSession().getId(),tmp);
        }


        //when a row is selected or deselected, populate form



        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);

        createGridLayout();
        createQueryBuilderLayout(splitLayout);

        add(splitLayout);
    }

    /**
     * Creates QueryBuilder layout in which all queries are hold
     * @param splitLayout layout in which QueryBuilder should be created
     */
    private void createQueryBuilderLayout(SplitLayout splitLayout) {
        Button search = new Button("Perform Search");
        VerticalLayout queries_builder = new VerticalLayout();
        VerticalLayout queries = new VerticalLayout();
        VerticalLayout buttons = new VerticalLayout();
        H2 info = new H2("Query Builder");
        queries_builder.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER,info);
        queries_builder.add(info);
        if(!History.getInstance().getLayouts().containsKey(VaadinSession.getCurrent().getSession().getId())){
            HashMap<String,Query> query_arr = new HashMap<>();
            Query tmp_query = new Query();
            query_arr.put(tmp_query.getId(),tmp_query);
            History.getInstance().getLayouts().put(VaadinSession.getCurrent().getSession().getId(),query_arr);
            queries.add(createQueryLayout(tmp_query,search));
        }else{
            TreeMap<String, Query> sorted = new TreeMap<>(History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId()));
            Set<Map.Entry<String, Query>> mappings = sorted.entrySet();
            for(Map.Entry<String, Query> mapping : mappings){
                queries.add(createQueryLayout(mapping.getValue(),search));
            }
        }

        Button add = new Button("Add Query");
        add.addClickListener(e -> {
            Query tmp_query = new Query();
            HashMap<String,Query> query_arr = History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId());
            query_arr.put(tmp_query.getId(),tmp_query);
            History.getInstance().getLayouts().put(VaadinSession.getCurrent().getSession().getId(),query_arr);
            queries.add(createQueryLayout(tmp_query,search));
            search.setEnabled(true);
        });
        search.addClickListener(e->{
                boolean valid = true;
                for(Query q : History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId()).values()){
                    if(!q.isValid()){
                        valid = false;
                    }
                }
                if(valid) {
                    History.getInstance().getResults().put(VaadinSession.getCurrent().getSession().getId(), ElasticClient.getInstance().perfomSearch(VaadinSession.getCurrent().getSession().getId()));
                    createGridLayout();
                }else{
                    Div div = new Div();
                    div.setWidth("450px");
                    div.add(new Label("Could not perform search, missing mandatory fields in query"));
                    Notification notification = new Notification();
                    notification.add(div);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.TOP_CENTER);
                    notification.open();
                }
        });
        search.setIcon(VaadinIcon.SEARCH.create());
        add.setIcon(VaadinIcon.PLUS.create());
        add.setWidth("100%");
        search.setWidth("100%");
        buttons.add(add,search);
        queries_builder.add(queries,buttons);
        splitLayout.addToSecondary(queries_builder);
    }

    private VerticalLayout createQueryLayout(Query query, Button search){
        VerticalLayout editor = new VerticalLayout();
        VerticalLayout rml = new VerticalLayout();
        Button remove = new Button();
        remove.addClickListener(e->{

            HashMap<String,Query> tmp_arr = History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId());
            Component child = e.getSource().getParent().get().getParent().get();
            VerticalLayout parent = (VerticalLayout) child.getParent().get();
            parent.remove(child);
            tmp_arr.remove(query.getId());
            History.getInstance().getLayouts().put(VaadinSession.getCurrent().getSession().getId(),tmp_arr);
            if(History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId()).isEmpty()){
                search.setEnabled(false);
            }
        });
        remove.setIcon(VaadinIcon.CLOSE_SMALL.create());
        editor.add(rml,new SearchField(query,VaadinSession.getCurrent().getSession().getId()));
        rml.add(remove);
        rml.getStyle().set("padding","0px 6px 0px 6px");
        rml.setAlignItems(FlexComponent.Alignment.END);
        editor.getStyle().set("border","3px outset lightskyblue");
        editor.setPadding(false);
        editor.setSpacing(false);
        rml.setSpacing(false);
        editor.setId(query.getId());
        return editor;
    }

    /**
     * creates table with results
     */
    private void createGridLayout() {
        wrapper.removeAll();
        documents = new Grid<>();
        documents.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        documents.setHeightFull();
        HashSet<String> fields = new HashSet<>();
        if(History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId())!=null) {
            for (Query query : History.getInstance().getLayouts().get(VaadinSession.getCurrent().getSession().getId()).values()) {
                if(query.getField()!=null) {
                    fields.add(query.getField().replaceAll(".keyword",""));
                }
            }
        }
        documents.addColumn(ESDocument::getFilename).setHeader("Filename");
        documents.addColumn(ESDocument::getScore).setHeader("Score");
        for(String field:fields){
            if(!field.equals("filename")) {
                documents.addColumn(esDocument -> esDocument.getFieldVal(field)).setHeader(field);
            }
        }
        documents.setItems(History.getInstance().getResults().get(VaadinSession.getCurrent().getSession().getId()));
        documents.asSingleSelect().addValueChangeListener(event -> {
            overview_info.setValue(event.getValue().getRaw_data().toString(4));
            overview.open();
        });
        wrapper.add(documents);
    }


}
