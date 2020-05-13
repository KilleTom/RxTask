package com.ypz.killetom.librxtask.exception;

public class RxTaskCancelException extends RxTaskSuperException {

    public RxTaskCancelException() {
        super();
    }

    public RxTaskCancelException(String message) {
        super(message);
    }

    public RxTaskCancelException(String message, Throwable cause) {
        super(message, cause);
    }

    public RxTaskCancelException(Throwable cause) {
        super(cause);
    }
}
