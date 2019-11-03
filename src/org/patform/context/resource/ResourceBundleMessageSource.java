package org.patform.context.resource;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author leber
 * date 2019-11-03
 */
public class ResourceBundleMessageSource extends AbstractMessageSource {

    private String[] baseNames;
    private Map<ResourceBundle, Map<String, MessageFormat>> cachedMessageFormats = new HashMap<>();

    public void setBaseName(String baseName) {
        setBaseNames(new String[]{baseName});
    }

    public void setBaseNames(String[] baseNames) {
        this.baseNames = baseNames;
    }


    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        MessageFormat messageFormat = null;
        for (int i = 0; Objects.isNull(messageFormat) && i < baseNames.length; i++) {
            messageFormat = resolve(baseNames[i], code, locale);
        }
        return messageFormat;
    }

    /**
     * 同步方法
     *
     * @param baseName
     * @param code
     * @param locale
     * @return
     */
    private MessageFormat resolve(String baseName, String code, Locale locale) {

        synchronized (cachedMessageFormats) {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
            Map<String, MessageFormat> stringObjectMap = cachedMessageFormats.get(bundle);
            if (Objects.nonNull(stringObjectMap)) {
                MessageFormat o = stringObjectMap.get(code);
                if (Objects.nonNull(o)) {
                    return o;
                }
            }
            String msg = bundle.getString(code);
            if (Objects.nonNull(msg)) {
                MessageFormat messageFormat = new MessageFormat(msg);
                if (Objects.nonNull(stringObjectMap)) {
                    stringObjectMap.put(code, messageFormat);
                } else {
                    stringObjectMap = new HashMap<>();
                    stringObjectMap.put(code, messageFormat);
                }
                return messageFormat;
            }
            return null;
        }
    }

    //--------------

    @Override
    public String toString() {
        return getClass().getName() + " with baseNames [" + String.join(",", baseNames) + "]";
    }
}
