package org.patform.context.resource;

import org.patform.context.resource.exception.NoSuchMessageException;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * message解析
 *
 * @author leber
 * date 2019-11-03
 */
public abstract class AbstractMessageSource implements HierarchicalMessageSource {

    private MessageSource parentMessageSource;

    private boolean useCodeAsDefaultMessage = false;

    @Override
    public void setParentMessageSource(MessageSource messageSource) {
        this.parentMessageSource = messageSource;
    }

    @Override
    public MessageSource getParentMessageSource() {
        return parentMessageSource;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {

        try {
            return getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {

        try {
            return getMessageInternal(code, args, locale);
        } catch (NoSuchMessageException e) {
            if (useCodeAsDefaultMessage) {
                return code;
            } else {
                throw e;
            }
        }
    }


    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {

        String[] codes = resolvable.getCodes();
        for (int i = 0; i < codes.length; i++) {
            try {
                return getMessageInternal(codes[i], resolvable.getArguments(), locale);
            } catch (NoSuchMessageException e) {
                //do nothing
            }
        }

        if (Objects.nonNull(resolvable.getDefaultMessage())) {
            return resolvable.getDefaultMessage();
        }
        if (this.useCodeAsDefaultMessage) {
            return codes[0];
        }
        throw new NoSuchMessageException();
    }

    private String getMessageInternal(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault();
        }
        MessageFormat messageFormat = resolveCode(code, locale);
        if (Objects.nonNull(messageFormat)) {

            return messageFormat.format(resolveArguments(args, locale));

        } else {
            if (Objects.nonNull(getParentMessageSource())) {
                return getParentMessageSource().getMessage(code, args, locale);
            } else {
                throw new NoSuchMessageException();
            }
        }
    }

    /**
     * 可能需要递归解析参数
     *
     * @param args
     * @param locale
     * @return
     */
    private Object[] resolveArguments(Object[] args, Locale locale) {
        if (Objects.isNull(args)) {
            return new Object[0];
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof MessageSourceResolvable) {
                MessageSourceResolvable resolvable = (MessageSourceResolvable) args[i];
                result[i] = getMessage(resolvable, locale);
            } else {
                result[i] = args[i];
            }
        }
        return result;
    }


    /**
     * 由子类实现
     *
     * @param code
     * @param locale
     * @return
     */
    protected abstract MessageFormat resolveCode(String code, Locale locale);

}
