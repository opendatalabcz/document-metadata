package odlgui.backend.objects;

/**
 * Class represents query object used in searcher screen.
 */
public class Query {
    private String field,val,operand, id;
    private double factor;

    public Query(){
        operand = "or";
        factor = 1.0;
        id = String.valueOf(System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public boolean isValid() {
        return field!=null&&!field.isEmpty()&&val!=null&&!val.isEmpty();
    }
}
