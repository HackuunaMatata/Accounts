package ru.frontend;


import org.json.JSONArray;
import org.json.JSONObject;
import ru.backend.ExcelWorker;
import ru.backend.MailManager;
import ru.backend.WorkWithDatabase;
import ru.dao.QuestionsEntity;
import ru.dao.UsersEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.frontend.DataType.LISTS;

/**
 * <p>Класс, который содержит логику обработки запросов от AngularJs-приложения AppForФвьшт, т.е.
 * здесь логика работы с администратором.</p>
 *
 * @author Жалдак Антон
 */
@WebServlet(urlPatterns = {"/adminWork"})
public class AdminWorkServlet extends HttpServlet {
   private final static WorkWithDatabase dataBase = new WorkWithDatabase();
   private final static MailManager mailManager = new MailManager();

   @Override
   protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      JSONObject jsonGet = UserWorkServlet.getJsonFromRequest(req);
      if (jsonGet == null) {
         throw new IOException("(root manual exp): jsonGet is null");
      }
      UserWorkServlet.setResponceHeaders(req, resp);

      String goal = jsonGet.getString("goal");
      JSONObject jsonOutput = null;

      if (goal.equals("getQuestionsList")) {
         jsonOutput = getQuestionsList();
      } else if (goal.equals("updateQuestionsList")) {
         JSONArray jsonArrayData = jsonGet.getJSONArray("data");
         jsonOutput = updateQuestionsList(jsonArrayData);
      } else if (goal.equals("getUsersListAndMailSettings")) {
         jsonOutput = getUserList();
         addMailInfoToJSON(jsonOutput);
      } else if (goal.equals("updateMailManagerSettings")) {
         JSONObject jsonGet_data = jsonGet.getJSONObject("data");
         jsonOutput = updateMailManagerSettings(jsonGet_data);
      } else if (goal.equals("sendUsersInfo")) {
         JSONArray jsonUserIDs = jsonGet.getJSONArray("data");
         jsonOutput = sendUserInfoAsXLS(jsonUserIDs);
      } else if (goal.equals("getListNames")) {
         jsonOutput = getListNames();
      } else if (goal.equals("getListContent")) {
         int questionID = jsonGet.getInt("idQuestion");
         jsonOutput = getListContent(questionID);
      } else if (goal.equals("updateList")) {
         JSONObject jsonGet_data = jsonGet.getJSONObject("data");
         jsonOutput = updateListContent(jsonGet_data);
      } else {
         throw new IOException("(root manual exp): wrong goal from host: " + req.getHeader("host"));
      }

      PrintWriter out = resp.getWriter();
      out.print(jsonOutput);
   }


   /**
    * <p>Функция, которая возвращает json со списком и параметрами вопросов.</p>
    *
    * @return объект json с вопросами
    */
   private JSONObject getQuestionsList () {
      JSONObject jsonOutput = new JSONObject();
      List<QuestionsEntity> questionsEntities = dataBase.getQuestions();
      List<JSONObject> jsonElements = new ArrayList<JSONObject>();

      if (questionsEntities != null) {
         for (QuestionsEntity questionsEntity : questionsEntities) {
            JSONObject jsonElement = new JSONObject();
            jsonElement.put("text", questionsEntity.getName());
            jsonElement.put("visible", questionsEntity.getVisible());
            jsonElement.put("format", questionsEntity.getFormattype());
            jsonElement.put("type", questionsEntity.getTtype());
            jsonElement.put("change", questionsEntity.getCchange());
            jsonElement.put("id", questionsEntity.getId());
            jsonElements.add(jsonElement);
         }
      }
      // Преобразование сонтейнера данных в массив
      JSONObject[] mas = new JSONObject[jsonElements.size()];
      for (int i = 0; i < mas.length; i++) {
         mas[i] = jsonElements.get(i);
      }
      jsonOutput.put("questions", mas);
      return jsonOutput;
   }


   /**
    * <p>Функция, которая возвращает json со списком пользователей.</p>
    *
    * @return объект json со списком пользователей.
    */
   private JSONObject getUserList () {
      JSONObject jsonOutput = new JSONObject();

      List<UsersEntity> users = dataBase.getUserEntities();
      JSONObject[] mas = new JSONObject[users.size()];
      for (int i = 0; i < mas.length; i++) {
         UsersEntity user = users.get(i);
         JSONObject jsonUser = new JSONObject();
         String text = " [" + user.getId() + "] " + user.getSurname() + " " + user.getName();
         jsonUser.put("id", user.getId());
         jsonUser.put("text", text);
         mas[i] = jsonUser;
      }

      jsonOutput.put("users", mas);

      return jsonOutput;
   }


   /**
    * <p>Функция, которая добавляет к json-объекту поле с информацией о mail manager'е</p>
    *
    * @param jsonObject json-объек, в который необходимо добавить информацию о mail-мэнеджере
    *
    * @throws IOException если передать на вход null
    */
   private void addMailInfoToJSON (JSONObject jsonObject) throws IOException {
      if (jsonObject == null) {
         throw new IOException("(manual exp): jsonObject is null");
      }

      JSONObject jsonMailInfo = new JSONObject();

      jsonMailInfo.put("address", mailManager.getToEmail());
      jsonMailInfo.put("delayMinForSend", mailManager.getDelayMinForSend());
      jsonMailInfo.put("delayMinForControl", mailManager.getDelayMinForControl());
      jsonMailInfo.put("enableTransmit", mailManager.isNeedRun());

      jsonObject.put("email", jsonMailInfo);
   }


   /**
    * <p>Функция, которая возвращает json-объект со списком вопросов, ответы на которые задаются
    * выпадающим списком</p>
    *
    * @return json-объект со списком вопросов
    */
   private JSONObject getListNames () {
      JSONObject jsonOutput = new JSONObject();

      List<QuestionsEntity> questionsEntities = dataBase.getQuestions();
      List<QuestionsEntity> listQuestions = new ArrayList<QuestionsEntity>();

      // Формирование вопросов, ответами на которые является выбор из списка
      for (QuestionsEntity questionsEntity : questionsEntities) {
         if (questionsEntity.getTtype() == LISTS.getVal()) {
            listQuestions.add(questionsEntity);
         }
      }

      List<JSONObject> jsonsForSend = new ArrayList<JSONObject>();
      for (QuestionsEntity listQuestion : listQuestions) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("id", listQuestion.getId());
         jsonObject.put("text", listQuestion.getName());
         jsonsForSend.add(jsonObject);
      }
      JSONObject[] mas = new JSONObject[jsonsForSend.size()];
      for (int i = 0; i < mas.length; i++) {
         mas[i] = jsonsForSend.get(i);
      }

      jsonOutput.put("variants", mas);

      return jsonOutput;
   }


   /**
    * <p>Функция, которая возвращает json-объект, содержащий список вариантов ответов на вопрос,
    * являющийся выпадающим списком.</p>
    *
    * @param questionID id вопроса, ответы на который нужно получить
    *
    * @return json-объект со списком ответов на вопрос
    */
   private JSONObject getListContent (int questionID) {
      JSONObject jsonOutput = new JSONObject();

      List<String> contents = dataBase.getFallList(questionID);
      String[] mas = new String[contents.size()];
      for (int i = 0; i < mas.length; i++) {
         mas[i] = contents.get(i);
      }

      jsonOutput.put("texts", mas);

      return jsonOutput;
   }


   /**
    * <p>Функция, которая обновляет список ответов на вопрос, ответом на который является выпадающий
    * список. Важно: на самом деле происходить только добавление новых вопросов. Обновление уже
    * существующих ответов или удаление ответов не реализовано. При успешном обновлении (точнее
    * всегда) возвращает json-объект с сообщением об успешном обновлении.</p>
    *
    * @param jsonData json-объект с необходимой информацией: id вопроса и список ответов
    *
    * @return json-объект с сообщением об успешном обновлении
    *
    * @throws IOException если передать на вход null
    */
   private JSONObject updateListContent (JSONObject jsonData) throws IOException {
      if (jsonData == null) {
         throw new IOException("(manual exp): jsonData is null");
      }

      int questionID = jsonData.getInt("id");
      JSONArray jsonArray = jsonData.getJSONArray("text");

      List<String> listNames = new ArrayList<String>();
      for (int i = 0; i < jsonArray.length(); i++) {
         String name = jsonArray.getString(i);
         listNames.add(name);
      }

      dataBase.updateListsTable(questionID, listNames);

      JSONObject jsonOutput = new JSONObject();
      jsonOutput.put("msg", "Successful update");
      return jsonOutput;
   }


   /**
    * <p>Функция, которая обновляет список вопросов и параметры вопросов. По завершении возвращает
    * json-объект с сообщением об успешном обновлении.</p>
    *
    * @param jsonArray массив json-объектов, в которых онформация о вопросах
    *
    * @return json-объект с сообщением об успешном обновлении
    *
    * @throws IOException если передать на вход null
    */
   private JSONObject updateQuestionsList (JSONArray jsonArray) throws IOException {
      if (jsonArray == null) {
         throw new IOException("(manual exp): jsonArray is null");
      }

      List<QuestionsEntity> questionsEntities = new ArrayList<QuestionsEntity>();
      for (int i = 0; i < jsonArray.length(); i++) {
         JSONObject jsonObject = jsonArray.getJSONObject(i);
         QuestionsEntity questionsEntity = new QuestionsEntity();
         questionsEntity.setId(jsonObject.getInt("id"));
         questionsEntity.setName(jsonObject.getString("text"));
         questionsEntity.setVisible(jsonObject.getInt("visible"));
         questionsEntity.setFormattype(jsonObject.getInt("format"));
         questionsEntity.setCchange(jsonObject.getInt("change"));
         questionsEntity.setTtype(jsonObject.getInt("type"));
         questionsEntities.add(questionsEntity);
      }

      dataBase.updateQuestion(questionsEntities);

      JSONObject jsonOutput = new JSONObject();
      jsonOutput.put("msg", "QuestionsList is update");
      return jsonOutput;
   }


   /**
    * <p>Функция, которая обновляет (устанавливает) настройки mail-мэнеджера. По завершении
    * обновления настроек возращает json-объект с сообщением об успешновсти обновления. Если
    * администратор не установил период, за который необходимо отслеживать изменения, то в
    * возращаемом сообщении содержится сообщение об ошибке.</p>
    *
    * @param jsonData json-объект с настройками mail-мэнеджера
    *
    * @return json-объект с сообщением об успешном (или не успешном) обновлении настроек
    *
    * @throws IOException если передать на вход null
    */
   private JSONObject updateMailManagerSettings (JSONObject jsonData) throws IOException {
      if (jsonData == null) {
         throw new IOException("(manual exp): jsonData is null");
      }

      long delayMinForSend = jsonData.getLong("delayMinForSend");
      long delayMinForControl = jsonData.getLong("delayMinForControl");
      String emailAddress = jsonData.getString("address");
      boolean needRunMail = jsonData.getBoolean("enableTransmit");

      mailManager.setToEmail(emailAddress);
      mailManager.setDelayMinForSend(delayMinForSend);
      mailManager.setDelayMinForControl(delayMinForControl);
      mailManager.setNeedRun(needRunMail);

      String msg = null;
      JSONObject jsonOutput = new JSONObject();
      if (delayMinForControl == 0) {
         msg = "Error: delay for control does not set. Mail will not send.";
      } else {
         msg = "Success: mail settings updated.";
      }
      jsonOutput.put("msg", msg);
      return jsonOutput;
   }


   /**
    * <p>Функция, которая формирует по списку id пользователей xls-файл с информацией об этих
    * пользователях и возвращает адрес, по которому доступен на скачивание файл.</p>
    *
    * @param jsonUserIDs массив id пользователей
    *
    * @return json-объект, содержащий путь на скачивание сгенерированного файла
    *
    * @throws IOException если передать на вход null
    */
   private JSONObject sendUserInfoAsXLS (JSONArray jsonUserIDs) throws IOException {
      if (jsonUserIDs == null) {
         throw new IOException("(manual exp): jsonUserIDs is null");
      }
      List<Integer> userIDs = new ArrayList<Integer>();
      for (int i = 0; i < jsonUserIDs.length(); i++) {
         userIDs.add(Integer.valueOf(jsonUserIDs.getString(i)));
      }
      ExcelWorker excelWorker = new ExcelWorker(userIDs, dataBase, getServletContext());

      JSONObject jsonOutput = new JSONObject();
      jsonOutput.put("filepath", excelWorker.getPathWithoutServletContext());
      return jsonOutput;
   }
}
