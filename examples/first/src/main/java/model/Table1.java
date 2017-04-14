package model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Table1 {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "VALUE")
    private String value;
    @Column(name = "DATA")
    private Date date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "table1")
    private List<Table2> listData = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Table2> getListData() {
        return listData;
    }

    public void setListData(List<Table2> listData) {
        this.listData = listData;
    }

    @Override
    public String toString() {
        return "Table1{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", date=" + date +
                '}';
    }
}
