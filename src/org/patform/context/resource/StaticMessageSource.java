package org.patform.context.resource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author leber
 * date 2019-11-03
 */
public class StaticMessageSource extends AbstractMessageSource {
    private Map<String, MessageFormat> messageFormatMap = new HashMap<>();

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        return messageFormatMap.get(code + "-" + locale.toString());
    }


    public void addMessage(String code, Locale locale, String message) {
        messageFormatMap.put(code + "-" + locale.toString(), new MessageFormat(message));
    }

    //----------toString---
    @Override
    public String toString() {
        return getClass().getName() + " :" + messageFormatMap;
    }
}
