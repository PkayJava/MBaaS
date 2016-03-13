package com.angkorteam.mbaas.server.nashorn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by socheat on 3/12/16.
 */
public class MBaaS {

    private static final Logger LOGGER = LoggerFactory.getLogger(MBaaS.class);

    public final Console Console;

    public MBaaS() {
        Console = new Console(LOGGER);
    }

}
