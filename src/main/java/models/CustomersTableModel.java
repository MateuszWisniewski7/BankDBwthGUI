package models;

public class CustomersTableModel extends TableModel {

    private Integer branchId;

    public CustomersTableModel(Integer id, String name, boolean selected, Integer branchId) {
        super(id, name, selected);
        this.branchId = branchId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
}
