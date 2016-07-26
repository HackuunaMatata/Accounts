package ru.dao;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;


public class DateTableEntityPK implements Serializable {
   private int idUser;
   private int idQuestion;

   @Column(name = "id_user", nullable = false, insertable = false, updatable = false)
   @Id
   public int getIdUser () {
      return idUser;
   }

   public void setIdUser (int idUser) {
      this.idUser = idUser;
   }

   @Column(name = "id_question", nullable = false, insertable = false, updatable = false)
   @Id
   public int getIdQuestion () {
      return idQuestion;
   }

   public void setIdQuestion (int idQuestion) {
      this.idQuestion = idQuestion;
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DateTableEntityPK that = (DateTableEntityPK) o;

      if (idUser != that.idUser) return false;
      if (idQuestion != that.idQuestion) return false;

      return true;
   }

   @Override
   public int hashCode () {
      int result = idUser;
      result = 31 * result + idQuestion;
      return result;
   }
}
