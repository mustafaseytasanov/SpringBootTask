package struct.validation;

import org.apache.commons.validator.routines.UrlValidator;
import java.net.MalformedURLException;

public class Validator {

    public static boolean isValidURL(String url) throws MalformedURLException {
        UrlValidator urlValidator = new UrlValidator();
        if (!url.contains("://")) {
            return urlValidator.isValid("https://" + url);
        }
        return urlValidator.isValid(url);
    }
}