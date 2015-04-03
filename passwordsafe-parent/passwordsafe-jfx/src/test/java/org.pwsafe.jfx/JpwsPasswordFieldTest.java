package org.pwsafe.jfx;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class JpwsPasswordFieldTest {

   private JpwsPasswordField jpwsPasswordField;

    @Before
    public void before(){
        jpwsPasswordField = new JpwsPasswordField();
    }

    @Test
    public void testErasePassword(){
        StringBuilder password = new StringBuilder("abc");
        JpwsPasswordField.erasePassword(password);
        StringBuilder result = new StringBuilder();
        result.append('\u0000');
        result.append('\u0000');
        result.append('\u0000');
        // beim StringBuilder funktioniert equals() nicht
        //Assert.assertTrue(result.equals(password));
        Assert.assertTrue(result.toString().equals(password.toString()));
    }

    @Test
    public void testErasePasswordWithNull(){
        StringBuilder password = null;
        JpwsPasswordField.erasePassword(password);
        Assert.assertNull(password);
    }

}
