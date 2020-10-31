package com.example.demo.money.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "km_money_give")
@Getter
@ToString
public class MoneyGive {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  @Column(length = ColumnLengths.UUID)
  private String id;

  @Column(length = ColumnLengths.UUID)
  private String roomId;

  private Long amount;

  private Integer count;

  @Column(name = "created_date")
  private LocalDate createdDate;

  @Column(name = "created_by", length = ColumnLengths.UUID)
  private String createdBy;


}
