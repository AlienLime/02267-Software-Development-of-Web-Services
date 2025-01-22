/**
 * Description:
 * Represents an error message.
 * @Author Katja
 */

package dtu.group17.helpers;

public class ErrorMessageHelper {
    private String errorMessage;

    public void clear() {
        errorMessage = null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
