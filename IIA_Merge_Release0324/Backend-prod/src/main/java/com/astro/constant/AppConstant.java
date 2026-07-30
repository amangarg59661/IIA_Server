package com.astro.constant;

public class AppConstant {
    public static final int API_SUCCESS = 0;

    // action Type
    public static final String CREATED_TYPE = "Created";
    public static final String IN_PROGRESS_TYPE = "In-progress";
    public static final String COMPLETED_TYPE = "Completed";
    public static final String CANCELED_TYPE = "Canceled";
    public static final String PENDING_TYPE = "Pending";
    public static final String DONE_TYPE = "Done";
    public static final String APPROVE_TYPE = "Approved";
    public static final String REJECT_TYPE = "Rejected";
    public static final String CHANGE_REQUEST_TYPE = "Change requested";

    // Error Type Codes
    public static final int ERROR_TYPE_CODE_DB = 1;
    public static final int ERROR_TYPE_CODE_VALIDATION = 2;
    public static final int ERROR_TYPE_CODE_INTERNAL = 3;
    public static final int ERROR_CODE_RESOURCE = 5;
    public static final int ERROR_TYPE_CODE_INVALID = 6;
    public static final int WORKFLOW_BRANCH_NOT_CONFIGURED = 1014;

    // Error Type
    public static final String ERROR_TYPE_ERROR = "error";
    public static final String ERROR_TYPE_VALIDATION = "validation";
    public static final String ERROR_TYPE_RESOURCE = "missing resource";

    // Error Codes for Email Failures
    public static final int EMAIL_NOT_SENT_ERROR_CODE = 2001;
    public static final String ERROR_TYPE_EMAIL = "email";
    public static final String ERROR_TYPE_CORRUPTED = "corrupted resource";
    public static final String ERROR_TYPE_INVALID = "invalid arguments";

    public static final int ERROR_TYPE_CODE_RESOURCE = 5001;
    public static final int INTER_SERVER_ERROR = 1000;

    public static final int WORKFLOW_NOT_FOUND = 1001;
    public static final int USER_INVALID_INPUT = 1002;
    public static final int USER_NOT_FOUND = 1003;
    public static final int TRANSITION_NOT_FOUND = 1004;
    public static final int WORKFLOW_ALREADY_EXISTS = 1005;
    public static final int INVALID_WORKFLOW_TRANSITION = 1006;
    public static final int UNAUTHORIZED_ACTION = 1007;
    public static final int INVALID_TRANSITION_ACTION = 1008;
    public static final int NEXT_TRANSITION_NOT_FOUND = 1009;
    public static final int INVALID_ACTION = 1010;
    public static final int INVALID_FILE_TYPE = 1011;
    public static final int FILE_NOT_FOUND = 1012;
    public static final int FILE_UPLOAD_ERROR = 1013;

    public static final int ERROR_CODE_INVALID = 2001;


    //Folder Path
}
