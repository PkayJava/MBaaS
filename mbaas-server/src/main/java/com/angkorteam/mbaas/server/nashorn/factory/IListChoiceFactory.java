//package com.angkorteam.mbaas.server.nashorn.factory;
//
//import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornListChoice;
//import org.apache.wicket.MarkupContainer;
//import org.apache.wicket.markup.html.form.IChoiceRenderer;
//import org.apache.wicket.model.IModel;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by socheat on 6/1/16.
// * <select wicket:id="select"></select>
// */
//public interface IListChoiceFactory extends Serializable {
//
//    NashornListChoice createListChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer);
//
//    NashornListChoice createListChoice(MarkupContainer markupContainer, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer);
//
//    NashornListChoice createListChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows);
//
//    NashornListChoice createListChoice(MarkupContainer markupContainer, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows);
//
//}
