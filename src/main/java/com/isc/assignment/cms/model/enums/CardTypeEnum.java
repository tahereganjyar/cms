package com.isc.assignment.cms.model.enums;

public enum CardTypeEnum {

    CREDIT((byte) 1), CASH((byte) 2);

    private final byte code;

    CardTypeEnum(byte code) {

        this.code = code;
    }

    public byte getCode() {

        return code;
    }

    public static CardTypeEnum fromCode(byte code) {

        for (CardTypeEnum s : values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
