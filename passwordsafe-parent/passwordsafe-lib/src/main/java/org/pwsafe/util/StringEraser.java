package org.pwsafe.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;

/** Class to overwrite the content of a string object in order to protect passwords.
 *
 * Sometimes we are forced to use a java.lang.String to store a password. This class
 * can delete the content of a string. It should be used with
 * care and should be considered as useful hack and not as good code.
 *
 * Tribute to Dr. Heinz M. Kabutz and his article "Insane Strings" (see
 * http://www.javaspecialists.eu/archive/Issue014.html ).
 *
 *
 */
public class StringEraser {

    private static Log log = LogFactory.getLog(StringEraser.class);

    /** Overwrites the content of a string with blanks in order to erase all
     * traces of the former value and then makes it a empty string. */
    public static void erase(String stringToErase){
        Field valueProperty = null;
        try{
            valueProperty = String.class.getDeclaredField("value");
            boolean formerAccessibility = valueProperty.isAccessible();
            valueProperty.setAccessible(true);
            Object valueProperyAsObject = valueProperty.get(stringToErase);
            char[] valueArray = (char[])valueProperyAsObject;
            // this erases the value of the password. Even if the char array is not
            // garbage collected now, there are no longer the chars of the
            // string in memory, in a swapfile writen after this call or in
            // a memory dump.
            for(int i = 0; i < valueArray.length; i++){
                valueArray[i] = ' ';
            }
            // this hides the length of the string / the password
            char[] emptyStringArray = new char[0];
            valueProperty.set(stringToErase, emptyStringArray);
            valueProperty.setAccessible(formerAccessibility);
        }
        catch(NoSuchFieldException nsfe){
            if (log.isDebugEnabled()){
                log.debug(nsfe);
            }
        }
        catch(IllegalAccessException iae){
            if (log.isDebugEnabled()){
                log.debug(iae);
            }
        }
    }

}
