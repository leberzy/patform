package org.patform.context.resource;

import java.util.Objects;

/**
 * @author leber
 * date 2019-11-03
 */
public class DefaultMessageSourceResolvable implements MessageSourceResolvable {

    private String codes[];
    private Object[] arguments;
    private String defaultMessage;

    public DefaultMessageSourceResolvable() {
    }

    public DefaultMessageSourceResolvable(String[] codes, Object[] arguments) {
        this(codes, arguments, null);
    }

    public DefaultMessageSourceResolvable(String[] codes, Object[] arguments, String defaultMessage) {
        this.codes = codes;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }


    public DefaultMessageSourceResolvable(MessageSourceResolvable resolvable) {
        this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
    }

    @Override
    public String[] getCodes() {
        return codes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setCodes(String[] codes) {
        this.codes = codes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String toString() {
        return resolvableToString();
    }
    //-------------------------------------------------------

    public String resolvableToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("codes=[");
        if (Objects.nonNull(codes)) {
            String code = String.join(",", codes);
            sb.append(code);
        }
        sb.append("];");
        sb.append("arguments=[");
        if (Objects.nonNull(arguments) && arguments.length > 0) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append('(').append(arguments[i].getClass().getName()).append(')')
                        .append('[').append(arguments[i]).append(']');
                if (i < arguments.length) {
                    sb.append(',');
                }
            }
        } else {
            sb.append("null");
        }
        sb.append(']');
        sb.append(";defaultMessage=[").append(defaultMessage).append(']');
        return sb.toString();
    }
}
