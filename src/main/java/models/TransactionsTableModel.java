package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionsTableModel {

    private LocalDateTime date;
    private String value;
    private Integer customerId;
    private String formattedDate;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

    public TransactionsTableModel(Timestamp date, String value, int customerId) {
        this.date = date.toLocalDateTime();
        this.value = value+"PLN";
        this.customerId = customerId;
        this.formattedDate = df.format(date.toLocalDateTime());

    }

    public LocalDateTime getDate() {
        return date;}

    public void setDate(String date) {
        this.date = LocalDateTime.parse(date);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }


}
