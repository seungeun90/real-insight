package io.insight.real.exception;


public interface ServiceErrorMessage {
    Integer getErrorCode();
    String getMessageCode();
    String[] getArgs();


}
