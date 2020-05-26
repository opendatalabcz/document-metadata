package odlgui.views.faq;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * component representing question and answer in FAQ
 */
public class QuestionBlock extends VerticalLayout {
    public QuestionBlock(String question, String answer){
        H3 question_comp = new H3();
        question_comp.setText(question);
        H4 answer_comp = new H4();
        answer_comp.setText(answer);
        add(question_comp,answer_comp);
    }
}
