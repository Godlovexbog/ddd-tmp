package net.zhaixing.blog.valuation.domain.share.valueobject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zhaixing.blog.user.common.domain.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 金额值对象
 * 使用不可变设计，金额使用 DECIMAL(18,2) 存储
 *
 * @author JanYork
 * @version 1.0.0
 * @date 2024-01-01
 * @since 1.0.0
 */
@Getter
@NoArgsConstructor(force = true)
public class Money implements ValueObject<Money> {

    /**
     * 金额
     */
    private final BigDecimal amount;

    /**
     * 货币单位
     */
    private final String currency;

    public Money(BigDecimal amount) {
        this(amount, "CNY");
    }

    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * 加法运算
     */
    public Money add(Money other) {
        if (other == null) {
            return this;
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 减法运算
     */
    public Money subtract(Money other) {
        if (other == null) {
            return this;
        }
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    @Override
    public boolean sameValueAs(Money other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(this.amount, other.amount) 
                && Objects.equals(this.currency, other.currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
