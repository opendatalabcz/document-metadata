package odlgui.views.search;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import odlgui.backend.model.History;
import odlgui.backend.objects.Query;
import odlgui.elasticsearch.ElasticClient;

/**
 * Query component used in Query Builder
 */
public class SearchField extends VerticalLayout {
    private static final String PADDING = "0px 6px 0px 6px";
    public SearchField(Query query, String user_id){
        Binder<Query> binder = new Binder<>();

        HorizontalLayout h2 = new HorizontalLayout();
        ComboBox<String> field = new ComboBox<>("Field for searching");
        TextField val = new TextField("Value to find");
        NumberField factor = new NumberField("Factor");
        Div div_rg = new Div();
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("Operator");
        radioGroup.setItems("and", "or");

        factor.setHasControls(true);
        factor.setStep(1d);
        factor.setMin(1);
        factor.setMax(500);

        val.setWidth("38%");
        div_rg.setWidth("40%");
        factor.setWidth("22%");
        div_rg.add(radioGroup);
        h2.add(val,factor,div_rg);
        h2.setSizeFull();
        add(field,h2);
        field.setAllowCustomValue(false);
        field.setRequired(true);
        val.setRequired(true);

        field.setWidthFull();
        field.setItems(ElasticClient.getInstance().getFields());

        setSpacing(false);
        getStyle().set("padding",PADDING);



        if(query.getVal()!=null){
            val.setValue(query.getVal());
        }
        if(query.getField()!=null){
            field.setValue(query.getField());
        }
        factor.setValue(query.getFactor());
        radioGroup.setValue(query.getOperand());

        Binder.Binding<Query, String> valBinder = binder.forField(val)
                .bind(Query::getVal, Query::setVal);
        Binder.Binding<Query, String> fielBinder = binder.forField(field)
                .bind(Query::getField, Query::setField);
        Binder.Binding<Query, Double> factorBinder = binder.forField(factor)
                .bind(Query::getFactor, Query::setFactor);
        Binder.Binding<Query, String> operandBinder = binder.forField(radioGroup)
                .bind(Query::getOperand, Query::setOperand);
        val.addValueChangeListener(e->{
            try {
                binder.writeBean(History.getInstance().getLayouts().get(user_id).get(query.getId()));
            } catch (ValidationException ex) {
                ex.printStackTrace();
            }
        });
        field.addValueChangeListener(e->{
            try {
                binder.writeBean(History.getInstance().getLayouts().get(user_id).get(query.getId()));
            } catch (ValidationException ex) {
                ex.printStackTrace();
            }
        });
        radioGroup.addValueChangeListener(e->{
            try {
                binder.writeBean(History.getInstance().getLayouts().get(user_id).get(query.getId()));
            } catch (ValidationException ex) {
                ex.printStackTrace();
            }
        });
        factor.addValueChangeListener(e->{
            try {
                if(e==null||e.getValue()<0){
                    factor.setValue(e.getOldValue());
                }else {
                    binder.writeBean(History.getInstance().getLayouts().get(user_id).get(query.getId()));
                }
            } catch (Exception ex) {
                factor.setValue(e.getOldValue());
            }
        });

    }
}
