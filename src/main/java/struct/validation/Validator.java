package struct.validation;

import org.apache.commons.validator.routines.UrlValidator;
import java.net.MalformedURLException;

public class Validator {

    public static boolean isValidURL(String url) throws MalformedURLException {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}