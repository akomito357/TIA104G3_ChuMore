package com.chumore.productcategory.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class UpdateUtil {

    /**
     * 比較當前值和新值，僅當值有變化時執行 Setter。
     *
     * @param currentValue 當前值
     * @param newValue     新值
     * @param setter       更新值的方法
     * @param <T>          值的類型
     */
    public static <T> void updateIfChanged(T currentValue, T newValue, Consumer<T> setter) {
        if (isValueChanged(currentValue, newValue)) {
            setter.accept(newValue);
        }
    }

    /**
     * 比較當前值與新值是否不同。
     * 對於 byte[] 使用 Arrays.equals，其餘使用 Objects.equals。
     *
     * @param currentValue 當前值
     * @param newValue     新值
     * @param <T>          值的類型
     * @return 是否發生變化
     */
    private static <T> boolean isValueChanged(T currentValue, T newValue) {
        if (currentValue instanceof byte[] && newValue instanceof byte[]) {
            // 特殊處理 byte[] 型別，使用 Arrays.equals
            return !Arrays.equals((byte[]) currentValue, (byte[]) newValue);
        }
        // 其他型別，使用 Objects.equals
        return !Objects.equals(currentValue, newValue);
    }
}
