package dipzo.ordenapp.orders;

public class Constans {

//    public static final String SERVER = "https://ordenapp.co";
//    public static final String SERVER = "http://192.168.0.144:3000";
//    public static final String SERVER = "http://beta.ordenapp.co:3000/";
    public static final String SERVER = "https://www.ordenapp.co";


    public static final String API_END = SERVER + "/api/v1";

    public static final String AUTH = "/auth/login";
    public static final String SEND_GPS = "/send_gps";
    public static final String ORDERS = "/orders";
    public static final String ARRIVE = "/orders-arrive/";
    public static final String SYNC = "/orders-sync";
    public static final String DSYNC = "/orders-desync/";
    public static final String CUSTOMERS = "/customers/";
    public static final String HISTORIES = "/things/histories/";
    public static final String SEARCH_DEVICE = "/things/search_devise/";
    public static final String PENDING = "/substatus/";


    public static final int BANNER_TRANSITION_DURATION = 5000;
    public static final int NOTICE_BUTTON_BLINK_DURATION = 5000;
    public static final int BANNER_FETCH_LIMIT = 3;
}
