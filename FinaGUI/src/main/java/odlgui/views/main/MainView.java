package odlgui.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import odlgui.backend.security.SecurityUtils;
import odlgui.views.console.ConsoleView;
import odlgui.views.faq.FaqView;
import odlgui.views.login.LoginView;
import odlgui.views.search.SearchView;
import odlgui.views.settings.SettingsView;
import odlgui.views.upload.UploadView;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "Similarity Searcher", shortName = "Similarity Searcher")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@Secured("ROLE_USER")
public class  MainView extends AppLayout {

    private final Tabs menu;

    public MainView() {
        //setPrimarySection(Section.DRAWER);
        setPrimarySection(Section.NAVBAR);

        addToNavbar(true, new DrawerToggle());
        menu = createMenuTabs();
        addToDrawer(menu);


    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
        tabs.setId("tabs");
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab("Search", SearchView.class,VaadinIcon.SEARCH));
        tabs.add(createTab("Upload", UploadView.class, VaadinIcon.UPLOAD));
        tabs.add(createTab("Settings", SettingsView.class, VaadinIcon.COGS));
        tabs.add(createTab("Console", ConsoleView.class, VaadinIcon.WRENCH));
        tabs.add(createTab("FAQ", FaqView.class, VaadinIcon.QUESTION));
        tabs.add(createTab("Logout ("+SecurityUtils.getUsername()+")", LoginView.class, VaadinIcon.SIGN_OUT));
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(String title, Class<? extends Component> viewClass, VaadinIcon icon) {
        HorizontalLayout tab_lay = new HorizontalLayout();
        tab_lay.add(icon.create(), new Label(title));
        RouterLink rl = new RouterLink(null, viewClass);
        rl.add(tab_lay);
        Tab t = createTab(rl);
        if((SecurityUtils.getUsername().equals("user"))&&(title.contains("Settings")||title.contains("Console"))){
            t.setEnabled(false);
        }
        return t;
        //return createTab(populateLink(new RouterLink(null, viewClass), title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }


}
