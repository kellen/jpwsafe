package org.pwsafe.jfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.bouncycastle.util.test.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class JpwsPasswordField extends TextFlow {

    private static final String NON_FOCUSED_STYLE = "-fx-border-color: blue; -fx-padding: 5px; -fx-min-height: 30px;";
    private static final String FOCUSED_STYLE = "-fx-border-color: red; -fx-padding: 5px; -fx-background-color: white; -fx-min-height: 30px;";
    private static final String MASK_STRING = "*";
    private static final Text[] texts = new Text[256]; // können wir uns tatsächlich auf die beschränken?

    public final BooleanProperty showPasswordProperty = new SimpleBooleanProperty(false);
    private List<PasswordCharacter> passwordCharacters = new ArrayList<>(32);

    private final class PasswordCharacter{
        private char c;
        private PasswordCharacter(char c){
            this.c = c;
        }
        private void reset(){
            this.c = '\u0000';
        }
    }

    private void setStyle(){
        if (focusedProperty().get()){
            this.setStyle(FOCUSED_STYLE);
        }
        else{
            this.setStyle(NON_FOCUSED_STYLE);
        }
    }

    private void outputPassword(){
        this.getChildren().clear();
        for(PasswordCharacter passwordCharacter : passwordCharacters){
            if(showPasswordProperty.getValue()) {
                this.getChildren().add(new Text(String.valueOf(passwordCharacter.c)));
            }
            else{
                this.getChildren().add(new Text(MASK_STRING));
            }
        }
    }

    public JpwsPasswordField() {
        this.getStyleClass().add("jpws-password-field");
        this.setStyle();
        this.focusTraversableProperty().set(true);
        this.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                setStyle();
            }
        });
        this.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // todo hier später aus liste nehmen
                // hier wird erstmal TAB und RETURN ausgefiltert - TAB wird beim Navigieren ins Feld als Event geworfen
                String charString = event.getCharacter();
                if ("\t".equals(charString) || "\r".equals(charString)) {
                    return;
                }
                if("\b".equals(charString)){
                    if (passwordCharacters.size() > 0) {
                        PasswordCharacter passwordCharacter = passwordCharacters.get(passwordCharacters.size() - 1);
                        passwordCharacter.reset();
                        passwordCharacters.remove(passwordCharacter);
                    }
                    outputPassword();
                    return;
                }
                // TODO handle backspace and delete
                else{
                    PasswordCharacter passwordCharacter = new PasswordCharacter(charString.charAt(0));
                    passwordCharacters.add(passwordCharacter);
                    getChildren().add(new Text(MASK_STRING));
                    //outputPassword();
                }
            }
        });
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(focusedProperty().get() == false){
                    ((JpwsPasswordField)event.getSource()).requestFocus();
                }
            }
        });
        /*
        this.onKeyReleasedProperty().addListener(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                System.out.println("Key Released: " + ke.getText());
            }
        });
        */
    }

    public StringBuilder getPassword(){
        StringBuilder password = new StringBuilder(passwordCharacters.size());
        for (int i = 0; i < passwordCharacters.size(); i++){
            PasswordCharacter passwordCharacter = passwordCharacters.get(i);
            password.append(passwordCharacter.c);
        }
        return password;
    }

    public void erasePassword(){
        for(PasswordCharacter passwordCharacter : passwordCharacters){
           passwordCharacter.reset();
        }
        passwordCharacters.clear();
        outputPassword();
    }

    /** Helper Method to set all characters in StringBuilder instance to null in order to sweep out all
     *  password information from memory,
     * @param password an array, which will be filled with null characters afterwards.
     */
    public static void erasePassword(StringBuilder password){
        if(password == null){
            return;
        }
        for(int i = 0; i < password.length(); i++){
            password.setCharAt(i, '\u0000');
        }
    }

}
