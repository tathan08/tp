package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s1-cs2103t-t08-4.github.io/tp/UserGuide.html";

    public static final String ADD_COMMAND = "Add: add n/NAME [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]...";
    public static final String LIST_COMMAND = "List: list";
    public static final String EDIT_COMMAND = "Edit: edit n/OLD_NAME [n/NEW_NAME] [p/PHONE] [e/EMAIL] [t/TAG]...";
    public static final String FIND_COMMAND = "Find: find n/NAME\n"
            + "   OR find t/TAG...\n"
            + "   OR find d/DATE...";
    public static final String DELETE_COMMAND = "Delete: delete n/PERSON_NAME\n"
            + "     OR delete n/PERSON_NAME t/TAG...\n"
            + "     OR delete n/PERSON_NAME b/BOOKING_INDEX...";
    public static final String BOOK_COMMAND = "Book: book dt/DATETIME c/CLIENT_NAME "
            + "n/PERSON_NAME [desc/DESCRIPTION]";
    public static final String CLEAR_COMMAND = "Clear: clear f/";
    public static final String ENDING_NOTE =
            "Note: Fields in [] are optional. ... means can be used multiple times.\n"
            + "At least 1 optional field is required for edit and find commands.";

    public static final String HELP_MESSAGE = ADD_COMMAND + "\n"
            + LIST_COMMAND + "\n"
            + EDIT_COMMAND + "\n"
            + FIND_COMMAND + "\n"
            + DELETE_COMMAND + "\n"
            + BOOK_COMMAND + "\n"
            + CLEAR_COMMAND + "\n\n"
            + ENDING_NOTE + "\n\n"
            + "For more information, please refer to the user guide: " + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Button quitButton;

    @FXML
    private Label helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        setupKeyboardHandlers();
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Sets up keyboard event handlers for the help window.
     */
    private void setupKeyboardHandlers() {
        getRoot().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                hide();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                // Arrow key navigation between buttons
                if (copyButton.isFocused()) {
                    quitButton.requestFocus();
                } else if (quitButton.isFocused()) {
                    copyButton.requestFocus();
                }
                event.consume();
            }
        });

        // Make both buttons focusable for navigation
        copyButton.setFocusTraversable(true);
        quitButton.setFocusTraversable(true);
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
        // Set focus to quit button by default
        quitButton.requestFocus();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }

    /**
     * Closes the help window.
     */
    @FXML
    private void handleQuit() {
        hide();
    }
}
