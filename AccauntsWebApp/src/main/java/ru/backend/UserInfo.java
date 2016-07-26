package ru.backend;

/**
 * <p>Класс для хранения связки "id вопроса"-"тип данных"-"ответ на вопрос"</p>
 *
 * @author Бутакова Мария
 */
public class UserInfo {

   private int idQuestion;
   private int type;
   private Object value;

   public int getIdQuestion () {
      return this.idQuestion;
   }

   public void setIdQuestion (int idQuestion) {
      this.idQuestion = idQuestion;
   }

   public int getType () {
      return this.type;
   }

   public void setType (int type) {
      this.type = type;
   }

   public Object getValue () {
      return this.value;
   }

   public void setValue (Object value) {
      this.value = value;
   }

}
