package models.internal.impl;

import com.arpnetworking.commons.builder.OvalBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * Model class for a PagerDuty alert context, as defined in their API document:
 * https://v2.developer.pagerduty.com/docs/events-api
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public class PagerDutyContext {
    public String getType() {
        return _type;
    }

    public String getHref() {
        return _href;
    }

    public String getText() {
        return _text;
    }

    private PagerDutyContext(final Builder builder) {
        _type = builder._type;
        _href = builder._href;
        _text = builder._text;
    }

    @JsonProperty("type")
    private String _type;

    @JsonProperty("href")
    private String _href;

    @JsonProperty("text")
    private String _text;

    /**
     * Implementation of the builder pattern for a {@link PagerDutyContext}.
     *
     * @author Sheldon White (sheldon.white at smartsheet dot com)
     */
    public static final class Builder extends OvalBuilder<PagerDutyContext> {
        /**
         * Public constructor.
         */
        public Builder() {
            super(PagerDutyContext::new);
        }

        /**
         * The type. Required. Cannot be null or empty.
         *
         * @param type The type.
         * @return This instance of {@link PagerDutyContext.Builder}.
         */
        PagerDutyContext.Builder setType(final String type) {
            _type = type;
            return this;
        }

        /**
         * The alert URL. Required. Cannot be null or empty.
         *
         * @param href The alert url.
         * @return This instance of {@link PagerDutyContext.Builder}.
         */
        PagerDutyContext.Builder setHref(final String href) {
            _href = href;
            return this;
        }

        /**
         * The alert text. Required. Cannot be null or empty.
         *
         * @param text The type.
         * @return This instance of {@link PagerDutyContext.Builder}.
         */
        PagerDutyContext.Builder setText(final String text) {
            _text = text;
            return this;
        }

        @NotNull
        @NotEmpty
        private String _type;

        @NotNull
        @NotEmpty
        private String _href;

        @NotNull
        @NotEmpty
        private String _text;
    }
}
