package com.fithub.codekienmee.fithub;

/**
 * Class that stores warning codes, and retrieves their respective warning messages.
 */
public enum WarningEnum {
    UNSAVED_POST,
    EMPTY_POST,
    EMPTY_FIELDS,
    INCORRECT_CRED,
    EMPTY_SIGN_IN,
    FAILED_SIGN_IN;

    /**
     * Returns the respective string ID holding the warning message for each warning code.
     */
    public int getWarningMessage() {
        switch(this) {
            case UNSAVED_POST:
                return R.string.unsaved_post_warning;
            case EMPTY_POST:
                return R.string.empty_post_warning;
            case EMPTY_FIELDS:
                return R.string.empty_fields_warning;
            case INCORRECT_CRED:
                return R.string.incorrect_cred_warning;
            case EMPTY_SIGN_IN:
                return R.string.empty_sign_in;
            case FAILED_SIGN_IN:
                return R.string.failed_sign_in;
            default:
                return 0;
        }
    }
}