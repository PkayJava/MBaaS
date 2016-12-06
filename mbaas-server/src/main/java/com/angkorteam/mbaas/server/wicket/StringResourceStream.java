package com.angkorteam.mbaas.server.wicket;

import org.apache.wicket.util.resource.AbstractStringResourceStream;
import org.apache.wicket.util.resource.IFixedLocationResourceStream;

/**
 * Created by socheat on 12/7/16.
 */
public class StringResourceStream extends AbstractStringResourceStream implements IFixedLocationResourceStream {

    private static final long serialVersionUID = 1L;

    private final String resourceId;

    /**
     * The string resource
     */
    private final CharSequence string;

    /**
     * Construct.
     *
     * @param string The resource string
     */
    public StringResourceStream(String resourceId, final CharSequence string) {
        this(resourceId, string, null);
    }

    /**
     * Construct.
     *
     * @param string      The resource string
     * @param contentType The mime type of this resource, such as "image/jpeg" or "text/html"
     */
    public StringResourceStream(String resourceId, final CharSequence string, final String contentType) {
        super(contentType);
        this.string = string;
        this.resourceId = resourceId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + ": " + string.toString();
    }

    /**
     * @see org.apache.wicket.util.resource.AbstractStringResourceStream#getString()
     */
    @Override
    protected String getString() {
        return string.toString();
    }

    @Override
    public String asString() {
        return getString();
    }

    @Override
    public String locationAsString() {
        return this.resourceId;
    }
}
