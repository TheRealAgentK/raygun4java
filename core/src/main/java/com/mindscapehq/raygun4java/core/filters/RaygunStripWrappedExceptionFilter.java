package com.mindscapehq.raygun4java.core.filters;

import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.messages.RaygunErrorMessage;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

public class RaygunStripWrappedExceptionFilter implements IRaygunOnBeforeSend {

    private Class[] stripClasses;

    public RaygunStripWrappedExceptionFilter(Class... stripClasses) {
        this.stripClasses = stripClasses;
    }

    public RaygunMessage onBeforeSend(RaygunMessage message) {

        if(message.getDetails() != null
                && message.getDetails().getError() != null
                && message.getDetails().getError().getInnerError() != null
                && message.getDetails().getError().getThrowable() != null) {

            for (Class stripClass : stripClasses) {
                if (stripClass.isAssignableFrom(message.getDetails().getError().getThrowable().getClass())) {
                    RaygunErrorMessage innerError = message.getDetails().getError().getInnerError();
                    message.getDetails().setError(innerError);

                    // rerun check on the reassigned error
                    onBeforeSend(message);
                }
            }
        }

        return message;
    }
}