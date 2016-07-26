package ru.dao;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;


public class ListsTableEntityPK implements Serializable {
   private int idQuestion;
   private String value;

   @Column(name = "id_question", nullable = false, insertable = false, updatable = false)
   @Id
   public int getIdQuestion () {
      return idQuestion;
   }

   public void setIdQuestion (int idQuestion) {
      this.idQuestion = idQuestion;
   }

   @Column(name = "value", nullable = false, length = 255, insertable = false, updatable = false)
   @Id
   public String getValue () {
      return value;
   }

   public void setValue (String value) {
      this.value = value;
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ListsTableEntityPK that = (ListsTableEntityPK) o;

      if (idQuestion != that.idQuestion) return false;
      if (value != null ? !value.equals(that.value) : that.value != null) return false;

      return true;
   }

   @Override
   public int hashCode () {
      int result = idQuestion;
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
   }
}
