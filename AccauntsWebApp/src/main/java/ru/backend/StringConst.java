package ru.backend;

/**
 * Класс, хранящий строковые константы. Создан для более удобного конфигурирования приложения.
 *
 * @author Жалдак Антон
 */
final class StringConst {
   /*
    * For ExcelWorker class
    */
   static final String excelDir = "/resources/excels/";
   // Xls name contain "begXlsName + date + endXlsName"
   static final String begXlsName = "OutputData_";
   static final String endXlsName = ".xls";

   /*
    * For MailManager class
    */
   static final String fromEmail = "132456789132456789nefaier13@mail.ru";
   static final String fromPassword = "qwerty1327";
   static final String toEmail = "kadze009@yandex.ru";
   static final String emailSubject = "Updated Users in Accaunts";

   private StringConst() {}
}
