package ru.backend;

import ru.dao.UsersEntity;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * <p>Класс, отвечающий за передачу email-сообщений. Реализована возможность задать периодичность
 * отправления email.</p>
 *
 * @author Панкратова Александра
 * @author Жалдак Антон (редактор)
 */
public class MailManager {
   private static String fromEmail = StringConst.fromEmail;       // адрес отправляющей почты
   private static String fromPassword = StringConst.fromPassword; // пароль от отправляющей почты

   private String toEmail = StringConst.toEmail; // адрес назначения
   private boolean needRun = false;              // флаг запуска периодичной отсылки
   private long delayMinForSend = 0;             // задержка отправления email, мин
   private long delayMinForControl = 0;          // как давно происходило обновление
   //    пользовательской информации, мин

   private final static WorkWithDatabase dataBase = new WorkWithDatabase();

   /**
    * <p>Конструктор, запускающий передачу email-сообщений.</p>
    */
   public MailManager () {
      Thread sender = new Thread(new AuthenticatedSender());
      sender.start();
   }

   /**
    * <p>Функция формирования и передачи email-сообщения.</p>
    *
    * @throws MessagingException проблемы с формированием или передачей сообщения
    */
   private void sendMessage () throws MessagingException {
//      System.out.println("Try send message to " + toEmail);
      Message message = new MimeMessage(getSession());
      message.setFrom(new InternetAddress(fromEmail));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

      // Получение интересуемых пользователей
      List<UsersEntity> updatedUsers = dataBase.getUpdatedUsers(delayMinForControl);
      if (updatedUsers.size() == 0) {
         return;
      }

      // Формирование email-сообщения
      StringBuilder text = new StringBuilder("");
      text.append("id | name | surname | updateLastDate\n");
      for (UsersEntity user : updatedUsers) {
         text.append(user.getId() + " | " + user.getName() + " | " + user.getSurname() + " | " + user.getDateOfLastChange() + "\n");
      }
      message.setSubject(StringConst.emailSubject);
      message.setText(text.toString());

      //Отправка сообщение
      Transport.send(message);
//      System.out.println("Message is send");
   }

   private static Session getSession () {
      Properties properties = System.getProperties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.mail.ru");
      properties.put("mail.smtp.port", "587");

      return Session.getDefaultInstance(properties, new Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication () {
            return new PasswordAuthentication(fromEmail, fromPassword);
         }
      });
   }

   /**
    * <p>Функция, выполняющая отправку email-сообщения с заданной периодичностью.</p>
    */
   private class AuthenticatedSender implements Runnable {
      public void run () {
         while (true) {
            try {
               // send one time
               if (delayMinForSend == -1 && delayMinForControl > 0) {
                  delayMinForSend = 0;
                  sendMessage();
               }

               if (needRun && delayMinForControl > 0 && delayMinForSend > 0) {
                  if (System.currentTimeMillis() / 1000 % (delayMinForSend * 60) == 0) {
                     sendMessage();
                  }
               }
               Thread.sleep(1000);
            } catch (MessagingException e) {
               e.printStackTrace();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
   }

   public void setToEmail (String toEmail) {
      this.toEmail = toEmail;
   }

   public void setNeedRun (boolean needRun) {
      this.needRun = needRun;
   }

   public void setDelayMinForSend (long delayMinForSend) {
      this.delayMinForSend = delayMinForSend;
   }

   public void setDelayMinForControl (long delayMinForControl) {
      this.delayMinForControl = delayMinForControl;
   }

   public String getToEmail () {
      return toEmail;
   }

   public boolean isNeedRun () {
      return needRun;
   }

   public long getDelayMinForSend () {
      return delayMinForSend;
   }

   public long getDelayMinForControl () {
      return delayMinForControl;
   }
}