package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2.NashornSingleChoiceProvider;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 */
public interface ISingleChoiceProviderFactory extends Serializable {

    NashornSingleChoiceProvider createSingleChoiceProvider();

}
