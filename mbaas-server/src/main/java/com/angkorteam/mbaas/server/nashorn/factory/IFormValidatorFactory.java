package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornFormValidator;

import java.io.Serializable;

/**
 * Created by socheat on 6/1/16.
 */
public interface IFormValidatorFactory extends Serializable {

    NashornFormValidator createFormValidator();
}
