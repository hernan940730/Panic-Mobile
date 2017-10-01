package com.panic.security.utils;

import android.app.Activity;

import com.panic.security.R;

/**
 * Created by maikb on 1/10/2017.
 */

public class CrimesConverter {

    private static String CRIMES_LIST[] = {
            "assault_crime",
            "auto_theft_crime",
            "burglary_crime",
            "shop_lifting_crime",
            "suspicious_activity_crime",
            "homicide_crime",
            "vandalism_crime",
            "drugs_crime",
            "other_crime"
    };

    public void keysCrimesConverter() {}

    public static String converter(Activity activity, String crimeType){

        String crime = "";
        if(crimeType.equals(CRIMES_LIST[0])){
            crime = activity.getResources().getString( R.string.assault_crime );
        }else if(crimeType.equals(CRIMES_LIST[1])){
            crime = activity.getResources().getString( R.string.auto_theft_crime );
        }else if(crimeType.equals(CRIMES_LIST[2])){
            crime = activity.getResources().getString( R.string.burglary_crime );
        }else if(crimeType.equals(CRIMES_LIST[3])){
            crime = activity.getResources().getString( R.string.shop_lifting_crime );
        }else if(crimeType.equals(CRIMES_LIST[4])){
            crime = activity.getResources().getString( R.string.suspicious_activity_crime );
        }else if(crimeType.equals(CRIMES_LIST[5])){
            crime = activity.getResources().getString( R.string.homicide_crime );
        }else if(crimeType.equals(CRIMES_LIST[6])){
            crime = activity.getResources().getString( R.string.vandalism_crime );
        }else if(crimeType.equals(CRIMES_LIST[7])){
            crime = activity.getResources().getString( R.string.drugs_crime );
        }else if(crimeType.equals(CRIMES_LIST[8])){
            crime = activity.getResources().getString( R.string.other_crime );
        }
        return crime;
    }

}
