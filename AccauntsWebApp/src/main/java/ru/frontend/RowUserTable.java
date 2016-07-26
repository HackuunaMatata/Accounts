package ru.frontend;


import org.json.JSONObject;

import static ru.frontend.DataType.LISTS;

/**
 * <p>Class for containing row of user table.</p> Table:<br>
 * <code><pre>| id | text | value | type | format (not visible) |</pre></code><br>
 * where, <ul>
 *    <li>id     - id question</li>
 *    <li>text   - text of question</li>
 *    <li>value  - user answer</li>
 *    <li>type   - type of answer (string, int, etc.) (value from DataType class)</li>
 *    <li>format - format index of data (see app.js)</li></ul>
 * <p>
 * <p>Using in UserWorkServlet class in method getUserInfoByIdAndFormTableInfo for collect info and
 * forming json.</p>
 * <p>Can replace as inner class for UserWorkServlet class.</p>
 *
 * @author Zhaldak Anton
 */
public class RowUserTable {
   private Object format;
   private String value;
   private String text;
   private int type;
   private int id;


   public RowUserTable () {
      value = "";
      text = "";
      type = 0;
      id = 0;
      format = null;
   }

   public void setFormat (Object format) {
      this.format = format;
   }

   public void setId (int id) {
      this.id = id;
   }

   public void setText (String text) {
      this.text = text;
   }

   public void setType (int type) {
      this.type = type;
   }

   public void setValue (String value) {
      this.value = value;
   }

   public Object getFormat () {
      return format;
   }

   public int getId () {
      return id;
   }

   public String getText () {
      return text;
   }

   public int getType () {
      return type;
   }

   public String getValue () {
      return value;
   }

   public JSONObject getJSONObject () {
      JSONObject jsonOutput = new JSONObject();
      jsonOutput.put("id", id);
      jsonOutput.put("text", text);
      jsonOutput.put("type", type);
      jsonOutput.put("value", value);
      if (type == LISTS.getVal()) {
         jsonOutput.put("format", (String[]) format);
      } else {
         jsonOutput.put("format", (Integer) format);
      }
      return jsonOutput;
   }
}
