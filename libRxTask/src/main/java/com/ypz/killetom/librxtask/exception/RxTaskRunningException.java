package com.ypz.killetom.librxtask.exception;

public class RxTaskRunningException extends RxTaskSuperException {


    public RxTaskRunningException() {
        super();
    }

    public RxTaskRunningException(String message) {
        super(message);
    }

    public RxTaskRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxTaskRunningException(Throwable cause) {
        super(cause);
    }
}
