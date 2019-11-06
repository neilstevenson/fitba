package neil.demo.fitba;

public class JsonData {
       
       public static final String[] ACCOUNT_BASELINE = new String[] {
               "{ \"account\" : \"N\", \"name\" : \"Neil\", \"date\" : \"2019-10-31\", "
                               + "\"balance\" : \"100.00\", \"kind\" : \"baseline\" }",
               "{ \"account\" : \"J\", \"name\" : \"Jonny\", \"date\" : \"2019-10-31\", "
                               + "\"balance\" : \"100.00\", \"kind\" : \"baseline\" }",
       };
       
       public static final String[] ACCOUNT_TRANSACTIONS = new String[] {
               "{ \"account\" : \"N\", \"timestamp\" : \"2019-11-01T23:58\", \"credit\" : \"100.00\", "
                               + "     \"description\" : \"Salary\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"J\", \"timestamp\" : \"2019-11-01T23:59\", \"credit\" : \"100.00\", "
                               + "     \"description\" : \"Salary\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"N\", \"timestamp\" : \"2019-11-14T13:00\", \"debit\" : \"2.00\", "
                               + "     \"description\" : \"Starbucks\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"N\", \"timestamp\" : \"2019-11-14T13:10\", \"debit\" : \"50.00\", "
                               + "     \"description\" : \"Alsterhaus\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"N\", \"timestamp\" : \"2019-11-14T16:16\", \"debit\" : \"2.00\", "
                               + "     \"description\" : \"Starbucks\", \"kind\" : \"transaction\" }",
       };
       
       public static final String[] MORE_ACCOUNT_TRANSACTIONS = new String[] {
               "{ \"account\" : \"J\", \"timestamp\" : \"2019-11-14T22:00\", \"debit\" : \"3.95\", "
                               + "     \"description\" : \"Bierkeller\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"J\", \"timestamp\" : \"2019-11-14T22:05\", \"debit\" : \"3.95\", "
                               + "     \"description\" : \"Bierkeller\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"J\", \"timestamp\" : \"2019-11-14T22:10\", \"debit\" : \"3.95\", "
                               + "     \"description\" : \"Bierkeller\", \"kind\" : \"transaction\" }",
               "{ \"account\" : \"N\", \"timestamp\" : \"2019-11-14T22:50\", \"debit\" : \"3.95\", "
                               + "     \"description\" : \"Bierkeller\", \"kind\" : \"transaction\" }",
       };

}
