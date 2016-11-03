package com.angkorteam.mbaas.server;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.server.bean.AuthorizationStrategy;
import com.angkorteam.mbaas.server.bean.ClassResolver;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.page.CmsPage;
import com.angkorteam.mbaas.server.page.DashboardPage;
import com.angkorteam.mbaas.server.page.LoginPage;
import groovy.lang.GroovyCodeSource;
import org.apache.logging.log4j.util.Strings;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.resource.DynamicJQueryResourceReference;
import org.apache.wicket.settings.ExceptionSettings;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 10/23/16.
 */
public class Application extends AuthenticatedWebApplication {

    public static final List<Character> CHARACTERS = new ArrayList<>();
    public static final List<Character> NUMBERS = new ArrayList<>();

    static {
        CHARACTERS.add('a');
        CHARACTERS.add('b');
        CHARACTERS.add('c');
        CHARACTERS.add('d');
        CHARACTERS.add('e');
        CHARACTERS.add('f');
        CHARACTERS.add('g');
        CHARACTERS.add('h');
        CHARACTERS.add('i');
        CHARACTERS.add('j');
        CHARACTERS.add('k');
        CHARACTERS.add('l');
        CHARACTERS.add('m');
        CHARACTERS.add('n');
        CHARACTERS.add('o');
        CHARACTERS.add('p');
        CHARACTERS.add('q');
        CHARACTERS.add('r');
        CHARACTERS.add('s');
        CHARACTERS.add('t');
        CHARACTERS.add('u');
        CHARACTERS.add('v');
        CHARACTERS.add('x');
        CHARACTERS.add('w');
        CHARACTERS.add('y');
        CHARACTERS.add('z');
        NUMBERS.add('0');
        NUMBERS.add('1');
        NUMBERS.add('2');
        NUMBERS.add('3');
        NUMBERS.add('4');
        NUMBERS.add('5');
        NUMBERS.add('6');
        NUMBERS.add('7');
        NUMBERS.add('8');
        NUMBERS.add('9');
    }

    public Application() {
    }

    @Override
    protected void init() {
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
        AuthorizationStrategy authorizationStrategy = Spring.getBean(AuthorizationStrategy.class);
        getExceptionSettings().setAjaxErrorHandlingStrategy(ExceptionSettings.AjaxErrorStrategy.REDIRECT_TO_ERROR_PAGE);
        getExceptionSettings().setThreadDumpStrategy(ExceptionSettings.ThreadDumpStrategy.THREAD_HOLDING_LOCK);
        getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_EXCEPTION_PAGE);
        getSecuritySettings().setAuthorizationStrategy(authorizationStrategy);
        getRequestCycleSettings().setBufferResponse(true);
        getRequestCycleSettings().setGatherExtendedBrowserInfo(true);
        getMarkupSettings().setCompressWhitespace(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getApplicationSettings().setClassResolver(Spring.getBean(ClassResolver.class));
        getJavaScriptLibrarySettings().setJQueryReference(new DynamicJQueryResourceReference());
        initLayout();
        initPageMount();
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return Session.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return DashboardPage.class;
    }

    protected void initLayout() {
        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
        DSLContext context = Spring.getBean(DSLContext.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        List<LayoutPojo> layouts = context.select(layoutTable.fields()).from(layoutTable).fetchInto(LayoutPojo.class);
        for (LayoutPojo layout : layouts) {
            if (!layout.getSystem() && !Strings.isEmpty(layout.getGroovy())) {
                String groovy = layout.getGroovy();
                StringBuffer newGroovy = new StringBuffer(groovy.substring(0, groovy.lastIndexOf("}")));
                newGroovy.append("\n @Override\n" +
                        "        public final String getLayoutUUID () {\n" +
                        "            return \"" + layout.getLayoutId() + "\";\n" +
                        "        } }");


                GroovyCodeSource source = new GroovyCodeSource(newGroovy.toString(), GroovyClassLoader.LAYOUT + layout.getLayoutId(), "/groovy/script");
                source.setCachable(true);
                classLoader.parseClass(source, true);
            }
        }
    }

    protected void initPageMount() {
        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        List<PagePojo> pages = context.select(pageTable.fields()).from(pageTable).fetchInto(PagePojo.class);
        for (PagePojo page : pages) {
            if (!page.getCmsPage()) {
                try {
                    mountPage(page.getPath(), (Class<WebPage>) Class.forName(page.getJavaClass()));
                } catch (ClassNotFoundException e) {
                    throw new WicketRuntimeException(e);
                }
            } else {
                String groovy = page.getGroovy();
                StringBuffer newGroovy = new StringBuffer(groovy.substring(0, groovy.lastIndexOf("}")));
                newGroovy.append("\n @Override\n" +
                        "        public final String getPageUUID () {\n" +
                        "            return \"" + page.getPageId() + "\";\n" +
                        "        } }");

                GroovyCodeSource source = new GroovyCodeSource(newGroovy.toString(), GroovyClassLoader.PAGE + page.getPageId(), "/groovy/script");
                source.setCachable(true);
                Class<? extends CmsPage> pageClass = classLoader.parseClass(source, true);
                Application.get().mountPage(page.getPath(), pageClass);
            }
        }
    }
}
