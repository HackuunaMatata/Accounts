package ru.dao;

import javax.persistence.*;


@Entity
@Table(name = "StrTable", schema = "Accaunts")
@IdClass(StrTableEntityPK.class)
public class StrTableEntity {
   private int idUser;
   private int idQuestion;
   private String value;
   private UsersEntity usersByIdUser;
   private QuestionsEntity questionsByIdQuestion;

   @Id
   @Column(name = "id_user", nullable = false)
   public int getIdUser () {
      return idUser;
   }

   public void setIdUser (int idUser) {
      this.idUser = idUser;
   }

   @Id
   @Column(name = "id_question", nullable = false)
   public int getIdQuestion () {
      return idQuestion;
   }

   public void setIdQuestion (int idQuestion) {
      this.idQuestion = idQuestion;
   }

   @Basic
   @Column(name = "value", nullable = true, length = 1024)
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

      StrTableEntity that = (StrTableEntity) o;

      if (idUser != that.idUser) return false;
      if (idQuestion != that.idQuestion) return false;
      if (value != null ? !value.equals(that.value) : that.value != null) return false;

      return true;
   }

   @Override
   public int hashCode () {
      int result = idUser;
      result = 31 * result + idQuestion;
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
   }

   @ManyToOne
   @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
   public UsersEntity getUsersByIdUser () {
      return usersByIdUser;
   }

   public void setUsersByIdUser (UsersEntity usersByIdUser) {
      this.usersByIdUser = usersByIdUser;
   }

   @ManyToOne
   @JoinColumn(name = "id_question", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
   public QuestionsEntity getQuestionsByIdQuestion () {
      return questionsByIdQuestion;
   }

   public void setQuestionsByIdQuestion (QuestionsEntity questionsByIdQuestion) {
      this.questionsByIdQuestion = questionsByIdQuestion;
   }
}
