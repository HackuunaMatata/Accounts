package ru.frontend;


import org.json.JSONArray;
import org.json.JSONObject;
import ru.backend.UserInfo;
import ru.backend.WorkWithDatabase;
import ru.dao.QuestionsEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;

import static ru.frontend.DataType.DATE;
import static ru.frontend.DataType.INTEGER;
import static ru.frontend.DataType.LISTS;

/**
 * <p>Класс, который содержит логику обработки запросов от AngularJs-приложения AppForUser, т.е.
 * здесь логика работы с пользователем.</p>
 *
 * @author Жалдак Антон
 */
@WebServlet(urlPatterns = {"/userWork"})
public class UserWorkServlet extends HttpServlet {
   private final static WorkWithDatabase dataBase = new WorkWithDatabase();

   @Override
   protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      JSONObject jsonGet = getJsonFromRequest(req);
      if (jsonGet == null) {
         throw new IOException("(manual exp): jsonGet is null");
      }
      setResponceHeaders(req, resp);
      JSONObject jsonOutput = null;
      String goal = jsonGet.getString("goal");

      if (goal.equals("getUserInfo")) {
         // Запрос на получение информации о пользователе
         // Возможно наличиче нескольких пользователей с одинаковым именем, поэтому может
         //    сформироваться запрос для уточнения пользователя по его id.
         JSONObject jsonGet_data = jsonGet.getJSONObject("data");
         jsonOutput = getUserInfoAndFormTableInfo(jsonGet_data);
      } else if (goal.equals("specifyUserId")) {
         // Запрос на получение информации о пользователе после уточнения пользователя
         JSONObject jsonGet_data = jsonGet.getJSONObject("data");
         Integer userId = jsonGet_data.getInt("id");
         String userName = jsonGet_data.getString("name");
         String userSurname = jsonGet_data.getString("surname");
         jsonOutput = getUserInfoByIdAndFormTableInfo(userId, userName, userSurname);
      } else if (goal.equals("updateUserInfo")) {
         // Запрос на обновление информации о пользователе
         JSONArray jsonGet_data = jsonGet.getJSONArray("data");
         Integer userID = jsonGet.getInt("id");
         jsonOutput = tryUpdateUserInfo(userID, jsonGet_data);
      } else {
         throw new IOException("(manual exp): wrong goal from host: " + req.getHeader("host"));
      }

      PrintWriter out = resp.getWriter();
      out.print(jsonOutput);
   }


   /**
    * <p>Функция формирования json из запроса.</p>
    *
    * @param req Запрос
    *
    * @return json объект, извлечённый из запроса
    *
    * @throws IOException если req == null
    */
   static JSONObject getJsonFromRequest (HttpServletRequest req) throws IOException {
      if (req == null) {
         throw new IOException("(manual exp): req is null");
      }

      BufferedReader bf = req.getReader();
      StringBuilder jsonContentString = new StringBuilder();
      String line = null;
      while ((line = bf.readLine()) != null) {
         jsonContentString.append(line);
      }

      return new JSONObject(jsonContentString.toString());
   }

   /**
    * <p><Устанавливает необходимые заголовки для http-ответа</p>
    *
    * @param req  запрос
    * @param resp ответ
    *
    * @throws IOException если запрос или ответ равны null
    */
   static void setResponceHeaders (HttpServletRequest req, HttpServletResponse resp) throws IOException {
      if (req == null || resp == null) {
         throw new IOException("(manual exp): req or resp is null");
      }

      String headerContent = null;
      // Массив с заголовками для установки
      String[] headerNames = {"accept-language", "content-type"};

      // Установка нужных заголовков
      for (String headerName : headerNames) {
         headerContent = req.getHeader(headerName);
         if (headerContent != null) {
            resp.setHeader(headerName, headerContent);
         }
      }
      // For send AJAX on some servers (или как-то так)
      resp.setHeader("Access-Control-Allow-Origin", "*");
   }

   /**
    * <p>Функция формирования информации о пользователе в формате json. Спрашивает базу о наличии
    * пользователя по имени и фамилии. Если не найдено ни одного пользователя, такой пользователь
    * создаётся. Если пользователь один, то происходит формирование json'а с подробной информацией.
    * Если пользователей с одинаковым именем несколько, то формируется json, в котором помещаются
    * имена, фамилии и id пользователей.</p> <p>За подготовку json с информацией о пользователе
    * отвечает функция getUserInfoByIdAndFormTableInfo</p>
    *
    * @param jsonData json с именем и фамилией пользователя
    *
    * @return json с информацией о пользователе или найденные пользователи
    *
    * @throws IOException если jsonData == null
    */
   private JSONObject getUserInfoAndFormTableInfo (JSONObject jsonData) throws IOException {
      if (jsonData == null) {
         throw new IOException("(manual exp): jsonData is null");
      }

      JSONObject jsonOutput = null;

      String userName = jsonData.getString("name");
      String userSurname = jsonData.getString("surname");
      boolean isNewUser = jsonData.getBoolean("isNewUser");

      List<Integer> userIDs = dataBase.getUserInfo(userName, userSurname);

      if (isNewUser || userIDs.size() == 0) {
         userIDs = new ArrayList<Integer>();
         userIDs.add(dataBase.createNewUser(userName, userSurname));
      }

      int numsIdUser = userIDs.size();
      if (numsIdUser == 0) {
         throw new IOException("(manual exp): user " + userName + " " + userSurname + "not found or create in database");
      } else if (numsIdUser > 1) {
         jsonOutput = new JSONObject();

         int[] allId = new int[numsIdUser];
         for (int i = 0; i < numsIdUser; i++) {
            allId[i] = userIDs.get(i);
         }

         jsonOutput.put("goal", "specifyUserId");
         jsonOutput.put("ids", allId);
         jsonOutput.put("name", userName);
         jsonOutput.put("surname", userSurname);
      } else {
         Integer userID = (Integer) userIDs.get(0);
         jsonOutput = getUserInfoByIdAndFormTableInfo(userID, userName, userSurname);
      }
      return jsonOutput;
   }

   /**
    * <p>Функция формирования информации о пользователе в формате json. Предполагается, что
    * пользователь определён одназначно.</p>
    *
    * @param id          id пользователя
    * @param userName    имя пользователя
    * @param userSurname фамилия пользователя
    *
    * @return json с информацией о пользователе
    *
    * @throws IOException если id, или имя, или фамилия == null
    */
   private JSONObject getUserInfoByIdAndFormTableInfo (Integer id, String userName, String userSurname) throws IOException {
      if (id == null || userName == null || userSurname == null) {
         throw new IOException("(manual exp): input has null");
      }
      JSONObject jsonOutput = new JSONObject();
      jsonOutput.put("goal", "showUserInfo");
      jsonOutput.put("name", userName);
      jsonOutput.put("surname", userSurname);
      jsonOutput.put("id", id);

      // Отправка запроса базе, чтобы получить информацию о пользователе
      List<UserInfo> userInfo = dataBase.getFullUserInfo(id);
      // Отправка запроса базе, чтобы получить список вопросов
      List<QuestionsEntity> questions = dataBase.getQuestions();
      // Отправка запроса базе, чтобы получить список номеров видимых вопросов
      List<Integer> visibleQuestionIDs = dataBase.getVisibleQuestionsions();

      List<QuestionsEntity> visibleQuestions = new ArrayList<QuestionsEntity>();

      // Удаление неотображаемых вопросов
      for (QuestionsEntity question : questions) {
         for (Integer visID : visibleQuestionIDs) {
            if (visID == question.getId()) {
               visibleQuestions.add(question);
               visibleQuestionIDs.remove(visID);
               break;
            }
         }
      }

      List<RowUserTable> rowsUserTable = new ArrayList<RowUserTable>();
      for (QuestionsEntity question : visibleQuestions) {
         RowUserTable userRow = new RowUserTable();
         userRow.setId(question.getId());
         userRow.setText(question.getName());
         userRow.setType(question.getTtype());

         // Формирование поля формата
         if (userRow.getType() == LISTS.getVal()) {
            // Отправка запроса базе, чтобы получить содержимое списков
            List<String> stringList = dataBase.getFallList(userRow.getId());
            String[] strings = new String[stringList.size()];
            for (int i = 0; i < stringList.size(); i++) {
               strings[i] = stringList.get(i);
            }
            userRow.setFormat(strings);
         } else {
            userRow.setFormat(question.getFormattype());
         }

         // Формирование поля Значение
         boolean isSetValue = false;
         if (userRow.getId() == 3) {
            userRow.setValue(userName);
            isSetValue = true;
         } else if (userRow.getId() == 2) {
            userRow.setValue(userSurname);
            isSetValue = true;
         } else {
            for (UserInfo uinfo : userInfo) {
               if (uinfo.getIdQuestion() == userRow.getId()) {
                  if (userRow.getType() == DATE.getVal()) {
                     Date userDateVeiw = new Date(((Timestamp) uinfo.getValue()).getTime());
                     Formatter formatter = new Formatter();
                     formatter.format("%1$tY-%1$tm-%1$td %tR", userDateVeiw);
                     userRow.setValue(formatter.toString());
                     formatter.close();
                  } else if (uinfo.getType() == INTEGER.getVal()) {
                     userRow.setValue(((Integer) uinfo.getValue()).toString());
                  } else {
                     userRow.setValue((String) uinfo.getValue());
                  }
                  isSetValue = true;
                  break;
               }
            }
         }

         if (!isSetValue) {
            userRow.setValue("");
         }

         rowsUserTable.add(userRow);
      }

      // Формирование ответного json
      JSONObject jsonSendData[] = new JSONObject[rowsUserTable.size()];

      for (int i = 0; i < rowsUserTable.size(); i++) {
         jsonSendData[i] = rowsUserTable.get(i).getJSONObject();
//         System.out.println(i + ". " + jsonSendData[i].toString());
      }

      jsonOutput.put("data", jsonSendData);

      return jsonOutput;
   }


   /**
    * <p>Функция формирования информации о пользователе в формате json. Предполагается, что
    * пользователь определён одназначно.</p>
    *
    * @param userID id пользователя
    * @param data   обновлённая информация о пользователе
    *
    * @return json с сообщением об успехе или провале обновления данных
    *
    * @throws IOException если id, или обновлённая информация == null
    */
   private JSONObject tryUpdateUserInfo (Integer userID, JSONArray data) throws IOException {
      if (data == null || userID == null) {
         throw new IOException("(manual exp): data or userID is null");
      }

      // Извлечение информации, которая будет обновлена (извлекаются все вопросы)
      ArrayList<UserInfo> userInfos = new ArrayList<UserInfo>();
      JSONObject rowTable = null;
      for (int i = 0; i < data.length(); i++) {
         rowTable = data.getJSONObject(i);
         UserInfo userInfo = new UserInfo();
         userInfo.setType(rowTable.getInt("type"));
         userInfo.setIdQuestion(rowTable.getInt("id"));
         userInfo.setValue(rowTable.get("value"));
         userInfos.add(userInfo);
      }

      // Попытка обновления информации о пользователе
      try {
         dataBase.setUpdate(userID, userInfos);
      } catch (Exception e) {
         e.printStackTrace();

         JSONObject jsonOutput = new JSONObject();
         jsonOutput.put("message", e.toString());
         jsonOutput.put("status", "1");
         return jsonOutput;
      }

      JSONObject jsonOutput = new JSONObject();
      jsonOutput.put("message", "success");
      jsonOutput.put("status", "0");
      return jsonOutput;
   }

}
