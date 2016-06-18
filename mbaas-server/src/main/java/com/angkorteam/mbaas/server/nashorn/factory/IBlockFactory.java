package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.block.BlockPanel;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/17/16.
 */
public interface IBlockFactory extends Serializable {

    BlockPanel createBlock(String id, String code);

    BlockPanel createBlock(MarkupContainer container, String id, String code);

}
