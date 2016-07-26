package ru.frontend;

/**
 * <p>Container types of data. For every type exist itself number.</p>
 *
 * @author Butakova Mariya
 */
public enum DataType {
   INTEGER(0),
   STRING(1),
   DATE(2),
   LISTS(3);

   private int value;

   DataType(int i) {
      this.value = i;
   }

   public int getVal() {
      return value;
   }
};
