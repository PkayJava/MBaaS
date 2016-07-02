package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.BlockPanel;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 6/17/16.
 */
public interface IBlockFactory extends Serializable {

    BlockPanel createBlock(String id, String blockCode);

    BlockPanel createBlock(MarkupContainer container, String id, String blockCode);

}
