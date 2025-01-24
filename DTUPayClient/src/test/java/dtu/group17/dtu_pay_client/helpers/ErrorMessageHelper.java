/**
 * Author: Kristoffer Magnus Overgaard (s194110)
 * Description:
 * Represents an error message.
 */

package dtu.group17.dtu_pay_client.helpers;

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
