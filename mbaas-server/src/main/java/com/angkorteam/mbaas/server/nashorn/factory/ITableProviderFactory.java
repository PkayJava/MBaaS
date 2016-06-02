package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;

import java.io.Serializable;

/**
 * Created by socheat on 6/2/16.
 */
public interface ITableProviderFactory extends Serializable {

    NashornTableProvider createTableProvider();
    
}
