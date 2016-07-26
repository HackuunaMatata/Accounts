package ru.backend;

import org.hibernate.Session;
import ru.dao.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.frontend.DataType.*;


/**
 * <p>Класс, содержащий функции для работы с базой данных.</p>
 *
 * @author Бутакова Мария
 * @author Жалдак Антон (редактор)
 * @author Панкратова Александра (редактор)
 */
public class WorkWithDatabase {
   private static Session session = null;

   public WorkWithDatabase () {
      session = HibernateSessionFactory.getSessionFactory().openSession();
   }


   /**
    * <p>Функция, возращающая id пользователей по заданным имени и фамилии</p>
    *
    * @param userName    имя пользователя
    * @param userSurname фамилия пользователя
    *
    * @return список id пользователей
    */
   public List<Integer> getUserInfo (String userName, String userSurname) {
      session.beginTransaction();
      List<UsersEntity> usersEntities = session.createQuery(" from UsersEntity where name = '" + userName
            + "' and surname = '" + userSurname + "'").list();

      List<Integer> userId = new ArrayList<Integer>();
      if (usersEntities == null) {
         return userId;
      }

      for (UsersEntity userEntity : usersEntities) {
         userId.add(userEntity.getId());
      }
      session.getTransaction().commit();
      return userId;
   }


   /**
    * <p>Функция, добавляющего нового пользователя.</p>
    *
    * @param userName    имя пользователя
    * @param userSurname фамилия пользователя
    *
    * @return id нового пользователя
    */
   public Integer createNewUser (String userName, String userSurname) {
      session.beginTransaction();
      UsersEntity usersEntity = new UsersEntity();
      usersEntity.setName(userName);
      usersEntity.setSurname(userSurname);
      usersEntity.setDateOfLastChange(new Timestamp(System.currentTimeMillis()));
      session.save(usersEntity);
      session.getTransaction().commit();
      return usersEntity.getId();
   }


   /**
    * <p>Функция возвращает всю информацию о заданном по id пользователе, а именно все ответы на
    * вопросы</p>
    *
    * @param userID id пользователя
    *
    * @return список объектов UserInfo, содержащих ответы на вопросы
    */
   public List<UserInfo> getFullUserInfo (int userID) {
      session.beginTransaction();
      ArrayList<UserInfo> userInfo = new ArrayList<UserInfo>();
      List<UsersEntity> usersEntities = (List<UsersEntity>) session.createQuery("from UsersEntity " +
            " where id = " + userID).list();
      UsersEntity usersEntity = usersEntities.get(0);

      // Получение ответов
      List<StrTableEntity> strTableEntities = (List<StrTableEntity>) usersEntity.getStrTablesById();
      List<IntTableEntity> intTableEntities = (List<IntTableEntity>) usersEntity.getIntTablesById();
      List<DateTableEntity> dateTableEntities = (List<DateTableEntity>) usersEntity.getDateTablesById();
      List<QuestionsEntity> questionsEntities = session.createQuery("from QuestionsEntity ").list();

      // Формирование списка UserInfo из ответов

      // StrTable
      if (strTableEntities != null) {
         for (StrTableEntity strTableData : strTableEntities) {
            for (QuestionsEntity question : questionsEntities) {
               if (question.getId() == strTableData.getIdQuestion()) {
                  UserInfo userInformation = new UserInfo();
                  userInformation.setIdQuestion(question.getId());
                  userInformation.setType(question.getTtype());
                  userInformation.setValue(strTableData.getValue());
                  userInfo.add(userInformation);
                  questionsEntities.remove(question);
                  break;
               }
            }
         }
      }

      // IntTable
      if (intTableEntities != null) {
         for (IntTableEntity inttable : intTableEntities) {
            for (QuestionsEntity question : questionsEntities) {
               if (question.getId() == inttable.getIdQuestion()) {
                  UserInfo userInformation = new UserInfo();
                  userInformation.setIdQuestion(question.getId());
                  userInformation.setType(question.getTtype());
                  userInformation.setValue(inttable.getValue());
                  userInfo.add(userInformation);
                  questionsEntities.remove(question);
                  break;
               }
            }
         }
      }

      // DateTable
      if (dateTableEntities != null) {
         for (DateTableEntity datetable : dateTableEntities) {
            for (QuestionsEntity question : questionsEntities) {
               if (question.getId() == datetable.getIdQuestion()) {
                  UserInfo userInformation = new UserInfo();
                  userInformation.setIdQuestion(question.getId());
                  userInformation.setType(question.getTtype());
                  userInformation.setValue(datetable.getValue());
                  userInfo.add(userInformation);
                  questionsEntities.remove(question);
                  break;
               }
            }
         }
      }

      session.getTransaction().commit();
      return userInfo;
   }


   /**
    * <p>Функция, которая возвращает список id вопросов, которые необходиом отобразить
    * пользователю</p>
    *
    * @return список id видимых вопросов
    */
   public List<Integer> getVisibleQuestionsions () {

      session.beginTransaction();
      List<Integer> visibleQuestions = session.createQuery("select id from QuestionsEntity " +
            " where visible = 1").list();
      session.getTransaction().commit();
      return visibleQuestions;
   }


   /**
    * <p>Функция, которая обновляет информацию (ответы на вопросы) заданного по id пользователя</p>
    *
    * @param userID           id пользователя
    * @param userInformations список с информацией UserInfo
    *
    * @throws ParseException если возникли проблемы с преобразованием даты из строки в объект Date
    */
   public void setUpdate (int userID, List<UserInfo> userInformations) throws ParseException {
      session.beginTransaction();
      List<UsersEntity> usersEntities = (List<UsersEntity>) session.createQuery("from UsersEntity where id = " + userID).list();
      UsersEntity usersEntity = usersEntities.get(0);
      List<StrTableEntity> strtableEntities = (List<StrTableEntity>) usersEntity.getStrTablesById();
      List<IntTableEntity> intTableEntities = (List<IntTableEntity>) usersEntity.getIntTablesById();
      List<DateTableEntity> datetableEntities = (List<DateTableEntity>) usersEntity.getDateTablesById();

      for (UserInfo uinfo : userInformations) {
         boolean isElementExist = false;

         if (uinfo.getType() == INTEGER.getVal()) {
            // Update IntTable
            if (uinfo.getValue().equals("")) {
               continue;
            }

            if (intTableEntities != null && intTableEntities.size() != 0) {
               for (IntTableEntity intTableEntity : intTableEntities) {
                  if (uinfo.getIdQuestion() == intTableEntity.getIdQuestion()) {
                     intTableEntity.setValue(Integer.valueOf((String) uinfo.getValue()));
                     isElementExist = true;
                  }
               }
            }
            if (!isElementExist) {
               IntTableEntity intTableEntity = new IntTableEntity();
               intTableEntity.setIdUser(userID);
               intTableEntity.setIdQuestion(uinfo.getIdQuestion());
               intTableEntity.setValue(Integer.valueOf((String) uinfo.getValue()));
               session.save(intTableEntity);
            }
            // END Update IntTable
         } else if (uinfo.getType() == STRING.getVal() || uinfo.getType() == LISTS.getVal()) {
            // Update StrTable
            if (strtableEntities != null && strtableEntities.size() != 0) {
               for (StrTableEntity strTableEntity : strtableEntities) {
                  if (uinfo.getIdQuestion() == strTableEntity.getIdQuestion()) {
                     strTableEntity.setValue((String) uinfo.getValue());
                     isElementExist = true;
                  }
               }
            }
            if (!isElementExist) {
               StrTableEntity strTableEntity = new StrTableEntity();
               strTableEntity.setIdUser(userID);
               strTableEntity.setIdQuestion(uinfo.getIdQuestion());
               strTableEntity.setValue((String) uinfo.getValue());
               session.save(strTableEntity);
            }
            // END Update StrTable
         } else if (uinfo.getType() == DATE.getVal()) {
            // Update DateTable

            if (uinfo.getValue().equals("")) {
               continue;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            // sdf.setLenient(false); // for auto check validity of date
            Date dateParse = sdf.parse((String) uinfo.getValue());
            Timestamp userDate = new Timestamp(dateParse.getTime());

            if (datetableEntities != null && datetableEntities.size() != 0) {
               for (DateTableEntity dateTableEntity : datetableEntities) {
                  if (uinfo.getIdQuestion() == dateTableEntity.getIdQuestion()) {
                     dateTableEntity.setValue(userDate);
                     isElementExist = true;
                  }
               }
            }
            if (!isElementExist) {
               DateTableEntity dateTable = new DateTableEntity();
               dateTable.setIdUser(userID);
               dateTable.setIdQuestion(uinfo.getIdQuestion());
               dateTable.setValue(userDate);
               session.save(dateTable);
            }
            // END Update DateTable
         }
      }
      usersEntity.setDateOfLastChange(new Timestamp(System.currentTimeMillis()));
      session.getTransaction().commit();

      session.clear(); // WARNING!!!
   }


   /**
    * <p>Функция, которая возвращает список ответов на вопрос. Ответом на вопрос является выпадающий
    * список.</p>
    *
    * @param idQuestion id вопроса
    *
    * @return список ответов на вопрос
    */
   public List<String> getFallList (int idQuestion) {
      session.beginTransaction();
      List<String> listsTable = session.createQuery("Select value from ListsTableEntity " +
            "where idQuestion = " + idQuestion).list();
      session.getTransaction().commit();
      return listsTable;
   }


   /**
    * <p>Функция, которая возвращает список Entity вопросов</p>
    *
    * @return список Entity вопросов
    */
   public List<QuestionsEntity> getQuestions () {
      session.beginTransaction();
      List<QuestionsEntity> questionsEntities = session.createQuery("from QuestionsEntity ").list();
      session.getTransaction().commit();
      return questionsEntities;
   }


   /**
    * <p>Функция, которая обновляет информацию о вопросах и добавляет новые вопросы.</p>
    *
    * @param questionsEntity список вопросов, которые необходимо обновить
    */
   public void updateQuestion (List<QuestionsEntity> questionsEntity) {
      session.beginTransaction();

      List<QuestionsEntity> allQuestions = session.createQuery("from QuestionsEntity ").list();

      for (QuestionsEntity questions : questionsEntity) {
         if (questions.getId() > 0) {
            for (QuestionsEntity question : allQuestions) {
               if (questions.getId() == question.getId()) {
                  question.setName(questions.getName());
                  question.setTtype(questions.getTtype());
                  question.setVisible(questions.getVisible());
                  question.setCchange(questions.getCchange());
                  question.setFormattype(questions.getFormattype());
                  session.save(question);
               }
            }
         } else {
            QuestionsEntity newQuestion = new QuestionsEntity();
            newQuestion.setName(questions.getName());
            newQuestion.setTtype(questions.getTtype());
            newQuestion.setVisible(questions.getVisible());
            newQuestion.setCchange(questions.getCchange());
            newQuestion.setFormattype(questions.getFormattype());
            allQuestions.add(newQuestion);
            session.save(newQuestion);
         }
      }

      session.getTransaction().commit();
   }


   /**
    * <p>Функция, которая обновляет (добавляет) варианты ответов на заданный вопрос.
    * Подразумивается, что ответом на вопрос является выпадающий список.</p>
    *
    * @param idQuestion id вопроса
    * @param listNames  список ответов на вопрос
    */
   public void updateListsTable (int idQuestion, List<String> listNames) {
      List<String> listsValue = (List<String>) session.createQuery("Select value from ListsTableEntity " +
            "where idQuestion = " + idQuestion).list();
      session.beginTransaction();
      if (listsValue.size() != 0) {
         for (String nameList : listNames) {
            if (!listsValue.contains(nameList)) {
               ListsTableEntity listsTableEntity = new ListsTableEntity();
               listsTableEntity.setIdQuestion(idQuestion);
               listsTableEntity.setValue(nameList);
               session.save(listsTableEntity);
            }
         }
      } else {
         for (String nameList : listNames) {
            ListsTableEntity listsTableEntity = new ListsTableEntity();
            listsTableEntity.setIdQuestion(idQuestion);
            listsTableEntity.setValue(nameList);
            session.save(listsTableEntity);
         }
      }
      session.getTransaction().commit();

   }


   /**
    * <p>Функция, которая возвращает список Entity всех пользователей</p>
    *
    * @return список Entity всех пользователей
    */
   public List<UsersEntity> getUserEntities () {
      session.beginTransaction();
      List<UsersEntity> allUsersEntity = session.createQuery(" from UsersEntity ").list();
      session.getTransaction().commit();
      return allUsersEntity;
   }


   /**
    * <p>Функция, которая возвращает Entity пользователя по id пользователя.</p>
    *
    * @param UserId id пользователя
    *
    * @return Entity пользователя
    */
   public UsersEntity getUserEntityById (int UserId) {
      session.beginTransaction();
      List<UsersEntity> allUsersEntity = session.createQuery(" from UsersEntity " +
            " where id = " + UserId).list();
      session.getTransaction().commit();
      return allUsersEntity.get(0);
   }


   /**
    * <p>Функция, которая возвращает список пользователей, информация о которых была изменена за
    * последние delayMinForControl минут</p>
    *
    * @param delayMinForControl период отслеживания, минуты
    *
    * @return список пользователей
    *
    * @author Панкратова Александра
    */
   public List<UsersEntity> getUpdatedUsers (long delayMinForControl) {

      Timestamp time = new Timestamp(System.currentTimeMillis() - 1000 * 60 * delayMinForControl);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String lastUpdate = sdf.format(time);

      session.beginTransaction();

      List<UsersEntity> list = session.createQuery(" from UsersEntity where dateOfLastChange > '" + lastUpdate + "'").list();

      session.getTransaction().commit();
      return list;
   }
}
