package com.angkorteam.mbaas.server.nashorn.factory;

import org.apache.wicket.model.util.ListModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by socheat on 6/10/16.
 */
public interface IList extends Serializable {

    <E> List<E> createList();

    <E> ListModel<E> createListModel(List<E> object);

}
