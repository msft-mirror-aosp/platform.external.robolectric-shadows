package org.robolectric.shadows;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.LOLLIPOP_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.Q;
import static android.os.Build.VERSION_CODES.S;
import static org.robolectric.RuntimeEnvironment.getApiLevel;

import java.util.Locale;
import libcore.icu.LocaleData;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.util.ReflectionHelpers;

/**
 * Robolectric only supports en_US regardless of the default locale set in the JVM.
 */
@Implements(value = LocaleData.class, isInAndroidSdk = false)
public class ShadowLocaleData {
  public static final String REAL_CLASS_NAME = "libcore.icu.LocaleData";

  @Implementation
  public static LocaleData get(Locale locale) {
    LocaleData localeData = (LocaleData) Shadow.newInstanceOf(REAL_CLASS_NAME);
    if (locale == null) {
      locale = Locale.getDefault();
    }
    setEnUsLocaleData(localeData);
    return localeData;
  }

  private static void setEnUsLocaleData(LocaleData localeData) {
    localeData.amPm = new String[]{"AM", "PM"};
    localeData.eras = new String[]{"BC", "AD"};

    localeData.firstDayOfWeek = 1;
    localeData.minimalDaysInFirstWeek = 1;

    localeData.longMonthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    localeData.shortMonthNames = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    if (getApiLevel() >= JELLY_BEAN_MR1) {
      localeData.tinyMonthNames = new String[]{"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"};
      localeData.tinyStandAloneMonthNames = localeData.tinyMonthNames;
      localeData.tinyWeekdayNames = new String[]{"", "S", "M", "T", "W", "T", "F", "S"};
      localeData.tinyStandAloneWeekdayNames = localeData.tinyWeekdayNames;

      localeData.today = "Today";
      localeData.tomorrow = "Tomorrow";
    }

    localeData.longStandAloneMonthNames = localeData.longMonthNames;
    localeData.shortStandAloneMonthNames = localeData.shortMonthNames;

    localeData.longWeekdayNames = new String[]{"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    localeData.shortWeekdayNames = new String[]{"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    localeData.longStandAloneWeekdayNames = localeData.longWeekdayNames;
    localeData.shortStandAloneWeekdayNames = localeData.shortWeekdayNames;

    if (getApiLevel() >= M) {
      localeData.timeFormat_hm = "h:mm a";
      localeData.timeFormat_Hm = "HH:mm";
    } else if (getApiLevel() >= JELLY_BEAN_MR2) {
      ReflectionHelpers.setField(localeData, "timeFormat12", "h:mm a");
      ReflectionHelpers.setField(localeData, "timeFormat24", "HH:mm");
    }


    // These fields are removed in Android R or later
    if (getApiLevel() <= Q) {
      ReflectionHelpers.setField(localeData, "currencySymbol", "$");
      ReflectionHelpers.setField(localeData, "internationalCurrencySymbol", "USD");
      ReflectionHelpers.setField(localeData, "yesterday", "Yesterday");
    }


    // These fields are removed in Android T or later
    if (getApiLevel() <= S) {
      ReflectionHelpers.setField(localeData, "fullDateFormat", "EEEE, MMMM d, y");
      ReflectionHelpers.setField(localeData, "longDateFormat", "MMMM d, y");
      ReflectionHelpers.setField(localeData, "mediumDateFormat", "MMM d, y");
      ReflectionHelpers.setField(localeData, "shortDateFormat", "M/d/yy");
      ReflectionHelpers.setField(localeData, "zeroDigit", '0');
      ReflectionHelpers.setField(localeData, "decimalSeparator", '.');
      ReflectionHelpers.setField(localeData, "groupingSeparator", ',');
      ReflectionHelpers.setField(localeData, "patternSeparator", ';');
      ReflectionHelpers.setField(localeData, "monetarySeparator", '.');
      ReflectionHelpers.setField(localeData, "exponentSeparator", "E");
      ReflectionHelpers.setField(localeData, "infinity", "\u221E");
      ReflectionHelpers.setField(localeData, "NaN", "NaN");
      ReflectionHelpers.setField(localeData, "numberPattern", "\u0023,\u0023\u00230.\u0023\u0023\u0023");
      ReflectionHelpers.setField(localeData, "integerPattern", "\u0023,\u0023\u00230");
      ReflectionHelpers.setField(localeData, "currencyPattern", "\u00A4\u0023,\u0023\u00230.00;(\u00A4\u0023,\u0023\u00230.00)");
      ReflectionHelpers.setField(localeData, "percentPattern", "\u0023,\u0023\u00230%");

      if (getApiLevel() >= LOLLIPOP_MR1) {
        // Lollipop MR1 uses a String
        ReflectionHelpers.setField(localeData, "percent", "%");
      } else {
        // Upto Lollipop was a char
        ReflectionHelpers.setField(localeData, "percent", '%');
      }

      if (getApiLevel() >= android.os.Build.VERSION_CODES.P) {
        // P uses a String
        ReflectionHelpers.setField(localeData, "perMill", "‰");
      } else {
        // Up to P was a char
        ReflectionHelpers.setField(localeData, "perMill", '‰');
      }

      if (getApiLevel() >= LOLLIPOP) {
        // Lollipop uses a String
        ReflectionHelpers.setField(localeData, "minusSign", "-");
      } else {
        // Upto KitKat was a char
        ReflectionHelpers.setField(localeData, "minusSign", '-');
      }
    }
  }
}
