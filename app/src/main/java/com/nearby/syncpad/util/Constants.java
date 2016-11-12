package com.nearby.syncpad.util;

public class Constants {
    /**
     * Request code to use when launching the resolution activity.
     */
    public static final int REQUEST_RESOLVE_ERROR = 1001;

    // The time-to-live when subscribing or publishing in this sample. Three minutes.
    public static final int TTL_IN_SECONDS = 5 * 60;

    // Keys to get and set the current subscription and publication tasks using SharedPreferences.
    public static final String KEY_SUBSCRIPTION_TASK = "subscription_task";
    public static final String KEY_PUBLICATION_TASK = "publication_task";

    // Tasks constants.
    public static final String TASK_SUBSCRIBE = "task_subscribe";
    public static final String TASK_UNSUBSCRIBE = "task_unsubscribe";
    public static final String TASK_PUBLISH = "task_publish";
    public static final String TASK_UNPUBLISH = "task_unpublish";
    public static final String TASK_NONE = "task_none";

    public static final String MEETING = "meeting";
    public static final String IS_HOST = "is_host";

}