package com.example.demo.money.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "km_money_take",
    uniqueConstraints = @UniqueConstraint(columnNames = {"money_give_id", "user_id"})
)
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"moneyGive"})
public class MoneyTake {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  @Column(length = ColumnLengths.UUID)
  private String id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "money_give_id", updatable = false)
  private MoneyGive moneyGive;

  @Column(name = "user_id", length = ColumnLengths.UUID)
  private String userId;

  @Column(nullable = false, updatable = false)
  private Long amount;

  @Column(name = "received_date", insertable = false)
  private LocalDateTime receivedDate;

  @Builder
  public MoneyTake(MoneyGive moneyGive, Long amount, String userId, LocalDateTime receivedDate) {
    this.moneyGive = moneyGive;
    this.amount = amount;
    this.userId = userId; // for TC
    this.receivedDate = receivedDate; // for TC
  }

  public static MoneyTake of(MoneyGive moneyGive, long amount) {
    return MoneyTake.builder()
        .moneyGive(moneyGive)
        .amount(amount)
        .build();
  }

  @Transient
  public boolean isNotReceived() {
    return !isReceived();
  }

  @Transient
  public boolean isReceived() {
    return this.receivedDate != null;
  }

  public void receive(String userId) {
    this.userId = userId;
    this.receivedDate = LocalDateTime.now();
  }
}
