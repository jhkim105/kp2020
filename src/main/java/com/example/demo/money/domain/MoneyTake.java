package com.example.demo.money.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "km_money_take")
@Getter
@ToString
public class MoneyTake {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  @Column(length = ColumnLengths.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "money_give_id")
  private MoneyGive moneyGive;

  @Column(length = ColumnLengths.UUID)
  private String userId;

  @Column(name = "received_date")
  private LocalDate received_date;

}
