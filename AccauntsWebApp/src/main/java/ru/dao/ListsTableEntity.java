package ru.dao;

import javax.persistence.*;


@Entity
@Table(name = "ListsTable", schema = "Accaunts")
@IdClass(ListsTableEntityPK.class)
public class ListsTableEntity {
   private int idQuestion;
   private String value;
   private QuestionsEntity questionsByIdQuestion;

   @Id
   @Column(name = "id_question", nullable = false)
   public int getIdQuestion () {
      return idQuestion;
   }

   public void setIdQuestion (int idQuestion) {
      this.idQuestion = idQuestion;
   }

   @Id
   @Column(name = "value", nullable = false, length = 255)
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

      ListsTableEntity that = (ListsTableEntity) o;

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

   @ManyToOne
   @JoinColumn(name = "id_question", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
   public QuestionsEntity getQuestionsByIdQuestion () {
      return questionsByIdQuestion;
   }

   public void setQuestionsByIdQuestion (QuestionsEntity questionsByIdQuestion) {
      this.questionsByIdQuestion = questionsByIdQuestion;
   }
}
