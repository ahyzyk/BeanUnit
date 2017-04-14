package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by ahyzyk on 14.04.2017.
 */
@Entity
public class Table2 {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TABLE1_ID")
    private Table1 table1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
