package com.ypz.killetom.rxjava3.lib_rxtask.exception;

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
