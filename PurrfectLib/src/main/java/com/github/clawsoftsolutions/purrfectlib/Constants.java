package com.github.clawsoftsolutions.purrfectlib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String LIB_NAME = "PurrfectLib";
    public static final Logger LOGGER = LoggerFactory.getLogger(LIB_NAME);

    public static Logger addExt(String extName) {
        return LoggerFactory.getLogger(LIB_NAME + "/" + extName);
    }

}
