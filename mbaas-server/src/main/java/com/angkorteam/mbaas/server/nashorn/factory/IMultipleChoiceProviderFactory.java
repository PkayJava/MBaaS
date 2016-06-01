package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornMultipleChoiceProvider;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 */
public interface IMultipleChoiceProviderFactory extends Serializable {

    NashornMultipleChoiceProvider createMultipleChoiceProvider();

}
