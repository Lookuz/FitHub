package com.fithub.codekienmee.fithub;

/**
 * Class that stores warning codes, and retrieves their respective warning messages.
 */
public enum WarningEnum {
    UNSAVED_POST,
    EMPTY_POST;

    /**
     * Returns the respective string ID holding the warning message for each warning code.
     */
    public int getWarningMessage() {
        switch(this) {
            case UNSAVED_POST:
                return R.string.unsaved_post_warning;
            case EMPTY_POST:
                return R.string.empty_post_warning;
            default:
                return 0;
        }
    }
}