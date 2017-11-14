/*
 * Copyright 2017 Smartsheet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models.internal.impl;

import com.arpnetworking.commons.builder.OvalBuilder;
import com.arpnetworking.logback.annotations.Loggable;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.google.inject.Injector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import models.internal.Alert;
import models.internal.AlertTrigger;
import models.internal.NotificationEntry;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Represents a notification by email.
 *
 * @author Brandon Arp (brandon dot arp at smartsheet dot com)
 */
@Loggable
public final class DefaultEmailNotificationEntry implements NotificationEntry {

    @Override
    @SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION",
            justification = "Bug in findbugs: https://github.com/findbugsproject/findbugs/issues/79")
    public CompletionStage<Void> notifyRecipient(final Alert alert, final AlertTrigger trigger, final Injector injector) {
        // TODO(brandon): fire the email
        LOGGER.debug()
                .setMessage("Sending email notification")
                .addData("address", _address)
                .addData("trigger", trigger)
                .log();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_address);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DefaultEmailNotificationEntry other = (DefaultEmailNotificationEntry) o;
        return Objects.equals(_address, other._address);
    }

    public String getAddress() {
        return _address;
    }

    private DefaultEmailNotificationEntry(final Builder builder) {
        _address = builder._address;
    }

    private final String _address;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEmailNotificationEntry.class);

    /**
     * Implementation of the builder pattern for a {@link DefaultEmailNotificationEntry}.
     *
     * @author Brandon Arp (brandon dot arp at smartsheet dot com)
     */
    public static final class Builder extends OvalBuilder<DefaultEmailNotificationEntry> {
        /**
         * Public constructor.
         */
        public Builder() {
            super(DefaultEmailNotificationEntry::new);
        }

        /**
         * The email address. Required. Cannot be null or empty.
         *
         * @param value The email address.
         * @return This instance of {@link Builder}.
         */
        public Builder setAddress(final String value) {
            _address = value;
            return this;
        }

        @NotNull
        @NotEmpty
        private String _address;
    }
}