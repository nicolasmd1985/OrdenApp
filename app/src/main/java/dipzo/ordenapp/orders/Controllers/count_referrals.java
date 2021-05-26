package dipzo.ordenapp.orders.Controllers;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import dipzo.ordenapp.orders.Sqlite.referrals;

public class count_referrals {
    private Context context;
    private ProgressDialog prgDialog;

    public count_referrals(Context context) {
        super();
        this.context = context;
    }

    public int count()
    {
        referrals referrals = new referrals(context);
        ArrayList<HashMap<String, String>> auto= referrals.get_refferals();
        ArrayList<HashMap<String, String>> manual = referrals.get_manual_refferals();
        return auto.size() + manual.size();
    }
}
