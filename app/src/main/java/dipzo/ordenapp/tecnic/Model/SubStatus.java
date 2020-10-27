package dipzo.ordenapp.tecnic.Model;

public class SubStatus {
    private int id_substatus;
    private String description;


    public SubStatus(int id_substatus, String description) {
        this.id_substatus = id_substatus;
        this.description = description;
    }

    public int getId_substatus() {
        return id_substatus;
    }

    public void setId_substatus(int id_substatus) {
        this.id_substatus = id_substatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

