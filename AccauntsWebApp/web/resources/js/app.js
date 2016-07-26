/* Data types:
 *   0. int
 *   1. string
 *   2. date
 *   3. list
 */
var dataTypes = {
    "int" : 0,
    "string" : 1,
    "date" : 2,
    "list" : 3
};

var typeNames = ["int", "string", "date", "list"];

/* Formats:
 *   0. not format       [any]
 *   1. only chars       [chars]
 *   2. chars and digits [symbols]
 *   3. email (custom)   [email]
 *   4. mobile number    [mobile]
 *   5. key (20 nums)    [key]
 *   6. date and time    [datetime]
 *   7. date             [date]
 *   8. time             [time]
 *   9. digits           [digits]
 */
var dataFormats = {
    "any": ".*",
    "chars": "[a-zA-Zа-яА-Я_]+",
    "symbols": "[\\wа-яА-Я$@#]+",
    "email": "[\\w!#$%&'*+/=?^_`{|}~]+(\\.[\\w!#$%&'*+/=?^_`{|}~]+)*@(\\w([\\w]{0,61}\\w)?\\.)*\\w+",
    "mobile": "[+\\d]{2,}",
    "key": "\\d{4,20}",
    "datetime": "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}",
    "date": "\\d{2}-\\d{2}-\\d{4}",
    "time": "\\d{2}:\\d{2}:\\d{2}",
    "digits" : "\\d+"
};

// Because database save numbers of format.
var index2formatName = [ "any", "chars", "symbols", "email", "mobile",
    "key", "datetime", "date", "time", "digits"];

var errorMessages = [
    "",                                            // any
    "Разрешены только латиница и кириллица",       // chars
    "Разрешены только буквы, цифры, $, #, _ и @",  // symbols
    "Некорректный e-mail адрес",                   // email
    "Разрешены только цифры и +",                  // mobile
    "Ключ должен состоять от 4 до 20 цифр",        // key
    "Требуется формат: yyyy-mm-dd HH:MM",          // datetime
    "Требуется формат: dd-mm-yyyy",                // date
    "Требуется формат: HH:MM:SS",                  // time
    "Зазрешены только цифры"                       // digits
];

