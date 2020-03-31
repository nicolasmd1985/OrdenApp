package mahecha.nicolas.elcaaplicacion;

public class Constans {

    public static final String SERVER = "https://ordenapp.co";
//    public static final String SERVER = "http://192.168.0.186:3000";
    public static final String API_END = SERVER + "/api/v1";

    public static final String AUTH = "/auth/login";
    public static final String SEND_GPS = "/send_gps";
    public static final String ORDERS = "/orders";
    public static final String SYNC = "/orders-sync";
    public static final String DSYNC = "/orders-desync/";
    public static final String CUSTOMERS = "/customers/";


    public static final int BANNER_TRANSITION_DURATION = 5000;
    public static final int NOTICE_BUTTON_BLINK_DURATION = 5000;
    public static final int BANNER_FETCH_LIMIT = 3;
}
