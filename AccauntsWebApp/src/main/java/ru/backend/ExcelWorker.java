package ru.backend;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import ru.dao.QuestionsEntity;
import ru.dao.UsersEntity;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Класс, занимающийся формированием файла в формате xls.</p>
 *
 * @author Бутакова Мария
 * @author Жалдак Антон (редактор)
 */
public class ExcelWorker {
   private final int rowBase = 1;   // Начальная строка
   private final int colmnBase = 1; // Начальный столбец
   private String pathToFile = "";  // Полный путь к созданному файлу
   private ServletContext servletContext = null;

   /**
    * <p>Констурктор, формирующий для заданных пользователей из заданной базы xls-файл с подробной
    * информацией о пользователях. Информация о каждом пользователе находится на своём листе. Файл
    * сохраняется в контекстную директорию сервлета, а именно getServletContext+excelDir (см. в
    * код).</p>
    *
    * @param idUsers        список id пользователей
    * @param dataBase       база данных
    * @param servletContext контекст сервлета
    */
   public ExcelWorker (List<Integer> idUsers, WorkWithDatabase dataBase, ServletContext servletContext) {
      this.servletContext = servletContext;
      HSSFWorkbook workbook = new HSSFWorkbook();          // создание excel-таблицы в памяти
      List<QuestionsEntity> questions = dataBase.getQuestions();

      List<Integer> idListQuestions = new ArrayList<Integer>();
      for (QuestionsEntity question : questions) {
         idListQuestions.add(question.getId());
      }

      // Для каждого пользователя
      for (Integer idUser : idUsers) {
         UsersEntity userEntity = dataBase.getUserEntityById(idUser);
         List<UserInfo> userInfoList = dataBase.getFullUserInfo(idUser);

         HSSFSheet sheet = workbook.createSheet(userEntity.getSurname() + "_" + userEntity.getName()
               + "_" + userEntity.getId());

         // Формирование шапки таблицы
         int rowNum = rowBase;
         Row row = sheet.createRow(rowNum++);
         row.createCell(colmnBase + 0).setCellValue("Вопрос");
         row.createCell(colmnBase + 1).setCellValue("Ответ");

         // Создание информационных строк таблицы
         for (UserInfo userInfo : userInfoList) {
            createRowInSheet(sheet, rowNum++, userInfo, questions);
            // Приведение к Object для того, чтобы удаление происходило не по индексу,
            //    а по самому объекту
            idListQuestions.remove((Object) userInfo.getIdQuestion());
         }

         for (Integer idQuestion : idListQuestions) {
            createEmptyRowInSheet(sheet, rowNum++, idQuestion, questions);
         }

         sheet.autoSizeColumn(colmnBase + 0);
         sheet.autoSizeColumn(colmnBase + 1);
      }

      // Запись созданного в памяти документа в файл
      // Получение времени для формирования имени файла
      long curTime = System.currentTimeMillis();
      String curStringDate = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(curTime);
      try {
         this.pathToFile = servletContext.getRealPath(".") + StringConst.excelDir
               + StringConst.begXlsName + curStringDate + StringConst.endXlsName;

         File file = new File(this.pathToFile);
         if (!file.exists()) {
            file.createNewFile();
         }

         FileOutputStream out = new FileOutputStream(file);
         workbook.write(out);
         out.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * <p>Функция, формирующая очередную строку с информацией на заданной странице.</p>
    *
    * @param sheet     страница
    * @param rowNum    номер строки
    * @param userInfo  объект с пользовательской информацией
    * @param questions список вопросов
    */
   private void createRowInSheet (HSSFSheet sheet, int rowNum, UserInfo userInfo,
                                  List<QuestionsEntity> questions) {
      for (QuestionsEntity quest : questions) {
         if (userInfo.getIdQuestion() == quest.getId()) {
            Row row = sheet.createRow(rowNum);
            row.createCell(colmnBase + 0).setCellValue(quest.getName());
            row.createCell(colmnBase + 1).setCellValue(userInfo.getValue().toString());
            break;
         }
      }
   }


   private void createEmptyRowInSheet (HSSFSheet sheet, int rowNum, Integer idQuestion,
                                       List<QuestionsEntity> questions) {
      for (QuestionsEntity questionsEntity : questions) {
         if (questionsEntity.getId() == idQuestion) {
            Row row = sheet.createRow(rowNum);
            row.createCell(colmnBase + 0).setCellValue(questionsEntity.getName());
            row.createCell(colmnBase + 1).setCellValue("");
            break;
         }
      }
   }


   /**
    * <p>Функция, формирующая путь к созданному файлу относительно контекста сервлета.</p>
    *
    * @return путь к файлу относительно контекста сервлета
    */
   public String getPathWithoutServletContext () {
      return pathToFile.replaceAll(servletContext.getRealPath("."), "");
   }
}


