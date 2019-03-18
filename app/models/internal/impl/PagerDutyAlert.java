package models.internal.impl;

import com.arpnetworking.commons.builder.OvalBuilder;
import com.arpnetworking.mql.grammar.AlertTrigger;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.internal.Alert;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * Model class for a PagerDuty alert, as defined in their API document:
 * https://v2.developer.pagerduty.com/docs/events-api
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public class PagerDutyAlert {
    public String getServiceKey() {
        return _serviceKey;
    }

    public String getEventType() {
        return _eventType;
    }

    public String getDescription() {
        return _description;
    }

    public Alert getAlert() {
        return _alert;
    }

    public AlertTrigger getTrigger() {
        return _trigger;
    }

    public String getAlertUrl() {
        return _alertUrl;
    }

    public PagerDutyContext[] getContexts() {
        return _contexts;
    }

    private PagerDutyAlert(final PagerDutyAlert.Builder builder) {
        _serviceKey = builder._serviceKey;
        _eventType = builder._eventType;
        _description = builder._description;
        _alert = builder._alert;
        _trigger = builder._trigger;
        _alertUrl = builder._alertUrl;
        _contexts = builder._contexts;
    }

    @JsonProperty("service_key")
    private String _serviceKey;

    @JsonProperty("event_type")
    private String _eventType;

    @JsonProperty("description")
    private String _description;

    @JsonProperty("alert")
    private Alert _alert;

    @JsonProperty("trigger")
    private AlertTrigger _trigger;

    @JsonProperty("alertUrl")
    private String _alertUrl;

    @JsonProperty("contexts")
    private PagerDutyContext[] _contexts;

    /**
     * Implementation of the builder pattern for a {@link DefaultEmailNotificationEntry}.
     *
     * @author Sheldon White (sheldon.white at smartsheet dot com)
     */
    public static final class Builder extends OvalBuilder<PagerDutyAlert> {
        /**
         * Public constructor.
         */
        public Builder() {
            super(PagerDutyAlert::new);
        }

        /**
         * The service key. Required. Cannot be null or empty.
         *
         * @param serviceKey The service key.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setServiceKey(final String serviceKey) {
            this._serviceKey = serviceKey;
            return this;
        }

        /**
         * The event type. Required. Cannot be null or empty.
         *
         * @param eventType The event type.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setEventType(final String eventType) {
            this._eventType = eventType;
            return this;
        }

        /**
         * The description. Required. Cannot be null or empty.
         *
         * @param description The description.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setDescription(final String description) {
            this._description = description;
            return this;
        }

        /**
         * The alert. Required. Cannot be null.
         *
         * @param alert The alert.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setAlert(final Alert alert) {
            this._alert = alert;
            return this;
        }

        /**
         * The trigger. Required. Cannot be null.
         *
         * @param trigger The trigger.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setTrigger(final AlertTrigger trigger) {
            this._trigger = trigger;
            return this;
        }

        /**
         * The alert url. Required. Cannot be null or empty.
         *
         * @param alertUrl The alert url.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setAlertUrl(String alertUrl) {
            this._alertUrl = alertUrl;
            return this;
        }

        /**
         * The pagerduty contexts. Required. Cannot be null or empty.
         *
         * @param contexts The contexts.
         * @return This instance of {@link PagerDutyAlert.Builder}.
         */
        PagerDutyAlert.Builder setContexts(PagerDutyContext[] contexts) {
            this._contexts = contexts;
            return this;
        }


        @NotNull
        @NotEmpty
        private String _serviceKey;

        @NotNull
        @NotEmpty
        private String _eventType;

        @NotNull
        @NotEmpty
        private String _description;

        @NotNull
        private Alert _alert;

        @NotNull
        private AlertTrigger _trigger;

        @NotNull
        @NotEmpty
        private String _alertUrl;

        @MinSize(value=1)
        private PagerDutyContext[] _contexts;
    }
}
