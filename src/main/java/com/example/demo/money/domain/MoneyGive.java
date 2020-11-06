package com.example.demo.money.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.CollectionUtils;

@Entity
@Table(name = "km_money_give")
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MoneyGive {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  @Column(length = ColumnLengths.UUID)
  private String id;

  @OneToMany(mappedBy = "moneyGive", cascade = CascadeType.PERSIST)
  private
  List<MoneyTake> moneyTakes = new ArrayList<>();

  @Column(name = "room_id", length = ColumnLengths.UUID, nullable = false, updatable = false)
  private String roomId;

  @Column(nullable = false, updatable = false)
  private Long amount;

  @Column(nullable = false, updatable = false)
  private Integer count;

  @Column(name = "created_date", nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdDate;

  @Column(name = "created_by", length = ColumnLengths.UUID, nullable = false, updatable = false)
  private String createdBy;

  @Column(name = "token", length = ColumnLengths.MONEY_TOKEN_LENGTH, nullable = false, unique = true, updatable = false)
  private String token;

  @Column(name = "finished_date", insertable = false)
  private LocalDateTime finishedDate;

  @Builder
  public MoneyGive(String roomId, Long amount, Integer count, String createdBy, String token) {
    this.roomId = roomId;
    this.amount = amount;
    this.count = count;
    this.createdBy = createdBy;
    this.token = token;
  }

  @Transient
  public Optional<MoneyTake> getAvailableMoneyTake() {
    MoneyTake moneyTake = null;
    List<MoneyTake> usableMoneyTakes = this.moneyTakes.stream().filter(MoneyTake::isNotReceived).collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(usableMoneyTakes)) {
      moneyTake = usableMoneyTakes.get(0);
    }
    return Optional.ofNullable(moneyTake);
  }

  @Transient
  public boolean isFinished() {
    return this.finishedDate != null;
  }

  public long getAmountDone() {
    return this.moneyTakes.stream()
        .filter(MoneyTake::isReceived)
        .map(MoneyTake::getAmount)
        .collect(Collectors.toList())
        .stream().reduce(0L, Long::sum);
  }
}
